/**
 * 
 */
package com.seda.data.procedure.statement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.seda.data.procedure.Procedure;
import com.seda.data.procedure.executor.Executor;
import com.seda.data.procedure.executor.ExecutorException;
import com.seda.data.procedure.parameter.Parameter;
import com.seda.data.procedure.parameter.ParameterDirection;
import com.seda.data.procedure.result.ResultBounds;
import com.seda.data.procedure.result.ResultHandler;
import com.seda.data.type.JdbcType;
/**
 * @author f.ricci
 *
 */
public class CallableStatementHandler extends BaseStatementHandler {

	public CallableStatementHandler(Executor executor, Procedure mappedProcedure, Object parameter, ResultBounds resultBounds, ResultHandler resultHandler) {
		super(executor, mappedProcedure, parameter, resultBounds, resultHandler);
	}

	@Override
	protected Statement instantiateStatement(Connection connection) throws SQLException {
		String sql = boundProcedure.getSql();
		return connection.prepareCall(sql);
	}

	public void parameterize(Statement statement) throws SQLException {
		registerOutputParameters((CallableStatement) statement);
		parameterHandler.setParameters((CallableStatement) statement);
	}

	public List<?> query(Statement statement, ResultHandler resultHandler)	throws SQLException {
	    CallableStatement cs = (CallableStatement) statement;
	    cs.execute();
	    List<?> resultList = resultSetHandler.processResultSets(cs);
	    resultSetHandler.processOutputParameters(cs);
	    return resultList;
	}

	public int execute(Statement statement) throws SQLException {
		CallableStatement cs = (CallableStatement) statement;
		cs.execute();
		int rows = cs.getUpdateCount();
		Object parameterObject = boundProcedure.getParameter();
		resultSetHandler.processOutputParameters(cs);
		return rows;
	}

	private void registerOutputParameters(CallableStatement cs) throws SQLException {
		List<Parameter> parameterList = boundProcedure.getParameterList();
		for (int i = 0, n = parameterList.size(); i < n; i++) {
			Parameter parameter = parameterList.get(i);
			if (parameter.getDirection() == ParameterDirection.OUT || parameter.getDirection() == ParameterDirection.INOUT) {
				if (null == parameter.getJdbcType()) {
					throw new ExecutorException("The JDBC Type must be specified for output parameterArray.  Parameter: " + parameter.getProperty());
				} else {
					if (parameter.getNumericScale() != null && (parameter.getJdbcType() == JdbcType.NUMERIC || parameter.getJdbcType() == JdbcType.DECIMAL)) {
						cs.registerOutParameter(i + 1, parameter.getJdbcType().TYPE_CODE, parameter.getNumericScale());
					} else {
						if (parameter.getJdbcTypeName() == null) {
							cs.registerOutParameter(i + 1, parameter.getJdbcType().TYPE_CODE);
						} else {
							cs.registerOutParameter(i + 1, parameter.getJdbcType().TYPE_CODE, parameter.getJdbcTypeName());
						}
					}
				}
			}
		}
	}
	
}
