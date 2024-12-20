package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class DateTimeHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		ps.setTimestamp(i, new java.sql.Timestamp(((Date) parameter).getTime()));
	}

	public Object getNullableResult(ResultSet rs, String columnName)
	throws SQLException {
		java.sql.Timestamp sqlTimestamp = rs.getTimestamp(columnName);
		if (sqlTimestamp != null) {
			return new java.util.Date(sqlTimestamp.getTime());
		}
		return null;
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex)
	throws SQLException {
		java.sql.Timestamp sqlTimestamp = cs.getTimestamp(columnIndex);
		if (sqlTimestamp != null) {
			return new java.util.Date(sqlTimestamp.getTime());
		}
		return null;
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		java.sql.Time sqlTime = (java.sql.Time)getNullableResult(rs, columnName);
		return TypeFormatter.format(sqlTime, finalLen);
	}

}
