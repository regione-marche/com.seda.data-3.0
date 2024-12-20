package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlTimeHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		ps.setTime(i, (java.sql.Time) parameter);
	}

	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getTime(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getTime(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		java.sql.Time sqlTime = (java.sql.Time)getNullableResult(rs,columnName);
		return TypeFormatter.format(sqlTime, finalLen);
	}

}
