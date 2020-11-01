package com.acey.spring.framework.context.support;

import com.acey.spring.framework.bean.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对配置文件进行定位，解析
 */
public class BeanDefinitionReader {
    private Properties config = new Properties();
    private List<String> registryBeanClasses = new ArrayList<>();
    private static String SCANNER_PACKAGE = "scannerPackage";

    public BeanDefinitionReader(String... configLocations) throws IOException {
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream(configLocations[0].replace("classpath:", ""));
        config.load(resource);
        doScan(SCANNER_PACKAGE);
    }

    public List<String> loadBeanDefinitions() {
        return this.registryBeanClasses;
    }

    public BeanDefinition registerBean(String className) {
        if (this.registryBeanClasses.contains(className)) {
            return new BeanDefinition(className);
        }
        return null;
    }

    public Properties getConfig() {
        return config;
    }

    private void doScan(String scannerPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scannerPackage.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                this.doScan(scannerPackage + "." + file.getName());
            } else {
                this.registryBeanClasses.add(scannerPackage + "." + file.getName().replace(".class", ""));
            }
        }
    }
}
