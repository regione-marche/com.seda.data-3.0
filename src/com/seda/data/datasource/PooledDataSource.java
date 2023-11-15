/**
 * 
 */
package com.seda.data.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.seda.commons.logger.CustomLoggerManager;
import com.seda.commons.logger.LoggerWrapper;
import com.seda.commons.management.annotations.Description;
import com.seda.commons.management.annotations.ManagedAttribute;
import com.seda.commons.management.annotations.ManagedOperation;
import com.seda.data.dao.DAOHelper;
import com.seda.data.event.ConnectionOpenEvent;
import com.seda.data.event.DAOEventHandler;
import com.seda.data.event.DAOEventProxy;
import com.seda.data.event.servlet.DAOEventContext;
import com.seda.data.event.servlet.DAOEventLocal;
import com.seda.data.logger.StatementLogger;
/**
 * @author f.ricci
 *
 */
public class PooledDataSource implements DataSource {

	private static final LoggerWrapper logger =  CustomLoggerManager.get(PooledDataSource.class);

	private final DataSourceMonitor monitor = new DataSourceMonitor(this);

	private final Object _lock = new Object();
	
	private final DataSourceImpl dataSource;
	private UserConnectionWrapperFactory connectionWrapperFactory;
	// OPTIONAL CONFIGURATION FIELDS
	protected int maximumActiveConnections = 10;
	protected int maximumIdleConnections = 5;
	protected int maximumCheckoutTime = 20000;
	protected int timeToWait = 20000;
	protected String pingQuery = "set with a valid ping query";
	protected boolean pingEnabled = false;
	protected int pingConnectionTimeout = 0;

	private int connectionTypeCode;

	private volatile boolean suspend=false;
	@ManagedOperation @Description("Suspend pop connection requests on this datasource")
	public void suspend() {
		if (suspend) return;
		synchronized (_lock) {
			suspend=true;
		}
	}
	
	@ManagedOperation @Description("Resume pop connection requests on this datasource")
	public void resume() {
		if (!suspend) return;
        synchronized (_lock) {
            suspend=false;
            notify();
        }
	}
	
	@ManagedAttribute @Description("If pop connection requests are suspended")
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
	
	public PooledDataSource() {
		dataSource = new DataSourceImpl();
	}

	public PooledDataSource(String driver, String url, String username, String password) {
		dataSource = new DataSourceImpl(driver, url, username, password);
		forceCloseAll();
	}

	public PooledDataSource(String driver, String url, Properties driverProperties) {
		dataSource = new DataSourceImpl(driver, url, driverProperties);
		forceCloseAll();
	}

	public PooledDataSource(ClassLoader driverClassLoader, String driver, String url, String username, String password) {
		dataSource = new DataSourceImpl(driverClassLoader, driver, url, username, password);
		forceCloseAll();		
	}

	public PooledDataSource(ClassLoader driverClassLoader, String driver, String url, Properties driverProperties) {
		dataSource = new DataSourceImpl(driverClassLoader, driver, url, driverProperties);
		forceCloseAll();
	}

	public Connection getConnection() throws SQLException {
		
		Connection conn = createUserConnectionWrapper(pop(dataSource.getUsername(), dataSource.getPassword()).getConnectionProxy());
		// before returning connection fire the open connection event
		DAOEventProxy.dispatch(conn);
		
		return conn;
	}

	public Connection getConnection(String username, String password) throws SQLException {
		
		Connection conn = createUserConnectionWrapper(pop(username, password).getConnectionProxy());
		
		// before returning connection fire the open connection event
		DAOEventProxy.dispatch(conn);
		
		return conn;
	}

	private Connection createUserConnectionWrapper(Connection connection) {
		if (connectionWrapperFactory!=null)
			return connectionWrapperFactory.createWrapper(connection);
		return connection;
	}
	
	public void setLoginTimeout(int loginTimeout) throws SQLException {
		DriverManager.setLoginTimeout(loginTimeout);
	}
	@ManagedAttribute
	public int getLoginTimeout() throws SQLException {
		return DriverManager.getLoginTimeout();
	}

	public void setLogWriter(PrintWriter logWriter) throws SQLException {
		DriverManager.setLogWriter(logWriter);
	}

	public PrintWriter getLogWriter() throws SQLException {
		return DriverManager.getLogWriter();
	}

	public void setUserConnectionWrapperFactory(UserConnectionWrapperFactory connectionWrapperFactory) {
		this.connectionWrapperFactory=connectionWrapperFactory;
		forceCloseAll();
	}	
	
	public void setDriver(String driver) {
		dataSource.setDriver(driver);
		forceCloseAll();
	}

	public void setUrl(String url) {
		dataSource.setUrl(url);
		forceCloseAll();
	}

	public void setUsername(String username) {
		dataSource.setUsername(username);
		forceCloseAll();
	}

	public void setPassword(String password) {
		dataSource.setPassword(password);
		forceCloseAll();
	}

	public void setAutoCommit(boolean autoCommit) {
		dataSource.setAutoCommit(autoCommit);
		forceCloseAll();
	}

	public void setIsolationLevel(Integer isolationLevel) {
		dataSource.setIsolationLevel(isolationLevel);
		forceCloseAll();
	}


	public void setDriverProperties(Properties driverProps) {
		dataSource.setDriverProperties(driverProps);
		forceCloseAll();
	}

	/**
	 * The maximum number of active connections
	 *
	 * @param poolMaximumActiveConnections The maximum number of active connections
	 */
	public void setMaximumActiveConnections(int maximumActiveConnections) {
		this.maximumActiveConnections = maximumActiveConnections;
		forceCloseAll();
	}

	/**
	 * The maximum number of idle connections
	 *
	 * @param poolMaximumIdleConnections The maximum number of idle connections
	 */
	public void setMaximumIdleConnections(int maximumIdleConnections) {
		this.maximumIdleConnections = maximumIdleConnections;
		forceCloseAll();
	}

	/**
	 * The maximum time a connection can be used before it *may* be
	 * given away again.
	 *
	 * @param poolMaximumCheckoutTime The maximum time
	 */
	public void setMaximumCheckoutTime(int maximumCheckoutTime) {
		this.maximumCheckoutTime = maximumCheckoutTime;
		forceCloseAll();
	}

	/**
	 * The time to wait before retrying to get a connection
	 *
	 * @param poolTimeToWait The time to wait
	 */
	public void setTimeToWait(int timeToWait) {
		this.timeToWait = timeToWait;
		forceCloseAll();
	}

	/**
	 * The query to be used to check a connection
	 *
	 * @param poolPingQuery The query
	 */
	@ManagedAttribute @Description("Query used to ping connection")
	public void setPingQuery(String pingQuery) {
		this.pingQuery = pingQuery;
		forceCloseAll();
	}

	/**
	 * Determines if the ping query should be used.
	 *
	 * @param poolPingEnabled True if we need to check a connection before using it
	 */
	@ManagedAttribute @Description("If this datasource ping available connection")	 
	public void setPingEnabled(boolean pingEnabled) {
		this.pingEnabled = pingEnabled;
		forceCloseAll();
	}

	/**
	 * If a connection has not been used in this many milliseconds, ping the
	 * database to make sure the connection is still good.
	 *
	 * @param milliseconds the number of milliseconds of inactivity that will trigger a ping
	 */
	@ManagedAttribute	 
	public void setPingConnectionTimeout(int milliseconds) {
		this.pingConnectionTimeout = milliseconds;
		forceCloseAll();
	}
	@ManagedAttribute
	public String getDriver() {
		return dataSource.getDriver();
	}
	@ManagedAttribute
	public String getUrl() {
		return dataSource.getUrl();
	}
	@ManagedAttribute
	public String getUsername() {
		return dataSource.getUsername();
	}

	public String getPassword() {
		return dataSource.getPassword();
	}
	@ManagedAttribute
	public boolean isAutoCommit() {
		return dataSource.isAutoCommit();
	}
	@ManagedAttribute
	public Integer getIsolationLevel() {
		return dataSource.getIsolationLevel();
	}
	@ManagedAttribute
	public Properties getDriverProperties() {
		return dataSource.getDriverProperties();
	}
	@ManagedAttribute
	public int getMaximumActiveConnections() {
		return maximumActiveConnections;
	}
	@ManagedAttribute
	public int getMaximumIdleConnections() {
		return maximumIdleConnections;
	}
	@ManagedAttribute
	public int getMaximumCheckoutTime() {
		return maximumCheckoutTime;
	}
	@ManagedAttribute
	public int getTimeToWait() {
		return timeToWait;
	}
	@ManagedAttribute
	public String getPingQuery() {
		return pingQuery;
	}
	@ManagedAttribute
	public boolean isPingEnabled() {
		return pingEnabled;
	}
	@ManagedAttribute
	public int getPingConnectionTimeout() {
		return pingConnectionTimeout;
	}

	/**
	 * Closes all active and idle connections in the pool
	 */
	@ManagedOperation @Description("Force to close all active connection")
	public void forceCloseAll() {
		synchronized (monitor) {
			connectionTypeCode = getConnectionTypeCode(dataSource.getUrl(), dataSource.getUsername(), dataSource.getPassword());
			for (int i = monitor.activeConnections.size(); i > 0; i--) {
				try {
					ConnectionWrapper conn = (ConnectionWrapper) monitor.activeConnections.remove(i - 1);
					conn.invalidate();

					Connection realConn = conn.getConnection();
					if (!realConn.getAutoCommit()) {
						realConn.rollback();
					}
					realConn.close();
				} catch (Exception e) {
					// ignore
				}
			}
			for (int i = monitor.idleConnections.size(); i > 0; i--) {
				try {
					ConnectionWrapper conn = (ConnectionWrapper) monitor.idleConnections.remove(i - 1);
					conn.invalidate();

					Connection realConn = conn.getConnection();
					if (!realConn.getAutoCommit()) {
						realConn.rollback();
					}
					realConn.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
		logger.debug("PooledDataSource forcefully closed/removed all connections.");
	}

	public DataSourceMonitor getPoolMonitor() {
		return monitor;
	}
	@ManagedOperation
	private int getConnectionTypeCode(String url, String username, String password) {
		return (""+url+username+password).hashCode();
	}

	protected void push(ConnectionWrapper conn) throws SQLException {

		synchronized (monitor) {
			monitor.activeConnections.remove(conn);
			if (conn.isValid()) {
				if (monitor.idleConnections.size() < maximumIdleConnections && conn.getConnectionTypeCode() == connectionTypeCode) {
					monitor.accumulatedCheckoutTime += conn.getCheckoutTime();
					if (!conn.getConnection().getAutoCommit()) {
						conn.getConnection().rollback();
					}
					ConnectionWrapper newConn = new ConnectionWrapper(conn.getConnection(), this);
					monitor.idleConnections.add(newConn);
					newConn.setCreationTime(conn.getCreationTime());
					newConn.setLastUsedTime(conn.getLastUsedTime());
					conn.invalidate();
					logger.debug("Returned connection " + newConn.getConnectionHashCode() + " to pool.");
					monitor.notifyAll();
				} else {
					monitor.accumulatedCheckoutTime += conn.getCheckoutTime();
					if (!conn.getConnection().getAutoCommit()) {
						conn.getConnection().rollback();
					}
					conn.getConnection().close();
					logger.debug("Closed connection " + conn.getConnectionHashCode() + ".");
					conn.invalidate();
				}
			} else {
				logger.debug("A bad connection (" + conn.getConnectionHashCode() + ") attempted to return to the pool, discarding connection.");
				monitor.badConnectionCount++;
			}
		}
	}

	private ConnectionWrapper pop(String username, String password) throws SQLException {
		boolean countedWait = false;
		ConnectionWrapper conn = null;
		long t = System.currentTimeMillis();
		int localBadConnectionCount = 0;

		checkForSuspension();
		
		while (conn == null) {
			synchronized (monitor) {
				if (monitor.idleConnections.size() > 0) {
					// Pool has available connection
					conn = (ConnectionWrapper) monitor.idleConnections.remove(0);
					logger.debug("Checked out connection " + conn.getConnectionHashCode() + " from pool.");
				} else {
					// Pool does not have available connection
					if (monitor.activeConnections.size() < maximumActiveConnections) {
						// Can create new connection
						conn = new ConnectionWrapper(dataSource.getConnection(), this);
						Connection realConn = conn.getConnection();
						logger.debug("Created connection " + conn.getConnectionHashCode() + ".");
					} else {
						// Cannot create new connection
						ConnectionWrapper oldestActiveConnection = (ConnectionWrapper) monitor.activeConnections.get(0);
						long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
						if (longestCheckoutTime > maximumCheckoutTime) {
							// Can claim overdue connection
							monitor.claimedOverdueConnectionCount++;
							monitor.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
							monitor.accumulatedCheckoutTime += longestCheckoutTime;
							monitor.activeConnections.remove(oldestActiveConnection);
							if (!oldestActiveConnection.getConnection().getAutoCommit()) {
								oldestActiveConnection.getConnection().rollback();
							}
							conn = new ConnectionWrapper(oldestActiveConnection.getConnection(), this);
							oldestActiveConnection.invalidate();
							logger.debug("Claimed overdue connection " + conn.getConnectionHashCode() + ".");
						} else {
							// Must wait
							try {
								if (!countedWait) {
									monitor.hadToWaitCount++;
									countedWait = true;
								}
								logger.debug("Waiting as long as " + timeToWait + " milliseconds for connection.");
								long wt = System.currentTimeMillis();
								monitor.wait(timeToWait);
								monitor.accumulatedWaitTime += System.currentTimeMillis() - wt;
							} catch (InterruptedException e) {
								break;
							}
						}
					}
				}
				if (conn != null) {
					if (conn.isValid()) {
						if (!conn.getConnection().getAutoCommit()) {
							conn.getConnection().rollback();
						}
						conn.setConnectionTypeCode(getConnectionTypeCode(dataSource.getUrl(), username, password));
						conn.setCheckoutElapsedTime(System.currentTimeMillis());
						conn.setLastUsedTime(System.currentTimeMillis());
						monitor.activeConnections.add(conn);
						monitor.requestCount++;
						monitor.accumulatedRequestTime += System.currentTimeMillis() - t;
					} else {
						logger.debug("A bad connection (" + conn.getConnectionHashCode() + ") was returned from the pool, getting another connection.");
						monitor.badConnectionCount++;
						localBadConnectionCount++;
						conn = null;
						if (localBadConnectionCount > (maximumIdleConnections + 3)) {
							logger.debug("PooledDataSource: Could not get a good connection to the database.");
							throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
						}
					}
				}
			}

		}

		if (conn == null) {
			logger.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
			throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
		}

		return conn;
	}

	/**
	 * Method to check to see if a connection is still usable
	 *
	 * @param conn - the connection to check
	 * @return True if the connection is still usable
	 */
	protected boolean pingConnection(ConnectionWrapper conn) {
		boolean result = true;

		try {
			result = !conn.getConnection().isClosed();
		} catch (SQLException e) {
			logger.debug("Connection " + conn.getConnectionHashCode() + " is BAD: " + e.getMessage());
			result = false;
		}

		if (result) {
			if (pingEnabled) {
				if (pingConnectionTimeout >= 0 && conn.getElapsedTimeSinceLastUse() > pingConnectionTimeout) {
					try {
						logger.debug("Testing connection " + conn.getConnectionHashCode() + " ...");
						Connection realConn = conn.getConnection();
						Statement statement = realConn.createStatement();
						ResultSet rs = statement.executeQuery(pingQuery);
						rs.close();
						statement.close();
						if (!realConn.getAutoCommit()) {
							realConn.rollback();
						}
						result = true;
						logger.debug("Connection " + conn.getConnectionHashCode() + " is GOOD!");
					} catch (Exception e) {
						logger.debug("Execution of ping query '" + pingQuery + "' failed: " + e.getMessage());
						try {
							conn.getConnection().close();
						} catch (Exception e2) {
							//ignore
						}
						result = false;
						logger.debug("Connection " + conn.getConnectionHashCode() + " is BAD: " + e.getMessage());
					}
				}
			}
		}
		return result;
	}

	/**
	 * Unwraps a pooled connection to get to the 'real' connection
	 *
	 * @param conn - the pooled connection to unwrap
	 * @return The 'real' connection
	 */
	public static Connection unwrapConnection(Connection conn) {
		if (conn instanceof ConnectionWrapper) {
			return ((ConnectionWrapper) conn).getConnection();
		} else {
			return conn;
		}
	}

	protected void finalize() throws Throwable {
		forceCloseAll();
	}

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
