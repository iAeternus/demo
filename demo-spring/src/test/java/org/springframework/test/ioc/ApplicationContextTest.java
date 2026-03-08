package org.springframework.test.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.ioc.bean.Car;
import org.springframework.test.ioc.bean.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationContextTest {

    @Test
    void test_application_context() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");

        Person person = applicationContext.getBean("person", Person.class);
        System.out.println(person);
        // name属性在CustomBeanFactoryPostProcessor中被修改为ivy
        assertEquals("ivy", person.getName());

        Car car = applicationContext.getBean("car", Car.class);
        System.out.println(car);
        // brand属性在CustomerBeanPostProcessor中被修改为lamborghini
        assertEquals("lamborghini", car.getBrand());
    }

}
