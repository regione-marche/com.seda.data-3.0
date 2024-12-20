package com.seda.data.type;

import java.io.ByteArrayInputStream;
import java.sql.*;

public class BlobHandler extends BaseHandler {

	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		byte[] bytes = (byte[]) parameter;
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ps.setBinaryStream(i, bis, bytes.length);
	}

	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Blob blob = rs.getBlob(columnName);
		byte[] returnValue = null;
		if (null != blob) {
			returnValue = blob.getBytes(1, (int) blob.length());
		}
		return returnValue;
	}

	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Blob blob = cs.getBlob(columnIndex);
		byte[] returnValue = null;
		if (null != blob) {
			returnValue = blob.getBytes(1, (int) blob.length());
		}
		return returnValue;
	}

	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException {
		throw new TypeException("Not implemented");
	}

}