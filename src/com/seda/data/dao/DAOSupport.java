/**
 * 
 */
package com.seda.data.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import com.seda.commons.logger.CustomLoggerManager;
import com.seda.commons.logger.LoggerWrapper;
import com.seda.data.datasource.DataSourceException;
import com.seda.data.logger.CallableStatementLogger;

/**
 * This class gives the support to a DAO bean
 * @author Seda Lab
 * @deprecated Questa classe e' deprecata usare com.seda.data.dao.DAOSupportExtended, non sar� pi� inserita nelle prossime release
 */
public class DAOSupport {

	private static final LoggerWrapper logger =  CustomLoggerManager.get(DAOSupport.class);
	
	private DataSource dataSource;
	private Connection  connection;
	private Properties connectionProperties;
	private String schema;
	/**
	 * Returns the schema of the database object used in the DAO fields' constructor
	 * @return <code>String</code> the schema of the database object
	 */
	protected String getSchema() { return this.schema; }
	/**
	 * Returns the {@link Connection} object for this DAO
	 * @return <code>{@link Connection}</code> the Connection object
	 */	
	protected Connection getConnection() {
		return getConnection(true);
	}	
	/**
	 * Returns the {@link Connection} object for this DAO
	 * @param autocommit true or false in according to the auto commit rule
	 * @return <code>{@link Connection}</code> the Connection object
	 */	
	protected Connection getConnection(boolean autocommit) {
		try {		
			if (connection==null || connection.isClosed()) {
				if (getDataSource()!=null) {
					connection=DAOHelper.getConnection(getDataSource(), autocommit);
				} else if (connectionProperties!=null ){
					connection=DAOHelper.getConnection(connectionProperties, autocommit);
				} 
			} 
		} catch (Throwable e) {
			if (e.getClass().equals(DataSourceException.class)&& 
					e.getMessage().contains("ConnectionWrapper") && 
					e.getMessage().contains("Connection is invalid")) { 
				// someone tried to reuse a closed connection in conjunction of a pooled datasource
				// bad practice, please review your code
				logger.warn(DAOSupport.class + " is deprecated and you are trying to reuse an invalid pooled connection, please review your code");
				connection=null;
				// reclaim a new connection
				return getConnection(autocommit);
			}

			e.printStackTrace(System.out);
		}
		return connection; 
	}	
	
	/**
	 * Returns the {@link DataSource} object used in the DAO fields' constructor or null if was used alternative constructor
	 * @return <code>{@link DataSource}</code> the DataSource object if one exists
	 */	
	private DataSource getDataSource() { return this.dataSource; }
	
	/**
	 * Constructor of DAO object
	 * 
	 * @param datasource The {@link DataSource} object used in the DAO to get a connection
	 * @param schema the <code>String</code> that represents schema of the database objects
	 */
	public DAOSupport(DataSource datasource, String schema) {
		this.dataSource=datasource;
		this.connectionProperties=null;
		this.connection=null;		
		this.schema = schema;		
	}
	
	/**
	 * Constructor of DAO object. The properties config must conains the following keys and values:</br>
	 * <code>driver</code>=the jdbc driver class name (required)</br>
	 * <code>url</code>=data base connection url (required)</br>
	 * <code>user</code>=authorized user name</br>
	 * <code>password</code>=user name password</br>


	 * @param config The Properties objects containing the connection configuration
	 * @param schema the <code>String</code> that represents schema of the database objects

	 */
	public DAOSupport(Properties config, String schema) {
		this.dataSource=null;
		this.connection=null;		
		this.connectionProperties=config;
		this.schema = schema;		
	}	
	
	/**
	 * Constructor of DAO object. This method is deprecated. Use {@link DAOSupport#DAOSupport(Properties, String)}
	 * 
	 * @param connection The {@link Connection} object used in the DAO
	 * @param schema the <code>String</code> that represents schema of the database objects
	 */
	@Deprecated
	public DAOSupport(Connection connection, String schema) {
		this.connection=connection;
		this.connectionProperties=null;
		this.dataSource=null;
		this.schema = schema;		
	}	

}
