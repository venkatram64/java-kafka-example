package com.venkat.kafka.embedded;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.KafkaConfig;
import kafka.server.KafkaConfig$;
import kafka.server.KafkaServer;
import kafka.utils.TestUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.common.network.ListenerName;
import org.apache.kafka.common.protocol.SecurityProtocol;
import org.apache.kafka.common.utils.Time;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by venkatram.veerareddy on 8/17/2017.
 */


public class KafkaEmbedded {

    private static final Logger log = LoggerFactory.getLogger(KafkaEmbedded.class);

    private static final String DEFAULT_ZK_CONNECT = "127.0.0.1:2181";
    private static final int DEFAULT_ZK_SESSION_TIMEOUT_MS = 10 * 1000;
    private static final int DEFAULT_ZK_CONNECTION_TIMEOUT_MS = 8 * 1000;

    private final Properties properties;
    private final File logDir;
    private final TemporaryFolder temporaryFolder;
    private final KafkaServer kafkaServer;

    public KafkaEmbedded(Properties props) throws IOException{
        this.temporaryFolder = new TemporaryFolder();
        this.temporaryFolder.create();
        this.logDir  = temporaryFolder.newFolder();
        this.properties = initConfig(props);
        boolean loggingEnabled = true;

        KafkaConfig kafkaConfig = new KafkaConfig(this.properties,loggingEnabled);
        log.info("Starting embedded Kafka broker (with log.dirs={} and ZK ensemble at {}) ...", logDir, zookeeperConnect());

        this.kafkaServer = TestUtils.createServer(kafkaConfig, Time.SYSTEM);
        log.info("Startup of embedded Kafka broker at {} completed (with ZK ensemble at {})...",brokerList(), zookeeperConnect());
    }

    private Properties initConfig(Properties properties) throws IOException{
        Properties props = new Properties();
        props.put(KafkaConfig$.MODULE$.BrokerIdProp(), 0);
        props.put(KafkaConfig$.MODULE$.HostNameProp(), "127.0.0.1");
        props.put(KafkaConfig$.MODULE$.PortProp(), "9092");
        props.put(KafkaConfig$.MODULE$.NumPartitionsProp(), 1);
        props.put(KafkaConfig$.MODULE$.ControlledShutdownEnableProp(), true);
        props.put(KafkaConfig$.MODULE$.MessageMaxBytesProp(), 1000000);
        props.put(KafkaConfig$.MODULE$.AutoCreateTopicsEnableProp(),true);

        props.putAll(properties);
        props.setProperty(KafkaConfig$.MODULE$.LogDirProp(), logDir.getAbsolutePath());

        return props;
    }

    public String zookeeperConnect(){
        return properties.getProperty("zookeeper.connect", DEFAULT_ZK_CONNECT);
    }

    public String brokerList(){
        return String.join(":", this.kafkaServer.config().hostName(),
                Integer.toString(this.kafkaServer.boundPort(ListenerName.forSecurityProtocol(SecurityProtocol.PLAINTEXT))));
    }

    public void stop(){
        log.info("Shutting down embedded Kafka broker at {} (with ZK ensemble at {})...",brokerList(), zookeeperConnect());
        this.kafkaServer.shutdown();
        this.kafkaServer.awaitShutdown();
        log.info("Removing temp folder {} with logs.dir at {} ...",this.temporaryFolder, this.logDir);
        this.temporaryFolder.delete();
        log.info("Shutting down of embedded Kafka broker at {} completed (with ZK ensemble at {})...",brokerList(), zookeeperConnect());
    }

    public void createTopic(String topicName){
        createTopic(topicName,1,1, new Properties());
    }

    public void createTopic(String topic, int partitions, int replication){
        createTopic(topic,partitions,replication, new Properties());
    }

    public void createTopic(String topic, int partitions, int replication, Properties properties){
        log.info("Creating topic {name: {}, partitions: {}, replication: {}, config: {}}", topic, partitions, replication);
        ZkClient zkClient = new ZkClient(zookeeperConnect(), DEFAULT_ZK_SESSION_TIMEOUT_MS, DEFAULT_ZK_CONNECTION_TIMEOUT_MS, ZKStringSerializer$.MODULE$);
        boolean isSecure = false;
        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperConnect()), isSecure);
        AdminUtils.createTopic(zkUtils, topic, partitions, replication, properties, RackAwareMode.Enforced$.MODULE$);
        zkClient.close();
    }


}
