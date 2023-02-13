/**
 * 
 */
package com.seda.data.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * 
 * 
 * 
 * @author Seda Lab
 */
public class HelperStatement {

	private String statement;
	private String schema;
	private String schemaPattern = "{schema}";
	private Connection connection;
	private PreparedStatement preparedStatement;
	
	private boolean update;
	
	private ResultSet resultSet;
	private int updateCount;
	
	public String getSchema() {
		return schema==null?"":schema;
	}
	
	public void setSchema(String schema) {
		destroy();
		this.schema=schema;
	}
	
	public String getSchemaPattern() {
		return this.schemaPattern==null?"":this.schemaPattern;		
	}
	
	public String getStatementString () {
		return statement.replace(getSchemaPattern(), getSchema());
	}
	
	public boolean isUpdate() {
		return this.update;
	}
	
	public ResultSet getResultSet() {
		return this.resultSet;
	}
	
	public int getUpdatecount() {
		return this.updateCount;
	}
	
	private PreparedStatement getPreparedStatement() throws SQLException {
		if (preparedStatement==null) {
			update=checkUpdate();			
			preparedStatement = connection.prepareStatement(getStatementString());
		}
		return preparedStatement;
	}

	private boolean checkUpdate() {
		StringTokenizer s = new StringTokenizer(statement);
		try {
			if (s.nextToken().equalsIgnoreCase("SELECT")) 
				return false;
			return true;
		} finally {
			s=null;
		}
	}
	
	public PreparedStatement prepare() throws SQLException {
		return getPreparedStatement();
	}
	
	public HelperStatement(Connection connection, String schema, String statement) {
		this.connection=connection;
		this.schema=schema;
		this.statement=statement;
	}
	
	public HelperStatement(Connection connection, String statement) {
		this.connection=connection;
		this.schema=null;
		this.statement=statement;
	}	
	
	public void execute() throws SQLException {
		if (isUpdate()) {
			updateCount=getPreparedStatement().executeUpdate();	
		} else {
			resultSet=getPreparedStatement().executeQuery();			
		}
	}
	
	public void finalize() {
		destroy();
	}
	
	public void destroy() {
		Helper.close(resultSet, preparedStatement);
	}
	
	
}
