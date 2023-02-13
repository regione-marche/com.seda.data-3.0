/**
 * 
 */
package com.seda.data.procedure.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.seda.data.procedure.ProcedureBound;
import com.seda.data.procedure.parameter.ParameterHandler;
import com.seda.data.procedure.result.ResultHandler;

/**
 * @author f.ricci
 *
 */
public interface StatementHandler {

	Statement prepare(Connection connection) throws SQLException;

	void parameterize(Statement statement) throws SQLException;

	int execute(Statement statement) throws SQLException;

	List query(Statement statement, ResultHandler resultHandler) throws SQLException;

	ProcedureBound getBoundProcedure();

	ParameterHandler getParameterHandler();

}
