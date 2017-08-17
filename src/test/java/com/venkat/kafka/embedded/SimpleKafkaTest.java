package com.venkat.kafka.embedded;

import com.venkat.kafka.config.KafkaDestinationInfo;
import com.venkat.kafka.consumer.EmployeeConsumer;
import com.venkat.kafka.model.Employee;
import com.venkat.kafka.producer.EmployeeProducer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by venkatram.veerareddy on 8/17/2017.
 */
public class SimpleKafkaTest extends BaseEmbeddedKafkaTest {

    private KafkaDestinationInfo kafkaDestinationInfo = null;

    @Before
    public void beforeTest() throws Exception{
        super.before();
        kafkaDestinationInfo = new KafkaDestinationInfo(broker.brokerList(), zooKeeperEmbedded.connectString() , "myTopic");
        publishDataOnTopic();
    }

    private void publishDataOnTopic(){
        EmployeeProducer ep = new EmployeeProducer(kafkaDestinationInfo);
        ep.produce();
    }

    @After
    public void afterTest(){
        super.after();
    }

    @Test
    public void testConsumer(){
        EmployeeConsumer ec = new EmployeeConsumer(kafkaDestinationInfo);
        ec.consume();
    }

}
