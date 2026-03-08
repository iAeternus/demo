package org.springframework.test.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.ioc.bean.Car;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FactoryBeanTest {

    @Test
    void test_factory_bean() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:factory-bean.xml");

        Car car = applicationContext.getBean("car", Car.class);
        assertEquals("porsche", car.getBrand());
    }

}
