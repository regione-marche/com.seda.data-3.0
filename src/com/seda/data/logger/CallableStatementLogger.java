package com.seda.data.logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;

import java.sql.ResultSet;

import com.seda.commons.logging.Log;
import com.seda.commons.logging.LogFactory;
import com.seda.commons.reflection.ExceptionUtil;
/**
 * PreparedStatement proxy to add logging
 */
public class CallableStatementLogger extends JdbcLogger implements InvocationHandler {

	private static final Log log = LogFactory.getLog(CallableStatement.class);

	private CallableStatement statement;
	private String sql;

	private CallableStatementLogger(CallableStatement stmt, String sql) {
		this.statement = stmt;
		this.sql = sql;
	}

	public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
		try {
			if (EXECUTE_METHODS.contains(method.getName())) {
				if (log.isDebugEnabled()) {
					log.debug("==>  Executing: " + removeExcessiveBlank(sql));
					log.debug("==> Parameters: " + getParameterValueString());
				}
				clearColumnInfo();
				if ("executeQuery".equals(method.getName())) {
					ResultSet rs = (ResultSet) method.invoke(statement, params);
					if (rs != null) {
						return ResultSetLogger.getInstance(rs);
					} else {
						return null;
					}
				} else {
					return method.invoke(statement, params);
				}
			} else if (SET_METHODS.contains(method.getName())) {
				if ("setNull".equals(method.getName())) {
					setColumn(params[0], null);
				} else {
					setColumn(params[0], params[1]);
				}
				return method.invoke(statement, params);
			} else if ("getResultSet".equals(method.getName())) {
				ResultSet rs = (ResultSet) method.invoke(statement, params);
				if (rs != null) {
					return ResultSetLogger.getInstance(rs);
				} else {
					return null;
				}
			} else if ("equals".equals(method.getName())) {
				Object ps = params[0];
				return ps instanceof Proxy && proxy == ps;
			} else if ("hashCode".equals(method.getName())) {
				return proxy.hashCode();
			} else {
				return method.invoke(statement, params);
			}
		} catch (Throwable t) {
			throw ExceptionUtil.unwrapThrowable(t);
		}
	}

	/**
	 * Creates a logging version of a CallableStatement
	 *
	 * @param stmt - the statement
	 * @param sql  - the sql statement
	 * @return - the proxy
	 */
	public static CallableStatement newInstance(CallableStatement stmt, String sql) {
		InvocationHandler handler = new CallableStatementLogger(stmt, sql);
		ClassLoader cl = CallableStatement.class.getClassLoader();
		return (CallableStatement) Proxy.newProxyInstance(cl, new Class[]{CallableStatement.class}, handler);
	}

	/**
	 * Return the wrapped prepared statement
	 *
	 * @return the PreparedStatement
	 */
	public CallableStatement getCallableStatement() {
		return statement;
	}

}
