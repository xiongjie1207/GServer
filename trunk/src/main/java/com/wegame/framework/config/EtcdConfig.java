package com.wegame.framework.config;

import lombok.Data;

/**
 * @Program: grpc-demo
 * @Description:
 * @Author: ccm
 * @CreateTime: 2021-11-18 16:20
 */
@Data
public class EtcdConfig {
    private final String prefix = "/wegame";

    private String[] etcdAddress;

    private String serviceName;

    private String serviceIp;

    private int servicePort;

    private long ttl;



    public String getEtcdKey() {
        return String.format("%s/%s/%s:%d",prefix, serviceName, serviceIp, servicePort);
    }

    public String getHttpKey(String port) {
        return String.format("%s/http_%s/%s:%s", prefix, serviceName, serviceIp, port);
    }

    public String getHttpHost(String port) {
        return String.format("%s:%s", serviceIp, port);
    }

    public String getEtcdValue() {
        return String.format("%s:%d", serviceIp, servicePort);
    }

    public String getPrefixFormat(String app_name) {return String.format("%s/%s", prefix, app_name);}
}
