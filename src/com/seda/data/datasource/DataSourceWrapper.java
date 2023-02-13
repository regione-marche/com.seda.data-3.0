/**
 * 
 */
package com.seda.data.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import javax.sql.DataSource;

import com.seda.commons.reflection.ExceptionUtil;
import com.seda.data.event.DAOEventProxy;

/**
 * @author f.ricci
 *
 */
public class DataSourceWrapper implements InvocationHandler {

	private static final String GET_CONNECTION = "getConnection";
	private static final Class[] IFACES = new Class[]{DataSource.class};

	private int hashCode = 0;
	private DataSource dataSource;
	private DataSource dataSourceProxy;

	/**
	 * Constructor for DataSourceWrapper that handle connection open event
	 *
	 * @param dataSource - the dataSource
	 */
	public DataSourceWrapper(DataSource dataSource) {
		this.hashCode = dataSource.hashCode();
		this.dataSource = dataSource;

		dataSourceProxy = (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(), IFACES, this);
	}	
	
	/**
	 * Getter for the *real* dataSource that this wraps
	 *
	 * @return The data source
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Getter for the proxy for the data source
	 *
	 * @return The proxy
	 */
	public DataSource getDataSourceProxy() {
		return dataSourceProxy;
	}
	
	/**
	 * Gets the hashcode of the real data source (or 0 if it is null)
	 *
	 * @return The hashcode of the real data source (or 0 if it is null)
	 */
	public int getDatasourceHashCode() {
		if (dataSource == null) {
			return 0;
		} else {
			return dataSource.hashCode();
		}
	}
	
	public int hashCode() {
		return hashCode;
	}
	
	/**
	 * Allows comparing this data source to another
	 *
	 * @param obj - the other data source to test for equality
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof DataSourceWrapper) {
			return dataSource.hashCode() == (((DataSourceWrapper) obj).dataSource.hashCode());
		} else if (obj instanceof DataSource) {
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
		if (GET_CONNECTION.hashCode() == methodName.hashCode() && GET_CONNECTION.equals(methodName)) {
			Connection connection = null;
			try {
				connection = (Connection)method.invoke(dataSource, args);
			} catch (Throwable t) {
				throw ExceptionUtil.unwrapThrowable(t);
			}
			
			DAOEventProxy.dispatch(connection);
			
			return connection;
		} else {
			try {
				return method.invoke(dataSource, args);
			} catch (Throwable t) {
				throw ExceptionUtil.unwrapThrowable(t);
			}
		}
	}

}
