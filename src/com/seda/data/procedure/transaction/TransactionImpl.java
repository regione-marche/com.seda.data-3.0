/**
 * 
 */
package com.seda.data.procedure.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author f.ricci
 *
 */
public class TransactionImpl implements Transaction {

	protected Connection connection;
	protected String catalog;
	protected String schema;	

	public TransactionImpl(Connection connection, String schema, boolean autoCommit) {
		this(connection,null,schema,autoCommit);
	}

	public TransactionImpl(Connection connection, String catalog, String schema, boolean autoCommit) {
		this.connection = connection;
		this.schema=schema;
		this.catalog=catalog;
		setAutoCommit(autoCommit);
	}	
	
	public Connection getConnection() {
		return connection;
	}

	public String getCatalog() {
		return catalog;
	}
	
	public String getSchema() {
		return schema;
	}
	
	public void commit() throws SQLException {
		if (!connection.getAutoCommit()) {
			connection.commit();
		}
	}

	public void rollback() throws SQLException {
		if (!connection.getAutoCommit()) {
			connection.rollback();
		}
	}

	public void close() throws SQLException {
		resetAutoCommit();
		connection.close();
	}

	protected void setAutoCommit(boolean autoCommit) {
		try {
			if (connection.getAutoCommit() != autoCommit) {
				connection.setAutoCommit(autoCommit);
			}
		} catch (SQLException e) {
			// Only a very poorly implemented driver would fail here,
			// and there's not much we can do about that.
			throw new TransactionException("Error configuring AutoCommit.  " +
					"Your driver may not support getAutoCommit() or setAutoCommit(). " +
					"Requested setting: " + autoCommit + ".  Cause: " + e, e);
		}
	}

	protected void resetAutoCommit() {
		try {
			if (!connection.getAutoCommit()) {
				// for compatibility we always use true, as some drivers don't like being left in "false" mode.
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			// Only a very poorly implemented driver would fail here,
			// and there's not much we can do about that.
			throw new TransactionException("Error configuring AutoCommit.  " +
					"Your driver may not support getAutoCommit() or setAutoCommit(). Cause: " + e, e);
		}
	}	
	
}
