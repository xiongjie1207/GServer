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

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Criteria {
    protected List<Criterion> criterions;

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

    public Column andColumn(String column) {
        return new Column(column);
    }


    public Criteria andColumnIsNull(String column) {
        addCriterion(Condition.IS_NULL, column);
        return this;
    }

    public Criteria andColumnIsNotNull(String column) {
        addCriterion(Condition.IS_NOT_NULL, column);
        return this;
    }

    public Criteria andColumnEqualTo(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.EQUAL, value, value.getClass().getName(), column);
        }
        return this;
    }

    public Criteria andColumnNotEqualTo(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.NOT_EQUAL, value, value.getClass().getName(), column);
        }
        return this;
    }

    public Criteria andColumnGreaterThan(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.GREATER_THAN, value, value.getClass().getName(), column);
        }
        return this;
    }

    public Criteria andColumnGreaterThanOrEqualTo(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.GREATER_THAN_OR_EQUAL, value, value.getClass().getName(), column);
        }
        return this;
    }

    public Criteria andColumnLessThan(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.LESS_THAN, value, value.getClass().getName(), column);
        }
        return this;
    }

    public Criteria andColumnLessThanOrEqualTo(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.LESS_THAN_OR_EQUAL, value, value.getClass().getName(), column);
        }
        return this;
    }

    public Criteria andColumnIn(String column, List<?> values) {
        if (null != values && values.size() > 0) {
            addCriterion(Condition.IN, values, values.getClass().getName(), column);
        }
        return this;
    }

    public Criteria andColumnNotIn(String column, List<?> values) {
        if (null != values && values.size() > 0) {
            addCriterion(Condition.NOT_IN, values, values.getClass().getName(), column);
        }
        return this;
    }

    public Criteria andColumnLike(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.LIKE, value, value.getClass().getName(), column);
        }
        return this;
    }

    public Criteria andColumnNotLike(String column, Object value) {
        if (null != value) {
            addCriterion(Condition.NOT_LIKE, value, value.getClass().getName(), column);
        }
        return this;
    }

    public Criteria andColumnBetween(String column, Object value1, Object value2) {
        addCriterion(Condition.BETWEEN, value1, value2, value1.getClass().getName(), column);
        return this;
    }

    public Criteria andColumnNotBetween(String column, Object value1, Object value2) {
        addCriterion(Condition.NOT_BETWEEN, value1, value2, value1.getClass().getName(), column);
        return this;
    }

    public String toString() {
        if (null != criterions) {
            return criterions.toString();
        }
        return null;
    }

    public class Column {
        private String name;

        private Column(String name) {
            this.name = name;
        }

        public Criteria isNull() {
            Criteria.this.andColumnIsNull(name);
            return Criteria.this;
        }

        public Criteria isNotNull() {
            Criteria.this.andColumnIsNotNull(name);
            return Criteria.this;
        }

        public Criteria equalTo(Object value) {
            Criteria.this.andColumnEqualTo(name, value);
            return Criteria.this;
        }

        public Criteria notEqualTo(Object value) {
            Criteria.this.andColumnNotEqualTo(name, value);
            return Criteria.this;
        }

        public Criteria greaterThan(Object value) {
            Criteria.this.andColumnGreaterThan(name, value);
            return Criteria.this;
        }

        public Criteria greaterThanOrEqualTo(Object value) {
            Criteria.this.andColumnGreaterThanOrEqualTo(name, value);
            return Criteria.this;
        }

        public Criteria lessThan(Object value) {
            Criteria.this.andColumnLessThan(name, value);
            return Criteria.this;
        }

        public Criteria lessThanOrEqualTo(Object value) {
            Criteria.this.andColumnLessThanOrEqualTo(name, value);
            return Criteria.this;
        }

        public Criteria in(List<?> values) {
            Criteria.this.andColumnIn(name, values);
            return Criteria.this;
        }

        public Criteria notIn(List<?> values) {
            Criteria.this.andColumnNotIn(name, values);
            return Criteria.this;
        }

        public Criteria like(Object value) {
            Criteria.this.andColumnLike(name, value);
            return Criteria.this;
        }

        public Criteria notLike(Object value) {
            Criteria.this.andColumnNotLike(name, value);
            return Criteria.this;
        }

        public Criteria between(Object value1, Object value2) {
            Criteria.this.andColumnBetween(name, value1, value2);
            return Criteria.this;
        }

        public Criteria notBetween(Object value1, Object value2) {
            Criteria.this.andColumnNotBetween(name, value1, value2);
            return Criteria.this;
        }
    }

}