package com.ricky.reflection;

import com.ricky.reflection.annotation.Autowired;
import com.ricky.reflection.annotation.Bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Container {

    private Object config;
    private Map<Class<?>, Method> map; // 存储有@Bean的方法
    private Map<Class<?>, Object> instanceMap; // 实现单例

    public void init() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        this.map = new HashMap<>();
        this.instanceMap = new HashMap<>();

        Class<?> clazz = Class.forName("com.ricky.reflection.Config");
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getDeclaredAnnotation(Bean.class) != null) {
                this.map.put(method.getReturnType(), method); // 拿到所有生成对象的方法
            }
        }

        this.config = clazz.getDeclaredConstructor().newInstance();
    }

    public Object getServiceInstanceByClass(Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        if (!this.map.containsKey(clazz)) {
            return null;
        }
        if (this.instanceMap.containsKey(clazz)) {
            return this.instanceMap.get(clazz);
        }

        Method method = map.get(clazz);
        Object instance = method.invoke(config);
        this.instanceMap.put(clazz, instance);
        return instance;
    }

    public Object createInstance(Class<?> clazz) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getDeclaredAnnotation(Autowired.class) != null) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Object[] arguments = new Object[parameterTypes.length];
                for (int i = 0; i < arguments.length; ++i) {
                    arguments[i] = getServiceInstanceByClass(parameterTypes[i]);
                }
                return constructor.newInstance(arguments);
            }
        }
        return clazz.getConstructor().newInstance();
    }

}
