/**
 * 
 */
package com.seda.data.helper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.seda.data.dao.DAOHelper;

/**
 * @author dbadm
 *
 */
public class HelperUnitTest {


	private String driver="com.ibm.db2.jcc.DB2Driver";
	private String url="jdbc:db2://10.10.9.52:50000/BAP00DB0:retrieveMessagesFromServerOnGetMessage=true;";
	private String user="SE000SV";
	private String schema="SE000SV";
	private String stored="BPPGMSP_INS";	
	private String password="SVSVIL09$";	
	

	public static void main(String[] args)  {
		try {
			new HelperUnitTest(args);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HelperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HelperUnitTest(String[] args) throws ClassNotFoundException, SQLException, HelperException {
		Connection connection = Helper.getConnection(driver, url, user, password, true);
//		CallableStatement callableStatement = Helper.prepareCall(connection, schema, "BPPGMSP_INS");
//		CallableStatement callableStatement = connection.prepareCall("{?=CALL SE000SV.BPPGMSP_INS(?,?,?,?,?,?,?,?,?)}");
//		callableStatement.registerOutParameter(1, Types.INTEGER);
//		callableStatement.setString(2, "proga");
//		callableStatement.setString(3, "proga");
//		callableStatement.setString(4, "");
//		callableStatement.setTimestamp(5, null);
//		callableStatement.setString(6, "");
//		callableStatement.setTimestamp(7, null);
//		callableStatement.setString(8, "");
//		callableStatement.setString(9, "");
//		callableStatement.setString(10, "");
		ResultSet parameters = connection.getMetaData().getProcedureColumns(null, schema, stored, "%");
		int i=0;
        if (parameters!=null) {
            while (parameters.next()) {
            	switch (parameters.getShort(5)) {
				case DatabaseMetaData.procedureColumnIn:
				case DatabaseMetaData.procedureColumnOut:
				case DatabaseMetaData.procedureColumnInOut:					
					i++;					
					break;
				case DatabaseMetaData.procedureColumnReturn:
					// the return value, if any, is first					
					i++;					
				default:
					break;
				}
            }
        }
//		callableStatement.setString(1, "proga");
//		callableStatement.setString(2, "proga");
//		callableStatement.setString(3, "");
//		callableStatement.setTimestamp(4, null);
//		callableStatement.setString(5, "");
//		callableStatement.setTimestamp(6, null);
//		callableStatement.setString(7, "");
//		callableStatement.setString(8, "");
//		callableStatement.setString(9, "");		
//		callableStatement.execute();
//		int returnvalue = callableStatement.getUpdateCount();
//		System.out.println(returnvalue + " " + callableStatement.toString());
        System.out.println(i);
		DAOHelper.closeIgnoringException(connection);
	}
}
