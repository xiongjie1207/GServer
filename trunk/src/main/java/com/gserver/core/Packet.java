package com.gserver.core;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Packet {

    public static final String PID = "pid";

    private Map<String, Object> json;
    private Logger logger = Logger.getLogger(Packet.class);

    public Packet(int id) {
        json = new HashMap<String, Object>();
        json.put(PID, id);
    }


    public Packet(Map<String, Object> json) {
        if (json == null) {
            throw new RuntimeException("数据不能为null");
        }
        if (!json.containsKey(PID)) {
            throw new RuntimeException("缺少协议id");
        }
        this.json = json;
    }

    public int getProtocoleId() {
        return Integer.parseInt(json.get(PID).toString());
    }

    public Packet(String jsonStr) {
        if (jsonStr == null) {
            jsonStr = "";
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json = objectMapper.readValue(jsonStr, HashMap.class);
        } catch (IOException e) {
            logger.error("", e);
        }
        if (!json.containsKey(PID)) {
            throw new RuntimeException("缺少协议id");
        }

    }

    public void put(String key, Object value) {
        if (this.containeKey(PID)) {
            new RuntimeException("协议id重复");
        }
        json.put(key, value);
    }

    public Map<String, Object> getRoot() {
        return json;
    }

    public Object get(String key) {
        if (json.containsKey(key)) {
            return json.get(key);
        }
        return null;
    }

    public String toJSONString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(json);
    }

    public void remove(String key) {
        // TODO Auto-generated method stub
        json.remove(key);
    }

    public boolean containeKey(String key) {
        // TODO Auto-generated method stub
        return json.containsKey(key);
    }

    public boolean containerValue(String value) {
        // TODO Auto-generated method stub
        return json.containsValue(value);
    }

    @Override
    public Packet clone() {
        return new Packet(json);
    }
}
