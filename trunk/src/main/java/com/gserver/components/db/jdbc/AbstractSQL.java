package com.gserver.components.db.jdbc;
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

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSQL<T> {
    private boolean forUpdate = false;
    private static final String AND = " \nAND ";
    private static final String OR = ") \nOR (";
    private StatementType statementType;
    private List<String> sets = new ArrayList<String>();
    private List<String> select = new ArrayList<String>();
    private List<String> tables = new ArrayList<String>();
    private List<String> where = new ArrayList<String>();
    private List<String> having = new ArrayList<String>();
    private List<String> groupBy = new ArrayList<String>();
    private List<String> orderBy = new ArrayList<String>();
    private List<String> lastList = new ArrayList<String>();
    private List<String> columns = new ArrayList<String>();
    private List<String> values = new ArrayList<String>();
    boolean distinct;

    public abstract T getSelf();

    public T UPDATE(String table) {
        this.statementType = StatementType.UPDATE;
        this.tables.add(table);
        return getSelf();
    }

    public boolean isForUpdate() {
        return forUpdate;
    }

    public void setForUpdate(boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    public T SET(String sets) {
        this.sets.add(sets);
        return getSelf();
    }

    public T INSERT_INTO(String tableName) {
        this.statementType = StatementType.INSERT;
        this.tables.add(tableName);
        return getSelf();
    }

    public T VALUES(String columns, String values) {
        this.columns.add(columns);
        this.values.add(values);
        return getSelf();
    }

    public T SELECT(String columns) {
        this.statementType = StatementType.SELECT;
        this.select.add(columns);
        return getSelf();
    }

    public T SELECT_DISTINCT(String columns) {
        this.distinct = true;
        SELECT(columns);
        return getSelf();
    }

    public T DELETE_FROM(String table) {
        this.statementType = StatementType.DELETE;
        this.tables.add(table);
        return getSelf();
    }

    public T FROM(String table) {
        this.tables.add(table);
        return getSelf();
    }


    public T WHERE(String conditions) {
        this.where.add(conditions);
        this.lastList = this.where;
        return getSelf();
    }

    public T OR() {
        this.lastList.add(OR);
        return getSelf();
    }

    public T AND() {
        if (lastList.size() > 0) {
            String condition = lastList.get(lastList.size() - 1);
            if (!condition.equals(OR) && !condition.equals(AND)) {
                this.lastList.add(AND);
            }
        }

        return getSelf();
    }

    public T GROUP_BY(String columns) {
        this.groupBy.add(columns);
        return getSelf();
    }

    public T HAVING(String conditions) {
        this.having.add(conditions);
        this.lastList = this.having;
        return getSelf();
    }

    public T ORDER_BY(String columns) {
        this.orderBy.add(columns);
        return getSelf();
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (statementType == null) {
            return builder.toString();
        }
        if (StatementType.DELETE.equals(statementType)) {
            buildDeleteSQL(builder);
        } else if (StatementType.INSERT.equals(statementType)) {
            buildInsertSQL(builder);
        } else if (StatementType.SELECT.equals(statementType)) {
            buildSelectSQL(builder);
        } else if (StatementType.UPDATE.equals(statementType)) {
            buildUpdateSQL(builder);
        }
        return builder.toString();
    }


    public enum StatementType {
        DELETE, INSERT, SELECT, UPDATE
    }


    private void sqlClause(StringBuilder builder, String keyword, List<String> parts, String open, String close) {
        if (parts.isEmpty()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append("\n");
        }
        builder.append(keyword);
        if (parts == null) {
            return;
        }
        builder.append(" ");
        builder.append(open);
        int i = 0;
        for (String part : parts) {
            if (i > 0) {
                if (!keyword.equals("WHERE")) {
                    builder.append(",");
                }
            }
            ++i;
            builder.append(part);

        }
        builder.append(close);

    }

    private void buildSelectSQL(StringBuilder builder) {
        if (distinct) {
            sqlClause(builder, "SELECT DISTINCT", select, "", "");
        } else {
            sqlClause(builder, "SELECT", select, "", "");
        }

        sqlClause(builder, "FROM", tables, "", "");
        sqlClause(builder, "WHERE", where, "(", ")");
        sqlClause(builder, "GROUP BY", groupBy, "", "");
        sqlClause(builder, "HAVING", having, "(", ")");
        sqlClause(builder, "ORDER BY", orderBy, "", "");
        if (forUpdate) {
            sqlClause(builder, "FOR UPDATE", null
                    , "", "");
        }

    }

    private void buildInsertSQL(StringBuilder builder) {
        sqlClause(builder, "INSERT INTO", tables, "", "");
        sqlClause(builder, "", columns, "(", ")");
        sqlClause(builder, "VALUES", values, "(", ")");

    }

    private void buildDeleteSQL(StringBuilder builder) {
        sqlClause(builder, "DELETE FROM", tables, "", "");
        sqlClause(builder, "WHERE", where, "(", ")");

    }

    private void buildUpdateSQL(StringBuilder builder) {

        sqlClause(builder, "UPDATE", tables, "", "");
        sqlClause(builder, "SET", sets, "", "");
        sqlClause(builder, "WHERE", where, "(", ")");
    }


}
