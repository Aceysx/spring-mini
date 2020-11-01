package com.acey.spring.framework.bean;

public class BeanWrapper {
    //通过代理
    private Object wrapperInstance;
    //通过反射创建
    private Object originalInstance;
    private BeanPostProcessor beanPostProcessor;

    public BeanWrapper(Object instance) {
        this.originalInstance = instance;
        this.wrapperInstance = instance;
        this.beanPostProcessor = new BeanPostProcessor();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }
}
