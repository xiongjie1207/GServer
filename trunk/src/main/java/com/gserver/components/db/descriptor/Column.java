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

public class Column implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8000107635261090463L;
    
    
    /**
     * 别名
     */
    private String aliasName;
    
    /**
     * 字段名
     */
    private String name;
    
    /**
     * 类型
     */
    private int jdbcType;
    
    /**
     * 字段sql值
     */
    private String fieldSql;
    
    /**
     * 字段长度
     */
    private int length;
    
    /**
     * 是否必填
     */
    private boolean request;
    
    /**
     * 是否为主键
     */
    private boolean primaryKey;
    
    public String getAliasName() {
        return aliasName;
    }
    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getJdbcType() {
        return jdbcType;
    }
    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }
  
    public String getFieldSql() {
        return fieldSql;
    }
    public void setFieldSql(String fieldSql) {
        this.fieldSql = fieldSql;
    }
    
    public boolean isPrimaryKey() {
        return primaryKey;
    }
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public boolean isRequest() {
		return request;
	}
	public void setRequest(boolean request) {
		this.request = request;
	}
    
    
    
    

}
