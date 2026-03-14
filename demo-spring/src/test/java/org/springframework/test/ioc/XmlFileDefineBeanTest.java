package org.springframework.test.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.test.bean.Car;
import org.springframework.test.bean.Person;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlFileDefineBeanTest {

    @Test
    void test_xml_file() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");

        Person person = (Person) beanFactory.getBean("person");
        System.out.println(person);
        assertEquals("derek", person.getName());
        assertEquals("porsche", person.getCar().getBrand());

        Car car = (Car) beanFactory.getBean("car");
        System.out.println(car);
        assertEquals("porsche", car.getBrand());
    }

}
