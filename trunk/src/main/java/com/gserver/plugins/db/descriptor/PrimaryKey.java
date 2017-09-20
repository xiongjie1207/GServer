package com.gserver.plugins.db.descriptor;
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
import java.util.ArrayList;
import java.util.List;

public class PrimaryKey implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5675310807548144110L;
    
    private String primaryKeyCondition;
    
    /**
     * 字段列表
     */
    private List<String> fields;
    
    public PrimaryKey() {
        fields = new ArrayList<String>();
    }
    
    
    public String getPrimaryKeyCondition() {
        return primaryKeyCondition;
    }



    public void setPrimaryKeyCondition(String primaryKeyCondition) {
        this.primaryKeyCondition = primaryKeyCondition;
    }



    public List<String> getFields() {
        return fields;
    }



    public void setFields(List<String> fields) {
        this.fields = fields;
    }



    public void addFieldName(String field){
        fields.add(field);
    }

}
