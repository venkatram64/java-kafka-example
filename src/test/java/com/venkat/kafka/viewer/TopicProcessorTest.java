package com.venkat.kafka.viewer;

import com.venkat.kafka.embedded.BaseEmbeddedKafkaTest;
import kafka.admin.TopicCommand;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.record.Record;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Properties;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by venkatram.veerareddy on 8/18/2017.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TopicProcessorTest extends BaseEmbeddedKafkaTest {

    private Properties getProps(){

        Properties properties = new Properties();
        properties.put("delete.retention.ms", "31536000000");
        properties.put("cleanup.policy", "compact");
        properties.put("min.compaction.lag.ms", "0");
        properties.put("segment.ms", "604800000");
        return properties;
    }

    @Before
    public void createTopicTest(){

        broker.createTopic("test1");
        broker.createTopic("test2");
        broker.createTopic("test3");
        broker.createTopic("test4");
        broker.createTopic("test5");
    }

    @Test
    public void testAviewTopic(){

        Predicate<String> patternPredicate = Pattern.compile("test").asPredicate();
        KafkaConsumer<Record, Record> kc =  broker.getConfig(broker.brokerList());
        ZkUtils zkUtils = broker.getZkUtils();
        kc.listTopics().keySet().stream().sorted().filter(patternPredicate).forEach(topicName->{
            //System.out.println(topicName);
            TopicCommand.TopicCommandOptions opts = new TopicCommand.TopicCommandOptions(new String[]{"--describe","--topic", topicName});
            TopicCommand.describeTopic(zkUtils,opts);
        });
        //kc.metrics();
        //zkUtils.close();
    }

    @Test
    public void testBtopicCount(){

        Predicate<String> patternPredicate = Pattern.compile("test").asPredicate();
        KafkaConsumer<Record, Record> kc =  broker.getConfig(broker.brokerList());
        System.out.printf("%-20s,%s\n", "topicName", "count");
        kc.listTopics().keySet().stream().sorted().filter(patternPredicate).forEach(topicName ->{
            long count = broker.getCountFor(topicName, broker.brokerList());
            System.out.printf("%-20s, %s\n", topicName, count);
        });
    }
}
