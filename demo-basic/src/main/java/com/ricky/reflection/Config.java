package com.ricky.reflection;

import com.ricky.reflection.annotation.Bean;
import com.ricky.reflection.service.Address;
import com.ricky.reflection.service.Customer;

public class Config {

    @Bean
    public Customer customer() {
        return new Customer("Ricky", "Ricky@163.com");
    }

    @Bean
    public Address address() {
        return new Address("center street", "114514");
    }

}
