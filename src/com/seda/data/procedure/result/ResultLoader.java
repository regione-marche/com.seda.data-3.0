package com.seda.data.procedure.result;

import javax.sql.DataSource;

import com.seda.commons.logger.CustomLoggerManager;
import com.seda.commons.logger.LoggerWrapper;
import com.seda.data.dao.DAOSupportExtended;
import com.seda.data.logger.ConnectionLogger;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.Plan;
import com.seda.data.procedure.Procedure;
import com.seda.data.procedure.executor.Executor;
import com.seda.data.procedure.executor.ExecutorException;
import com.seda.data.procedure.transaction.Transaction;
import com.seda.data.procedure.transaction.TransactionImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResultLoader {

	private static final LoggerWrapper log =  CustomLoggerManager.get(ResultLoader.class);

	protected final SubSystem system;
	protected final Executor executor;
	protected final Procedure mappedProcedure;
	protected final Object parameterObject;
	protected final Class targetType;

	protected boolean loaded;
	protected Object resultObject;

	public ResultLoader(SubSystem system, Executor executor, Procedure mappedProcedure, Object parameterObject, Class targetType) {
		this.system = system;
		this.executor = executor;
		this.mappedProcedure = mappedProcedure;
		this.parameterObject = parameterObject;
		this.targetType = targetType;
	}

	public Object loadResult() throws SQLException {
		List list = selectList();
		if (targetType != null && Set.class.isAssignableFrom(targetType)) {
			resultObject = new HashSet(list);
		} else if (targetType != null && Collection.class.isAssignableFrom(targetType)) {
			resultObject = list;
		} else if (targetType != null && targetType.isArray()) {
			resultObject = listToArray(list, targetType.getComponentType());
		} else {
			if (list.size() > 1) {
				throw new ExecutorException("Call " + mappedProcedure.getId() + " returned more than one row, where no more than one was expected.");
			} else if (list.size() == 1) {
				resultObject = list.get(0);
			}
		}
		return resultObject;
	}

	private List selectList() throws SQLException {
		Executor localExecutor = executor;
		if (localExecutor.isClosed()) {
			throw new ExecutorException("ResultLoader could not load result.  Executor was closed.");
			//			localExecutor = getExecutor();
		}
		try {
			return localExecutor.query(mappedProcedure, parameterObject, ResultBounds.DEFAULT, Executor.NO_RESULT_HANDLER);
		} finally {
			if (localExecutor != executor) {
				localExecutor.close(false);
			}
		}
	}


	// Gestire il reperimento del plan dell'attuale sessione in modo da riprendere il datasource
	//	private Executor getExecutor() throws SQLException {
	//		Plan procedurePlan = system.getDefaultPlan();
	//		if (procedurePlan == null)
	//			throw new ExecutorException("ResultLoader could not load lazily.  Plan was not configured.");
	//		DataSource ds = procedurePlan.getDataSource();
	//		if (ds == null) throw new ExecutorException("ResultLoader could not load lazily.  DataSource was not configured.");
	//		Connection conn = ds.getConnection();
	//	    conn = wrapConnection(conn);
	//		Transaction tx = new TransactionImpl(conn, null, false);
	//		return system.getExecutor(tx);
	//	}

	public boolean wasNull() {
		return resultObject == null;
	}

	private Connection wrapConnection(Connection connection) {
		if (log.isDebugEnabled()) {
			return ConnectionLogger.newInstance(connection);
		} else {
			return connection;
		}
	}	

	private Object[] listToArray(List list, Class type) {
		Object array = java.lang.reflect.Array.newInstance(type, list.size());
		array = list.toArray((Object[]) array);
		return (Object[]) array;
	}

}
