package com.acey.spring.framework.webmcv.servlet;

import com.acey.spring.framework.context.ApplicationContext;
import lombok.SneakyThrows;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DispatchServlet extends HttpServlet {

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
    public void init(ServletConfig config) throws ServletException {
        ApplicationContext applicationContext = new ApplicationContext(config.getInitParameter("contextConfigLocation"));
    }
}
