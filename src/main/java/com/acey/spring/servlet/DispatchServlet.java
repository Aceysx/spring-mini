package com.acey.spring.servlet;

import com.acey.spring.annotation.Autowired;
import com.acey.spring.annotation.Controller;
import com.acey.spring.annotation.Service;
import com.acey.spring.utls.BeanUtil;
import lombok.SneakyThrows;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DispatchServlet extends HttpServlet {
    private Properties contextConfig = new Properties();
    private List<String> classNames = new ArrayList<String>();
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("start do post");
    }

    @SneakyThrows
    @Override
    public void init() throws ServletException {
//        定位
        doLoadConfig(this.getInitParameter("contextConfigLocation"));
//        加载
        doScan(contextConfig.getProperty("scannerPackage"));
//        注册
        doRegistry();
//        自动依赖注入
        doAutowired();
//      将@RequestMapping 中的配置的url和一个method关联起来，以便用户输入url后可以通过反射调用
        initHandlerMapping();
    }

    private void initHandlerMapping() {

    }

    private void doAutowired() throws IllegalAccessException {
        if (this.beanMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> bean : this.beanMap.entrySet()) {
            Field[] fields = bean.getValue().getClass().getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);
                String autowiredName = "".equals(autowired.value().trim())
                    ? BeanUtil.lowerFirstCase(field.getType().getName())
                    : autowired.value().trim();
                field.setAccessible(true);
                field.set(field.getName(), this.beanMap.get(autowiredName));
            }
        }
    }

    private void doRegistry() throws Exception {
        if (this.classNames.isEmpty()) {
            return;
        }
        for (String className : classNames) {
            Class<?> klass = Class.forName(className);
            String beanName = BeanUtil.lowerFirstCase(klass.getSimpleName());
            Object instance = klass.newInstance();
            if (klass.isAnnotationPresent(Controller.class)) {
                this.beanMap.put(beanName, instance);
            }
            if (klass.isAnnotationPresent(Service.class)) {
                Service service = klass.getAnnotation(Service.class);
                if (!"".equals(service.value().trim())) {
                    beanName = service.value();
                }
                this.beanMap.put(beanName, instance);

                for (Class<?> k : klass.getInterfaces()) {
                    beanName = BeanUtil.lowerFirstCase(k.getName());
                    if (this.beanMap.containsKey(beanName)) {
                        throw new Exception("bean name is already exist with " + beanName);
                    }
                    this.beanMap.put(beanName, instance);
                }
            }
        }
    }

    private void doScan(String scannerPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scannerPackage.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                this.doScan(scannerPackage + "." + file.getName());
            } else {
                this.classNames.add(scannerPackage + "." + file.getName().replace(".class", ""));
            }
        }
    }

    private void doLoadConfig(String location) throws IOException {
        InputStream resource = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:", ""));
        contextConfig.load(resource);
    }
}
