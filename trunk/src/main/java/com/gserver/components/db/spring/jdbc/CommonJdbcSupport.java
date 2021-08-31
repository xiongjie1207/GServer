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

import com.gserver.components.db.descriptor.JavaType;
import com.gserver.components.db.descriptor.JavaTypeConversion;
import com.gserver.components.db.descriptor.JavaTypeResolver;
import com.gserver.components.db.descriptor.Table;
import com.gserver.components.db.spring.jdbc.model.ModifyParams;
import com.gserver.components.db.spring.jdbc.template.SqlTemplate;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.gserver.components.db.descriptor.JavaType.DATE;

class CommonJdbcSupport extends JdbcDaoSupport {


    public List<Map<String, Object>> selectByCriteria(final Table table) {
        SqlTemplate sqlTemplate = new SqlTemplate();
        final String sql = sqlTemplate.selectByCriteria(table);
        final List<Object> param = buildParameters(table);

        logger.debug(ArrayUtils.toString(param.toArray()));
        List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql, param.toArray());
        return result;

    }

    private List<Object> buildParameters(Table table) {

        ArrayList<Object> params = new ArrayList<Object>();
        for (Object value : table.getConditions().values()) {
            if (value instanceof List) {
                params.addAll((List) value);
            } else {
                params.add(value);
            }
        }
        return params;
    }

    private List<Object> buildPrimaryKeyParameters(Table table) {
        List<Object> params = new ArrayList<>();
        for (String item : table.getPrimaryKey().getFields()) {
            for (StringBuilder key : table.getConditions().keySet()) {
                if (key.toString().equals(item)) {
                    params.add(table.getConditions().get(key));
                }
            }

        }
        return params;
    }

    public int countByCriteria(final Table table) {
        SqlTemplate sqlTemplate = new SqlTemplate();
        final String sql = sqlTemplate.countByCriteria(table);
        List<Object> param = buildParameters(table);

        logger.debug(ArrayUtils.toString(param.toArray()));
        return getJdbcTemplate().queryForObject(sql, param.toArray(), Integer.class);
    }

    public List<Map<String, Object>> executeBySql(final String sql) {
        List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql);
        return result;
    }

    public Map<String, Object> selectByPrimaryKey(boolean forUpdate,final Table table) {
        SqlTemplate sqlTemplate = new SqlTemplate();
        String sql = sqlTemplate.selectByPrimaryKey(table);
        if(forUpdate){
            sql+=" FOR UPDATE";
        }
        List<Object> param = buildPrimaryKeyParameters(table);

        logger.debug(ArrayUtils.toString(param.toArray()));
        List<Map<String, Object>> result = getJdbcTemplate().queryForList(sql, param.toArray());
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public long insert(final Table table) {
        try {
            SqlTemplate sqlTemplate = new SqlTemplate();
            final String sql = sqlTemplate.insert(table);
            final ModifyParams modifyParams = buildInsertParameters(table);

            logger.debug(ArrayUtils.toString(modifyParams.getParamsValue().toArray()));
            if (table.getPrimaryKey().getFields().size() > 0) {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                this.getJdbcTemplate().update((con) -> createPreparedStatement(con, sql, modifyParams, table.getPrimaryKey().getFields()), keyHolder);
                if (keyHolder.getKey() != null) {
                    return keyHolder.getKey().longValue();
                }
            } else {
                this.getJdbcTemplate().update((con) -> createPreparedStatement(con, sql, modifyParams, table.getPrimaryKey().getFields()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private PreparedStatement createPreparedStatement(Connection con, String sql, ModifyParams modifyParams, List<String> returnFields) throws SQLException {
        PreparedStatement ps;
        if (returnFields.size() > 0) {
            ps = con.prepareStatement(sql, new String[]{returnFields.get(0)});
        } else {
            ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        }
        for (int i = 1; i <= modifyParams.getParamsValue().size(); i++) {
            JavaType type = JavaTypeResolver.calculateJavaType(modifyParams.getParamsType().get(i - 1));
            String value = String.valueOf(modifyParams.getParamsValue().get(i - 1));
            switch (type) {
                case STRING:
                    ps.setString(i, value);
                    break;
                case BOOLEAN:
                    ps.setBoolean(i, Boolean.valueOf(value));
                    break;
                case BYTE:
                    ps.setString(i, value);
                    break;
                case CHARACTER:
                    ps.setString(i, value);
                    break;
                case DATE:
                    ps.setTimestamp(i, JavaTypeConversion.convert(DATE, value, Timestamp.class));
                    break;
                case DOUBLE:
                    ps.setDouble(i, Double.valueOf(value));
                    break;
                case FLOAT:
                    ps.setFloat(i, Float.valueOf(value));
                    break;
                case INTEGER:
                    String[] vals = value.split("\\.");
                    ps.setInt(i, Integer.valueOf(vals[0]));
                    break;
                case LONG:
                    ps.setLong(i, Long.valueOf(value));
                    break;
                case SHORT:
                    ps.setShort(i, Short.valueOf(value));
                    break;
            }
        }
        return ps;
    }

    private ModifyParams buildInsertParameters(Table table) {
        ModifyParams modifyParams = new ModifyParams();
        for (Entry<String, Object> entry : table.getQueryFields().entrySet()) {
            if (entry.getValue() != null) {
                modifyParams.getParamsValue().add(entry.getValue());
                modifyParams.getParamsType().add(table.getColumn(entry.getKey()).getJdbcType());
            }
        }
        return modifyParams;
    }


    public int updateByCriteria(final Table table) {
        SqlTemplate sqlTemplate = new SqlTemplate();
        final String sql = sqlTemplate.updateByCriteria(table);
        List<Object> params = buildUpdateParameters(table);

        logger.debug(ArrayUtils.toString(params.toArray()));
        int rs = this.getJdbcTemplate().update(sql, params.toArray());
        return rs;
    }

    private List<Object> buildUpdateParameters(Table table) {
        List<Object> params = new ArrayList<>();
        for (String key : table.getQueryFields().keySet()) {
            params.add(table.getQueryFields().get(key));
        }
        for (Object value : table.getConditions().values()) {
            if (value instanceof List) {
                params.addAll((List) value);
            } else {
                params.add(value);
            }

        }
        return params;
    }


    public int updateByPrimaryKey(final Table table) {
        SqlTemplate sqlTemplate = new SqlTemplate();
        final String sql = sqlTemplate.updateByPrimaryKey(table);
        List<Object> params = buildUpdateByPrimaryKeyParameters(table);

        logger.debug(ArrayUtils.toString(params.toArray()));
        int rs = this.getJdbcTemplate().update(sql, params.toArray());
        return rs;
    }

    private List<Object> buildUpdateByPrimaryKeyParameters(Table table) {
        List<Object> params = new ArrayList<>();

        for (String key : table.getQueryFields().keySet()) {
            params.add(table.getQueryFields().get(key));
        }
        for (String name : table.getPrimaryKey().getFields()) {
            params.add(table.getConditionValue(name));
        }
        return params;
    }


    public int deleteByPrimaryKey(final Table table) {
        SqlTemplate sqlTemplate = new SqlTemplate();
        final String sql = sqlTemplate.deleteByPrimaryKey(table);
        List<Object> params = buildDeleteByPrimaryKey(table);

        logger.debug(ArrayUtils.toString(params.toArray()));
        int rs = this.getJdbcTemplate().update(sql, params.toArray());
        return rs;
    }

    private List<Object> buildDeleteByPrimaryKey(Table table) {
        List<Object> params = new ArrayList<>();
        for (String key : table.getPrimaryKey().getFields()) {
            params.add(table.getConditionValue(key));
        }
        return params;
    }


    public int deleteByCriteria(final Table table) {
        SqlTemplate sqlTemplate = new SqlTemplate();
        final String sql = sqlTemplate.deleteByCriteria(table);
        List<Object> params = buildDeleteParameters(table);

        logger.debug(ArrayUtils.toString(params.toArray()));
        int rs = this.getJdbcTemplate().update(sql, params.toArray());
        return rs;
    }

    private List<Object> buildDeleteParameters(Table table) {
        List<Object> params = new ArrayList<>();
        for (Object value : table.getConditions().values()) {
            if (value instanceof List) {
                params.addAll((List) value);
            } else {
                params.add(value);
            }

        }
        return params;
    }


}
