package com.venkat.kafka;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.TestUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import kafka.zk.EmbeddedZookeeper;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.common.utils.Time;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by venkatram.veerareddy on 8/16/2017.
 */

public class KafkaBase {

    public static final String MY_TOPIC = "my-topic";
    protected static String ZOO_KEEPER_HOST = "127.0.0.1";
    protected static String BROKER_HOST = ZOO_KEEPER_HOST;
    protected static String BROKER_PORT = "9900";
    protected static String TOPIC = MY_TOPIC;

    protected static EmbeddedZookeeper zkServer;
    protected static ZkClient zkClient;
    protected static KafkaServer kafkaServer;
    protected static ZkUtils zkUtils;

    private static Path tempDirectory;

    @BeforeClass
    public static void start() throws Exception{
        //setup Zookeeper
        zkServer = new EmbeddedZookeeper();
        String zkConnectStr = ZOO_KEEPER_HOST + ":" + zkServer.port();
        zkClient = new ZkClient(zkConnectStr, 3000,3000, ZKStringSerializer$.MODULE$);
        zkUtils = ZkUtils.apply(zkClient, false);
        tempDirectory = Files.createTempDirectory("kafka_");
        //setup broker
        Properties properties = new Properties();
        properties.setProperty("zookeeper.connect", zkConnectStr);
        properties.setProperty("broker.id", "0");
        properties.setProperty("log.dirs", tempDirectory.toAbsolutePath().toString());
        properties.setProperty("listener","PLAINTEXT://"+BROKER_HOST + ":" + BROKER_PORT);
        properties.setProperty("delete.topic.enable","true");

        KafkaConfig config = new KafkaConfig(properties);
        kafkaServer = TestUtils.createServer(config, Time.SYSTEM);
    }

    @AfterClass
    public static void shutDown(){
        kafkaServer.shutdown();
        zkClient.close();
        zkServer.shutdown();
        tempDirectory.toFile().deleteOnExit();
    }

}
