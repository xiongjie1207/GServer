package com.wegame.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import static java.lang.System.exit;

@WebListener
public class ServerListener implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        exit(0);
    }
}