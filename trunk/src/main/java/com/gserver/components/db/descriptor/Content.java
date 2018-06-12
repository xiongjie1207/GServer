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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 表内容
 */
public class Content implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4742799207318016984L;

    /**
     * 所有字段-字符串
     */
    private String selectFields;


    private String database;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段列表
     */
    private Map<String, Column> columns;


    /**
     * 主键
     */
    private PrimaryKey primaryKey;

    /**
     * 版本字段
     */
    private Column versionField;

    public Content() {
        super();
        columns = new HashMap<String, Column>();
        primaryKey = new PrimaryKey();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, Column> getColumns() {
        return columns;
    }

    public Column getColumn(String fieldName) {
        return columns.get(fieldName);
    }

    public void setColumns(Map<String, Column> columns) {
        this.columns = columns;
    }

    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void addPrimaryFieldName(String fieldName) {
        primaryKey.addFieldName(fieldName);
    }

    public void addField(Column field) {
        this.columns.put(field.getName(), field);
    }



    public Column getVersionField() {
        return versionField;
    }

    public void setVersionField(Column versionField) {
        this.versionField = versionField;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }


    public String getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(String selectFields) {
        this.selectFields = selectFields;
    }
}
