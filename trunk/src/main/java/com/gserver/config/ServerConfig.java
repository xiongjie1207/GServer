package com.gserver.config;
/**
 * Copyright (c) 2015-2016, James Xiong 熊杰 (xiongjie.cn@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Created by xiongjie on 2016/12/22.
 */
public class ServerConfig {
    private ServerConfig(){

    }
    private static ServerConfig instance;
    public static ServerConfig getInstance(){
        if(instance==null){
            instance = new ServerConfig();
        }
        return instance;
    }
    /**
     * boss线程池
     */
    private short bossCount = 10;
    /**
     * work线程池
     */
    private short workerCount = 10;
    /**
     * 业务处理线程池
     */
    private short handlerTreadCount=10;
    /**
     * 监听端口
     */
    private short port = 80;
    /**
     * 客户端读空闲时间
     */
    private int readerIdleTimeSeconds = 60;
    /**
     * 客户端写空闲时间
     */
    private int writerIdleTimeSeconds = 60;
    /**
     * 客户端读写空闲时间
     */
    private int allIdleTimeSeconds = 30;

    public int getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public int getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public int getAllIdleTimeSeconds() {
        return allIdleTimeSeconds;
    }


    public void setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }

    public void setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }

    public void setAllIdleTimeSeconds(int allIdleTimeSeconds) {
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }



    public short getBossCount() {
        return bossCount;
    }

    public void setBossCount(short bossCount) {
        this.bossCount = bossCount;
    }

    public short getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(short workerCount) {
        this.workerCount = workerCount;
    }

    public short getPort() {
        return port;
    }

    public void setPort(short port) {
        this.port = port;
    }


    public short getHandlerTreadCount() {
        return handlerTreadCount;
    }

    public void setHandlerTreadCount(short handlerTreadCount) {
        this.handlerTreadCount = handlerTreadCount;
    }
}
