package com.ricky.reflection.service;

public class Address {

    public String street;
    public String postCode;

    public Address() {
    }

    public Address(String street, String postCode) {
        this.street = street;
        this.postCode = postCode;
    }

    public void printStreet() {
        System.out.println(this.street);
    }

    public void printPostCode() {
        System.out.println(this.postCode);
    }

}
