package com.gserver.components.db.core;
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

import com.gserver.components.db.asyn.AsynRecord;
import com.gserver.components.db.asyn.AsynSQLTask;
import com.gserver.components.db.asyn.Method;
import com.gserver.components.db.criteria.Model;
import com.gserver.components.db.criteria.QueryCriteria;
import com.gserver.components.db.descriptor.*;
import com.gserver.utils.db.DBTableUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractBaseDAL implements BaseDAL {


    protected ResolveDataBase resolveDatabase;



    private ExecutorService asynWriterService;
    protected String version;

    protected List<String> versionTables;

    private List<AsynSQLTask> tasks;
    private BlockingQueue<AsynRecord> sqlQueue;

    private Logger logger = Logger.getLogger(this.getClass());

    public AbstractBaseDAL() {
        int asynWriterThreadSize = 2;
        tasks = new ArrayList<>();
        sqlQueue = new LinkedBlockingQueue<AsynRecord>();
        asynWriterService = Executors.newFixedThreadPool(asynWriterThreadSize);
        for (int i = 0; i < asynWriterThreadSize; i++) {
            AsynSQLTask task = new AsynSQLTask(this);
            task.setLogQueue(sqlQueue);
            tasks.add(task);
            asynWriterService.submit(task);
        }
        logger.info("Asyn dal init ok!");
    }

    @Override
    public void stopAsyTask() {
        for (AsynSQLTask asynSQLTask:tasks){
            asynSQLTask.setActiveFlag(false);
        }
        asynWriterService.shutdownNow();
    }

    @Override
    public QueryResult selectByCriteria(QueryCriteria queryCriteria, String... fields) {
        return selectByCriteria(queryCriteria, Arrays.asList(fields));
    }

    public QueryResult selectByCriteria(QueryCriteria queryCriteria, String fields) {
        return selectByCriteria(queryCriteria, fields.split(","));
    }

    @Override
    public QueryResult selectByCriteria(QueryCriteria queryCriteria, List<String> fields) {

        QueryResult queryResult = new QueryResult();

        //根据数据库生成表映射
        Table table = retrievalTable(queryCriteria.getDatabase(), queryCriteria.getTable());
        if (fields != null && !fields.contains("*")) {
            for (String field : fields) {
                table.putQueryField(field, true);
            }
        }
        table.setQueryCriteria(queryCriteria);
        //查询数据
        List<Map<String, Object>> result = _selectByCriteria(table);
        queryResult.setResultList(result);

        return queryResult;
    }

    public <T> T executeBySql(final String sql){
        return _executeBySql(sql);
    }

    protected abstract List<Map<String, Object>> _selectByCriteria(final Table table);

    protected abstract <T> T _executeBySql(final String sql);


    protected Table retrievalTable(String database, String dataTable) {
        if (StringUtils.isEmpty(dataTable)) {
            throw new RuntimeException("dataTable " + dataTable + " is empty");
        }
        Table table = resolveDatabase.loadTable(database, dataTable, version);
        if (table == null) {
            throw new RuntimeException("dataTable " + dataTable + " is not exist");
        }
        return table;
    }


    @Override
    public int countByCriteria(QueryCriteria queryCriteria) {


        Table table = retrievalTable(queryCriteria.getDatabase(), queryCriteria.getTable());
        table.setQueryCriteria(queryCriteria);
        int result = _countByCriteria(table);

        return result;
    }


    @Override
    public QueryResult selectByPrimaryKey(Object id, Class<? extends IEntity> clazz, String... fields) {
        if (fields == null) {
            return selectByPrimaryKey(id, clazz);
        } else {
            return selectByPrimaryKey(id, clazz, Arrays.asList(fields));
        }

    }

    @Override
    public QueryResult selectByPrimaryKey(Object id, String table, String... fields) {
        if (fields == null) {
            return selectByPrimaryKey(id, table);
        } else {
            return selectByPrimaryKey(id, table, Arrays.asList(fields));
        }

    }

    @Override
    public QueryResult selectByPrimaryKey(Object id, Class<? extends IEntity> clazz, String fields) {
        if (StringUtils.isEmpty(fields)) {
            return selectByPrimaryKey(id, clazz);
        } else {
            return selectByPrimaryKey(id, clazz, fields.split(","));
        }
    }

    @Override
    public QueryResult selectByPrimaryKey(Object id, String tableName, String fields) {
        if (StringUtils.isEmpty(fields)) {
            return selectByPrimaryKey(id, tableName);
        } else {
            return selectByPrimaryKey(id, tableName, fields.split(","));
        }
    }

    protected abstract int _countByCriteria(final Table table);

    @Override
    public QueryResult selectByPrimaryKey(Object id, Class<? extends IEntity> clazz) {
        List<String> fields = null;
        return selectByPrimaryKey(id, clazz, fields);
    }


    @Override
    public QueryResult selectByPrimaryKey(Object id, Class<? extends IEntity> clazz, List<String> fields) {
        String tableName = DBTableUtil.loadTableName(clazz);
        Model model = new Model(tableName);
        return selectByPrimaryKey(fields, model, id);
    }

    @Override
    public QueryResult selectByPrimaryKey(Object id, String tableName) {
        List<String> fields = null;
        Model model = new Model(tableName);
        return selectByPrimaryKey(fields, model, id);
    }


    @Override
    public QueryResult selectByPrimaryKey(Object id, String tableName, List<String> fields) {
        Model model = new Model(tableName);
        return selectByPrimaryKey(fields, model, id);
    }


    private QueryResult selectByPrimaryKey(List<String> fields, Model model, Object primaryKeyValue) {

        Table table = retrievalTable(model.getDatabase(), model.getTableName());
        QueryResult queryResult = new QueryResult();
        if (fields != null && !fields.contains("*")) {
            for (String field : fields) {
                table.putQueryField(field, true);
            }
        }
        if (primaryKeyValue != null) {
            table.putCondition(table.getPrimaryKey().getFields().get(0), primaryKeyValue);
        }
        Map<String, Object> result = _selectByPrimaryKey(table);

        if (result != null) {
            queryResult.setResultMap(result);
            return queryResult;
        } else {
            return null;
        }
    }

    protected abstract Map<String, Object> _selectByPrimaryKey(final Table table);


    @Override
    public long insert(IEntity obj) {
        return insert(new Model(obj));
    }

    public long insert(String table, Map<String, Object> obj) {
        Model model = new Model(table);
        model.addContent(obj);
        return insert(model);
    }

    @Override
    public long insert(String database, String table, Map<String, Object> obj) {
        Model model = new Model(table);
        model.addContent(obj);
        return insert(model);
    }

    private long insert(Model model) {

        Table table = retrievalTable(model.getDatabase(), model.getTableName());
        for (String key : model.getContent().keySet()) {
            Object value = model.getContent().get(key);
            Column column = table.getColumn(key);
            if (value != null && column != null) {
                table.putQueryField(key, model.getContent().get(key));
            }

        }
        long result = _insert(table);
        return result;
    }

    @Override
    public void asynInsert(IEntity obj) {
        sqlQueue.offer(new AsynRecord(Method.INSERT, obj));
    }

    @Override
    public void asynInsert(String table, Map<String, Object> obj) {
        sqlQueue.offer(new AsynRecord(Method.INSERT_TABLE, table, obj));
    }


    /**
     * insert option
     *
     * @param table table instance
     * @return result
     */
    protected abstract long _insert(Table table);

    @Override
    public int updateByCriteria(IEntity obj, QueryCriteria queryCriteria) {
        return updateByCriteria(new Model(obj), queryCriteria);
    }

    private int updateByCriteria(Model model, QueryCriteria queryCriteria) {

        Table table = retrievalTable(queryCriteria.getDatabase(), queryCriteria.getTable());
        String tableName = queryCriteria.getTable();
        for (String key : model.getContent().keySet()) {
            Object value = model.getContent().get(key);
            Column column = table.getColumn(key);
            if (!table.getPrimaryKey().getFields().contains(key) && value != null && column != null) {
                table.putQueryField(key, model.getContent().get(key));
            }
        }

        if (containsTables(tableName) == false) {
            table.setVersionField(null);
        }
        if (table.hasVersion()) {
            Object value = queryCriteria.getVersion();
            if (null == value) {
                throw new RuntimeException("Version is request.");
            }
            model.getContent().remove(Model.VERSION);
        }
        table.setQueryCriteria(queryCriteria);
        int result = _updateByCriteria(table);

        return result;

    }
    protected abstract int _updateByCriteria(Table table);

    @Override
    public int updateByPrimaryKey(IEntity obj) {
        return updateByPrimaryKey(new Model(obj), null);
    }

    @Override
    public int updateByCriteria(Map<String, Object> obj, QueryCriteria queryCriteria) {
        return updateByCriteria(new Model(obj), queryCriteria);
    }

    @Override
    public int updateByPrimaryKey(String table, Map<String, Object> obj, Object id) {
        Model model = new Model(table, obj);
        return updateByPrimaryKey(model, id);
    }


    private int updateByPrimaryKey(Model model, Object id) {
        Table table = retrievalTable(model.getDatabase(), model.getTableName());
        if (id != null) {
            model.setField(table.getPrimaryKey().getFields().get(0), id);
        }
        for (String key : model.getContent().keySet()) {
            Object value = model.getContent().get(key);
            Column column = table.getColumn(key);
            if (value != null && column != null) {
                JavaType javaType = JavaTypeResolver.calculateJavaType(column.getJdbcType());
                if (table.getPrimaryKey().getFields().contains(key)) {
                    table.putCondition(key, JavaTypeConversion.convert(javaType, value));
                } else {
                    table.putQueryField(key, JavaTypeConversion.convert(javaType, value));
                }
            }
        }
        if (containsTables(model.getTableName()) == false) {
            table.setVersionField(null);
        }
        if (table.hasVersion()) {
            Object value = model.getVersion();
            if (null == value) {
                throw new RuntimeException("Version is request.");
            }
            table.putCondition(version, value);
            model.getContent().remove(Model.VERSION);
        }

        int result = _updateByPrimaryKey(table);

        return result;
    }


    protected abstract int _updateByPrimaryKey(Table table);

    @Override
    public int deleteByPrimaryKey(Object id, Class<? extends IEntity> clazz) {
        String tableName = DBTableUtil.loadTableName(clazz);
        Model model = new Model(tableName);
        return deleteByPrimaryKey(model, id);
    }

    @Override
    public int deleteByPrimaryKey(Object id, String table) {
        Model model = new Model(table);
        return deleteByPrimaryKey(model, id);
    }


    private int deleteByPrimaryKey(Model model, Object primaryKeyValue) {
        Table table = retrievalTable(model.getDatabase(), model.getTableName());
        if (table.getPrimaryKey().getFields().size() == 1) {
            table.putCondition(table.getPrimaryKey().getFields().get(0), primaryKeyValue);
        }

        int result = _deleteByPrimaryKey(table);

        return result;
    }

    protected abstract int _deleteByPrimaryKey(Table table);


    @Override
    public int deleteByCriteria(QueryCriteria queryCriteria) {
        Table table = retrievalTable(queryCriteria.getDatabase(), queryCriteria.getTable());
        table.setQueryCriteria(queryCriteria);
        int result = _deleteByCriteria(table);
        return result;
    }

    protected abstract int _deleteByCriteria(Table table);


    @Override
    public QueryResult selectByCriteria(QueryCriteria queryCriteria) {
        List<String> fields = null;
        return selectByCriteria(queryCriteria, fields);
    }


    public void setResolveDatabase(ResolveDataBase resolveDatabase) {
        this.resolveDatabase = resolveDatabase;
    }


    public void reloadTable(String tableName) {
        reloadTable(null, tableName);
    }


    public void reloadTable(String database, String tableName) {
        resolveDatabase.reloadTable(database, tableName);
    }


    public void setVersion(String version) {
        this.version = version;
    }

    public void setVersionTables(List<String> versionTables) {
        this.versionTables = versionTables;
    }

    public boolean containsTables(String table) {
        if (versionTables == null) {
            return false;
        }
        return versionTables.contains(table);
    }

}
