package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteArrayHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		ps.setBytes(i, (byte[]) parameter);
	}

	public Object getNullableResult(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getBytes(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex)
	throws SQLException {
		return cs.getBytes(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		byte[] b = (byte[])getNullableResult(rs, columnName);
		return TypeFormatter.format(new String(b), finalLen);		
	}


}
