package com.ricky.reflection;

import com.ricky.reflection.annotation.Printable;
import com.ricky.reflection.service.Customer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Container container = new Container();
        container.init();

        String className = "com.ricky.reflection.service.Order";
        String fieldName = "customer";

        Class<?> clazz = Class.forName(className);
        Object instance = container.createInstance(clazz);
        Field filed = clazz.getDeclaredField(fieldName);
        filed.setAccessible(true);
        Object fieldValue = filed.get(instance);
        System.out.println(fieldValue);

        Method[] methods = fieldValue.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if(method.getAnnotation(Printable.class) != null) {
                System.out.println(method.getName());
                method.invoke(fieldValue);
            }
        }

        System.out.println(fieldValue == container.getServiceInstanceByClass(Customer.class)); // true
    }

}
