package com.venkat.kafka.producer;

import com.venkat.kafka.model.Employee;
import org.apache.kafka.clients.producer.*;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by venkatram.veerareddy on 8/9/2017.
 */

public class EmployeeProducer {

    private String topicName = "topic-1";

    public void produce(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");//xxx:30031, localhost:9092
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "com.venkat.kafka.producer.EmployeeSerializer");
        props.put("acks", "all");

        Producer<String, Employee> producer = new KafkaProducer<>(props);


        Employee emp = new Employee("emp-01","Venkatram","venkatram.reddy@gmail.com");
        try{

            // producer.send(new ProducerRecord<String, Employee>(topicName, emp.getEmpId().toString(),emp)).get();
            producer.send(new ProducerRecord<String, Employee>(topicName, emp.getEmpId().toString(), emp),
                    new Callback() {
                        @Override
                        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                            if(e != null){
                                e.printStackTrace();
                            }
                            System.out.println("Sending completed...");
                        }
                    });
            Thread.sleep(100);

        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            producer.flush();
            producer.close();
        }
    }



    public static void main(String[] args) throws IOException {
        EmployeeProducer employeeProducer = new EmployeeProducer();
        employeeProducer.produce();
    }


}
