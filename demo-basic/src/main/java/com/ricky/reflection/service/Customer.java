package com.ricky.reflection.service;

import com.ricky.reflection.annotation.Printable;

public class Customer {

    private String name;
    private String email;

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Customer() {
    }

    @Printable
    public void printEmail() {
        System.out.println(email);
    }

    public void printName() {
        System.out.println(name);
    }

}
