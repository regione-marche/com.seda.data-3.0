/**
 * 
 */
package com.seda.data.procedure.transaction;

import java.sql.Connection;
import java.util.Properties;

/**
 * @author f.ricci
 *
 */
public class TransactionFactoryImpl implements TransactionFactory {

	public Transaction getTransaction(Connection conn, String catalog, String schema, boolean autoCommit) {
		return new TransactionImpl(conn, catalog, schema, autoCommit);
	}
	
//	public Transaction getTransaction(Connection conn, String schema, boolean autoCommit) {
//		return new TransactionImpl(conn, schema, autoCommit);
//	}
	
	public void setProperties(Properties props) {

	}

}
