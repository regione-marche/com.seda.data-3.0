package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setDouble(i, (Double) parameter);
	}

	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getDouble(columnName);
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getDouble(columnIndex);
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		double doubleData = (Double)getNullableResult(rs, columnName);
		return TypeFormatter.format(doubleData,
				getRsmd(rs).getPrecision(getColumnIndexer().lookUpIndex(columnName)),
				getRsmd(rs).getScale(getColumnIndexer().lookUpIndex(columnName)),finalLen);
	}

}
