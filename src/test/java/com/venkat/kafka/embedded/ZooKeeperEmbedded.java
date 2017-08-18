package com.venkat.kafka.embedded;

import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.curator.test.TestingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by venkatram.veerareddy on 8/17/2017.
 */
public class ZooKeeperEmbedded {

    private static final Logger log = LoggerFactory.getLogger(ZooKeeperEmbedded.class);
    private final TestingServer server;

    public ZooKeeperEmbedded() throws Exception{
        log.info("Starting embedded Zookeeper server...");
        this.server = new TestingServer();
        log.info("Embedded Zookeper server at {} uses the temp directory at {}", server.getConnectString(), server.getTempDirectory());
    }

    public void stop() throws Exception{
        log.info("Shutting down embedded Zookeeper server at {} ...", server.getConnectString());
        server.close();
        log.info("Shutting down of embedded Zookeeper server at {} completed", server.getConnectString());
    }

    public String connectString(){
        return server.getConnectString();
    }

    public String hostname(){
        //server:1:2:3 -> server:1:2
        return connectString().substring(0,connectString().lastIndexOf(':'));
    }



}
