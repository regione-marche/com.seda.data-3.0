/**
 * 
 */
package com.seda.data.dao;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import com.seda.commons.logging.Log;
import com.seda.commons.logging.LogFactory;
import com.seda.data.dao.params.DAOSupportParams;
import com.seda.data.datasource.DataSourceException;
import com.seda.data.datasource.DataSourceFactoryImpl;
import com.seda.data.event.ConnectionOpenEvent;
import com.seda.data.event.DAOEventHandler;
import com.seda.data.event.DAOEventProxy;
import com.seda.data.event.servlet.DAOEventContext;
import com.seda.data.event.servlet.DAOEventLocal;

/**
 * Extended DAO support
 * @author f.ricci
 *
 */
public class DAOSupportExtended {
	
	private static final Log logger = LogFactory.getLog(DAOSupportExtended.class);
	private DAOSupportParams params = new DAOSupportParams();
	private DataSource dataSource;
	private String schema;
	private boolean knownds;
	
	/**
	 * Returns the schema of the database object used in the DAO fields' constructor
	 * @return <code>String</code> the schema of the database object
	 */
	protected String getSchema() { return this.schema; }
	/**
	 * Returns a new {@link Connection} object for this DAO
	 * @return <code>{@link Connection}</code> the Connection object
	 * @throws DataSourceException as a result of connection error 
	 */	
	protected Connection getConnection() {
		return getConnection(params.getConnectionAutocommit());
	}	
	/**
	 * Returns the {@link Connection} object for this DAO
	 * @return <code>{@link Connection}</code> the Connection object
	 * @throws DataSourceException as a result of connection error
	 */	
	protected Connection getConnection(boolean autoCommit) {
		Connection connection=null;
		
		try {
			connection=dataSource.getConnection();
			if (autoCommit != connection.getAutoCommit()) {
				connection.setAutoCommit(autoCommit);
			}
		} catch (Throwable x) {
			throw new DataSourceException(x.getMessage(),x);
		}
		
		if (!knownds) {
			DAOEventProxy.dispatch(connection);
		}
		
		return connection; 
	}
	/**
	 * Return the Connection object with autoCommit=default and waitBetweenRetry=default and retry to get it retriesLeft=default time if an error occurred
	 * 
	 * @return the Connection object
	 */
	protected Connection getConnectionWithRetry() {
		return getConnectionWithRetry(params.getConnectionAutocommit(), params.getConnectionRetry(), params.getConnectionRetryWaitTime());
	}	
	/**
	 * Return the Connection object with waitBetweenRetry=default and retry to get it retriesLeft=default time if an error occurred
	 * 
	 * @param autoCommit
	 * @return the Connection object
	 */
	protected Connection getConnectionWithRetry(boolean autoCommit) {
		return getConnectionWithRetry(autoCommit, params.getConnectionRetry(), params.getConnectionRetryWaitTime());
	}
	/**
	 * Return the Connection object with waitBetweenRetry=default and retry to get it retriesLeft time if an error occurred
	 * 
	 * @param autoCommit
	 * @param retriesLeft number of time that the getConnection will be called against an error occurred
	 * @return the Connection object
	 */
	protected Connection getConnectionWithRetry(boolean autoCommit, int retriesLeft) {
		return getConnectionWithRetry(autoCommit, retriesLeft, params.getConnectionRetryWaitTime());
	}
	/**
	 * Return the Connection object with autocommit=default and waitBetweenRetry=default and retry to get it retriesLeft time if an error occurred
	 * 
	 * @param retriesLeft number of time that the getConnection will be called against an error occurred
	 * @return the Connection object
	 */	
	protected Connection getConnectionWithRetry(int retriesLeft) {
		return getConnectionWithRetry(params.getConnectionAutocommit(), retriesLeft);
	}
	/**
	 * Return the Connection object with autocommit=default and retry to get it retriesLeft time if an error occurred
	 * 
	 * @param retriesLeft number of time that the getConnection will be called against an error occurred
	 * @param waitBetweenRetry wait time in millis between retries 
	 * @return the Connection object
	 */
	protected Connection getConnectionWithRetry(int retriesLeft, long waitBetweenRetry) {
		return getConnectionWithRetry(params.getConnectionAutocommit(), retriesLeft, waitBetweenRetry);
	}
	/**
	 * Return the Connection object and retry to get it retriesLeft time if an error occurred
	 * 
	 * @param autoCommit
	 * @param retriesLeft number of time that the getConnection will be called against an error occurred
	 * @param waitBetweenRetry wait time in millis between retries 
	 * @return the Connection object
	 */
	protected Connection getConnectionWithRetry(boolean autoCommit, int retriesLeft, long waitBetweenRetry) {
		Connection connection=null;
		try {
			connection=getConnection(autoCommit);
		} catch (DataSourceException x) {
			if (retriesLeft <= 0) {
				throw x;
			} else {
				if (waitBetweenRetry>0) {
					try {
						Thread.sleep(waitBetweenRetry);
					} catch (InterruptedException e) {}
				}
				Throwable cause = x.getCause()!=null?x.getCause():x;
				logger.error("com.seda.data.dao.DAOSupportExtended getConnectionWithRetry() ");
				logger.error("INFO: exception("+cause.getClass()+") caught when processing request: " + cause.getMessage());
				logger.error("com.seda.data.dao.DAOSupportExtended getConnectionWithRetry() ");
				logger.error("INFO: Retrying request ");
				return getConnectionWithRetry(autoCommit, retriesLeft - 1, waitBetweenRetry); // retry get
			}
		}
		return connection;
	}

	/**
	 * Executes the methodName of this DAO extended with default retriesLeft and waitBetweenRetry.
	 * @param methodName the name of the method of this DAO extended
	 * @param args arguments of the method 
	 * @return the return object after method invoke
	 */	
	protected <T> T executeWithRetry(String methodName, Object[] args) {
		return executeWithRetry(params.getExecuteRetry(), params.getExecuteRetryWaitTime(), methodName, args);
	}
	
	protected <T> T executeWithRetry(String methodName, Object[] args, Class<?>[] signature) {
		return executeWithRetry(params.getExecuteRetry(), params.getExecuteRetryWaitTime(), methodName, args, signature);
	}	
	
	/**
	 * Executes the methodName of this DAO extended with default waitBetweenRetry.
	 * @param retriesLeft number of time that the getConnection will be called against an error occurred
	 * @param methodName the name of the method of this DAO extended
	 * @param args arguments of the method 
	 * @return the return object after method invoke
	 */
	protected <T> T executeWithRetry(int retriesLeft, String methodName, Object[] args) {
		return executeWithRetry(retriesLeft, params.getExecuteRetryWaitTime(), methodName, args);
	}
	
	protected <T> T executeWithRetry(int retriesLeft, String methodName, Object[] args, Class<?>[] signature) {
		return executeWithRetry(retriesLeft, params.getExecuteRetryWaitTime(), methodName, args, signature);
	}	
	
	/**
	 * Executes the methodName of this DAO extended.
	 * @param retriesLeft number of time that the getConnection will be called against an error occurred
	 * @param waitBetweenRetry wait time in millis between retries
	 * @param methodName the name of the method of this DAO extended
	 * @param args arguments of the method 
	 * @return the return object after method invoke
	 */
	protected <T> T executeWithRetry(int retriesLeft, long waitBetweenRetry, String methodName, Object[] args) {
		return executeWithRetry(retriesLeft, waitBetweenRetry, methodName, args, resolveParameterTypes(args));
	}
	
	/**
	 * Executes the methodName of this DAO extended.
	 * @param retriesLeft number of time that the getConnection will be called against an error occurred
	 * @param waitBetweenRetry wait time in millis between retries
	 * @param methodName the name of the method of this DAO extended
	 * @param args arguments of the method 
	 * @param signature used to discover method 
	 * @return the return object after method invoke
	 */
	protected <T> T executeWithRetry(int retriesLeft, long waitBetweenRetry, String methodName, Object[] args, Class<?>[] signature) {
		Method method = null;
		try {
			method = this.getClass().getDeclaredMethod(methodName, signature);
			if (!method.isAccessible())
				method.setAccessible(true);
		} catch (Exception e) {
			throw new DataSourceException(e.getMessage(), e);
		}
		
		return executeWithRetry(retriesLeft, waitBetweenRetry, method, args);
	}	
	
	/**
	 * DAO extended method invocation
	 */
	@SuppressWarnings("unchecked")
	private <T> T executeWithRetry(int retriesLeft, long waitBetweenRetry, Method method, Object[] args) {
		T result=null;
		try {
			result=(T)method.invoke(this, args);
		} catch (Throwable x) {
			if (retriesLeft <= 0) {
				throw new DataSourceException(x.getMessage(),x);
			} else {
				if (waitBetweenRetry>0) {
					try {
						Thread.sleep(waitBetweenRetry);
					} catch (InterruptedException e) {}
				}
				Throwable cause = x.getCause()!=null?x.getCause():x;
				logger.error("com.seda.data.dao.DAOSupportExtended "+method+"() ");
				logger.error("INFO: exception("+cause.getClass()+") caught when processing request: " + cause.getMessage());
				logger.error("com.seda.data.dao.DAOSupportExtended "+method+"() ");
				logger.error("INFO: Retrying request ");
				return executeWithRetry(retriesLeft - 1, waitBetweenRetry, method, args); // retry method
			}
		}
		return result;
	}

	/**
	 * Resolves the class types of the object array
	 */
	private Class<?>[] resolveParameterTypes(Object[] args) {
		if (args==null || args.length==0) {
			return new Class[]{};
		}
		Class<?>[] argsType = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			argsType[i]=args[i].getClass();
		}
		
		return argsType;
	}
	/**
	 * Return the provided statement <T> after the execute method was done. In case of retry the statement may be result
	 * closed and in a unpredictable state.  
	 * 
	 * @param <T> prepared statement type
	 * @param s prepared statement to execute
	 * @return the executed statement object
	 */
//	protected <T extends PreparedStatement> T executeWithRetry(T s) {
//		return executeWithRetry(s, params.getExecuteRetry(), params.getExecuteRetryWaitTime());
//	}	
	/**
	 * Return the provided statement <T> after the execute method was done. In case of retry the statement may be result
	 * closed and in a unpredictable state.  
	 * 
	 * @param <T> prepared statement type
	 * @param s prepared statement to execute
	 * @param retriesLeft number of time that the getConnection will be called against an error occurred
	 * @return the executed statement object
	 */
//	protected <T extends PreparedStatement> T executeWithRetry(T s, int retriesLeft) {
//		return executeWithRetry(s, retriesLeft,params.getExecuteRetryWaitTime());
//	}
	/**
	 * Return the provided statement <T> after the execute method was done. In case of retry the statement may be result
	 * closed and in a unpredictable state. 
	 * 
	 * @param <T> prepared statement type
	 * @param s prepared statement to execute
	 * @param retriesLeft number of time that the getConnection will be called against an error occurred
	 * @param waitBetweenRetry wait time in millis between retries
	 * @return the executed statement object
	 */
//	protected <T extends PreparedStatement> T executeWithRetry(T s, int retriesLeft, long waitBetweenRetry) {
//		try {
//			s.execute();
//		} catch(SQLException cause) {
//			if (retriesLeft <= 0) {
//				throw new DataSourceException(cause);
//			} else {
//				if (waitBetweenRetry>0) {
//					try {
//						Thread.sleep(waitBetweenRetry);
//					} catch (InterruptedException e) {}
//				}
//				logger.error("com.seda.data.dao.DAOSupportExtended executeWithRetry() ");
//				logger.error("INFO: exception("+cause.getClass()+") caught when processing request: " + cause.getMessage());
//				logger.error("com.seda.data.dao.DAOSupportExtended executeWithRetry() ");
//				logger.error("INFO: Retrying request ");
//				return executeWithRetry(s, retriesLeft - 1, waitBetweenRetry); // retry execute
//			}
//		}
//		
//		return s;
//	}
	
	/**
	 * Returns the {@link DataSource} object used in the DAO
	 * @return <code>{@link DataSource}</code> the DataSource object
	 */	
	public DataSource getDataSource() {
		if (knownds) {
			logger.warn("this datasource has no effect on dao events.");			
		}
		
		return this.dataSource; 
	}
	
	/**
	 * Constructor of DAO object
	 * 
	 * @param datasource The {@link DataSource} object used in the DAO to get a connection
	 * @param schema the <code>String</code> that represents schema of the database objects
	 */
	public DAOSupportExtended(DataSource datasource, String schema) {
		if (datasource.getClass().getName().startsWith("com.seda.data.datasource")) {
			knownds=true; // events already handled
		} else {
			knownds=false; // no events handled
		}
		this.dataSource=datasource;
		this.schema = schema;		
	}
	
	/**
	 * Constructor of DAO object. The properties will be used to build an unpooled and stable datasource.
	 * <br/><code>config</code> must conains the following keys and values:</br>
	 * <i>driver</i>=the jdbc driver class name (required)</br>
	 * <i>url</i>=data base connection url (required)</br>
	 * <i>username</i>=authorized user name</br>
	 * <i>password</i>=user name password</br>
	 * <i>autocommit</i>=if connection wants to use autocommit</br>
	 * @param config The Properties objects containing the connection configuration
	 * @param schema the <code>String</code> that represents schema of the database objects
	 * @throws DataSourceException for unexpected parameter
	 */
	public DAOSupportExtended(Properties config, String schema) {
		if (config.containsKey("user")) {
			config.setProperty(DAOHelper.JDBC_USER, (String) config.remove("user"));
		}
		DataSourceFactoryImpl dataSourceFactoryImpl = new DataSourceFactoryImpl();
		dataSourceFactoryImpl.setProperties(config);
		this.dataSource=dataSourceFactoryImpl.getDataSource();
		this.schema = schema;
		this.knownds=true; // events already handled
	}
	
}
