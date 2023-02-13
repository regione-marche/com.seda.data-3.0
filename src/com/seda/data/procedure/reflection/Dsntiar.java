/**
 * 
 */
package com.seda.data.procedure.reflection;

import java.io.PrintWriter;
import java.sql.SQLException;
import com.ibm.db2.jcc.DB2Diagnosable; // Import packages for DB2
import com.ibm.db2.jcc.DB2Sqlca; // SQLException support
/**
 * @author f.ricci
 *
 */
public class Dsntiar {
	public static void display(SQLException sqle) {
		while(sqle != null) { // Check whether there are more
			// SQLExceptions to process
			//=====> Optional IBM Data Server Driver for JDBC and SQLJ-only
			// error processing
			if (sqle instanceof DB2Diagnosable) { 
				// Check if IBM Data Server Driver for JDBC and SQLJ-only
				// information exists
				com.ibm.db2.jcc.DB2Diagnosable diagnosable =
					(com.ibm.db2.jcc.DB2Diagnosable)sqle; 
				diagnosable.printTrace (new PrintWriter(System.out), ""); 
				java.lang.Throwable throwable =
					diagnosable.getThrowable(); 
				if (throwable != null) {
					// Extract java.lang.Throwable information
					// such as message or stack trace.
					throwable.printStackTrace();
				}
				DB2Sqlca sqlca = diagnosable.getSqlca(); 
				// Get DB2Sqlca object
				if (sqlca != null) { // Check that DB2Sqlca is not null
					int sqlCode = sqlca.getSqlCode(); // Get the SQL error code 5d5
					String sqlErrmc = sqlca.getSqlErrmc(); 
					// Get the entire SQLERRMC
					String[] sqlErrmcTokens = sqlca.getSqlErrmcTokens();
					// You can also retrieve the
					// individual SQLERRMC tokens
					String sqlErrp = sqlca.getSqlErrp(); 
					// Get the SQLERRP
					int[] sqlErrd = sqlca.getSqlErrd(); 
					// Get SQLERRD fields
					char[] sqlWarn = sqlca.getSqlWarn(); 
					// Get SQLWARN fields
					String sqlState = sqlca.getSqlState(); 
					// Get SQLSTATE
					String errMessage=null;
					try {
						errMessage = sqlca.getMessage();
					} catch (SQLException e) {
						errMessage="Exception during sqlca.getMessage() " + e.getMessage();
					} 
					// Get error message
					System.err.println ("Server error message: " + errMessage);
					System.err.println ("--------------- SQLCA ---------------");
					System.err.println ("Error code: " + sqlCode);
					System.err.println ("SQLERRMC: " + sqlErrmc);
					if (sqlErrmcTokens != null) {
						for (int i=0; i< sqlErrmcTokens.length; i++) {
							System.err.println (" token " + i + ": " + sqlErrmcTokens[i]);
						}
					}
					System.err.println ( "SQLERRP: " + sqlErrp );
					System.err.println (
							"SQLERRD(1): " + sqlErrd[0] + "\n" +
							"SQLERRD(2): " + sqlErrd[1] + "\n" +
							"SQLERRD(3): " + sqlErrd[2] + "\n" +
							"SQLERRD(4): " + sqlErrd[3] + "\n" +
							"SQLERRD(5): " + sqlErrd[4] + "\n" +
							"SQLERRD(6): " + sqlErrd[5] );
					System.err.println (
							"SQLWARN1: " + sqlWarn[0] + "\n" +
							"SQLWARN2: " + sqlWarn[1] + "\n" +
							"SQLWARN3: " + sqlWarn[2] + "\n" +
							"SQLWARN4: " + sqlWarn[3] + "\n" +
							"SQLWARN5: " + sqlWarn[4] + "\n" +
							"SQLWARN6: " + sqlWarn[5] + "\n" +
							"SQLWARN7: " + sqlWarn[6] + "\n" +
							"SQLWARN8: " + sqlWarn[7] + "\n" +
							"SQLWARN9: " + sqlWarn[8] + "\n" +
							"SQLWARNA: " + sqlWarn[9] );
					System.err.println ("SQLSTATE: " + sqlState);
					// portion of SQLException
				}
				sqle=sqle.getNextException(); // Retrieve next SQLException		
			}

		}
	}
}