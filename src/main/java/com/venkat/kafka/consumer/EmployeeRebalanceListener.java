package com.venkat.kafka.consumer;

import com.venkat.kafka.model.Employee;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EmployeeRebalanceListener implements ConsumerRebalanceListener {

    private KafkaConsumer<String, Employee> consumer;
    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();


    public EmployeeRebalanceListener(){}

    public EmployeeRebalanceListener(KafkaConsumer<String, Employee> consumer){
        this.consumer = consumer;
    }

    public void addOffset(String topic, int partition, long offset){
        currentOffsets.put(new TopicPartition(topic, partition),new OffsetAndMetadata(offset,"Commit"));
    }

    public Map<TopicPartition, OffsetAndMetadata> getCurrentOffsets(){
        return currentOffsets;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> collection) {
        System.out.println("Revoked Partitions are ....");
        for(TopicPartition partition: collection) {
            System.out.println(partition.partition() + ",");
        }


        System.out.println("Commited Partitions are ...." );
        for(TopicPartition tp: currentOffsets.keySet()) {
            System.out.println(tp.partition());
        }

        consumer.commitSync(currentOffsets);
        currentOffsets.clear();
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> collection) {
        System.out.println("Assigned Partitioned are ....");
        for(TopicPartition partition: collection) {
            System.out.println(partition.partition() + ",");
        }
    }
}
