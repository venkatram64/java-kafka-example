package com.venkat.kafka.consumer;

import com.venkat.kafka.config.KafkaDestinationInfo;
import com.venkat.kafka.model.Employee;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by venkatram.veerareddy on 8/9/2017.
 */


public class EmployeeConsumer {

    private String topicName = "testForReblance";
    private String groupName ="testForReblanceGP";

    KafkaConsumer<String, Employee> consumer;

    public EmployeeConsumer(){

        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.99.100:9092");//xxx.int:30031, localhost:9092
        props.put("group.id", groupName);
        props.put("enable.auto.commit", false);
        //props.put("auto.commit.interval.ms", 100);

        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "com.venkat.kafka.consumer.EmployeeDeserializer");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(props);
    }

    public void rebalance(){

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");//xxx.int:30031, localhost:9092
        props.put("group.id", groupName);
        props.put("enable.auto.commit", false);
        //props.put("auto.commit.interval.ms", 100);

        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "com.venkat.kafka.consumer.EmployeeDeserializer");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        this.consumer = new KafkaConsumer<>(props);
    }

    public EmployeeConsumer(KafkaDestinationInfo kafkaDestinationInfo){

        this.topicName = kafkaDestinationInfo.getTopicName();
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaDestinationInfo.getBootstrapServiceConfig());//xxx.int:30031, localhost:9092
        props.put("group.id", groupName);
        props.put("enable.auto.commit", false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //props.put("auto.commit.interval.ms", 100);

        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "com.venkat.kafka.consumer.EmployeeDeserializer");

        this.consumer = new KafkaConsumer<>(props);
    }

    public void consume(){

        this.consumer.subscribe(Arrays.asList(topicName));
        //consumer.seekToBeginning();
        try{
            while(true){
                ConsumerRecords<String, Employee> records = consumer.poll(100);
                for(ConsumerRecord<String, Employee> rec: records){
                    System.out.println(" employee info " + rec.value().getEmpId() +", " + rec.value().getEmpName() + ", " + rec.value().getEmail());
                    System.out.println(" value, key, offset, partition and topic " + rec.value() +", " + rec.key() + ", " + rec.offset() + ", " + rec.partition() + ", " + rec.topic() );
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            this.consumer.close();
        }
    }

    public void rebalancedConsume(){

        EmployeeRebalanceListener rebalanceListener = new EmployeeRebalanceListener(this.consumer);

        this.consumer.subscribe(Arrays.asList(topicName),rebalanceListener);
        //consumer.seekToBeginning();
        try{
            while(true){
                ConsumerRecords<String, Employee> records = consumer.poll(100);
                for(ConsumerRecord<String, Employee> rec: records){
                    System.out.println(" employee info " + rec.value().getEmpId() +", " + rec.value().getEmpName() + ", " + rec.value().getEmail());
                    System.out.println(" value, key, offset, partition and topic " + rec.value() +", " + rec.key() + ", " + rec.offset() + ", " + rec.partition() + ", " + rec.topic() );
                    rebalanceListener.addOffset(rec.topic(),rec.partition(),rec.offset());
                }
                this.consumer.commitSync(rebalanceListener.getCurrentOffsets());
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            this.consumer.close();
        }
    }

    public static void main(String[] args){
        EmployeeConsumer employeeConsumer = new EmployeeConsumer();
        //employeeConsumer.consume();
        employeeConsumer.rebalance();
        employeeConsumer.rebalancedConsume();
        /*
        step 1: create a topic
        bin/windows/kafka-topics.bat --zookeeper localhost:2181 --create --topic testForReblance --partitions 2 --replication-factor 1
        step 2:
        start a producer
        step 3:
        start two consumers and stop one of them, it will be rebalanced
         */
    }
}
