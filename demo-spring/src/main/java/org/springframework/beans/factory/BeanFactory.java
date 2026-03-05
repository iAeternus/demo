package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

public interface BeanFactory {

    /**
     * 获取bean
     *
     * @throws BeansException bean不存在时
     */
    Object getBean(String name) throws BeansException;

}
