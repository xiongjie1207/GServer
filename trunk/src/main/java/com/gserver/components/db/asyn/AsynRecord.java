package com.gserver.components.db.asyn;
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
import java.util.HashMap;
import java.util.Map;

public class AsynRecord extends HashMap<String, Object> {


	private static final long serialVersionUID = -7574370441086237881L;
	
	public AsynRecord(Method method, Object obj){
		this.put("_method", method);
		this.put("_obj", obj);
	}
	
	public AsynRecord(Method method, String table, Map<String, Object> obj){
		this.put("_method", method);
		this.put("_table", table);
		this.put("_map", obj);
	}
	
	public AsynRecord(Method method, String database, String table, Map<String, Object> obj){
		this.put("_method", method);
		this.put("_database", database);
		this.put("_table", table);
		this.put("_map", obj);
	}
	
	public Object getObj(){
		return this.get("_obj");
	}
	
	public String getTable(){
		return (String) this.get("_table");
	}
	
	public String getDatabase(){
		return (String) this.get("_database");
	}
	
	public Method getMethod(){
		return (Method) this.get("_method");
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMapObj(){
		return (Map<String, Object>) this.get("_map");
	}

}
