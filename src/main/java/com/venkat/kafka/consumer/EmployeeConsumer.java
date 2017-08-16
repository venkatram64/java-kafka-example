package com.venkat.kafka.consumer;

import com.venkat.kafka.config.KafkaDestinationInfo;
import com.venkat.kafka.model.Employee;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by venkatram.veerareddy on 8/9/2017.
 */


public class EmployeeConsumer {

    private String topicName = "topic-1";
    private String groupName ="MyTest";

    KafkaConsumer<String, Employee> consumer;

    public EmployeeConsumer(){

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");//xxx.int:30031, localhost:9092
        props.put("group.id", groupName);
        props.put("enable.auto.commit", false);
        //props.put("auto.commit.interval.ms", 100);

        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "com.venkat.kafka.consumer.EmployeeDeserializer");

        consumer = new KafkaConsumer<>(props);
    }

    public EmployeeConsumer(KafkaDestinationInfo kafkaDestinationInfo){

        this.topicName = kafkaDestinationInfo.getTopicName();
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaDestinationInfo.getBootstrapServiceConfig());//xxx.int:30031, localhost:9092
        props.put("group.id", groupName);
        props.put("enable.auto.commit", false);
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
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            this.consumer.close();
        }
    }

    public static void main(String[] args){
        EmployeeConsumer employeeConsumer = new EmployeeConsumer();
        employeeConsumer.consume();
    }
}
