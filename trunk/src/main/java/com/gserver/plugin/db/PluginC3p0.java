package com.gserver.plugin.db;

import com.gserver.plugin.IPlugin;
import com.gserver.plugins.db.descriptor.ResolveDataBase;
import com.gserver.plugins.db.descriptor.SimpleResolveDatabase;
import com.gserver.plugins.db.spring.jdbc.SpringJDBCBuilder;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * Created by xiongjie on 2016/12/23.
 */
public class PluginC3p0 implements IPlugin {

    private LinkedHashMap<String, ResolveDataBase> dataSourceLinkedHashMap;

    public PluginC3p0(List<ResolveDataBase> listResolveDataBase) {
        if(dataSourceLinkedHashMap==null){
            dataSourceLinkedHashMap = new LinkedHashMap<String, ResolveDataBase>();
        }
        for (ResolveDataBase resolveDataBase:listResolveDataBase){
            dataSourceLinkedHashMap.put(resolveDataBase.getName(),resolveDataBase);
        }
    }
    public PluginC3p0(LinkedHashMap<String, DataSource> dataSourceMap) {
        if(dataSourceLinkedHashMap==null){
            dataSourceLinkedHashMap = new LinkedHashMap<>();
        }
        for (Map.Entry<String,DataSource> entry:dataSourceMap.entrySet()){
            dataSourceLinkedHashMap.put(entry.getKey(),new SimpleResolveDatabase(entry.getKey(), entry.getValue()));
        }
    }
    @Override
    public boolean start() {
        SpringJDBCBuilder.getInstance().configDataSource(this.dataSourceLinkedHashMap);
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}