package com.seda.data.procedure.reflection;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

import com.seda.data.dao.DAOHelper;

public class TestDB2 {
	
	static class Location {
		String location;
		String server;
		int port;
		String username;
		String password;
		public Location(String location, String server, int port, String username,
				String password) {
			super();
			this.location = location;
			this.server = server;
			this.port = port;
			this.username = username;
			this.password = password;
		}
		@Override
		public String toString() {
			return "Location [location=" + location 
					+ ", port=" + port + ", server=" + server + ", username="
					+ username + ", password=******]";
		}
		
	}
	
	static HashMap<String, Location> locations = new HashMap<String, Location>();
	
	static {
		locations.put("KDB2", new Location("KDB2DDF","10.10.150.10",5023,"PAGONET", "PAGONET"));		
		locations.put("MDB2", new Location("MDB2DDF","10.10.150.10",5027,"PAGONET", "PAGONET"));		
		locations.put("SDB2", new Location("SDB2DDF","10.10.150.10",5029,"PAGONET", "PAGONET"));
		locations.put("SDB2X", new Location("SDB2DDF","10.10.9.58",50000,"PAGONET", "PAGONET"));
		locations.put("TDB2", new Location("TDB2DDF","10.10.150.10",5021,"PAGONET", "PAGONET"));
		locations.put("XDB2", new Location("XDB2DDF","10.10.150.10",446 ,"PAGONET", "PAGONET"));
	}
	
	public static void main(String[] args) {
//		Properties prop = new Properties();
//		prop.put("driver", "com.ibm.db2.jcc.DB2Driver");
//		prop.put("url", "jdbc:db2://10.10.150.10:5027/MDB2DDF:retrieveMessagesFromServerOnGetMessage=true;emulateParameterMetaDataForZCalls=1;");
//		prop.put("url", "jdbc:db2://10.10.150.10:5021/TDB2DDF:retrieveMessagesFromServerOnGetMessage=true;emulateParameterMetaDataForZCalls=1;");		
//		prop.put("username", "SC17");
//		prop.put("password", "FLAVIO37");
		
//		DataSourceFactoryImpl dataFactoryImpl = new DataSourceFactoryImpl();
//		dataFactoryImpl.setProperties(prop);
//		DataSource dataSource = dataFactoryImpl.getDataSource();
		Location location=null;
		File trace=null;
		File folder=null;
		
		if (args.length>2) {
			System.err.println("sono stati specificati troppi parametri " + Arrays.toString(args));
			System.err.println("testdb2 [ssid] [file_di_trace] ");
			return;
		} else if (args.length==2) {
			location=locations.get(args[0]);
			trace=new File(args[1]);
		} else if (args.length==1) {
			location=locations.get(args[0]);
		} else{
			location=locations.get("MDB2");
		}
		
		if (location==null) {
			System.err.println("sottosistema non trovato " + args[0]);
			return;
		}
		System.out.println("DataSource -> " + location);
		
		if (trace!=null) {
			folder=trace.getParentFile();
			if (!folder.exists()) {
				folder.mkdirs();
			}
			System.out.println("trace -> " + trace);			
		}
		
		// create data source
        com.ibm.db2.jcc.DB2SimpleDataSource ds = new com.ibm.db2.jcc.DB2SimpleDataSource();

        // set connection properties
        ds.setServerName(location.server);
        ds.setPortNumber(location.port); // sdb2
        ds.setDatabaseName(location.location);
        ds.setDriverType(4);
        ds.setUser(location.username);
        ds.setPassword(location.password);

        // set trace properties
        if (trace!=null) {
            ds.setTraceDirectory(folder.toString());
            ds.setTraceFile(trace.getName());
            ds.setTraceFileAppend(false);
            ds.setTraceLevel(com.ibm.db2.jcc.DB2BaseDataSource.TRACE_ALL);        	
        }
		
		Connection connection=null;
		CallableStatement callableStatement=null;
		ResultSet resultSet=null;
		try {
			connection=ds.getConnection();
			
			ResultSet parameterMetaData = connection.getMetaData().getProcedureColumns(null, "SYSIBM", "SQLPROCEDURECOLS", "%");
			ResultSetMetaData rsmd = parameterMetaData.getMetaData();
			System.out.println("=================================================================================");
		    int cols = rsmd.getColumnCount();
			while (parameterMetaData.next()) {
		         for (int i = 1; i <= cols; i++) {
		            System.out.print(parameterMetaData.getString(i));
		            System.out.print(",");
		         }
		         System.out.println("");
		         System.out.println("=================================================================================");
			}
		} catch (SQLException e) {
			Dsntiar.display(e);
		} finally {
			DAOHelper.closeIgnoringException(resultSet);
			DAOHelper.closeIgnoringException(callableStatement);
			DAOHelper.closeIgnoringException(connection);
		}
	}

}
