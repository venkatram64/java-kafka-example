package com.venkat.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by venkatram.veerareddy on 8/15/2017.
 */

@Configuration
@ConfigurationProperties(prefix = "KafkaLocalDestinationSettings")
public class KafkaDestinationInfo {

    private String bootstrapServiceConfig;
    private String zookeeperUrl;
    private String topicName;

    public KafkaDestinationInfo(){}

    public KafkaDestinationInfo(String bootstrapServiceConfig, String zookeeperUrl, String topicName) {
        this.bootstrapServiceConfig = bootstrapServiceConfig;
        this.zookeeperUrl = zookeeperUrl;
        this.topicName = topicName;
    }

    public String getBootstrapServiceConfig() {
        return bootstrapServiceConfig;
    }

    public void setBootstrapServiceConfig(String bootstrapServiceConfig) {
        this.bootstrapServiceConfig = bootstrapServiceConfig;
    }

    public String getZookeeperUrl() {
        return zookeeperUrl;
    }

    public void setZookeeperUrl(String zookeeperUrl) {
        this.zookeeperUrl = zookeeperUrl;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
