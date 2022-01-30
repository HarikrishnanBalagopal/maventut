package com.mycompany.app;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

@Entity
@Table(name = "customers")
class Customer {
    @Id
    int id;
    String name;
    int age;
    String address;
    Customer() {}
    Customer(int id, String name, int age, String address){
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }
    int getId() {
        return this.id;
    }
    void setId(int id) {
        this.id = id;
    }
    String getName() {
        return this.name;
    }
    void setName(String name) {
        this.name = name;
    }
    int getAge() {
        return this.age;
    }
    void setAge(int age) {
        this.age = age;
    }
    String getAddress() {
        return this.address;
    }
    void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("Customer{id: %d, name: \"%s\", age: %d, address: \"%s\"}", this.getId(), this.getName(), this.getAge(), this.getAddress());
    }
}
