package com.wegame.framework.config;

import lombok.Data;

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
@Data
public class ServerConfig {


    private String bindIP = "0.0.0.0";

    private short webSocketPort = 8081;
    /**
     * boss线程池
     */
    private short bossCount = 0;
    /**
     * work线程池
     */
    private short workerCount = 0;
    /**
     * 监听端口
     */
    private int port = 8080;
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

}
