package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unchecked")
public class EnumHandler extends BaseHandler implements TypeHandler {

	private Class type;

	public EnumHandler(Class type) {
		this.type = type;
	}

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.toString());
	}

	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String s = rs.getString(columnName);
		return s == null ? null : Enum.valueOf(type, s);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String s = cs.getString(columnIndex);
		return s == null ? null : Enum.valueOf(type, s);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		throw new TypeException("Not implemented");
	}

}