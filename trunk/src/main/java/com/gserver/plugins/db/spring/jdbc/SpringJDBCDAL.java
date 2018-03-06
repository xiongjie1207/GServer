package com.gserver.plugins.db.spring.jdbc;
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
import com.gserver.plugins.db.core.AbstractBaseDAL;
import com.gserver.plugins.db.core.BaseDAL;
import com.gserver.plugins.db.descriptor.Table;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class SpringJDBCDAL extends AbstractBaseDAL implements BaseDAL {


    private CommonJdbcSupport commonJdbcSupport;

    public void setJdbcDaoSupport(CommonJdbcSupport commonJdbcSupport) {
        this.commonJdbcSupport = commonJdbcSupport;
    }

    @Override
    protected List<Map<String, Object>> _selectByCriteria(Table table) {
        return commonJdbcSupport.selectByCriteria(table);
    }
    @Override
    protected int _countByCriteria(Table table) {
        return commonJdbcSupport.countByCriteria(table);
    }

    @Override
    protected Map<String, Object> _selectByPrimaryKey(Table table) {
        return commonJdbcSupport.selectByPrimaryKey(table);
    }

    @Override
    protected int _insert(Table table) {
        return commonJdbcSupport.insert(table);
    }

    @Override
    protected int _updateByCriteria(Table table) {
        return commonJdbcSupport.updateByCriteria(table);
    }

    @Override
    protected int _updateByPrimaryKey(Table table) {
        return commonJdbcSupport.updateByPrimaryKey(table);
    }

    @Override
    protected int _deleteByPrimaryKey(Table table) {
        return commonJdbcSupport.deleteByPrimaryKey(table);
    }

    @Override
    protected int _deleteByCriteria(Table table) {
        return commonJdbcSupport.deleteByCriteria(table);
    }
    public JdbcTemplate getJdbcTemplate() {
        return commonJdbcSupport.getJdbcTemplate();
    }


}
