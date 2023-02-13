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
public interface Transaction {

	  String getCatalog();
	
	  Connection getConnection();
	  
	  String getSchema();	  

	  void commit() throws SQLException;

	  void rollback() throws SQLException;

	  void close() throws SQLException;
	  
}
