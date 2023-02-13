package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
	throws SQLException {
		ps.setShort(i, (Short) parameter);
	}

	public Object getNullableResult(ResultSet rs, String columnName)
	throws SQLException {
		return rs.getShort(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex)
	throws SQLException {
		return cs.getShort(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		short shortData = (Short)getNullableResult(rs, columnName);
		return TypeFormatter.format(shortData, finalLen);
	}

}
