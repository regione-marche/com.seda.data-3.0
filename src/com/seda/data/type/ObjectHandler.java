package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		ps.setObject(i, parameter);
	}

	public Object getNullableResult(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getObject(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex)
	throws SQLException {
		return cs.getObject(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		String s = (getNullableResult(rs, columnName)).toString();
		return TypeFormatter.format(s, finalLen);		
	}

}
