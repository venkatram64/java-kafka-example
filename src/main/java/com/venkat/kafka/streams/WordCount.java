package com.venkat.kafka.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Arrays;
import java.util.Properties;

public class WordCount {

    //goto kafka/bin/windows
    //kafka-topics --zookeeper localhost:2181 --create --topic word-count-input --partitions 2 --replication-factor 1
    //kafka-topics --zookeeper localhost:2181 --create --topic word-count-output --partitions 2 --replication-factor 1
    /*
        kafka-console-consumer.bat --bootstrap-server localhost:9092 ^
        --topic word-count-output ^
        --from-beginning ^
        --formatter kafka.tools.DefaultMessageFormatter ^
        --property print.key=true ^
        --property print.value=true ^
        --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer ^
        --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

        to run on windows console

        kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic word-count-output --from-beginning --formatter kafka.tools.DefaultMessageFormatter --property print.key=true --property print.value=true --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

        goto producer \bin\windows run the following command and give some input
        kafka-console-producer --broker-list localhost:9092 --topic word-count-input

        kafka-topics --zookeeper localhost:2181 --list  // two extra topic will be created
     */
    private void init(){
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG,"word-count");//similar to groupid
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> wordCountInput = builder.stream("word-count-input");//topic name

        KTable<String, Long> wordCount = wordCountInput.mapValues(line -> line.toLowerCase())
                .flatMapValues(line -> Arrays.asList(line.split(" ")))
                .selectKey((k, v) -> v)
                .groupByKey()
                .count("Count");

        wordCount.to(Serdes.String(),Serdes.Long(),"word-count-output");//topic name

        KafkaStreams streams = new KafkaStreams(builder,config);
        streams.start();

        System.out.println(streams.toString());

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public static void main(String[] args) {
        new WordCount().init();
    }
}
