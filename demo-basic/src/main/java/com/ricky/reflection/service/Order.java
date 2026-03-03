package com.ricky.reflection.service;

import com.ricky.reflection.annotation.Autowired;

public class Order {

    private Customer customer;
    private Address address;

    public Order() {
    }

    @Autowired
    public Order(Customer customer, Address address) {
        this.customer = customer;
        this.address = address;
    }

}
