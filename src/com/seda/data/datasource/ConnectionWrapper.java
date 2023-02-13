/**
 * 
 */
package com.seda.data.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import com.seda.commons.reflection.ExceptionUtil;

/**
 * This is a special class that allows intercept a set of methods identified by a Connection interface
 * 
 * @author f.ricci
 *
 */
public class ConnectionWrapper implements InvocationHandler {

	private static final String CLOSE = "close";
	private static final String IS_CLOSED = "isClosed";
	private static final Class[] IFACES = new Class[]{Connection.class};

	private int hashCode = 0;
	private PooledDataSource dataSource;
	private Connection connection;
	private Connection connectionProxy;
	private long checkoutElpaseTime;
	private long creationTime;
	private long lastUsedTime;
	private int connectionTypeCode;
	private boolean valid;	

	/**
	 * Constructor for ConnectionWrapper that uses the Connection and PooledDataSource passed in
	 *
	 * @param connection - the connection that is to be presented as a pooled connection
	 * @param dataSource - the dataSource that the connection is from
	 */
	public ConnectionWrapper(Connection connection, PooledDataSource dataSource) {
		this.hashCode = connection.hashCode();
		this.connection = connection;
		this.dataSource = dataSource;
		this.creationTime = System.currentTimeMillis();
		this.lastUsedTime = System.currentTimeMillis();
		this.valid = true;

		connectionProxy = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
	}

	/**
	 * Invalidates the connection
	 */
	public void invalidate() {
		valid = false;
	}

	/**
	 * Method to see if the connection is usable
	 *
	 * @return True if the connection is usable
	 */
	public boolean isValid() {
		return valid && connection != null && dataSource.pingConnection(this);
	}

	/**
	 * Getter for the *real* connection that this wraps
	 *
	 * @return The connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Getter for the proxy for the connection
	 *
	 * @return The proxy
	 */
	public Connection getConnectionProxy() {
		return connectionProxy;
	}

	/**
	 * Gets the hashcode of the real connection (or 0 if it is null)
	 *
	 * @return The hashcode of the real connection (or 0 if it is null)
	 */
	public int getConnectionHashCode() {
		if (connection == null) {
			return 0;
		} else {
			return connection.hashCode();
		}
	}

	/**
	 * Getter for the connection type (based on url + user + password)
	 *
	 * @return The connection type
	 */
	public int getConnectionTypeCode() {
		return connectionTypeCode;
	}

	/**
	 * Setter for the connection type
	 *
	 * @param connectionTypeCode - the connection type
	 */
	public void setConnectionTypeCode(int connectionTypeCode) {
		this.connectionTypeCode = connectionTypeCode;
	}

	/**
	 * Getter for the time that the connection was created
	 *
	 * @return The creation timestamp
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * Setter for the time that the connection was created
	 *
	 * @param createdTimestamp - the timestamp
	 */
	public void setCreationTime(long createdTimestamp) {
		this.creationTime = createdTimestamp;
	}

	/**
	 * Getter for the time that the connection was last used
	 *
	 * @return - the timestamp
	 */
	public long getLastUsedTime() {
		return lastUsedTime;
	}

	/**
	 * Setter for the time that the connection was last used
	 *
	 * @param lastUsedTimestamp - the timestamp
	 */
	public void setLastUsedTime(long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	/**
	 * Getter for the time since this connection was last used
	 *
	 * @return - the time since the last use
	 */
	public long getElapsedTimeSinceLastUse() {
		return System.currentTimeMillis() - lastUsedTime;
	}

	/**
	 * Getter for the age of the connection
	 *
	 * @return the age
	 */
	public long getAge() {
		return System.currentTimeMillis() - creationTime;
	}

	/**
	 * Getter for the timestamp that this connection was checked out
	 *
	 * @return the timestamp
	 */
	public long getCheckoutElapsedTime() {
		return checkoutElpaseTime;
	}

	/**
	 * Setter for the timestamp that this connection was checked out
	 *
	 * @param timestamp the timestamp
	 */
	public void setCheckoutElapsedTime(long timestamp) {
		this.checkoutElpaseTime = timestamp;
	}

	/**
	 * Getter for the time that this connection has been checked out
	 *
	 * @return the time
	 */
	public long getCheckoutTime() {
		return System.currentTimeMillis() - checkoutElpaseTime;
	}

	public int hashCode() {
		return hashCode;
	}

	/**
	 * Allows comparing this connection to another
	 *
	 * @param obj - the other connection to test for equality
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ConnectionWrapper) {
			return connection.hashCode() == (((ConnectionWrapper) obj).connection.hashCode());
		} else if (obj instanceof Connection) {
			return hashCode == obj.hashCode();
		} else {
			return false;
		}
	}

	/**
	 * Required for InvocationHandler implementation.
	 *
	 * @param proxy  - not used
	 * @param method - the method to be executed
	 * @param args   - the parameters to be passed to the method
	 * @see java.lang.reflect.InvocationHandler#invoke(Object, java.lang.reflect.Method, Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String methodName = method.getName();
		if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
			dataSource.push(this);
			return null;
		} else if (IS_CLOSED.hashCode()==methodName.hashCode() && IS_CLOSED.equals(methodName) && !valid) {
			return Boolean.TRUE;
		} else {
			try {
				return method.invoke(getValidConnection(), args);
			} catch (Throwable t) {
				throw ExceptionUtil.unwrapThrowable(t);
			}
		}
	}

	private Connection getValidConnection() {
		if (!valid) {
			throw new DataSourceException("Error accessing ConnectionWrapper. Connection is invalid.");
		}
		return connection;
	}

}
