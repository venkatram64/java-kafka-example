package com.venkat.kafka.embedded;

import kafka.server.KafkaConfig$;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by venkatram.veerareddy on 8/17/2017.
 */

public class BaseEmbeddedKafkaTest  {

    private static final Logger log = LoggerFactory.getLogger(BaseEmbeddedKafkaTest.class);
    private static final int DEFAULT_BROKER_PORT = 0;

    protected static ZooKeeperEmbedded zooKeeperEmbedded;
    protected static KafkaEmbedded broker;

    @BeforeClass
    public static void start() throws Exception{

        log.info("Initiating embedded Kafka cluster startup");
        log.info("Starting a ZooKeeper instance...");

        zooKeeperEmbedded = new ZooKeeperEmbedded();
        log.info("ZooKeeper instance is running at {}", zooKeeperEmbedded.connectString());

        Properties brokerConfig = initBrokerConfig(new Properties(), zooKeeperEmbedded);
        log.info("Starting a Kafka instance on port {} ...", brokerConfig.getProperty(KafkaConfig$.MODULE$.PortProp()));

        broker = new KafkaEmbedded(brokerConfig);
        log.info("Kafka instance is running at {}, connected to ZooKeeper at {}", broker.brokerList(), broker.zookeeperConnect());
    }

    public static Properties initBrokerConfig(Properties props, ZooKeeperEmbedded zooKeeperEmbedded){
        Properties config = new Properties();
        config.putAll(props);

        config.put(KafkaConfig$.MODULE$.ZkConnectProp(), zooKeeperEmbedded.connectString());
        config.put(KafkaConfig$.MODULE$.PortProp(), DEFAULT_BROKER_PORT);
        config.put(KafkaConfig$.MODULE$.DeleteTopicEnableProp(), true);
        config.put(KafkaConfig$.MODULE$.LogCleanerDedupeBufferSizeProp(), 2 * 1024 * 1024L);

        return config;
    }


    @AfterClass
    public static void stop(){
        if(broker != null){
            broker.stop();
        }
        try{
            if(zooKeeperEmbedded != null){
                zooKeeperEmbedded.stop();
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
