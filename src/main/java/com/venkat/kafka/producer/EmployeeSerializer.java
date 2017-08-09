package com.venkat.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkat.kafka.model.Employee;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created by venkatram.veerareddy on 8/9/2017.
 */

public class EmployeeSerializer implements Serializer<Employee> {

    private String encoding = "UTF8";

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, Employee employee) {

        try{
            if(employee == null){
                return null;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            byte[] retVal = objectMapper.writeValueAsString(employee).getBytes();

            return retVal;

        }catch (Exception e){
            throw new SerializationException("Error when serializing Employee to byte array");
        }

    }

    @Override
    public void close() {

    }
}
