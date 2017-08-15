package com.venkat.kafka.service;

import com.venkat.kafka.config.KafkaDestinationInfo;
import com.venkat.kafka.consumer.EmployeeConsumer;
import com.venkat.kafka.producer.EmployeeProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by venkatram.veerareddy on 8/9/2017.
 */


@Service
public class EmployeeService {

    @Autowired
    private KafkaDestinationInfo kafkaDestinationInfo;

    @PostConstruct
    public void process(){

        EmployeeProducer employeeProducer = new EmployeeProducer(kafkaDestinationInfo);
        employeeProducer.produce();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EmployeeConsumer employeeConsumer = new EmployeeConsumer(kafkaDestinationInfo);
        employeeConsumer.consume();

    }
}

