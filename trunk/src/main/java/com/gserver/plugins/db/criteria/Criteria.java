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

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Criteria {
    protected List<Criterion> criterions;
    private String column = null;

    protected Criteria() {
        criterions = new ArrayList<Criterion>();
    }

    public boolean isValid() {
        return criterions.size() > 0;
    }


    public List<Criterion> getCriterions() {
        return criterions;
    }

    protected void addCriterion(String sql) {
        if (StringUtils.isEmpty(sql)) {
            throw new RuntimeException("Sql cannot be null");
        }
        criterions.add(new Criterion(sql));
    }

    protected void addCriterion(Condition condition, String column) {
        if (condition == null || StringUtils.isEmpty(column)) {
            throw new RuntimeException("Column for condition cannot be null");
        }
        criterions.add(new Criterion(condition, column));
    }

    protected void addCriterion(Condition condition, Object value, String typeHandler, String column) {
        if (value == null || condition == null || StringUtils.isEmpty(column)) {
            throw new RuntimeException("Value for condition cannot be null");
        }
        criterions.add(new Criterion(condition, value, typeHandler, column));
    }

    protected void addCriterion(Condition condition, Object value1, Object value2, String typeHandler, String column) {
        if (value1 == null || value2 == null) {
            throw new RuntimeException("Between values for " + column + " cannot be null");
        }
        criterions.add(new Criterion(condition, value1, value2, typeHandler, column));
    }

    public Criteria andColumnSql(String sql) {
        addCriterion(sql);
        return this;
    }

    public Criteria andColumn(String column) {
        if (this.column != null) {
            throw new RuntimeException("Column has been initialized");
        }
        this.column = column;
        return this;
    }

    public Criteria isNull() {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnIsNull(column);
        return this;
    }

    public Criteria isNotNull() {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnIsNotNull(column);
        return this;
    }

    public Criteria equalTo(Object value) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnEqualTo(column, value);
        return this;
    }

    public Criteria notEqualTo(Object value) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnNotEqualTo(column, value);
        return this;
    }

    public Criteria greaterThan(Object value) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnGreaterThan(column, value);
        return this;
    }

    public Criteria greaterThanOrEqualTo(Object value) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnGreaterThanOrEqualTo(column, value);
        return this;
    }

    public Criteria lessThan(Object value) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnLessThan(column, value);
        return this;
    }

    public Criteria lessThanOrEqualTo(Object value) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnLessThanOrEqualTo(column, value);
        return this;
    }

    public Criteria in(List<?> values) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnIn(column, values);
        return this;
    }

    public Criteria notIn(List<?> values) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnNotIn(column, values);
        return this;
    }

    public Criteria like(Object value) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnLike(column, value);
        return this;
    }

    public Criteria notLike(Object value) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnNotLike(column, value);
        return this;
    }

    public Criteria between(Object value1, Object value2) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnBetween(column, value1, value2);
        return this;
    }

    public Criteria notBetween(Object value1, Object value2) {
        if (column == null) {
            throw new RuntimeException("Column cannot be null");
        }
        andColumnNotBetween(column, value1, value2);
        return this;
    }

    public Criteria andColumnIsNull(String column) {
        addCriterion(Condition.IS_NULL, column);
        this.column = null;
        return this;
    }

    public Criteria andColumnIsNotNull(String column) {
        addCriterion(Condition.IS_NOT_NULL, column);
        this.column = null;
        return this;
    }

    public Criteria andColumnEqualTo(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.EQUAL, value, value.getClass().getName(), column);
        }
        this.column = null;
        return this;
    }

    public Criteria andColumnNotEqualTo(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.NOT_EQUAL, value, value.getClass().getName(), column);
        }
        this.column = null;
        return this;
    }

    public Criteria andColumnGreaterThan(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.GREATER_THAN, value, value.getClass().getName(), column);
        }
        this.column = null;
        return this;
    }

    public Criteria andColumnGreaterThanOrEqualTo(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.GREATER_THAN_OR_EQUAL, value, value.getClass().getName(), column);
        }
        this.column = null;
        return this;
    }

    public Criteria andColumnLessThan(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.LESS_THAN, value, value.getClass().getName(), column);
        }
        this.column = null;
        return this;
    }

    public Criteria andColumnLessThanOrEqualTo(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.LESS_THAN_OR_EQUAL, value, value.getClass().getName(), column);
        }
        this.column = null;
        return this;
    }

    public Criteria andColumnIn(String column, List<?> values) {
        if (null != values && values.size() > 0) {
            addCriterion(Condition.IN, values, values.getClass().getName(), column);
        }
        this.column = null;
        return this;
    }

    public Criteria andColumnNotIn(String column, List<?> values) {
        if (null != values && values.size() > 0) {
            addCriterion(Condition.NOT_IN, values, values.getClass().getName(), column);
        }
        this.column = null;
        return this;
    }

    public Criteria andColumnLike(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.LIKE, value, value.getClass().getName(), column);
        }
        this.column = null;
        return this;
    }

    public Criteria andColumnNotLike(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.NOT_LIKE, value, value.getClass().getName(), column);
        }
        this.column = null;
        return this;
    }

    public Criteria andColumnBetween(String column, Object value1, Object value2) {
        addCriterion(Condition.BETWEEN, value1, value2, value1.getClass().getName(), column);
        this.column = null;
        return this;
    }

    public Criteria andColumnNotBetween(String column, Object value1, Object value2) {
        addCriterion(Condition.NOT_BETWEEN, value1, value2, value1.getClass().getName(), column);
        this.column = null;
        return this;
    }

    public String toString() {
        if (null != criterions) {
            return criterions.toString();
        }
        return null;
    }
}