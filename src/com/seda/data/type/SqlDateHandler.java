package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlDateHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		ps.setDate(i, (java.sql.Date) parameter);
	}

	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getDate(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getDate(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		java.sql.Date sqlDate = (java.sql.Date)getNullableResult(rs, columnName);
		return TypeFormatter.format(sqlDate, finalLen);
	}

}
