package com.venkat.kafka.streams.eo;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.kstream.KTable;

import java.time.Instant;
import java.util.Properties;

public class BankBalanceExactlyOnce {

    /*
        rem create input topic with one partition to get full ordering
        bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic bank-transactions

        rem create output log compacted topic
        bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic bank-balance-exactly-once --config cleanup.policy=compact

        rem launch a Kafka consumer
        bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
            --topic bank-balance-exactly-once ^
            --from-beginning ^
            --formatter kafka.tools.DefaultMessageFormatter ^
            --property print.key=true ^
            --property print.value=true ^
            --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
            --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer

          bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
            --topic bank-transactions ^
            --from-beginning ^
            --formatter kafka.tools.DefaultMessageFormatter ^
            --property print.key=true ^
            --property print.value=true ^
            --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
            --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
     */

    public Properties getProperties(){
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-balance-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // we disable the cache to demonstrate all the "steps" involved in the transformation - not recommended in prod
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        // Exactly once processing!!
        config.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);

        return config;
    }

    private static JsonNode newBalance(JsonNode transaction, JsonNode balance) {
        // create a new balance json object
        ObjectNode newBalance = JsonNodeFactory.instance.objectNode();
        newBalance.put("count", balance.get("count").asInt() + 1);
        newBalance.put("balance", balance.get("balance").asInt() + transaction.get("amount").asInt());

        Long balanceEpoch = Instant.parse(balance.get("time").asText()).toEpochMilli();
        Long transactionEpoch = Instant.parse(transaction.get("time").asText()).toEpochMilli();
        Instant newBalanceInstant = Instant.ofEpochMilli(Math.max(balanceEpoch, transactionEpoch));
        newBalance.put("time", newBalanceInstant.toString());
        return newBalance;
    }

    public void process(){
        // json Serde
        final Serializer<JsonNode> jsonSerializer = new JsonSerializer();
        final Deserializer<JsonNode> jsonDeserializer = new JsonDeserializer();
        final Serde<JsonNode> jsonSerde = Serdes.serdeFrom(jsonSerializer, jsonDeserializer);


        KStreamBuilder builder = new KStreamBuilder();

        KStream<String, JsonNode> bankTransactions =
                builder.stream(Serdes.String(), jsonSerde, "bank-transactions");


        // create the initial json object for balances
        ObjectNode initialBalance = JsonNodeFactory.instance.objectNode();
        initialBalance.put("count", 0);
        initialBalance.put("balance", 0);
        initialBalance.put("time", Instant.ofEpochMilli(0L).toString());

        KTable<String, JsonNode> bankBalance = bankTransactions
                .groupByKey(Serdes.String(), jsonSerde)
                .aggregate(
                        () -> initialBalance,
                        (key, transaction, balance) -> newBalance(transaction, balance),
                        jsonSerde,
                        "bank-balance-agg"
                );

        bankBalance.to(Serdes.String(), jsonSerde,"bank-balance-exactly-once");

        KafkaStreams streams = new KafkaStreams(builder, getProperties());
        streams.cleanUp();
        streams.start();

        // print the topology
        System.out.println(streams.toString());

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public static void main(String[] args) {
        new BankBalanceExactlyOnce().process();
    }
}
