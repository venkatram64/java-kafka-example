package com.venkat.kafka.service;

import com.venkat.kafka.consumer.EmployeeConsumer;
import com.venkat.kafka.producer.EmployeeProducer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by venkatram.veerareddy on 8/9/2017.
 */


@Service
public class EmployeeService {

    @PostConstruct
    public void process(){

        EmployeeProducer employeeProducer = new EmployeeProducer();
        employeeProducer.produce();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        EmployeeConsumer employeeConsumer = new EmployeeConsumer();
        employeeConsumer.consume();

    }
}

