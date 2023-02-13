package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		ps.setString(i, ((String) parameter));
	}

	public Object getNullableResult(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getString(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex)
	throws SQLException {
		return cs.getString(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		String s = (String)getNullableResult(rs, columnName);
		return TypeFormatter.format(s, finalLen);		
	}

}
