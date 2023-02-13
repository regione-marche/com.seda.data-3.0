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
public interface TransactionFactory {
	  void setProperties(Properties props);

//	  Transaction getTransaction(Connection conn, String schema, boolean autoCommit);
	  
	  Transaction getTransaction(Connection conn, String catalog, String schema, boolean autoCommit);
	  
}
