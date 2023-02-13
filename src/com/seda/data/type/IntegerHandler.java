package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		ps.setInt(i, (Integer) parameter);
	}

	public Object getNullableResult(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getInt(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex)
	throws SQLException {
		return cs.getInt(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		int intData = (Integer)getNullableResult(rs, columnName);
		return TypeFormatter.format(intData, finalLen);
	}

}
