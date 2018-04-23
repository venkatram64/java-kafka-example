package com.venkat.kafka.streams.joins;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.util.Properties;

/*
rem create input topic for user purchases
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic user-purchases

rem create table of user information - log compacted for optimisation
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic user-table --config cleanup.policy=compact

rem create out topic for user purchases enriched with user data (left join)
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic user-purchases-enriched-left-join

rem create out topic for user purchases enriched with user data (inner join)
bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic user-purchases-enriched-inner-join

rem start a consumer on the output topic (left join)
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
    --topic user-purchases-enriched-left-join ^
    --from-beginning ^
    --formatter kafka.tools.DefaultMessageFormatter ^
    --property print.key=true ^
    --property print.value=true ^
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer


rem start a consumer on the output topic (inner join)
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
    --topic user-purchases-enriched-inner-join ^
    --from-beginning ^
    --formatter kafka.tools.DefaultMessageFormatter ^
    --property print.key=true ^
    --property print.value=true ^
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
 */

public class UserEventEnricherJoin {

    public Properties getProperties(){
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "user-event-enricher-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());


        return config;
    }

    public void process(){
        KStreamBuilder builder = new KStreamBuilder();

        // we get a global table out of Kafka. This table will be replicated on each Kafka Streams application
        // the key of our globalKTable is the user ID
        GlobalKTable<String, String> usersGlobalTable = builder.globalTable("user-table");

        // we get a stream of user purchases
        KStream<String, String> userPurchases = builder.stream("user-purchases");

        // we want to enrich that stream
        KStream<String, String> userPurchasesEnrichedJoin =
                userPurchases.join(usersGlobalTable,
                        (key, value) -> key, /* map from the (key, value) of this stream to the key of the GlobalKTable */
                        (userPurchase, userInfo) -> "Purchase=" + userPurchase + ",UserInfo=[" + userInfo + "]"
                );

        userPurchasesEnrichedJoin.to("user-purchases-enriched-inner-join");

        // we want to enrich that stream using a Left Join
        KStream<String, String> userPurchasesEnrichedLeftJoin =
                userPurchases.leftJoin(usersGlobalTable,
                        (key, value) -> key, /* map from the (key, value) of this stream to the key of the GlobalKTable */
                        (userPurchase, userInfo) -> {
                            // as this is a left join, userInfo can be null
                            if (userInfo != null) {
                                return "Purchase=" + userPurchase + ",UserInfo=[" + userInfo + "]";
                            } else {
                                return "Purchase=" + userPurchase + ",UserInfo=null";
                            }
                        }
                );

        userPurchasesEnrichedLeftJoin.to("user-purchases-enriched-left-join");


        KafkaStreams streams = new KafkaStreams(builder, getProperties());
        streams.cleanUp(); // only do this in dev - not in prod
        streams.start();

        // print the topology
        System.out.println(streams.toString());

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public static void main(String[] args) {
        new UserEventEnricherJoin().process();
    }
}
