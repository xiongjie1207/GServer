package com.wegame.framework.core;
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


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class ActionMapping {
    private static final ActionMapping instance = new ActionMapping();
    private final Map<String, Action> mapping = new HashMap<>();

    public static ActionMapping getInstance() {
        return instance;
    }

    public void addAction(Action action) {
        LoggerFactory.getLogger(this.getClass()).debug("action mapping:" + action.toString());
        String key = createKey(action.getModule(),action.getPid());
        if (this.mapping.containsKey(key)) {
            throw new RuntimeException(MessageFormat.format("Duplicate method for module:{0} key:{1}",action.getModule(),action.getPid()));
        }
        this.mapping.put(key, action);

    }

    public Action getAction(short module,int pid) {
        return this.mapping.get(createKey(module,pid));
    }
    private String createKey(short module,int pid){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(module).append("/").append(pid);
        return stringBuilder.toString();
    }
}
