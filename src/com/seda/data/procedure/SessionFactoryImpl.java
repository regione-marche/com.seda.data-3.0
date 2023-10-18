/**
 * 
 */
package com.seda.data.procedure;


import javax.sql.DataSource;

import com.seda.commons.logger.CustomLoggerManager;
import com.seda.commons.logger.LoggerWrapper;
import com.seda.data.logger.ConnectionLogger;
import com.seda.data.procedure.executor.Executor;
import com.seda.data.procedure.result.ResultLoader;
import com.seda.data.procedure.transaction.Transaction;
import com.seda.data.procedure.transaction.TransactionFactory;
import com.seda.data.procedure.transaction.TransactionFactoryImpl;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * 
 * @author f.ricci
 *
 */
public class SessionFactoryImpl implements SessionFactory {

	private static final LoggerWrapper log =  CustomLoggerManager.get(SessionFactoryImpl.class);

	private final SubSystem system;
	private final TransactionFactory transactionFactory;

	public SessionFactoryImpl(SubSystem system) {
		this.system = system;
		this.transactionFactory = new TransactionFactoryImpl();
	}

	public Session open() {
		return openSessionFromDataSource(null,null, null,false);
	}
	
	public Session open(String schema) {
		return openSessionFromDataSource(null,null, schema,false);
	}
	
	public Session open(String planId, String schema) {
		return openSessionFromDataSource(planId,null, schema,false);
	}		

	public Session open(boolean autoCommit) {
		return openSessionFromDataSource(null, null, null, autoCommit);
	}
	
	public Session open(String schema, boolean autoCommit) {
		return openSessionFromDataSource(null, null, schema, autoCommit);
	}
	
	public Session open(String planId, String schema, boolean autoCommit) {
		return openSessionFromDataSource(planId, null, schema, autoCommit);
	}
	
	public Session open(String planId, String catalog, String schema, boolean autoCommit) {
		return openSessionFromDataSource(planId, catalog, schema, autoCommit);
	}			

	public SubSystem getSystemConfig() {
		return system;
	}

	private Session openSessionFromDataSource(String planId, String catalog, String schema, boolean autoCommit) {
		try {
			final Plan plan = planId==null?system.getDefaultPlan():system.getMappedPlan(planId);
			final DataSource dataSource = getDataSourceFromPlan(plan);
			TransactionFactory transactionFactory = getTransactionFactoryFromEnvironment(plan);
			Connection connection = dataSource.getConnection();
			connection = wrapConnection(connection);
			Transaction tx = transactionFactory.getTransaction(connection, catalog, schema, autoCommit);
			Executor executor = system.getExecutor(tx);
			return new SessionImpl(system, executor, autoCommit);
		} catch (SQLException e) {
			throw new RuntimeSystemException("Error opening session.  Cause: " + e, e);
		} finally {
//			ErrorContext.instance().reset();
		}
	}

	private DataSource getDataSourceFromPlan(Plan plan) {
		if (plan == null || plan.getDataSource() == null) {
			throw new RuntimeSystemException("Configuration does not include a plan with a DataSource, so session cannot be created.");
		}
		return plan.getDataSource();
	}

	private TransactionFactory getTransactionFactoryFromEnvironment(Plan plan) {
		if (plan == null || plan.getTransactionFactory() == null) {
			return transactionFactory;
		}
		return plan.getTransactionFactory();
	}

	private Connection wrapConnection(Connection connection) {
		if (log.isDebugEnabled()) {
			return ConnectionLogger.newInstance(connection);
		} else {
			return connection;
		}
	}

}

