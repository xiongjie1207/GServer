package com.gserver.utils.db;
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
import com.gserver.plugins.db.descriptor.Column;
import com.gserver.plugins.db.descriptor.JavaType;
import com.gserver.plugins.db.descriptor.JavaTypeResolver;

public class VersionWrapperUtils {

    public static String wrapSetSql(Column column) {
        JavaType javaType = JavaTypeResolver.calculateJavaType(column.getJdbcType());
        StringBuffer sql = new StringBuffer();
        if (JavaType.INTEGER == javaType) {
            sql.append(ColumnWrapperUtils.columnWrap(column.getName()));
            sql.append("=");
            sql.append(ColumnWrapperUtils.columnWrap(column.getName()));
            sql.append("+1");
        }
        return sql.toString();
    }

    public static String wrapWhereSql(Column column, Object value) {
        JavaType javaType = JavaTypeResolver.calculateJavaType(column.getJdbcType());
        StringBuffer sql = new StringBuffer();
        if (JavaType.INTEGER == javaType) {
            sql.append(ColumnWrapperUtils.columnWrap(column.getName()));
            sql.append("=");
            if (value instanceof String) {
                sql.append(Integer.valueOf((String) value));
            } else {
                sql.append((Integer) value);
            }
        }
        return sql.toString();
    }

}

