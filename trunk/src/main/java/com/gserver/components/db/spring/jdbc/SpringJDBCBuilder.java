package com.gserver.components.db.spring.jdbc;
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

import com.gserver.components.db.core.BaseDAL;
import com.gserver.components.db.descriptor.ResolveDataBase;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpringJDBCBuilder {

    private static SpringJDBCBuilder instance = new SpringJDBCBuilder();
    private static String DEFAULT_DAL = "DEFAULT";


    private static final Map<String, BaseDAL> BASE_DAL_MAP = new HashMap<String, BaseDAL>();
    private static final Map<String, ResolveDataBase> DATA_SOURCE_MAP = new HashMap<String, ResolveDataBase>();

    public static SpringJDBCBuilder getInstance(){
        return instance;
    }
    public void configDataSource(LinkedHashMap<String, ResolveDataBase> dataSourceLinkedHashMap) {
        for (Map.Entry<String, ResolveDataBase> entry : dataSourceLinkedHashMap.entrySet()) {
            if (DATA_SOURCE_MAP.size() == 0) {
                DEFAULT_DAL = entry.getKey();
            }
            DATA_SOURCE_MAP.put(entry.getKey(), entry.getValue());
        }
    }

    public BaseDAL buildDAL() {
        BaseDAL baseDAL = BASE_DAL_MAP.get(DEFAULT_DAL);
        if (baseDAL == null) {
            baseDAL = createBaseDAL(DATA_SOURCE_MAP.get(DEFAULT_DAL));
            BASE_DAL_MAP.put(DEFAULT_DAL, baseDAL);
        }
        return baseDAL;
    }

    public BaseDAL buildDAL(String name) {
        BaseDAL baseDAL = BASE_DAL_MAP.get(name);
        if (baseDAL == null) {
            baseDAL = createBaseDAL(DATA_SOURCE_MAP.get(name));
            BASE_DAL_MAP.put(name, baseDAL);
        }
        return baseDAL;
    }

    private BaseDAL createBaseDAL(ResolveDataBase resolveDataBase) {
        CommonJdbcSupport commonJdbcSupport = new CommonJdbcSupport();
        commonJdbcSupport.setDataSource(resolveDataBase.getDataSource());
        SpringJDBCDAL baseDAL = new SpringJDBCDAL();
        baseDAL.setJdbcDaoSupport(commonJdbcSupport);
        baseDAL.setResolveDatabase(resolveDataBase);
        return baseDAL;
    }
    public DataSource getDataSource(){
        return DATA_SOURCE_MAP.get(DEFAULT_DAL).getDataSource();
    }

}
