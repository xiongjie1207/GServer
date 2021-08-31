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
import com.gserver.components.db.criteria.QueryCriteria;
import com.gserver.utils.db.ColumnWrapperUtils;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Table {

    /**
     * 表内容
     */
    private Content content;

    /**
     * 查询条件
     */
    private QueryCriteria queryCriteriaLocal;

    /**
     * 条件参数封装
     */
    private LinkedHashMap<StringBuilder, Object> conditionsLocal;

    /**
     * 更新参数封装
     */
    private LinkedHashMap<String, Object> queryFields;


    public Table(Content content) {
        this.content = content;
        conditionsLocal = new LinkedHashMap<>();
        queryFields = new LinkedHashMap<String, Object>();
    }


    /**
     * 处理后的表字段 {@code String} 格式的字符串
     * <ul>
     * <li>对字段时行排序处理，sql也会进行排序；</li>
     * <li>最高级别隐藏不需要显示的字段；</li>
     * <li>生成主键相关信息；</li>
     * <li>生成外键相关信息；</li>
     * <li>生成自定义显示信息；</li>
     * </ul>
     *
     * @return 处理后的表字段 {@code String} 格式的字符串。
     * @since 1.0
     */
    public String getAllSelectFields() {
        if (StringUtils.isNotEmpty(content.getSelectFields())) {
            return content.getSelectFields();
        } else {
            StringBuffer sb = new StringBuffer();
            for (Column f : this.content.getColumns().values()) {
                sb.append(ColumnWrapperUtils.columnWrap(f.getName())).append(",");
            }
            content.setSelectFields(sb.deleteCharAt(sb.lastIndexOf(",")).toString());
        }
        return content.getSelectFields();
    }

    public String getTableName() {
        return content.getTableName();
    }

    public void setTableName(String tableName) {
        content.setTableName(tableName);
    }

    public String getDatabase() {
        return content.getDatabase();
    }

    public void setDatabase(String database) {
        content.setDatabase(database);
    }

    public Map<String, Column> getColumns() {
        return content.getColumns();
    }

    public Column getColumn(String fieldName) {
        return content.getColumns().get(fieldName);
    }

    public void setColumns(Map<String, Column> columns) {
        content.setColumns(columns);
    }

    public PrimaryKey getPrimaryKey() {
        return content.getPrimaryKey();
    }

    public void addPrimaryFieldName(String fieldName) {
        content.getPrimaryKey().addFieldName(fieldName);
    }

    public void addColumn(Column field) {
        content.getColumns().put(field.getName(), field);
    }

    public QueryCriteria getQueryCriteria() {
        return queryCriteriaLocal;
    }

    public void setQueryCriteria(QueryCriteria queryCriteria) {
        this.queryCriteriaLocal = queryCriteria;
    }

    public Map<StringBuilder, Object> getConditions() {
        return conditionsLocal;
    }

    public Object getConditionValue(String key) {
        for (StringBuilder conditionKey : this.conditionsLocal.keySet()) {
            if (conditionKey.toString().equals(key)) {
                return this.conditionsLocal.get(conditionKey);
            }
        }
        return null;
    }

    public LinkedHashMap<String, Object> getQueryFields() {
        return queryFields;
    }


    public void putCondition(String key, Object value) {
        this.conditionsLocal.put(new StringBuilder(key), value);
    }

    public void putQueryField(String key, Object value) {
        this.queryFields.put(key, value);
    }

    public void resetQueryCriteria() {
        this.queryCriteriaLocal = new QueryCriteria(this.queryCriteriaLocal.getTable());
    }

    public void resetQueryConditions() {
        conditionsLocal = new LinkedHashMap<StringBuilder, Object>();
    }

    public void resetQueryFields() {
        queryFields = new LinkedHashMap<String, Object>();
    }

    public boolean hasVersion() {
        return null != getVersion();

    }

    public Column getVersion() {
        if (null != content) {
            return content.getVersionField();
        }
        return null;
    }

    public void putConditions(HashMap<String, Object> conditions) {
        for (String key : conditions.keySet()) {
            this.putCondition(key, conditions.get(key));
        }
    }


    public void setVersionField(Column versionField) {
        this.content.setVersionField(versionField);
    }
}
