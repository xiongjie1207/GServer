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

import com.gserver.utils.Loggers;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ActionMapping {
    private static ActionMapping instance = new ActionMapping();
    private Map<Integer, Action> mapping = new HashMap<Integer, Action>();

    public static ActionMapping getInstance() {
        return instance;
    }

    public void addAction(Action action) {
        Loggers.GameLogger.debug("action mapping:" + action.toString());
        if (this.mapping.get(action.getActionKey()) != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Duplicate method ").append(" for key ").append(action.getActionKey());
            throw new RuntimeException(sb.toString());
        }
        this.mapping.put(action.getActionKey(), action);
    }

    public Action getAction(Integer actionKey) {
        return this.mapping.get(actionKey);
    }
}
