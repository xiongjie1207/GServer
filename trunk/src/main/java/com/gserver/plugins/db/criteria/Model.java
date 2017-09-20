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
import com.gserver.plugins.db.descriptor.IEntity;
import com.gserver.utils.JsonUtils;
import com.gserver.utils.db.DBTableUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class  Model implements Serializable {

    private static final long serialVersionUID = 1;

    private final HashMap<String, Object> content;


    private String tableName;

    private String database = "";

    public static final String VERSION = "VERSION";

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }


    public Model(String modelName) {
        content = new HashMap<String, Object>();
        setTableName(modelName.trim());
    }

    public Model(IEntity entity) {
        this(DBTableUtil.loadTableName(entity.getClass()));
        Map<String, Object> map = (Map<String, Object>) JsonUtils.objToMap(entity);
        if(map.size()==0){
            throw new RuntimeException("Entity no data");
        }
        content.putAll(map);
    }

    public Model(Model model) {
        content = new HashMap<String, Object>();
        content.putAll(model.getContent());
    }

    public Model(Map<String, Object> map) {
        content = new HashMap<String, Object>();
        if (map != null) {
            content.putAll(map);
        }
    }


    public Model(String tableName, Map<String, Object> obj) {
        this(tableName);
        if (null != obj) {
            content.putAll(obj);
        }
    }


    public String getVersion() {
        if (content.containsKey(VERSION)) {
            return content.get(VERSION).toString();
        }
        return null;

    }

    public void setVersion(String version) {
        this.content.put(VERSION, version);
    }

    public void setField(String field, Object value) {
        if (StringUtils.isEmpty(field)) {
            return;
        }
        content.put(field, value);
    }

    public Object getField(String field) {
        if (StringUtils.isEmpty(field)) {
            return null;
        }
        return content.get(field);
    }

    public HashMap<String, Object> getContent() {
        return content;
    }

    public void addContent(Map<String, Object> content) {
        this.content.putAll(content);
    }


}
