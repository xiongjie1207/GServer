package com.wegame.util;

import static java.lang.System.exit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServerListener implements ServletContextListener{

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        exit(0);
    }
}