package com.venkat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venkat.kafka.model.Employee;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created by venkatram.veerareddy on 8/9/2017.
 */

public class EmployeeDeserializer implements Deserializer<Employee> {

    private String encoding = "UTF8";

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public Employee deserialize(String s, byte[] bytes) {
        try {
            if (bytes == null) {
                System.out.println("Null recieved at deserialize ");
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            Employee emp = mapper.readValue(bytes, Employee.class);

            return emp;
        }catch (Exception ex){
            throw new SerializationException("Error when deserializing byte[] to Employee");
        }
    }

    @Override
    public void close() {

    }
}

