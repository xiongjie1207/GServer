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

package com.gserver.plugins.db.generator;

import java.util.HashMap;
import java.util.Map;

/**
 * TypeMapping 建立起 ResultSetMetaData.getColumnClassName(i)到 java类型的映射关系
 * 特别注意所有时间型类型全部映射为 java.util.Date，可通过继承扩展该类来调整映射满足特殊需求
 * 将所有时间型类型全部对应到 java.util.Date
 */
public class TypeMapping {

    @SuppressWarnings("serial")
    protected Map<String, String> map = new HashMap<String, String>() {{

        // date, year
        put("java.sql.Date", "java.util.Date");

        // time
        put("java.sql.Time", "java.util.Date");

        // timestamp, datetime
        put("java.sql.Timestamp", "java.util.Date");

        put("[B", "byte[]");

        // ---------

        // varchar, char, enum, set, text, tinytext, mediumtext, longtext
        put("java.lang.String", "java.lang.String");

        // int, integer, tinyint, smallint, mediumint
        put("java.lang.Integer", "java.lang.Integer");

        // bigint
        put("java.lang.Long", "java.lang.Long");

        // real, double
        put("java.lang.Double", "java.lang.Double");

        // float
        put("java.lang.Float", "java.lang.Float");

        // bit
        put("java.lang.Boolean", "java.lang.Boolean");

        // decimal, numeric
        put("java.math.BigDecimal", "java.lang.Long");

        // unsigned bigint
        put("java.math.BigInteger", "java.lang.Long");
    }};

    public String getType(String typeString) {
        return map.get(typeString);
    }
}
