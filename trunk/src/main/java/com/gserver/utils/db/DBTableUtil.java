package com.gserver.utils.db;

import com.gserver.plugins.db.descriptor.IEntity;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

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
 * Created by xiongjie on 2017/2/24.
 */
public class DBTableUtil {
    private static Map<String,String> tableNameCache = new HashMap<>();
    public static String loadTableName(Class<? extends IEntity> clazz){
        String tableName = tableNameCache.get(clazz.getSimpleName());
        if(tableName==null) {
            if (clazz.isAnnotationPresent(com.gserver.plugin.db.annotation.Table.class)) {
                Annotation[] annotations = clazz.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof com.gserver.plugin.db.annotation.Table) {
                        com.gserver.plugin.db.annotation.Table tableNameAnnotation = (com.gserver.plugin.db.annotation.Table) annotation;
                        tableName = tableNameAnnotation.value();
                        tableNameCache.put(clazz.getSimpleName(),tableName);
                        break;
                    }
                }
            }
        }
        return tableName;
    }
}