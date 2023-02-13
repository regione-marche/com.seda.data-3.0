package com.seda.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.seda.commons.data.AttributesModel;

public interface TypeHandler {

	public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException;
	
	public void setAttribute(AttributesModel as, String attr, Object value);	

	public Object getResult(ResultSet rs, String columnName) throws SQLException;
	
	public String getResult(ResultSet rs, String columnName, int finalLen) throws SQLException;  

	public Object getResult(CallableStatement cs, int columnIndex) throws SQLException;

}
