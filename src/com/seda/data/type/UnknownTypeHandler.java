package com.seda.data.type;

import java.sql.*;

public class UnknownTypeHandler extends BaseHandler {

	private ResolverTypes resolverTypes;

	public UnknownTypeHandler(HandlerRegistry typeHandlerRegistry) {
		resolverTypes=new ResolverTypes(typeHandlerRegistry);
	}

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		TypeHandler handler = resolverTypes.resolveTypeHandler(parameter, jdbcType);
		handler.setParameter(ps, i, parameter, jdbcType);
	}

	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		TypeHandler handler = resolverTypes.resolveTypeHandler(rs, columnName);
		return handler.getResult(rs, columnName);
	}
	
	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getObject(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		TypeHandler handler = resolverTypes.resolveTypeHandler(rs, columnName);
		return handler.getResult(rs, columnName, finalLen);		
	}

}
