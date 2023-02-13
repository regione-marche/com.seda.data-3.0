package com.seda.data.logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.seda.commons.logging.Log;
import com.seda.commons.logging.LogFactory;
import com.seda.commons.reflection.ExceptionUtil;
import com.seda.data.export.ExportHandler;
/**
 * ResultSet proxy to add logging
 */
public class ResultSetLogger extends JdbcLogger implements InvocationHandler {

	private static final Log log = LogFactory.getLog(ResultSet.class);

	boolean first = true;
	private ExportHandler exportHandler;
	private ResultSet resultSet;

	private ResultSetLogger(ResultSet rs) {
		super();
		this.resultSet = rs;
	}

	public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
		try {
			Object o = method.invoke(resultSet, params);
			if ("next".equals(method.getName())) {
				boolean hasNext = (Boolean) o; 
				if (hasNext) {
					if (log.isDebugEnabled()) {
						if (first) {
							first = false;
							exportHandler = new ResultSetExport();
							exportHandler.setCharacterDelimiter(',');
							printColumnHeaders(exportHandler.open(resultSet, true, false));
						}
						printColumnValues(exportHandler.fetch(hasNext));
					}
				}
			}
			return o;
		} catch (Throwable t) {
			throw ExceptionUtil.unwrapThrowable(t);
		}
	}

	private void printColumnHeaders(String[] headerRows) throws SQLException {
		if (headerRows!=null) {
			if (headerRows.length==1 && headerRows[0]!=null)
				log.debug("<==    Columns: ".concat(headerRows[0]));
			if (headerRows.length==2 && headerRows[1]!=null)
				log.debug("                ".concat(headerRows[1]));			
		}
	}

	private void printColumnValues(String row) throws SQLException {
		log.debug("<==        Row: ".concat(row));
	}

	/**
	 * Creates a logging version of a ResultSet
	 *
	 * @param rs - the ResultSet to proxy
	 * @return - the ResultSet with logging
	 */
	public static ResultSet getInstance(ResultSet rs) {
		InvocationHandler handler = new ResultSetLogger(rs);
		ClassLoader cl = ResultSet.class.getClassLoader();
		return (ResultSet) Proxy.newProxyInstance(cl, new Class[]{ResultSet.class}, handler);
	}

	/**
	 * Get the wrapped result set
	 *
	 * @return the resultSet
	 */
	public ResultSet getResultSet() {
		return resultSet;
	}

}
