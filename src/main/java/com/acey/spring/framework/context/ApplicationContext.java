package com.acey.spring.framework.context;

import com.acey.spring.framework.annotation.Autowired;
import com.acey.spring.framework.annotation.Controller;
import com.acey.spring.framework.annotation.Service;
import com.acey.spring.framework.bean.BeanDefinition;
import com.acey.spring.framework.bean.BeanPostProcessor;
import com.acey.spring.framework.bean.BeanWrapper;
import com.acey.spring.framework.context.support.BeanDefinitionReader;
import com.acey.spring.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext implements BeanFactory {
    private String[] configLocations;
    private BeanDefinitionReader beanDefinitionReader;
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private Map<String, Object> beanCacheMap = new ConcurrentHashMap<>();
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();


    public ApplicationContext(String... configLocations) throws Exception {
        this.configLocations = configLocations;
        refresh();
    }

    public void refresh() throws Exception {
//        定位
        beanDefinitionReader = new BeanDefinitionReader(configLocations);
//        加载
        List<String> beanDefinitions = beanDefinitionReader.loadBeanDefinitions();
//        注册
        doRegistry(beanDefinitions);
//        依赖注入（lazy-init=false）
        doAutowired();
    }

    private void doAutowired() throws Exception {
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            if (!beanDefinitionEntry.getValue().getLazyInit()) {
                getBean(beanDefinitionEntry.getKey());
            }
        }
    }

    private void doRegistry(List<String> beanDefinitions) throws ClassNotFoundException {
        for (String className : beanDefinitions) {
            Class<?> klass = Class.forName(className);
            // 1. 类名首字母小写
            if (klass.isInterface()) {
                continue;
            }
            BeanDefinition beanDefinition = new BeanDefinition(className);
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
            // 2. 自定义(先不考虑）
            // 3. 接口注入
            for (Class<?> i : klass.getInterfaces()) {
                this.beanDefinitionMap.put(i.getName(), beanDefinition);
            }
        }
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        if (null == beanDefinition) {
            return null;
        }
        BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

        Object instance = doCreateBean(beanDefinition);
        beanPostProcessor.postProcessorBeforeInitialization(instance, beanName);
        BeanWrapper beanWrapper = new BeanWrapper(instance);
        this.beanWrapperMap.put(beanDefinition.getFactoryBeanName(), beanWrapper);
        beanPostProcessor.postProcessorAfterInitialization(instance, beanName);
        populateBean(instance);
        return this.beanWrapperMap.get(beanName).getWrapperInstance();
    }

    private void populateBean(Object instance) throws IllegalAccessException {
        Class<?> instanceClass = instance.getClass();
        if (!(instanceClass.isAnnotationPresent(Controller.class)
            || instanceClass.isAnnotationPresent(Service.class))) {
            Field[] fields = instanceClass.getFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredName = autowired.value().trim();
                    if ("".equals(autowiredName)) {
                        autowiredName = field.getType().getName();
                    }
                    field.setAccessible(true);
                    field.set(instance, this.beanWrapperMap.get(autowiredName).getWrapperInstance());
                }
            }
        }
    }

    private Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
        String beanName = beanDefinition.getFactoryBeanName();
        if (this.beanCacheMap.containsKey(beanName)) {
            return this.beanDefinitionMap.get(beanName);
        }
        Class<?> klass = Class.forName(beanDefinition.getBeanClassName());
        Object instance = klass.newInstance();
        this.beanCacheMap.put(beanName, instance);
        return instance;
    }
}
