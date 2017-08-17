package com.venkat.kafka.producer;

import com.venkat.kafka.config.KafkaDestinationInfo;
import com.venkat.kafka.model.Employee;
import org.apache.kafka.clients.producer.*;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by venkatram.veerareddy on 8/9/2017.
 */

public class EmployeeProducer {

    private String topicName = "topic-1";
    Producer<String, Employee> producer;

    public EmployeeProducer(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");//xxx:30031, localhost:9092
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "com.venkat.kafka.producer.EmployeeSerializer");
        props.put("acks", "all"); //for acknowledgement 0, 1, all
        //retries
        //max.in.flight.requests.per.connection 1
        producer = new KafkaProducer<>(props);
    }

    public EmployeeProducer(KafkaDestinationInfo kafkaDestinationInfo){

        this.topicName = kafkaDestinationInfo.getTopicName();
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaDestinationInfo.getBootstrapServiceConfig());//xxx:30031, localhost:9092
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "com.venkat.kafka.producer.EmployeeSerializer");
        props.put("acks", "all"); //for acknowledgement 0, 1, all
        //retries
        //max.in.flight.requests.per.connection 1
        this.producer = new KafkaProducer<>(props);
    }

    public void produce(){

        Employee emp = new Employee("emp-02","Srijan","srijan.veerareddy@gmail.com");
        try{
            // producer.send(new ProducerRecord<String, Employee>(topicName, emp.getEmpId().toString(),emp)).get();
            this.producer.send(new ProducerRecord<String, Employee>(topicName, emp.getEmpId().toString(), emp),
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
            this.producer.flush();
            //this.producer.close();
        }
    }



    public static void main(String[] args) throws IOException {
        EmployeeProducer employeeProducer = new EmployeeProducer();
        employeeProducer.produce();
    }


}
