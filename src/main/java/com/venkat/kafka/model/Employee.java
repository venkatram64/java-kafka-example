package com.venkat.kafka.model;

/**
 * Created by venkatram.veerareddy on 8/9/2017.
 */
public class Employee {

    private String empId;
    private String empName;
    private String email;

    public Employee(){}

    public Employee(String empId, String empName, String email) {
        this.empId = empId;
        this.empName = empName;
        this.email = email;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
