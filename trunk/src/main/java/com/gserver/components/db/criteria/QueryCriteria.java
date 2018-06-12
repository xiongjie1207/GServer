package com.gserver.components.db.criteria;
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
import com.gserver.components.db.descriptor.IEntity;
import com.gserver.utils.db.DBTableUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class QueryCriteria {

    private String database;

    private String table;

    private String orderByClause;

    private String groupBy;

    private boolean distinct;

    private List<Criteria> criterias;

    private boolean selectOne;


    private int limitStart = 0;

    private int rowCount;

    private Object version;

    public QueryCriteria(String table) {
        criterias = new ArrayList<Criteria>();
        this.setTable(table);
    }
    public QueryCriteria(Class<? extends IEntity> clazz){
        this(DBTableUtil.loadTableName(clazz));
    }
    public QueryCriteria clone() {
        QueryCriteria clone = new QueryCriteria(table);
        clone.setDatabase(database);
        clone.setTable(table);
        clone.setOrderByClause(orderByClause);
        clone.setGroupBy(groupBy);
        clone.setDistinct(distinct);
        clone.setOrderByClause(orderByClause);
        clone.setSelectOne(selectOne);
        clone.setLimitStart(limitStart);
        clone.setRowCount(rowCount);
        clone.setVersion(version);
        return clone;
    }

    public void setOrderByClause(String...orderByClause) {
        this.orderByClause = StringUtils.join(orderByClause, ",");
    }
    public void setOrderByClauseDesc(String...orderByClause) {
        this.orderByClause = StringUtils.join(orderByClause, " desc,")+" desc";
    }
    public String getOrderByClause() {
        return orderByClause;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean getDistinct() {
        return distinct;
    }


    public String getTable() {
        return table;
    }

    private void setTable(String table) {
        this.table = table.trim();
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Object getVersion() {
        return version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setTable(Class<?> clazz) {
        String name = clazz.getSimpleName();
        this.table = name.trim();
    }

    public List<Criteria> getCriterias() {
        return criterias;
    }

    public void or(Criteria criteria) {
        criterias.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        or(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (criterias.size() == 0) {
            criterias.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        criterias.clear();
        orderByClause = null;
        distinct = false;
    }

    public void setSelectOne(boolean selectOne) {
        this.selectOne = selectOne;
    }

    public boolean getSelectOne() {
        return this.selectOne;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderByClause == null) ? 0 : orderByClause.hashCode());
        result = prime * result + ((database == null) ? 0 : database.hashCode());
        result = prime * result + ((table == null) ? 0 : table.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result + ((groupBy == null) ? 0 : groupBy.hashCode());
        result = prime * result + (distinct ? 1231 : 1237);
        result = prime * result + (selectOne ? 1231 : 1237);
        result = prime * result + limitStart + rowCount;
        for (Criteria criteria : criterias) {
            for (Criterion cter : criteria.getCriterions()) {
                result = prime * result + ((cter == null) ? 0 : cter.hashCode());
            }
        }
        return result;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (StringUtils.isNotEmpty(database)) {
            sb.append("database:").append(database).append(",");
        }
        if (StringUtils.isNotEmpty(table)) {
            sb.append("table:").append(table).append(",");
        }
        if (StringUtils.isNotEmpty(orderByClause)) {
            sb.append("orderByClause:").append(orderByClause).append(",");
        }
        if (StringUtils.isNotEmpty(groupBy)) {
            sb.append("groupBy:").append(groupBy).append(",");
        }
        sb.append("distinct:").append(distinct).append(",");
        if (null != criterias) {
            sb.append("criterias:").append(criterias).append(",");
        }
        sb.append("selectOne:").append(selectOne).append(",");
        sb.append("limitStart:").append(limitStart).append(",");
        if (null != version) {
            sb.append("version:").append(version).append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));

        return sb.toString();
    }


}