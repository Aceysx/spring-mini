package com.acey.spring.framework.bean;

import com.acey.spring.utls.BeanUtil;

public class BeanDefinition {
    private String beanClassName;
    private String factoryBeanName;
    private Boolean lazyInit;

    public BeanDefinition(String className) {
        this.beanClassName = className;
        this.factoryBeanName = BeanUtil.lowerFirstCase(className.substring(className.lastIndexOf(".") + 1));
        this.lazyInit = false;
    }


    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public Boolean getLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(Boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
}
