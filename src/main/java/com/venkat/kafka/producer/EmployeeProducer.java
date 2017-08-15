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

    private Producer<String, Employee> producer;
    private KafkaDestinationInfo kafkaDestinationInfo;

    public EmployeeProducer(){}

    public EmployeeProducer(KafkaDestinationInfo kafkaDestinationInfo){
        this.kafkaDestinationInfo = kafkaDestinationInfo;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaDestinationInfo.getBootstrapServiceConfig());//xxxx.int:30031, localhost:9092
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "com.venkat.kafka.producer.EmployeeSerializer");
        props.put("acks", "all");
        this.producer = new KafkaProducer<>(props);
    }

    public void produce(){

        Employee emp = new Employee("emp-01","Venkatram","venkatram.reddy@gmail.com");
        try{

            producer.send(new ProducerRecord<String, Employee>(this.kafkaDestinationInfo.getTopicName(), emp.getEmpId().toString(), emp),
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
