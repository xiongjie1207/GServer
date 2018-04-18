package com.gserver.plugins.db.jdbc.template;
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

import com.gserver.plugins.db.criteria.Criteria;
import com.gserver.plugins.db.criteria.Criterion;
import com.gserver.plugins.db.criteria.QueryCriteria;
import com.gserver.plugins.db.descriptor.Table;
import com.gserver.plugins.db.jdbc.SQL;
import com.gserver.utils.db.ColumnWrapperUtils;
import com.gserver.utils.db.VersionWrapperUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


public abstract class AbstractSqlTemplate {

    /**
     * 根据查询条件成生sql
     *
     * @param sql   buildSql
     * @param table table
     */
    protected void caculationQueryCriteria(SQL sql, Table table) {
        List<Criteria> criterias = table.getQueryCriteria().getCriterias();
        for (Criteria criteria : criterias) {
            sql.OR();
            if (criteria.isValid()) {
                for (Criterion criterion : criteria.getCriterions()) {
                    sql.AND();
                    sql.WHERE(convertCondition(criterion, table));
                }
            }
        }
    }

    protected void caculationPrimaryKey(SQL sql, Table table) {
        List<String> names = table.getPrimaryKey().getFields();
        for (String name : names) {
            sql.AND();
            sql.WHERE(buildSingleParamSql(name, "="));
        }
    }

    /**
     * 根据不同条件拼装sql
     *
     * @param criterion criterion
     * @param table     table
     * @return condition
     */
    protected String convertCondition(Criterion criterion, Table table) {

        String conditionStr = "";
        if (null != criterion.getCondition()) {
            switch (criterion.getCondition()) {
                case IS_NULL:
                    conditionStr = ColumnWrapperUtils.columnWrap(criterion.getColumn()) + " is null ";
                    break;
                case IS_NOT_NULL:
                    conditionStr = ColumnWrapperUtils.columnWrap(criterion.getColumn()) + " is not null ";
                    break;
                case EQUAL:
                    conditionStr = buildSingleParamSql(criterion.getColumn(), "=");
                    table.putCondition(criterion.getColumn(), criterion.getValue());
                    break;
                case NOT_EQUAL:
                    conditionStr = buildSingleParamSql(criterion.getColumn(), "<>");
                    table.putCondition(criterion.getColumn(), criterion.getValue());
                    break;
                case GREATER_THAN:
                    conditionStr = buildSingleParamSql(criterion.getColumn(), ">");
                    table.putCondition(criterion.getColumn(), criterion.getValue());
                    break;
                case GREATER_THAN_OR_EQUAL:
                    conditionStr = buildSingleParamSql(criterion.getColumn(), ">=");
                    table.putCondition(criterion.getColumn(), criterion.getValue());
                    break;
                case LESS_THAN:
                    conditionStr = buildSingleParamSql(criterion.getColumn(), "<");
                    table.putCondition(criterion.getColumn(), criterion.getValue());
                    break;
                case LESS_THAN_OR_EQUAL:
                    conditionStr = buildSingleParamSql(criterion.getColumn(), "<=");
                    table.putCondition(criterion.getColumn(), criterion.getValue());
                    break;
                case IN: {
                    @SuppressWarnings("unchecked")
                    List<Object> values = (List<Object>) criterion.getValue();
                    if (values.size() > 0) {
                        table.putCondition(criterion.getColumn(), criterion.getValue());
                        conditionStr = buildListParamSql(criterion.getColumn(), table, "in");
                    }
                }

                break;
                case NOT_IN: {
                    @SuppressWarnings("unchecked")
                    List<Object> values = (List<Object>) criterion.getValue();
                    if (values.size() > 0) {
                        table.putCondition(criterion.getColumn(), criterion.getValue());
                        conditionStr = buildListParamSql(criterion.getColumn(), table, "not in");
                    }
                }
                break;
                case BETWEEN:
                    conditionStr = buildBetweenParamSql(criterion.getColumn(), "between");
                    table.putCondition(criterion.getColumn(), criterion.getValue());
                    table.putCondition(criterion.getColumn(), criterion.getSecondValue());
                    break;
                case NOT_BETWEEN:
                    conditionStr = buildBetweenParamSql(criterion.getColumn(), "not between");
                    table.putCondition(criterion.getColumn(), criterion.getValue());
                    table.putCondition(criterion.getColumn(), criterion.getSecondValue());
                    break;
                case LIKE:
                    conditionStr = buildSingleParamSql(criterion.getColumn(), "like");
                    table.putCondition(criterion.getColumn(), "%" + criterion.getValue() + "%");
                    break;
                case NOT_LIKE:
                    conditionStr = buildSingleParamSql(criterion.getColumn(), "not like");
                    table.putCondition(criterion.getColumn(), criterion.getValue());
                    break;
            }
        } else {
            if (StringUtils.isNotEmpty(criterion.getColumn())) {
                conditionStr += criterion.getColumn();
            }
        }
        return conditionStr;
    }

    protected abstract String buildSingleParamSql(String column, String keyword);

    protected abstract String buildBetweenParamSql(String column, String keyword);

    protected abstract String buildListParamSql(String column, Table model, String keyword);


    //--------------------------------------
    // delete
    //--------------------------------------
    public String deleteByCriteria(Table table) {
        table.resetQueryConditions();
        SQL sql = new SQL();
        sql.DELETE_FROM(ColumnWrapperUtils.columnWrap(table.getTableName()));
        QueryCriteria queryCriteria = table.getQueryCriteria();

        if (queryCriteria.getCriterias() != null && queryCriteria.getCriterias().size() > 0) {
            caculationQueryCriteria(sql, table);
        }
        table.resetQueryCriteria();
        String sqlStr = sql.toString();


        return sqlStr;
    }


    public String deleteByPrimaryKey(Table table) {
        SQL sql = new SQL();
        sql.DELETE_FROM(ColumnWrapperUtils.columnWrap(table.getTableName()));
        caculationPrimaryKey(sql, table);
        String sqlStr = sql.toString();

        return sqlStr;
    }

    //--------------------------------------
    // insert
    //--------------------------------------
    public String insert(Table table) {
        SQL sql = new SQL();
        sql.INSERT_INTO(ColumnWrapperUtils.columnWrap(table.getTableName()));
        LinkedHashMap<String, Object> params = table.getQueryFields();
        for (String key : params.keySet()) {
            if (table.getColumns().containsKey(key)) {
                if (params.get(key) != null) {
                    sql.VALUES(ColumnWrapperUtils.columnWrap(key), buildSingleParamSql(key, null));
                }
            }

        }
        String sqlStr = sql.toString();


        return sqlStr;
    }

    //--------------------------------------
    // select
    //--------------------------------------
    public String selectByCriteria(Table table) {
        SQL sql = new SQL();
        table.resetQueryConditions();
        QueryCriteria queryCriteria = table.getQueryCriteria();
        //拼装select
        String selectFields = caculationSelectField(table);
        if (queryCriteria.getDistinct()) {
            if (StringUtils.isNotEmpty(selectFields)) {
                sql.SELECT_DISTINCT(selectFields);
            } else {
                sql.SELECT_DISTINCT(table.getAllSelectFields());
            }
        } else {
            if (StringUtils.isNotEmpty(selectFields)) {
                sql.SELECT(selectFields);
            } else {
                sql.SELECT(table.getAllSelectFields());
            }
        }
        sql.FROM(ColumnWrapperUtils.columnWrap(table.getTableName()));
        //拼装where
        caculationQueryCriteria(sql, table);

        if (StringUtils.isNotEmpty(queryCriteria.getOrderByClause())) {
            sql.ORDER_BY(queryCriteria.getOrderByClause());
        }
        if (StringUtils.isNotBlank(table.getQueryCriteria().getGroupBy())) {
            sql.GROUP_BY(table.getQueryCriteria().getGroupBy());
        }
        if (queryCriteria.getSelectOne()) {

            return sql.toString() + " limit 0,1";
        }

        if (queryCriteria.getRowCount() > 0) {

            return sql.toString() + " limit " + queryCriteria.getLimitStart() + "," + queryCriteria.getRowCount();
        }
        table.resetQueryCriteria();
        table.resetQueryFields();
        String sqlStr = sql.toString();


        return sqlStr;
    }

    /**
     * 计算自定义定段，
     * 已经过排序和字段隐藏处理
     *
     * @param table table
     * @return fields string
     */
    private String caculationSelectField(Table table) {
        List<String> fields = new ArrayList<>();
        for (String key : table.getQueryFields().keySet()) {
            fields.add(ColumnWrapperUtils.columnWrap(key));
        }
        return StringUtils.join(fields.toArray(), ",");
    }


    public String selectByPrimaryKey(Table table) {
        SQL sql = new SQL();
        String selectFields = caculationSelectField(table);
        if (StringUtils.isNotEmpty(selectFields)) {
            sql.SELECT(selectFields);
        } else {
            sql.SELECT(table.getAllSelectFields());
        }
        sql.FROM(ColumnWrapperUtils.columnWrap(table.getTableName()));
        caculationPrimaryKey(sql, table);
        String sqlStr = sql.toString() + " for update";


        return sqlStr;
    }

    public String countByCriteria(Table table) {
        SQL sql = new SQL();
        table.resetQueryConditions();
        QueryCriteria queryCriteria = table.getQueryCriteria();
        if (queryCriteria.getDistinct()) {
            String customFields = caculationSelectField(table);
            sql.SELECT(" count(distinct " + customFields + ") ");
        } else {
            sql.SELECT(" count(1) ");
        }
        sql.FROM(ColumnWrapperUtils.columnWrap(table.getTableName()));

        if (queryCriteria.getCriterias() != null && queryCriteria.getCriterias().size() > 0) {
            caculationQueryCriteria(sql, table);
        }
        table.resetQueryCriteria();
        String sqlStr = sql.toString();


        return sqlStr;
    }

    //--------------------------------------
    // update
    //--------------------------------------
    public String updateByCriteria(Table table) {
        table.resetQueryConditions();
        SQL sql = new SQL();
        sql.UPDATE(ColumnWrapperUtils.columnWrap(table.getTableName()));
        Iterator<String> iter = table.getQueryFields().keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (!table.getPrimaryKey().getFields().contains(key)) {
                sql.SET(buildSingleParamSql(key, "="));
            }
        }

        if (table.hasVersion()) {
            sql.SET(VersionWrapperUtils.wrapSetSql(table.getVersion()));
        }
        QueryCriteria queryCriteria = table.getQueryCriteria();
        if (queryCriteria.getCriterias() != null && queryCriteria.getCriterias().size() > 0) {
            caculationQueryCriteria(sql, table);
        }
        if (table.hasVersion()) {
            Object value = queryCriteria.getVersion();
            if (null == value) {
                throw new RuntimeException("Version is request.");
            }
            sql.AND();
            sql.WHERE(VersionWrapperUtils.wrapWhereSql(table.getVersion(), value));
        }
        table.resetQueryCriteria();
        String sqlStr = sql.toString();


        return sqlStr;
    }


    public String updateByPrimaryKey(Table table) {
        SQL sql = new SQL();
        sql.UPDATE(ColumnWrapperUtils.columnWrap(table.getTableName()));
        Iterator<String> iter = table.getQueryFields().keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (!table.getPrimaryKey().getFields().contains(key)) {
                sql.SET(buildSingleParamSql(key, "="));

            }
        }

        if (table.hasVersion()) {
            sql.SET(VersionWrapperUtils.wrapSetSql(table.getVersion()));
        }
        caculationPrimaryKey(sql, table);
        if (table.hasVersion()) {
            Object value = table.getConditionValue(table.getVersion().getName());
            if (null == value) {
                throw new RuntimeException("Version is request.");
            }
            sql.AND();
            sql.WHERE(VersionWrapperUtils.wrapWhereSql(table.getVersion(), value));
        }
        String sqlStr = sql.toString();


        return sqlStr;
    }
}
