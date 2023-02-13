package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlTimestampHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		ps.setTimestamp(i, (java.sql.Timestamp) parameter);
	}

	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getTimestamp(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getTimestamp(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		java.sql.Timestamp sqlTimestamp = (java.sql.Timestamp)getNullableResult(rs, columnName);
		return TypeFormatter.format(sqlTimestamp, finalLen);
	}

}
