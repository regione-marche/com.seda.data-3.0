/**
 * 
 */
package com.seda.data.type;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.seda.commons.resource.ResourceManager;

/**
 * @author f.ricci
 *
 */
public class ResolverTypes {

	private static final ObjectHandler OBJECT_TYPE_HANDLER = new ObjectHandler();	
	
	private HandlerRegistry typeHandlerRegistry;
	
	public ResolverTypes(HandlerRegistry typeHandlerRegistry) {
		this.typeHandlerRegistry = typeHandlerRegistry;
	}
	
	public TypeHandler resolveTypeHandler(Object parameter, JdbcType jdbcType) {
		TypeHandler handler;
		if (parameter == null) {
			handler = OBJECT_TYPE_HANDLER;
		} else {
			handler = typeHandlerRegistry.getTypeHandler(parameter.getClass(), jdbcType);
			if (handler instanceof UnknownTypeHandler) {
				handler = OBJECT_TYPE_HANDLER;
			}
		}
		return handler;
	}

	public TypeHandler resolveTypeHandler(ResultSet rs, String column) {
		try {
			Map<String,Integer> columnIndexLookup = new HashMap<String,Integer>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			for (int i=1; i <= count; i++) {
				String name = rsmd.getColumnName(i);
				columnIndexLookup.put(name,i);
			}
			Integer columnIndex = columnIndexLookup.get(column);
			TypeHandler handler = null;
			if (columnIndex != null) {
				handler = resolveTypeHandler(rsmd, columnIndex);
			}
			if (handler == null || handler instanceof UnknownTypeHandler) {
				handler = OBJECT_TYPE_HANDLER;
			}
			return handler;
		} catch (SQLException e) {
			throw new TypeException("Error determining JDBC type for column " + column + ".  Cause: " + e, e);
		}
	}

	public TypeHandler resolveTypeHandler(ResultSetMetaData rsmd, Integer columnIndex) throws SQLException {
		TypeHandler handler = null;
		JdbcType jdbcType = getJdbcTypeForColumn(rsmd, columnIndex);
		Class<?> javaType = getClassForColumn(rsmd, columnIndex);
		if (javaType != null && jdbcType != null) {
			handler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
		} else if (javaType != null) {
			handler = typeHandlerRegistry.getTypeHandler(javaType);
		} else if (jdbcType != null) {
			handler = typeHandlerRegistry.getTypeHandler(jdbcType);
		}
		return handler;
	}

	private JdbcType getJdbcTypeForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
		try {
			return JdbcType.forCode(rsmd.getColumnType(columnIndex));
		} catch (Exception e) {
			return null;
		}
	}

	private Class<?> getClassForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
		try {
			return ResourceManager.classForName(rsmd.getColumnClassName(columnIndex));
		} catch (Exception e) {
			return null;
		}
	}
}
