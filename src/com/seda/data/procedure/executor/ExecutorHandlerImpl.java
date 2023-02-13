/**
 * 
 */
package com.seda.data.procedure.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seda.commons.cache.CacheKey;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.Procedure;
import com.seda.data.procedure.ProcedureBound;
import com.seda.data.procedure.result.ResultBounds;
import com.seda.data.procedure.result.ResultHandler;
import com.seda.data.procedure.statement.StatementHandler;
import com.seda.data.procedure.transaction.Transaction;

/**
 * @author f.ricci
 *
 */
public class ExecutorHandlerImpl extends ExecutorHandler {

	private final Map<String, Statement> statementMap = new HashMap<String, Statement>();	
	
	public ExecutorHandlerImpl(SubSystem system, Transaction transaction) {
		super(system, transaction);
	}

	@Override
	protected List doQuery(Procedure procedure, Object parameter, ResultBounds rowBounds, ResultHandler resultHandler)
	throws SQLException {
		Statement stmt = null;
		try {
			SubSystem system = procedure.getSubSystem();
			StatementHandler handler = system.getStatementHandler(this, procedure, parameter, rowBounds, resultHandler);
			stmt = prepareStatement(handler);
			return handler.query(stmt, resultHandler);
		} finally {
			closeStatement(stmt);
		}
	}

	@Override
	protected int doExecute(Procedure mc, Object parameter) throws SQLException {
		Statement stmt = null;
		try {
			SubSystem system = mc.getSubSystem();
			StatementHandler handler = system.getStatementHandler(this, mc, parameter, ResultBounds.DEFAULT, null);
			stmt = prepareStatement(handler);
			return handler.execute(stmt);
		} finally {
			closeStatement(stmt);
		}
	}

	public List doFlushStatements()  throws SQLException {
		return Collections.EMPTY_LIST;
	}

	private Statement prepareStatement(StatementHandler handler) throws SQLException {
		Statement stmt;
	    ProcedureBound boundProcedure = handler.getBoundProcedure();
	    String hashKey = boundProcedure.getHashKey();
	    if (hasStatementFor(hashKey)) {
	      stmt = getStatement(hashKey);
	    } else {
	      Connection connection = transaction.getConnection();
	      stmt = handler.prepare(connection);
//	      putStatement(hashKey, stmt); //TODO gestire la cache degli statement in modo che in fase di chiusura della session gli statement vengano preventivamente chiusi
	    }
	    handler.parameterize(stmt);
		return stmt;
	}	
	
	private boolean hasStatementFor(String hashKey) {
		try {
			return statementMap.keySet().contains(hashKey) && !statementMap.get(hashKey).getConnection().isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	private Statement getStatement(String hashKey) {
		return statementMap.get(hashKey);
	}

	private void putStatement(String hashKey, Statement stmt) {
		statementMap.put(hashKey, stmt);
	}
	
}
