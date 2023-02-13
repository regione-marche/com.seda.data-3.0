package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FloatHandler extends BaseHandler {
	
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setFloat(i, (Float) parameter);
	}

	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getFloat(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getFloat(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		float floatData = (Float)getNullableResult(rs, columnName);
		return TypeFormatter.format(floatData, 
				getRsmd(rs).getPrecision(getColumnIndexer().lookUpIndex(columnName)),
				getRsmd(rs).getScale(getColumnIndexer().lookUpIndex(columnName)),finalLen);
	}

}
