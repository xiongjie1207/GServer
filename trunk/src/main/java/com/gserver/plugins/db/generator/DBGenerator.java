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

package com.gserver.plugins.db.generator;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * 生成器
 * 1：生成时会强制覆盖 entity和dao，建议不要修改三类文件，在数据库有变化重新生成一次便可
 */
public class DBGenerator {

    protected MetaBuilder metaBuilder;
    protected EntityGenerator baseEntityGenerator;
    protected DaoGenerator daoGenerator;
    protected DaoInterfaceGenerator daoInterfaceGenerator;
    private boolean isGeneratDao = true;

    /**
     *
     * @param dataSource 数据源
     * @param cacheName 缓存名称
     * @param modelPackageName 实体累的包名
     * @param daoPackageName dao类的包名
     * @param modelOutputDir 输出目录
     */
    public DBGenerator(DataSource dataSource,String cacheName, String modelPackageName, String daoPackageName, String modelOutputDir) {
        this(dataSource, new EntityGenerator(modelPackageName, modelOutputDir), new DaoGenerator(cacheName,daoPackageName,modelPackageName, modelOutputDir));
    }

    public DBGenerator(DataSource dataSource, EntityGenerator baseModelGenerator, DaoGenerator daoGenerator) {
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource can not be null.");
        }
        if (baseModelGenerator == null) {
            throw new IllegalArgumentException("baseEntityGenerator can not be null.");
        }

        this.metaBuilder = new MetaBuilder(dataSource);
        this.baseEntityGenerator = baseModelGenerator;
        this.daoGenerator = daoGenerator;
        this.daoInterfaceGenerator = new DaoInterfaceGenerator(daoGenerator.daoPackageName,daoGenerator.modelPakageName,daoGenerator.daoOutputDir);
    }


    /**
     * 设置 MetaBuilder，便于扩展自定义 MetaBuilder
     */
    public void setMetaBuilder(MetaBuilder metaBuilder) {
        if (metaBuilder != null) {
            this.metaBuilder = metaBuilder;
        }
    }

    public void setTypeMapping(TypeMapping typeMapping) {
        this.metaBuilder.setTypeMapping(typeMapping);
    }


    /**
     * 添加不需要处理的数据表
     */
    public void addExcludedTable(String... excludedTables) {
        metaBuilder.addExcludedTable(excludedTables);
    }

    /**
     * 生成全部的表实体
     */
    public void generate() {
        List<TableMeta> tableMetas = metaBuilder.build();

        generate(tableMetas);
    }

    /**
     * 生成指定的表实体
     *
     * @param tables
     * @return
     */

    public List<String> generate(String tables) {
        String[] tableName = tables.split(",");
        List<String> nonexistent = new ArrayList<>();
        List<TableMeta> tableMetas = metaBuilder.build();
        List<TableMeta> tempTableMetas = new ArrayList<>();
        for (String name : tableName) {
            nonexistent.add(name);
            for (TableMeta meta : tableMetas) {
                if (meta.name.equalsIgnoreCase(name)) {
                    tempTableMetas.add(meta);
                    nonexistent.remove(name);
                }
            }
        }
        generate(tempTableMetas);
        System.out.println("表 " + nonexistent + " 不存在");
        return nonexistent;
    }

    private void generate(List<TableMeta> tableMetas) {
        long start = System.currentTimeMillis();
        if (tableMetas.size() == 0) {
            System.out.println("TableMeta 数量为 0，不生成任何文件");
            return;
        }

        baseEntityGenerator.generate(tableMetas);
        if (isGeneratDao) {
            daoInterfaceGenerator.generate(tableMetas);
            daoGenerator.generate(tableMetas);
        }
        long usedTime = (System.currentTimeMillis() - start) / 1000;
        System.out.println("Generate complete in " + usedTime + " seconds.");
    }


    public void setGeneratDao(boolean isGeneratDao) {
        this.isGeneratDao = isGeneratDao;
    }

}



