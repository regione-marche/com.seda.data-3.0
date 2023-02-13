/**
 * 
 */
package com.seda.data.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.seda.commons.management.annotations.Description;
import com.seda.commons.management.annotations.ManagedAttribute;
import com.seda.commons.management.annotations.ManagedOperation;
import com.seda.commons.resource.ResourceManager;
import com.seda.data.event.DAOEventProxy;
/**
 * @author f.ricci
 *
 */
public class DataSourceImpl implements DataSource {

	private ClassLoader driverClassLoader;
	private Properties driverProperties;
	private boolean driverInitialized;

	private String driver;
	private String url;
	private String username;
	private String password;

	private boolean autoCommit;
	private Integer isolationLevel;

	private final Object _lock = new Object();
	
	private UserConnectionWrapperFactory connectionWrapperFactory;
	
	private volatile boolean suspend=false;

	@ManagedOperation @Description("Suspend connection requests on this datasource")
	public void suspend() {
		if (suspend) return;
		synchronized (_lock) {
			suspend=true;
		}
	}
	
	@ManagedOperation @Description("Resume connection requests on this datasource")
	public void resume() {
		if (!suspend) return;
        synchronized (_lock) {
            suspend=false;
            notify();
        }
	}
	
	@ManagedAttribute @Description("If connection requests are suspended")
	public boolean getSuspend() {
		return suspend;
	}	
	
	private void checkForSuspension() {
		// Check if should wait
        synchronized (_lock) {
            while (suspend) {
                try {
                	wait();
                } catch (Exception e) {
                }
            }
        }
	}	
	
	/**
	 * Sets the connection proxy factory 
	 * @param connectionWrapperFactory
	 */
	public void setUserConnectionWrapperFactory(UserConnectionWrapperFactory connectionWrapperFactory) {
		this.connectionWrapperFactory=connectionWrapperFactory;
	}
	
	public DataSourceImpl() {
	}

	public DataSourceImpl(String driver, String url, String username, String password) {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public DataSourceImpl(String driver, String url, Properties driverProperties) {
		this.driver = driver;
		this.url = url;
		this.driverProperties = driverProperties;
	}

	public DataSourceImpl(ClassLoader driverClassLoader, String driver, String url, String username, String password) {
		this.driverClassLoader = driverClassLoader;
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public DataSourceImpl(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
		this.driverClassLoader = driverClassLoader;
		this.driver = driver;
		this.url = url;
		this.driverProperties = driverProperties;
	}

	public Connection getConnection() throws SQLException {
		initializeDriver();
		Connection connection;
		if (driverProperties != null) {
			connection = DriverManager.getConnection(url, driverProperties);
		} else if (username == null || password == null) {
			connection = DriverManager.getConnection(url);
		} else {
			connection = DriverManager.getConnection(url, username, password);
		}
		
		return configureConnection(connection);
	}

	public Connection getConnection(String username, String password) throws SQLException {
		initializeDriver();
		Connection connection = DriverManager.getConnection(url, username, password);
		
		return configureConnection(connection);
	}

	public void setLoginTimeout(int loginTimeout) throws SQLException {
		DriverManager.setLoginTimeout(loginTimeout);
	}

	public int getLoginTimeout() throws SQLException {
		return DriverManager.getLoginTimeout();
	}

	public void setLogWriter(PrintWriter logWriter) throws SQLException {
		DriverManager.setLogWriter(logWriter);
	}

	public PrintWriter getLogWriter() throws SQLException {
		return DriverManager.getLogWriter();
	}

	public ClassLoader getDriverClassLoader() {
		return driverClassLoader;
	}

	public void setDriverClassLoader(ClassLoader driverClassLoader) {
		this.driverClassLoader = driverClassLoader;
	}

	public Properties getDriverProperties() {
		return driverProperties;
	}

	public void setDriverProperties(Properties driverProperties) {
		this.driverProperties = driverProperties;
	}
	
	@ManagedAttribute
	public String getDriver() {
		return driver;
	}

	public synchronized void setDriver(String driver) {
		this.driver = driver;
		driverInitialized = false;
	}
	
	@ManagedAttribute
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@ManagedAttribute
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@ManagedAttribute
	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	@ManagedAttribute
	public Integer getIsolationLevel() {
		return isolationLevel;
	}

	public void setIsolationLevel(Integer isolationLevel) {
		this.isolationLevel = isolationLevel;
	}

	private Connection configureConnection(Connection conn) throws SQLException {
		if (autoCommit != conn.getAutoCommit()) {
			conn.setAutoCommit(autoCommit);
		}
		if (isolationLevel != null) {
			conn.setTransactionIsolation(isolationLevel);
		}
		if (connectionWrapperFactory!=null) {
			conn=connectionWrapperFactory.createWrapper(conn);
		}
		
		// before returning connection fire the open connection event
		DAOEventProxy.dispatch(conn);
		
		return conn;
	}

	private synchronized void initializeDriver() {
		checkForSuspension();
		if (!driverInitialized) {
			driverInitialized = true;
			Class<?> driverType;
			try {
				if (driverClassLoader != null) {
					driverType = Class.forName(driver, true, driverClassLoader);
				} else {
					driverType = ResourceManager.classForName(driver);
				}
				DriverManager.registerDriver(new DriverWrapper((Driver) driverType.newInstance()));
			} catch (Exception e) {
				throw new DataSourceException("Error setting driver on DataSourceImpl. Cause: " + e, e);
			}
		}
	}

	private static class DriverWrapper implements Driver {
		private Driver driver;

		DriverWrapper(Driver d) {
			this.driver = d;
		}

		public boolean acceptsURL(String u) throws SQLException {
			return this.driver.acceptsURL(u);
		}

		public Connection connect(String u, Properties p) throws SQLException {
			return this.driver.connect(u, p);
		}

		public int getMajorVersion() {
			return this.driver.getMajorVersion();
		}

		public int getMinorVersion() {
			return this.driver.getMinorVersion();
		}

		public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
			return this.driver.getPropertyInfo(u, p);
		}

		public boolean jdbcCompliant() {
			return this.driver.jdbcCompliant();
		}

		public Logger getParentLogger() throws SQLFeatureNotSupportedException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	// version 1.6
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}
