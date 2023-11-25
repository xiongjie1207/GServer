package com.wegame.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Program: grpc-demo
 * @Description:
 * @Author: ccm
 * @CreateTime: 2021-11-18 16:20
 */
@Data
@Component("etcdConfig")
@ConfigurationProperties("etcd-config")
public class EtcdConfig {
    private final String prefix = "/wegame";

    private String[] etcdAddress;

    private String serverName;

    private String ip;

    private int port;

    private long ttl;



    public String getEtcdKey() {
        return String.format("%s/%s/%s:%d",prefix,serverName,ip,port);
    }

    public String getHttpKey(String port) {
        return String.format("%s/http_%s/%s:%s", prefix, serverName, ip, port);
    }

    public String getHttpHost(String port) {
        return String.format("%s:%s", ip, port);
    }

    public String getEtcdValue() {
        return String.format("%s:%d",ip,port);
    }

    public String getPrefixFormat(String app_name) {return String.format("%s/%s", prefix, app_name);}
}
