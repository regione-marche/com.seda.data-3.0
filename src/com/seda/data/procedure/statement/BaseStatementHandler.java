/**
 * 
 */
package com.seda.data.procedure.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.seda.commons.reflection.factory.ObjectFactory;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.Procedure;
import com.seda.data.procedure.ProcedureBound;
import com.seda.data.procedure.executor.Executor;
import com.seda.data.procedure.executor.ExecutorException;
import com.seda.data.procedure.parameter.ParameterHandler;
import com.seda.data.procedure.result.ResultBounds;
import com.seda.data.procedure.result.ResultHandler;
import com.seda.data.procedure.result.ResultSetHandler;
import com.seda.data.type.HandlerRegistry;

/**
 * @author f.ricci
 *
 */
public abstract class BaseStatementHandler implements StatementHandler {

	protected final SubSystem system;
	protected final ObjectFactory objectFactory;
	protected final HandlerRegistry handlerRegistry;
	protected final ResultSetHandler resultSetHandler;
	protected final ParameterHandler parameterHandler;

	protected final Executor executor;
	protected final Procedure procedure;
	protected final ResultBounds resultBounds;

	protected final ProcedureBound boundProcedure;

	protected BaseStatementHandler(Executor executor, Procedure procedure, Object parameterObject, ResultBounds resultBounds, ResultHandler resultHandler) {
		this.system = procedure.getSubSystem();
		this.executor = executor;
		this.procedure = procedure;
		this.resultBounds = resultBounds;

		this.handlerRegistry = system.getHandlerRegistry();
		this.objectFactory = system.getObjectFactory();

		this.boundProcedure = procedure.getProcedureBound(this.executor.getTransaction(), parameterObject);

		this.parameterHandler = system.getParameterHandler(procedure, parameterObject, boundProcedure);
		this.resultSetHandler = system.getResultSetHandler(executor, procedure, resultBounds, parameterHandler, boundProcedure);
	}

	public ProcedureBound getBoundProcedure() {
		return boundProcedure;
	}

	public ParameterHandler getParameterHandler() {
		return parameterHandler;
	}

	public Statement prepare(Connection connection) throws SQLException {
		Statement statement = null;
		try {
			statement = instantiateStatement(connection);
			setStatementTimeout(statement);
			setFetchSize(statement);
			return statement;
		} catch (SQLException e) {
			closeStatement(statement);
			throw e;
		} catch (Exception e) {
			closeStatement(statement);
			throw new ExecutorException("Error preparing statement.  Cause: " + e, e);
		}
	}

	protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

	protected void setStatementTimeout(Statement stmt) throws SQLException {
		Integer timeout = procedure.getTimeout();
		Integer defaultTimeout = system.getDefaultCallTimeout();
		if (timeout != null) {
			stmt.setQueryTimeout(timeout);
		} else if (defaultTimeout != null) {
			stmt.setQueryTimeout(defaultTimeout);
		}
	}

	protected void setFetchSize(Statement stmt) throws SQLException {
		Integer fetchSize = procedure.getFetchSize();
		if (fetchSize != null) {
			stmt.setFetchSize(fetchSize);
		}
	}

	protected void closeStatement(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {}

	}

}
