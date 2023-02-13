package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		ps.setBoolean(i, (Boolean) parameter);
	}

	public Object getNullableResult(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getBoolean(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex)
	throws SQLException {
		return cs.getBoolean(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		boolean b = (Boolean)getNullableResult(rs, columnName);
		return TypeFormatter.format(Boolean.toString(b), finalLen);		
	}


}
