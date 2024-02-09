package com.wegame.framework.config;

import lombok.Data;

/**
 * @Program: grpc-demo
 * @Description:
 * @Author: ccm
 * @CreateTime: 2021-11-18 16:20
 */
@Data
public class ZKConfig {
    private String ZKAddress;

    private String serviceName;

    private String serviceIp;

    private int servicePort;

    private long ttl;


    public String getZKKey() {
        return String.format("/%s/%s:%d", serviceName, serviceIp, servicePort);
    }

    public String getHttpKey(String port) {
        return String.format("/%s/%s:%s", serviceName, serviceIp, port);
    }

    public String getHttpHost(String port) {
        return String.format("%s:%s", serviceIp, port);
    }

    public String getZKValue() {
        return String.format("%s:%d", serviceIp, servicePort);
    }

    public String getPrefixFormat(String app_name) {
        return String.format("/%s", app_name);
    }
}
