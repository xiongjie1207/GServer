package com.wegame.framework.grpc;


import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * @author fanqiechaodan
 * @Classname ServiceRegistry
 * @Description 服务注册
 */
@Slf4j
public class Registry {

    private static final int SESSION_TIMEOUT = 20000;

    private final ZooKeeper zooKeeper;

    private String serviceName;

    public Registry(String zooKeeperAddress) throws IOException {
        this.zooKeeper = new ZooKeeper(zooKeeperAddress, SESSION_TIMEOUT, System.out::println);
    }

    /**
     * 注册服务
     *
     * @param serviceName
     * @param serviceAddress
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void registryService(String serviceName, String serviceAddress) throws KeeperException, InterruptedException {
        this.serviceName = serviceName;
        // 如果根节点不存在就创建
        if (zooKeeper.exists(serviceName, false) == null) {
            zooKeeper.create(serviceName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        String nodePath = serviceName + "/" + serviceAddress;
        Stat state = zooKeeper.exists(nodePath, false);
        if (state != null) {
            zooKeeper.delete(nodePath, state.getVersion());
        }
        String serviceNodePath = zooKeeper.create(nodePath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("注册成功;serviceNodePath:" + serviceNodePath);
    }

    public void close() {
        try {
            List<String> childrenList = zooKeeper.getChildren(this.serviceName, true);
            for (String serviceNode : childrenList) {
                String nodePath = this.serviceName + "/" + serviceNode;
                Stat state = zooKeeper.exists(nodePath, false);
                if (state != null) {
                    zooKeeper.delete(nodePath, state.getVersion());
                }
            }
            zooKeeper.close();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
