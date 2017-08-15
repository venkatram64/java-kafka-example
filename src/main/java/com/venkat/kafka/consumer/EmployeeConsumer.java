package com.venkat.kafka.consumer;

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



    public void consume(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");//kafka-1.devos.saccap.int:30031, localhost:9092
        props.put("group.id", groupName);
        props.put("enable.auto.commit", false);

        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "com.venkat.kafka.consumer.EmployeeDeserializer");


        KafkaConsumer<String, Employee> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topicName));
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
            consumer.close();
        }
    }

    public static void main(String[] args){
        EmployeeConsumer employeeConsumer = new EmployeeConsumer();
        employeeConsumer.consume();
    }
}
