package com.venkat.kafka;

import com.venkat.kafka.config.KafkaDestinationInfo;
import com.venkat.kafka.consumer.EmployeeConsumer;
import com.venkat.kafka.model.Employee;
import com.venkat.kafka.producer.EmployeeProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by venkatram.veerareddy on 8/16/2017.
 */

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { KafkaDestinationInfo.class })
@EnableConfigurationProperties
public class ProducerTest extends KafkaBase {

    private KafkaDestinationInfo kafkaDestinationInfo = new KafkaDestinationInfo(BROKER_HOST + ":" + BROKER_PORT, ZOO_KEEPER_HOST + ":" + zkServer.port() , "myTopic");

    @Test
    public void producerTest(){
        Employee emp = new Employee("1234","Venkatram","venkat@venkat.com");
        EmployeeProducer ep = new EmployeeProducer(kafkaDestinationInfo);
        ep.produce();
    }

    @Test
    public void consumerTest(){
        EmployeeConsumer ec = new EmployeeConsumer(kafkaDestinationInfo);
        ec.consume();
    }


}
