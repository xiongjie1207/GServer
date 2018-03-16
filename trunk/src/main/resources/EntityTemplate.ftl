package com.mafia.server.model.entity;

import java.io.Serializable;
import com.gserver.plugins.db.descriptor.IEntity;
import com.gserver.plugins.db.annotation.Table;

/**
 * Generated by GServer, do not modify this file.
 **/
@Table("${tableName}")
public class ${entity} implements Serializable, IEntity {
	<#list fields as field>
	private ${field.fieldType} ${field.fieldName};
	</#list>

	<#list fields as field>
	public void set${field.fieldName?cap_first}(${field.fieldType} ${field.fieldName}) {
		this.${field.fieldName} = ${field.fieldName};
	}

	public ${field.fieldType} get${field.fieldName?cap_first}() {
		return this.${field.fieldName};
	}

	</#list>
}
