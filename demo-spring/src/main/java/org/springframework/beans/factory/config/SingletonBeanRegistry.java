package org.springframework.beans.factory.config;

/**
 * 单例注册表
 */
public interface SingletonBeanRegistry {

    /**
     * 获取单例bean
     */
    Object getSingleton(String name);

    /**
     * 注册单例bean
     */
    void addSingleton(String beanName, Object singletonObject);

}
