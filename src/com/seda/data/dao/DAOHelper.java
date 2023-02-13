/**
 * 
 */
package com.seda.data.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.WebRowSet;

import com.seda.commons.resource.ResourceManager;
import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.WebRowSetImpl;

/**
 * @author Seda Lab
 *
 */
public class DAOHelper {

	public final static String JDBC_DRIVER = "driver";
	public final static String JDBC_URL = "url";
	public final static String JDBC_USER = "username";
	public final static String JDBC_PASSWORD = "password";

	private static boolean incontainer;
	
	static {
		try {
			ResourceManager.classForName("javax.servlet.Servlet");
			incontainer=true;
		} catch (Exception x) {
			incontainer=false;
		}
		
	}

	public static boolean isInServletContainer() {
		return incontainer;
	}
	
	/**
	 * Gets a connection from a data source with auto commit true
	 * @param dataSource
	 * @return a SQL Connection
	 * @throws DAOSysException in case of SQLException
	 */
	public static Connection getConnection(DataSource dataSource) throws DAOSysException {
		return getConnection(dataSource,true);
	}	
	/**
	 * Gets a connection from a data source
	 * @param dataSource
	 * @param autoCommit
	 * @return a SQL Connection
	 * @throws DAOSysException in case of SQLException
	 */
	public static Connection getConnection(DataSource dataSource, boolean autoCommit) throws DAOSysException {
		Connection connection=null;
		try {
			connection = dataSource.getConnection();	
			connection.setAutoCommit(autoCommit);
		} catch (SQLException se) {
			throw new DAOSysException("SQL Exception while getting "
					+ "DB connection : \n" + se);
		}
		return connection;
	}
	/**
	 * Returns a connection created using config properties
	 * @throws DAOSysException in case of SQLException or ClassNotFoundException
	 */
	public static Connection getConnection(Properties config) throws DAOSysException {
		return getConnection(config, true);
	}
	/**
	 * Returns a connection created using config properties
	 * @throws DAOSysException in case of SQLException or ClassNotFoundException
	 */
	public static Connection getConnection(Properties config, boolean autoCommit) throws DAOSysException {
		Connection connection=null;
		try {
			//Class.forName(driver).newInstance();
			Class.forName(config.getProperty(JDBC_DRIVER));
			if ((config.getProperty(JDBC_USER)==null) || (config.getProperty(JDBC_PASSWORD)==null)) 
				connection = DriverManager.getConnection( config.getProperty(JDBC_URL) );
			else 
				connection = DriverManager.getConnection(config.getProperty(JDBC_URL), 
						config.getProperty(JDBC_USER), config.getProperty(JDBC_PASSWORD));
			
	        connection.setAutoCommit(autoCommit);			
		} catch (NumberFormatException x) {
			
		} catch (ClassNotFoundException e) {
			throw new DAOSysException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new DAOSysException(e.getMessage(), e);
		}
		return connection;
	}

	/**
	 * Returns the {@link CachedRowSet} object filled from result set.
	 * @return <code>{@link CachedRowSet}</code> the ChachedRowSet object
	 */
	public static CachedRowSet getCachedRowSet(ResultSet resultSet) {		
		if (resultSet==null) {
			throw new NullPointerException("resultset");
		}
		CachedRowSetImpl cachedRowSetImpl = null;
		try {		
			cachedRowSetImpl =  new CachedRowSetImpl();
			cachedRowSetImpl.populate(resultSet);			
		} catch (SQLException x) {
			throw new DAORuntimeException(x.getMessage(),x);
		}
		return cachedRowSetImpl;
	}	
	
	/**
	 * Returns the {@link WebRowSet} object filled from ResultSet.
	 * @return <code>{@link WebRowSet}</code> the WebRowSet object
	 */
	public static WebRowSet getWebRowSet(ResultSet resultSet) {
		// if rs is null means that there are no more results		
		if (resultSet==null) {
			throw new NullPointerException("resultset");
		}
		WebRowSetImpl webRowSet = null;
		try {
			// define a new cached rowset		
			webRowSet = new WebRowSetImpl();
			// fill data		
			webRowSet.populate(resultSet);			
		} catch (SQLException x) {
			throw new DAORuntimeException(x.getMessage(), x);
		}
		return webRowSet;
		
	}	
	
	/**
	 * Closes the provided resource
	 * @param connection
	 * @param resultset
	 * @param statement
	 * @throws DAOSysException in case of SQLException
	 */
	public static void closeAll(Connection connection, ResultSet resultset, Statement statement) throws DAOSysException {
		close(statement);
		close(resultset);
		close(connection);
	} 	
	
	public static void closeAllIgnoringException(Connection connection, ResultSet resultset, Statement statement) {
		closeIgnoringException(statement);
		closeIgnoringException(resultset);
		closeIgnoringException(connection);
	} 		
	
	/**
	 * Closes the database connection 
	 * @param connection
	 * @throws DAOSysException in case of SQLExeption
	 */
	public static void close(Connection connection) throws DAOSysException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException se) {
            throw new DAOSysException("SQL Exception while closing "
            + "DB connection : \n" + se);
        }
    }
	public static void closeIgnoringException(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception x) {
            // ignore exception
        }
	}
    /**
     * Closes the result set
     * @param result
     * @throws DAOSysException in case of SQLExeption
     */
	public static void close(ResultSet result) throws DAOSysException {
        try {
            if (result != null) {
                result.close();
                result = null;
            }
        } catch (SQLException se) {
            throw new DAOSysException("SQL Exception while closing "
            + "Result Set : \n" + se);
        }
    }
	
	public static void closeIgnoringException(ResultSet result) {
        try {
            if (result != null) {
                result.close();
                result = null;
            }
        } catch (Exception x) {
            // ignore exception
        }
    }	
	
    /**
     * Closes the statement
     * @param stmt
     * @throws DAOSysException in case of SQLExeption
     */
    public static void close(Statement stmt) throws DAOSysException {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException se) {
            throw new DAOSysException("SQL Exception while closing "
            + "Statement : \n" + se);
        }
    }	
	
    public static void closeIgnoringException(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception x) {
            // ignore exception
        }
    }	    
	/**
	 * Executes a commit operation on this DAO database connection
	 * @throws DAOSysException in case of SQLException
	 */
	public static void commit(Connection connection) throws DAOSysException {
		if (connection == null ) return;
		try {
			if (connection.isClosed()) return;
		} catch (SQLException x){
			try {
				connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			throw new DAOSysException("SQL Exception while rollback connection "+ x);
		}
	}
	/**
	 * Executes a rollback operation on this DAO database connection
	 * @throws DAOSysException in case of SQLException
	 */
	public static void rollback(Connection connection) throws DAOSysException {
		if (connection == null ) return;
		try {
			if (connection.isClosed()) return;
		} catch (SQLException x){
			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			} 
			throw new DAOSysException("SQL Exception while rollback connection "+ x);
		}
	}	
}
