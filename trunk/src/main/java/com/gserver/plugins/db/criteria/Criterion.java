package com.gserver.plugins.db.criteria;
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
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Criterion {
    
    private String column;
    
    private Condition condition;
    
    private Object value;

    private Object secondValue;

    private boolean noValue;

    private boolean singleValue;

    private boolean betweenValue;

    private boolean listValue;

    private String typeHandler;

    public Condition getCondition() {
        return condition;
    }

    public Object getValue() {
        return value;
    }

    public Object getSecondValue() {
        return secondValue;
    }

    public boolean isNoValue() {
        return noValue;
    }

    public boolean isSingleValue() {
        return singleValue;
    }

    public boolean isBetweenValue() {
        return betweenValue;
    }

    public boolean isListValue() {
        return listValue;
    }

    public String getTypeHandler() {
        return typeHandler;
    }
    
    public String getColumn() {
        return column;
    }


    public Criterion(Condition condition, Object value, String typeHandler, String column) {
        super();
        this.condition = condition;
        this.value = value;
        this.typeHandler = typeHandler;
        this.column = column;
        if (value instanceof List<?>) {
            this.listValue = true;
        } else {
            this.singleValue = true;
        }
        if(value == null){
            this.noValue = true;
        }
    }
    
    public Criterion(String sql) {
        this(null, null, null, sql);
    }
    
    public Criterion(Condition condition, String column) {
        this(condition, null, null, column);
    }

    public Criterion(Condition condition, Object value, String column) {
        this(condition, value, null, column);
    }

    public Criterion(Condition condition, Object value, Object secondValue, String typeHandler, String column) {
        super();
        this.condition = condition;
        this.value = value;
        this.secondValue = secondValue;
        this.typeHandler = typeHandler;
        this.betweenValue = true;
        this.column = column;
    }

    public Criterion(Condition condition, Object value, Object secondValue, String column) {
        this(condition, value, secondValue, null, column);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((column == null) ? 0 : column.hashCode());
        result = prime * result + ((condition == null) ? 0 : condition.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((secondValue == null) ? 0 : secondValue.hashCode());
        result = prime * result + (noValue ? 1231 : 1237);
        result = prime * result + (singleValue ? 1231 : 1237);
        result = prime * result + (betweenValue ? 1231 : 1237);
        result = prime * result + (listValue ? 1231 : 1237);
        result = prime * result + ((typeHandler == null) ? 0 : typeHandler.hashCode());
        return result;
    }
    
    public String toString(){
    	StringBuffer sb = new StringBuffer();
    	if(StringUtils.isNotEmpty(column)){
    		sb.append("column:").append(column).append(",");
    	}
    	if(null != condition){
    		sb.append("condition:").append(condition).append(",");
    	}
    	if(null != value){
    		sb.append("pid:").append(value).append(",");
    	}
    	if(null != secondValue){
    		sb.append("secondValue:").append(secondValue).append(",");
    	}
    	if(StringUtils.isNotEmpty(typeHandler)){
    		sb.append("typeHandler:").append(typeHandler).append(",");
    	}
    	sb.append("noValue:").append(noValue).append(",");
    	sb.append("singleValue:").append(singleValue).append(",");
    	sb.append("betweenValue:").append(betweenValue).append(",");
    	sb.append("listValue:").append(listValue).append(",");
    	sb.deleteCharAt(sb.lastIndexOf(","));
    	
    	return sb.toString();
    }

}
