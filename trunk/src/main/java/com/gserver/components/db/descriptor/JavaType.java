package com.gserver.components.db.descriptor;
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
import java.util.HashMap;
import java.util.Map;

public enum JavaType {

    BOOLEAN("java.lang.Boolean"),
    CHARACTER("java.lang.Character"),
    DOUBLE("java.lang.Double"),
    FLOAT("java.lang.Float"),
    INTEGER("java.lang.Integer"),
    LONG("java.lang.Long"),
    STRING("java.lang.String"),
    SHORT("java.lang.Short"),
    DATE("java.util.Date"),
    BYTE("java.lang.Byte"),
    OBJECT("java.lang.Object");

    JavaType(String type) {
        this.TYPE = type;
    }

    public String value() {
        return TYPE;
    }

    public final String TYPE;

    private static Map<String, JavaType> codeLookup = new HashMap<String, JavaType>();

    static {
        for (JavaType type : JavaType.values()) {
            codeLookup.put(type.TYPE, type);
        }
    }

    public static JavaType forType(String type) {
        return codeLookup.get(type);
    }


}
