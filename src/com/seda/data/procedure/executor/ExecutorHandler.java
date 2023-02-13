/**
 * 
 */
package com.seda.data.procedure.executor;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import com.seda.commons.cache.CacheKey;
import com.seda.commons.reflection.MetaObject;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.Procedure;
import com.seda.data.procedure.ProcedureBound;
import com.seda.data.procedure.parameter.Parameter;
import com.seda.data.procedure.result.ResultBounds;
import com.seda.data.procedure.result.ResultHandler;
import com.seda.data.procedure.result.cache.NestedResultCache;
import com.seda.data.procedure.transaction.Transaction;
import com.seda.data.type.HandlerRegistry;
/**
 * @author f.ricci
 *
 */
public abstract class ExecutorHandler implements Executor {

	protected Transaction transaction;
	protected SubSystem system;
	protected NestedResultCache localCache;
	
	private boolean closed;

	protected ExecutorHandler(SubSystem system, Transaction transaction) {
		this.transaction = transaction;
		this.closed = false;
		this.system = system;
	    this.localCache = new NestedResultCache("NestedResultCache");
	}

	public Transaction getTransaction() {
		if (closed) throw new ExecutorException("Executor was closed.");
		return transaction;
	}	  
	public void close(boolean forceRollback) {
		try {
			try {
				rollback(forceRollback);
			} finally {
				if (transaction != null) transaction.close();
			}
		} catch (SQLException e) {
			// Ignore.  There's nothing that can be done at this point.
		} finally {
			transaction = null;
			localCache=null;
			closed = true;
		}
	}	

	public boolean isClosed() {
		return closed;
	}

	public int execute(Procedure procedure, Object parameter) throws SQLException {
		if (closed) throw new ExecutorException("Executor was closed.");
	    clearLocalCache();
		return doExecute(procedure, parameter);
	}

	public List query(Procedure procedure, Object parameter, ResultBounds resultBounds, ResultHandler resultHandler) throws SQLException {
		if (closed) throw new ExecutorException("Executor was closed.");
		List list;
		// fix me, voglio usare l'hahscode della cachekey e non della stringa!!!!!!!
		// che cavolo mi succede all'hashCode????
		String key = createResultKey(procedure, parameter, resultBounds).toString();
		final List cachedList = (List) localCache.getObject(key);
		if (cachedList != null) {
			list = cachedList;
		} else {
			try {
				list = doQuery(procedure, parameter, resultBounds, resultHandler);
			} finally {
				localCache.removeObject(key);
			}
			localCache.putObject(key, list);
		}
		return list;
	}	
	
	public void commit(boolean required) throws SQLException {
		if (closed) {
			throw new ExecutorException("Cannot commit, transaction is already closed");
		}
	    clearLocalCache();
		if (required) {
			transaction.commit();
		}
	}

	public void rollback(boolean required) throws SQLException {
		if (!closed) {
		    clearLocalCache();
			if (required) {
				transaction.rollback();
			}
		}
	}

	protected abstract int doExecute(Procedure procedure, Object parameter) throws SQLException;

	protected abstract List doQuery(Procedure procedure, Object parameter, ResultBounds rowBounds, ResultHandler resultHandler) throws SQLException;

	protected void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {     }
		}
	}

	public void clearLocalCache() {
		if (!closed) {
			localCache.clear();
		}
	}

	public CacheKey createResultKey(Procedure procedure, Object parameterObject, ResultBounds resultBounds) {
		if (closed) throw new ExecutorException("Executor was closed.");
		ProcedureBound procedureBound = procedure.getProcedureBound(getTransaction(), parameterObject);
		CacheKey cacheKey = new CacheKey();
		cacheKey.add(procedure.getId());
		cacheKey.add(resultBounds.getOffset());
		cacheKey.add(resultBounds.getLimit());
		cacheKey.add(procedureBound.getSql());
		List<Parameter> parameterList = procedureBound.getParameterList();
		if (parameterList.size() > 0 && parameterObject != null) {
			HandlerRegistry typeHandlerRegistry = procedure.getSubSystem().getHandlerRegistry();
			if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
				cacheKey.add(parameterObject);
			} else {
				MetaObject metaObject = system.getMetaObject(parameterObject);
				for (Parameter parameter : parameterList) {
					String propertyName = parameter.getProperty();
					if (metaObject.hasGetter(propertyName)) {
						cacheKey.add(metaObject.getValue(propertyName));
					} else if (procedureBound.hasAdditionalParameter(propertyName)) {
						cacheKey.add(procedureBound.getAdditionalParameter(propertyName));
					}
				}
			}
		}
		return cacheKey;
	}

	public boolean isCached(CacheKey key) {
		return localCache.getObject(key) != null;
	}

}
