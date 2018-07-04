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
import com.gserver.utils.JsonUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class QueryResult implements Serializable {


    private static final long serialVersionUID = 5675310807548144110L;

    private List<Map<String, Object>> copyForShardResult = new ArrayList<Map<String, Object>>();

    private List<Map<String, Object>> resultList;

    private Map<String, Object> resultMap;


    public void addShardResult(List<Map<String, Object>> shardResult) {
        copyForShardResult.addAll(shardResult);
    }

    public void setResultList(List<Map<String, Object>> resultList) {
        this.resultList = resultList;
    }

    public void setResultMap(Map<String, Object> resultMap) {
        this.resultMap = resultMap;
    }

    public Map<String, Object> get() {
        if (resultMap != null && resultMap.size() > 0) {
            return resultMap;
        }
        if (resultList != null && resultList.size() > 0) {
            return resultList.get(0);
        }
        return resultMap;
    }

    public Map<String, Object> getWithAliasName(Map<String, String> aliasName) {
        Map<String, Object> result = get();
        return getResultWithAliasName(result, aliasName);
    }

    public Map<String, Object> getWithAliasName(List<String> hiddenFields, Map<String, String> aliasName) {
        Map<String, Object> result = get(hiddenFields);
        result = getResultWithAliasName(result, aliasName);
        return result;
    }


    public Map<String, Object> get(List<String> hiddenFields) {
        Map<String, Object> temp = get();
        if (temp != null && hiddenFields != null) {
            hiddenField(temp, hiddenFields);
        }
        return temp;
    }

    public List<Map<String, Object>> getList() {
        return resultList;
    }

    public List<Map<String, Object>> getList(List<String> hiddenFields) {
        if (hiddenFields != null && resultList != null) {
            for (Map<String, Object> map : resultList) {
                hiddenField(map, hiddenFields);
            }
        }
        return resultList;
    }

    private void hiddenField(Map<String, Object> resultMap, List<String> hiddenFields) {
        if (hiddenFields != null) {
            for (String field : hiddenFields) {
                if (resultMap.containsKey(field)) {
                    resultMap.remove(field);
                }
            }
        }
    }

    public List<Map<String, Object>> getListWithAliasName(Map<String, String> aliasName) {
        if (aliasName != null && resultList != null) {
            resultList = getResultListWithAliasName(resultList, aliasName);
        }
        return resultList;
    }


    public List<Map<String, Object>> getListWithAliasName(List<String> hiddenFields, Map<String, String> aliasName) {
        resultList = getList(hiddenFields);
        resultList = getListWithAliasName(aliasName);
        return resultList;
    }

    public <T> T as(Class<T> beanClass) {
        Map<String, Object> result = get();
        if (result != null) {
            return JsonUtils.mapToObj(result, beanClass);
        }
        return null;
    }

    public <T> List<T> asList(Class<T> beanClass) {
        if (resultList == null||resultList.size()==0) {
            return new ArrayList<T>();
        }

        return JsonUtils.mapListToObjList(resultList, beanClass);
    }


    private Map<String, Object> getResultWithAliasName(Map<String, Object> result, Map<String, String> aliasName) {
        if (result != null && result.size() > 0 && null != aliasName) {
            for (Entry<String, String> item : aliasName.entrySet()) {
                if (StringUtils.isNotBlank(item.getKey()) && StringUtils.isNotBlank(item.getValue())) {
                    result.put(item.getValue(), result.get(item.getKey()));
                    result.remove(item.getKey());
                }
            }
        }
        return result;
    }

    private List<Map<String, Object>> getResultListWithAliasName(List<Map<String, Object>> result, Map<String, String> aliasName) {
        if (result != null && result.size() > 0 && null != aliasName) {
            for (Map<String, Object> map : result) {
                getResultWithAliasName(map, aliasName);
            }
        }
        return result;
    }

}
