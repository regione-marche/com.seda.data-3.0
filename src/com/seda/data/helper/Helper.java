package com.seda.data.helper;

import java.io.PrintStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Properties;

import com.seda.commons.logger.LoggerWrapper;
import com.seda.data.helper.HelperException;
import com.seda.data.helper.SpParamCache;

/**
 * @author SEDA Lab
 */
public final class Helper {
	/**
	 * Returns the {@link CallableStatement}.
	 * @param connection - The current {@link Connection} to pass.
	 * @param schemaPattern - The current schema to pass.
	 * @param procedureNamePattern - The current procedure to pass.
	 * @param parameterCountExpected - The number of parameters to  be checked. -1 skips the control statement
	 * @return <code>CallableStatement</code> - The {@link CallableStatement} object.
	 * @throws SQLException In case of SQL Exception.
	 * @throws HelperException In case of DAO Exception.
	 */
	public synchronized static final CallableStatement prepareCall(final Connection connection, final String schemaPattern, final String procedureNamePattern, final int parameterCountExpected) throws SQLException, HelperException {
        if( connection == null ) throw new HelperException(Messages.ARGUMENT_NULL.format("connection"));
        if( schemaPattern == null || schemaPattern.length() == 0 ) throw new HelperException(Messages.ARGUMENT_NULL.format("schemaPattern"));        
        if( procedureNamePattern == null || procedureNamePattern.length() == 0 ) throw new HelperException(Messages.ARGUMENT_NULL.format("procedureNamePattern"));
        
        SpParamMetaData helperParameterSetMetaData = SpParamCache.getParameterSet(connection, schemaPattern, procedureNamePattern);
        
        if (parameterCountExpected>=0) {
        	if (helperParameterSetMetaData.getParameterCount() != parameterCountExpected) {
        		throw new HelperException(Messages.PARAMETER_COUNT_ERROR.format(helperParameterSetMetaData.getParameterCount(), parameterCountExpected, schemaPattern, procedureNamePattern));        		
        	}
        }
        
        return connection.prepareCall(helperParameterSetMetaData.getParameterSet());
	}
	/**
	 * Returns the {@link CallableStatement} skipping the match of number of parameters.
	 * @param connection - The current {@link Connection} to pass.
	 * @param schemaPattern - The current schema to pass.
	 * @param procedureNamePattern - The current procedure to pass.
	 * @return <code>CallableStatement</code> - The {@link CallableStatement} object.
	 * @throws SQLException In case of SQL Exception.
	 * @throws HelperException In case of DAO Exception.
	 */
	public synchronized  static final CallableStatement prepareCall(final Connection connection, final String schemaPattern, final String procedureNamePattern) throws SQLException, HelperException {
        return prepareCall(connection, schemaPattern, procedureNamePattern, -1);
	}	
	
	/* =================================== */
	/* Connection Section */
	/* =================================== */	
	public synchronized static final Connection getConnection(String driver, String url, 
			String user, String password, boolean autoCommit) throws ClassNotFoundException, SQLException {
		Connection connection=null;
		Properties creds=null;
		if (user!= null && password != null) {
			creds = new Properties();
			creds.setProperty("user", user);
	        creds.setProperty("password", password);
		}
		//Class.forName(driver).newInstance();
		Class.forName(driver);
		if (creds == null) 
			connection = DriverManager.getConnection( url );
		else 
			connection = DriverManager.getConnection( url, creds );			
        connection.setAutoCommit(autoCommit);			
		return connection;
	}
	public synchronized static final DriverInfo getDriverInfo(Connection connection) throws SQLException, HelperException {
		if( connection == null ) throw new HelperException(Messages.ARGUMENT_NULL.format("connection"));
        return DriverInfoCache.getDriverInfo(connection);
	}	
	
	public synchronized static final void commit(Connection connection) {
		checkUOW(connection, true);
	}
	public synchronized static final void rollback(Connection connection) {
		checkUOW(connection, false);
	}
	public synchronized static final void checkUOW(Connection connection) {
		checkUOW(connection,true);
	}
	public synchronized static final void checkUOW(Connection connection, boolean commit) {
		if (connection == null ) return;
		try {
			if (connection.isClosed()) return;
		} catch (SQLException x){
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			return;
		}
		
		try {
			if (commit) {
				connection.commit();			
			} else {
				connection.rollback();			
			}
		} catch (SQLException x) {
			x.printStackTrace();
		}
	}
	
	/* =================================== */
	/* SQLException Section */
	/* =================================== */
	public synchronized static final void showSQLException(SQLException x) {
		showSQLException(x,System.err);
	}
	
	public synchronized static final void showSQLException(SQLException x, PrintStream printer) {
		while (x!=null) {
			printer.println(Messages.SQLE_MESSAGE.format(x.getMessage()));
			printer.println(Messages.SQLE_SQLSTATE.format(x.getSQLState()));
			printer.println(Messages.SQLE_ERROR_CODE.format(x.getErrorCode()));
			printer.println(Messages.SQLE_STACK.format());
			x.printStackTrace(printer);
			x=x.getNextException();
		}
	}	
	public synchronized static final void showSQLException(SQLException x, LoggerWrapper logger) {
		while (x!=null) {
			logger.error(Messages.SQLE_MESSAGE.format(x.getMessage()));
			logger.error(Messages.SQLE_SQLSTATE.format(x.getSQLState()));
			logger.error(Messages.SQLE_ERROR_CODE.format(x.getErrorCode()));
			logger.error(Messages.SQLE_STACK.format());
			logger.error(x);			
			x=x.getNextException();
		}		
	}

	/* =================================== */
	/* SQLWarning Section */
	/* =================================== */	
	public synchronized static final void showSQLWarnings(Statement statement) throws SQLException {
		showSQLWarnings(statement.getWarnings());
	}
	public synchronized static final void showSQLWarnings(ResultSet resultSet) throws SQLException {
		showSQLWarnings(resultSet.getWarnings());		
	}	
	
	public synchronized static final void showSQLWarnings(SQLWarning w) {
		showSQLWarnings(w, System.err);
	}
	public synchronized static final void showSQLWarnings(SQLWarning w, PrintStream printer) {
		while (w!=null) {
			printer.println(Messages.SQLW_MESSAGE.format(w.getMessage()));
			printer.println(Messages.SQLW_SQLSTATE.format(w.getSQLState()));
			printer.println(Messages.SQLW_ERROR_CODE.format(w.getErrorCode()));
			w=w.getNextWarning();
		}		
	}		
	public synchronized static final void showSQLWarnings(SQLWarning w, LoggerWrapper logger) {
		while (w!=null) {
			logger.warn(Messages.SQLW_MESSAGE.format(w.getMessage()));
			logger.warn(Messages.SQLW_SQLSTATE.format(w.getSQLState()));
			logger.warn(Messages.SQLW_ERROR_CODE.format(w.getErrorCode()));
			w=w.getNextWarning();
		}
	}
	
	/* =================================== */
	/* Destroy Section */
	/* =================================== */	
 	public static final void close(ResultSet resultSet, Statement statement, Connection connection) {
 		close(resultSet, statement);
 		close(connection);
	}
	public static final void close(ResultSet resultSet, Statement statement) {
		close(resultSet);
		close(statement);
	}	
	public static final void close(ResultSet resultSet) {
		if (resultSet == null) return;
		try {
			resultSet.close();
		} catch (SQLException x) {
			x.printStackTrace();
		}
		resultSet=null;
	}
	
	public static final void close(Statement statement) {
		if (statement == null) return;
		try {
			statement.close();
		} catch (SQLException x) {
			x.printStackTrace();
		}
		statement=null;
	}
	public static final void close(Connection connection) {
		if (connection==null) return;
		try {
			connection.close();
		} catch (SQLException x) {
			x.printStackTrace();
		}
		connection=null;
	}
	/**
	 * This method is deprecated, use {@link Helper#close(Connection)} instead
	 */
	@Deprecated
	public static final void destroy(Connection connection) {
		if (connection==null) return;
		try {
			connection.close();
		} catch (SQLException x) {
			x.printStackTrace();
		}
		connection=null;
	}	
}
