package com.seda.data.logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.Statement;

import com.seda.commons.logger.CustomLoggerManager;
import com.seda.commons.logger.LoggerWrapper;
import com.seda.commons.reflection.ExceptionUtil;
import com.seda.data.procedure.SessionFactoryImpl;

/**
 * Statement proxy to add logging
 */
public class StatementLogger extends JdbcLogger implements InvocationHandler {

	private static final LoggerWrapper log =  CustomLoggerManager.get(StatementLogger.class);

	private Statement statement;

	private StatementLogger(Statement stmt) {
		super();
		this.statement = stmt;
	}

	public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
		try {
			if (EXECUTE_METHODS.contains(method.getName())) {
				if (log.isDebugEnabled()) {
					log.debug("==>  Executing: " + removeExcessiveBlank((String) params[0]));
				}
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
	 * Creates a logging version of a Statement
	 *
	 * @param stmt - the statement
	 * @return - the proxy
	 */
	public static Statement newInstance(Statement stmt) {
		InvocationHandler handler = new StatementLogger(stmt);
		ClassLoader cl = Statement.class.getClassLoader();
		return (Statement) Proxy.newProxyInstance(cl, new Class[]{Statement.class}, handler);
	}

	/**
	 * return the wrapped statement
	 *
	 * @return the statement
	 */
	public Statement getStatement() {
		return statement;
	}

}
