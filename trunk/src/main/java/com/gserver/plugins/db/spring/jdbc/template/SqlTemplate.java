package com.gserver.plugins.db.spring.jdbc.template;
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

import com.gserver.plugins.db.descriptor.Table;
import com.gserver.plugins.db.jdbc.template.AbstractSqlTemplate;
import com.gserver.utils.db.ColumnWrapperUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class SqlTemplate extends AbstractSqlTemplate {

    protected String buildSingleParamSql(String column, String keyword) {
        StringBuffer sql = new StringBuffer();
        if (StringUtils.isNotEmpty(keyword)) {
            sql.append(ColumnWrapperUtils.columnWrap(column)).append(" ").append(keyword);
        }
        sql.append(" ? ");
        return sql.toString();
    }

    protected String buildBetweenParamSql(String column, String keyword) {
        StringBuffer sql = new StringBuffer();
        if (StringUtils.isNotEmpty(keyword)) {
            sql.append(ColumnWrapperUtils.columnWrap(column)).append(" ").append(keyword);
        }
        sql.append(" ? and ? ");
        return sql.toString();
    }

    protected String buildListParamSql(String column, Table model, String keyword) {
        StringBuffer sql = new StringBuffer();

        if ("in".equals(keyword) || "not in".equals(keyword)) {
            @SuppressWarnings("unchecked")
            Object values = model.getConditionValue(column);
            int len = ((List<Object>)values).size();
            sql.append(ColumnWrapperUtils.columnWrap(column)).append(" ").append(keyword).append(" (");
            for (int i = 0; i < len; i++) {
                sql.append("?");
                if ((i + 1) != len) {
                    sql.append(", ");
                }
            }
            sql.append(") ");
        } else {
            sql.append(ColumnWrapperUtils.columnWrap(column)).append(" ").append(keyword).append(" (?) ");
        }

        return sql.toString();
    }


}
