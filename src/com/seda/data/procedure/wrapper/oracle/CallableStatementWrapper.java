/**
 * 
 */
package com.seda.data.procedure.wrapper.oracle;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import com.seda.commons.logging.Log;
import com.seda.commons.logging.LogFactory;
import com.seda.commons.reflection.ExceptionUtil;
import com.seda.data.dao.DAOHelper;
import com.seda.data.logger.JdbcLogger;
import com.seda.data.procedure.reflection.MetaProcedure;
import com.seda.data.procedure.reflection.ProcedureParameter;

/**
 * @author f.ricci
 *
 */
public class CallableStatementWrapper extends JdbcLogger implements InvocationHandler {

	private static final Log log = LogFactory.getLog(CallableStatement.class);

	private CallableStatement callableStatement;
	private MetaProcedure metaProcedure;
	private List<ProcedureParameter> refCursorList;
	private Iterator<ProcedureParameter> refCursorIterator;
	private ResultSet currentResultSet=null;

	public CallableStatementWrapper(CallableStatement callableStatement, MetaProcedure metaProcedure) {
		this.callableStatement=callableStatement;
		this.metaProcedure=metaProcedure;
		this.refCursorList=metaProcedure.getParameterCursorList();
		this.refCursorIterator=refCursorList.iterator();
	}

	public Object invoke(Object proxy, Method method, Object[] params)
			throws Throwable {
		try {
			if (RESULT_METHODS.contains(method.getName())) {
				if (log.isDebugEnabled()) {
					log.debug("==>  Executing: " + metaProcedure.getSQLCall() + " > " + method.getName());
				}
				if ("executeQuery".equals(method.getName())) {
					method.invoke(callableStatement, params);
					if (pointToNextResult(null)) {
						return currentResultSet;
					} else {
						return null;
					}
				} else if ("execute".equals(method.getName())) {
					method.invoke(callableStatement, params);
					return pointToNextResult(null);
				} else if ("getResultSet".equals(method.getName())) {
					return currentResultSet;
				} else if ("getMoreResults".equals(method.getName())) {
					return pointToNextResult(params);
				}
			}
			if ("equals".equals(method.getName())) {
				Object ps = params[0];
				return ps instanceof Proxy && proxy == ps;
			} else if ("hashCode".equals(method.getName())) {
				return proxy.hashCode();
			} else {
				return method.invoke(callableStatement, params);
			}
		} catch (Throwable t) {
			throw ExceptionUtil.unwrapThrowable(t);
		}
	}

	private boolean pointToNextResult(Object[] params) throws SQLException {
		if (refCursorIterator.hasNext()) {
			
			if (params!=null && params.length==1 && params[0] instanceof java.lang.Integer) {
				switch ((Integer)params[0]) {
				case Statement.KEEP_CURRENT_RESULT:
					break;
				case Statement.CLOSE_ALL_RESULTS:
					while (refCursorIterator.hasNext()) {
						final ProcedureParameter refCursor = refCursorIterator.next();
						currentResultSet=(ResultSet)callableStatement.getObject(refCursor.getIndex());
						DAOHelper.closeIgnoringException(currentResultSet);
					}
					return false;
				case Statement.CLOSE_CURRENT_RESULT:
					DAOHelper.closeIgnoringException(currentResultSet);
				default:
					throw new SQLException("current must be one of the following of the following: Statement.CLOSE_CURRENT_RESULT, Statement.KEEP_CURRENT_RESULT, or Statement.CLOSE_ALL_RESULTS");
				}
			} else {
				DAOHelper.closeIgnoringException(currentResultSet);
			}
			
			ProcedureParameter refCursor = refCursorIterator.next();
			currentResultSet=(ResultSet)callableStatement.getObject(refCursor.getIndex());
			return true;
		}
		return false;
	}

	/**
	 * Creates a version of a CallableStatement
	 *
	 * @param stmt - the statement
	 * @param sql  - the sql statement
	 * @return - the proxy
	 */
	public static CallableStatement newInstance(CallableStatement callableStatement, MetaProcedure metaProcedure) {
		InvocationHandler handler = new CallableStatementWrapper(callableStatement, metaProcedure);
		ClassLoader cl = CallableStatement.class.getClassLoader();
		return (CallableStatement) Proxy.newProxyInstance(cl, new Class[]{CallableStatement.class}, handler);
	}

	/**
	 * Return the wrapped prepared statement
	 *
	 * @return the PreparedStatement
	 */
	public CallableStatement getCallableStatement() {
		return callableStatement;
	}
}
