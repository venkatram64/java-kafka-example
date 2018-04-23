package com.venkat.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Arrays;
import java.util.Properties;

public class FavouriteColor {

    /*
    what is exactly once?
    Exactly once is the ability to guarantee that data processing on each message will happen only once,
    and that pushing the message back to Kafka will also happen effectively only once(Kafka will de-dup)
     */

    private void init(){

        /*
        rem create input topic with one partition to get full ordering
        bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic favourite-colour-input

        rem create intermediary log compacted topic
        bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic user-keys-and-colours --config cleanup.policy=compact

        rem create output log compacted topic
        bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic favourite-colour-output --config cleanup.policy=compact


        rem launch a Kafka consumer
        bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
            --topic favourite-colour-output ^
            --from-beginning ^
            --formatter kafka.tools.DefaultMessageFormatter ^
            --property print.key=true ^
            --property print.value=true ^
            --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
            --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

        rem launch the streams application

        rem then produce data to it
        bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic favourite-colour-input
        rem
        venkat,blue
        ram,green
        venkat,red
        ani,red


        rem list all topics that we have in Kafka (so we can observe the internal topics)
        bin\windows\kafka-topics.bat --list --zookeeper localhost:2181
         */
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "myGroup");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        // we disable the cache to demonstrate all the "steps" involved in the transformation - not recommended in prod
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");


        KStreamBuilder builder = new KStreamBuilder();

        // Step 1: We create the topic of users keys to colours
        KStream<String, String> textLines = builder.stream("favourite-colour-input"); //from topic

        KStream<String, String> usersAndColours = textLines
                // 1 - we ensure that a comma is here as we will split on it
                .filter((key, value) -> value.contains(","))
                // 2 - we select a key that will be the user id (lowercase for safety)
                .selectKey((key, value) -> value.split(",")[0].toLowerCase())
                // 3 - we get the colour from the value (lowercase for safety)
                .mapValues(value -> value.split(",")[1].toLowerCase())
                // 4 - we filter undesired colours (could be a data sanitization step
                .filter((user, colour) -> Arrays.asList("green", "blue", "red").contains(colour));

        usersAndColours.to("user-keys-and-colours");//to the topic

        // step 2 - we read that topic as a KTable so that updates are read correctly
        KTable<String, String> usersAndColoursTable = builder.table("user-keys-and-colours");

        // step 3 - we count the occurences of colours
        KTable<String, Long> favouriteColours = usersAndColoursTable
                // 5 - we group by colour within the KTable
                .groupBy((user, colour) -> new KeyValue<>(colour, colour))
                .count("CountsByColours");

        // 6 - we output the results to a Kafka Topic - don't forget the serializers
        favouriteColours.to(Serdes.String(), Serdes.Long(),"favourite-colour-output");

        KafkaStreams streams = new KafkaStreams(builder, config);
        // only do this in dev - not in prod
        streams.cleanUp();
        streams.start();

        // print the topology
        System.out.println(streams.toString());

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }


    public static void main(String[] args) {
        new FavouriteColor().init();
    }
}
