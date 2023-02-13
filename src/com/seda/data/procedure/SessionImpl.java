/**
 * 
 */
package com.seda.data.procedure;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import com.seda.data.procedure.executor.Executor;
import com.seda.data.procedure.result.ResultBounds;
/**
 * 
 * @author f.ricci
 *
 */
public class SessionImpl implements Session {

	private SubSystem context;
	private Executor executor;

	private boolean autoCommit;
	private boolean dirty;

	public SessionImpl(SubSystem context, Executor executor, boolean autoCommit) {
		this.context = context;
		this.executor = executor;
		this.autoCommit = autoCommit;
		this.dirty = false;
	}

	public Object selectOne(String statement) {
		return selectOne(statement, null);
	}

	public Object selectOne(String statement, Object parameter) {
		List list = selectList(statement, parameter);
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() > 1) {
			throw new RuntimeSystemException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
		} else {
			return null;
		}
	}

	public List selectList(String statement) {
		return selectList(statement, null);
	}

	public List selectList(String statement, Object parameter) {
		return selectList(statement, parameter, ResultBounds.DEFAULT);
	}

	public List selectList(String statement, Object parameter, ResultBounds resultBounds) {
		try {
			Procedure procedure = context.getMappedProcedure(statement);
			return executor.query(procedure, wrapCollection(parameter), resultBounds, Executor.NO_RESULT_HANDLER);
		} catch (Exception e) {
			throw new RuntimeSystemException("Error querying database.  Cause: " + e, e);
		} finally {
//			ErrorContext.instance().reset();
		}
	}

	public int execute(String statement) {
		return execute(statement, null);
	}	
	
	public int execute(String statement, Object parameter) {
		try {
			dirty = true;
			Procedure procedure = context.getMappedProcedure(statement);
			return executor.execute(procedure, wrapCollection(parameter));
		} catch (Exception e) {
			throw new RuntimeSystemException("Error executing on database.  Cause: " + e, e);
		} finally {
//			ErrorContext.instance().reset();
		}
	}

	public int delete(String statement) {
		return execute(statement, null);
	}

	public int delete(String statement, Object parameter) {
		return execute(statement, wrapCollection(parameter));
	}

	public void commit() {
		commit(false);
	}

	public void commit(boolean force) {
		try {
			executor.commit(isCommitOrRollbackRequired(force));
			dirty = false;
		} catch (Exception e) {
			throw new RuntimeSystemException("Error committing transaction.  Cause: " + e, e);
		} finally {
//			ErrorContext.instance().reset();
		}
	}

	public void rollback() {
		rollback(false);
	}

	public void rollback(boolean force) {
		try {
			executor.rollback(isCommitOrRollbackRequired(force));
			dirty = false;
		} catch (Exception e) {
			throw new RuntimeSystemException("Error rolling back transaction.  Cause: " + e, e);
		} finally {
//			ErrorContext.instance().reset();
		}
	}

	public void close() {
		try {
			executor.close(isCommitOrRollbackRequired(false));
			dirty = false;
		} catch (Exception e) {
			throw new RuntimeSystemException("Error closing transaction.  Cause: " + e, e);
		} finally {
//			ErrorContext.instance().reset();
		}
	}

	public SubSystem getSubSystem() {
		return context;
	}

	public Connection getConnection() {
		return executor.getTransaction().getConnection();
	}

	private boolean isCommitOrRollbackRequired(boolean force) {
		return (!autoCommit && dirty) || force;
	}

	private Object wrapCollection(final Object object) {
		if (object instanceof List) {
			return new HashMap() {{
				put("list", object);
			}};
		} else if (object != null && object.getClass().isArray()) {
			return new HashMap() {{
				put("array", object);
			}};
		}
		return object;
	}

}
