/**
 * 
 */
package com.seda.data.procedure.reflection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Properties;

import javax.sql.DataSource;

import com.seda.data.dao.DAOHelper;
import com.seda.data.datasource.DataSourceFactoryImpl;
import com.seda.data.export.ExportHandler;

/**
 * @author f.ricci
 *
 */
public class TestOracle {

	public static void main(String[] args) {
		Properties prop = new Properties();
		prop.put("driver", "oracle.jdbc.driver.OracleDriver");
		prop.put("url", "jdbc:oracle:thin:@svil01.seda.intra:1521:orasvil");
		prop.put("username", "MCERIONI");
		prop.put("password", "Cerioni%Seda");
		
		DataSourceFactoryImpl dataFactoryImpl = new DataSourceFactoryImpl();
		dataFactoryImpl.setProperties(prop);
		DataSource dataSource = dataFactoryImpl.getDataSource();
		Connection connection=null;
		CallableStatement callableStatement=null;
		ResultSet resultSet=null;
		try {
			connection=dataSource.getConnection();
//			MetaProcedure metaProcedure = MetaProcedure.forProcedure(connection, "MPACENTI", "TESTREFCURSOR");
//			MetaProcedure metaProcedure = MetaProcedure.forProcedure(connection, "MCERIONI", "PROCEDURE1_TEST_GIACOMO");
//			callableStatement=MetaProcedure.prepareCall(connection, "MPACENTI", "TestRefCursor");
//			System.out.println(metaProcedure.getSQLCall());
			callableStatement=MetaProcedure.prepareCall(connection, "MPACENTI", "TESTREFCURSOR");
			callableStatement.setString(1, "a");
//			callableStatement=MetaProcedure.prepareCall(connection, "MCERIONI", "PROCEDURE1_TEST_GIACOMO");
//			callableStatement.setString(1, "RCCFLV74C30E058A");
//			callableStatement.setInt(2, 1974);
			boolean hasResult=callableStatement.execute();
			if (hasResult) {
				resultSet=callableStatement.getResultSet();
				ExportHandler exportHandler = new ExportHandler() {
					@Override
					protected void printRow(String row) {
						System.out.println(row);
						
					}
				};
				exportHandler.setHeader(true);
				exportHandler.setHeaderSeparator(true);
				exportHandler.setTabular(true);
				exportHandler.export(resultSet);
			}
			
//			statement=connection.prepareStatement("");
//			statement.setString(1, "");
//			statement.execute();
//			resultSet=statement.executeQuery();
//			
//			i=statement.executeUpdate();
//			
//			resultSet=statement.getResultSet();
			
			connection.getMetaData();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DAOHelper.closeIgnoringException(resultSet);
			DAOHelper.closeIgnoringException(callableStatement);
			DAOHelper.closeIgnoringException(connection);
		}
	}
	
	
}
