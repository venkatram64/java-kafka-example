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

    private String groupName ="MyTest";
    private KafkaDestinationInfo kafkaDestinationInfo;
    private  KafkaConsumer<String, Employee> consumer;

    public EmployeeConsumer(){}

    public EmployeeConsumer(KafkaDestinationInfo kafkaDestinationInfo){

        this.kafkaDestinationInfo = kafkaDestinationInfo;
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.kafkaDestinationInfo.getBootstrapServiceConfig());//xxxx.int:30031, localhost:9092
        props.put("group.id", groupName);
        props.put("enable.auto.commit", false);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "com.venkat.kafka.consumer.EmployeeDeserializer");

        this.consumer = new KafkaConsumer<>(props);
    }
    public void consume(){

        consumer.subscribe(Arrays.asList(this.kafkaDestinationInfo.getTopicName()));
        try{
            while(true){
                ConsumerRecords<String, Employee> records = consumer.poll(1000);
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
