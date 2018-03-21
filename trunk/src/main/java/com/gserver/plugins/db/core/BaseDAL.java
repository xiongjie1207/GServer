package com.gserver.plugins.db.core;
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

import com.gserver.plugins.db.criteria.QueryCriteria;
import com.gserver.plugins.db.descriptor.IEntity;
import com.gserver.plugins.db.descriptor.QueryResult;
import com.gserver.plugins.db.descriptor.ResolveDataBase;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public interface BaseDAL {

    //-------------------------
    // selectByCriteria
    //-------------------------

    /**
     *
     * @param fields e.g."id,name,password"
     * @param queryCriteria
     * @return
     */
    QueryResult selectByCriteria(String fields, QueryCriteria queryCriteria);

    QueryResult selectByCriteria(List<String> fields, QueryCriteria queryCriteria);

    QueryResult selectByCriteria(String[] fields, QueryCriteria queryCriteria);


    QueryResult selectByCriteria(QueryCriteria queryCriteria);


    //-------------------------
    // countByCriteria
    //-------------------------

    int countByCriteria(QueryCriteria queryCriteria);


    //-------------------------
    // selectByPrimaryKey
    //-------------------------

    QueryResult selectByPrimaryKey(Class<? extends IEntity> clazz, Object id);

    QueryResult selectByPrimaryKey(String tableName, Object id);


    QueryResult selectByPrimaryKey(List<String> fields, Class<? extends IEntity> clazz, Object id);

    QueryResult selectByPrimaryKey(List<String> fields, String tableName, Object id);


    QueryResult selectByPrimaryKey(String[] fields, Class<? extends IEntity> clazz, Object id);

    QueryResult selectByPrimaryKey(String[] fields, String tableName, Object id);


    //-------------------------
    // insert
    //-------------------------
    long insert(IEntity obj);

    long insert(String table, Map<String, Object> obj);

    long insert(String database, String table, Map<String, Object> obj);

    void asynInsert(IEntity obj);

    void asynInsert(String table, Map<String, Object> obj);


    //-------------------------
    // update
    //-------------------------
    int updateByCriteria(IEntity obj, QueryCriteria queryCriteria);

    int updateByCriteria(Map<String, Object> obj, QueryCriteria queryCriteria);

    int updateByPrimaryKey(IEntity obj);

    int updateByPrimaryKey(String table, Map<String, Object> obj, Object id);

    //-------------------------
    // delete
    //-------------------------

    int deleteByPrimaryKey(String table, Object id);

    int deleteByPrimaryKey(Class<? extends IEntity> clazz, Object id);


    int deleteByCriteria(QueryCriteria queryCriteria);

    void reloadTable(String tableName);

    void setResolveDatabase(ResolveDataBase resolveDatabase);

    void reloadTable(String database, String tableName);

    JdbcTemplate getJdbcTemplate();

}
