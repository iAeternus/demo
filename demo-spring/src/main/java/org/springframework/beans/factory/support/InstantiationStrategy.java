package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;

/**
 * bean实例化策略
 */
public interface InstantiationStrategy {

    /**
     * bean实例化
     */
    Object instantiate(BeanDefinition beanDefinition) throws BeansException;

}
