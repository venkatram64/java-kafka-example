package com.venkat.kafka.streams;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkat.kafka.streams.eo.BankTrxProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BankTrxProducerTest {

    @Test
    public void newRandomTrxTest(){
        ProducerRecord<String, String> record = BankTrxProducer.newRandomTransaction("venkatram");
        String key = record.key();
        String value = record.value();

        assertEquals(key,"venkatram");
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(value);
            assertEquals(node.get("name").asText(),"venkatram");
            assertTrue("Amount should be less than 100",node.get("amount").asInt() < 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(value);
    }
}
