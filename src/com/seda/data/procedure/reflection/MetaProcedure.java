/**
 * 
 */
package com.seda.data.procedure.reflection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

import com.seda.data.procedure.transaction.Transaction;
import com.seda.data.procedure.wrapper.oracle.CallableStatementWrapper;

/**
 * @author f.ricci
 *
 */
public class MetaProcedure {

	private ProcedureReflector procedureReflector; 
	
	private MetaProcedure(Connection connection, String catalog, String schema, String procedure){
		this.procedureReflector=ProcedureReflector.forProcedure(connection, 
				catalog!=null?catalog.toUpperCase():null,
						schema!=null?schema.toUpperCase():null,
								procedure!=null?procedure.toUpperCase():null);
	}
	
	public static MetaProcedure forProcedure(Connection connection, String procedure){
		return new MetaProcedure(connection, null, null, procedure);
	}	
	
	public static MetaProcedure forProcedure(Connection connection, String schema, String procedure){
		return new MetaProcedure(connection, null, schema, procedure);
	}	
	
	public static MetaProcedure forProcedure(Transaction transaction, String procedure){
		return new MetaProcedure(transaction.getConnection(), transaction.getCatalog(), transaction.getSchema(), procedure);
	}
	
	public static CallableStatement prepareCall(Transaction transaction, String procedure){
		return prepareCall(transaction.getConnection(), transaction.getCatalog(), transaction.getSchema(), procedure);
	}
	
	public static CallableStatement prepareCall(Transaction transaction, String procedure, boolean registerOutputParameter){
		return prepareCall(transaction.getConnection(), transaction.getCatalog(), transaction.getSchema(), procedure, registerOutputParameter);
	}		
	
	public static CallableStatement prepareCall(Connection connection, String procedure) {
		return prepareCall(connection, null, null, procedure);
	}
	
	public static CallableStatement prepareCall(Connection connection, String procedure, boolean registerOutputParameter) {
		return prepareCall(connection, null, null, procedure, registerOutputParameter);
	}		
	
	public static CallableStatement prepareCall(Connection connection, String schema, String procedure) {
		return prepareCall(connection, null, schema, procedure);
	}
	
	public static CallableStatement prepareCall(Connection connection, String schema, String procedure, boolean registerOutputParameter) {
		return prepareCall(connection, null, schema, procedure,registerOutputParameter);
	}	
	
	public static CallableStatement prepareCall(Connection connection, String catalog, String schema, String procedure) {
		return prepareCall(connection, catalog, schema, procedure,false);
	}
	
	public static CallableStatement prepareCall(Connection connection, String catalog, String schema, String procedure, boolean registerOutputParameter) {
		MetaProcedure metaProcedure = new MetaProcedure(connection, catalog, schema, procedure);
		CallableStatement callableStatement=null;
		try {
			callableStatement=connection.prepareCall(metaProcedure.getSQLCall());
			if (metaProcedure.hasParameterCursor()) {
				// parameters cursor are always auto registered 
				for (ProcedureParameter procedureParameter : metaProcedure.getParameterCursorList()) {
					callableStatement.registerOutParameter(procedureParameter.getIndex(), procedureParameter.getDataType());
				}
			}
			if (registerOutputParameter) {
				for (ProcedureParameter procedureParameter : metaProcedure.getParameterList()) {
					switch (procedureParameter.getColumnType()) {
					case DatabaseMetaData.procedureColumnReturn:
					case DatabaseMetaData.procedureColumnInOut:
					case DatabaseMetaData.procedureColumnOut:
						callableStatement.registerOutParameter(procedureParameter.getIndex(), procedureParameter.getDataType());
						break;
					default:
						break;
					}
				}
			}
			if (metaProcedure.hasParameterCursor()) {
				callableStatement=CallableStatementWrapper.newInstance(callableStatement, metaProcedure);
			}
		} catch (SQLException e) {
			throw new ProcedureReflectorException(e.getMessage(), e);
		}  finally {
			metaProcedure=null;
		}
		return callableStatement;
	}	
	
	public String getHashKey() {
		return procedureReflector.getHashKey();
	}	

	public String getSQLCall() {
		return procedureReflector.getSQLCall();
	}	
	
	public boolean hasReturnValue() {
		return procedureReflector.hasReturnValue();
	}
	
	public boolean hasParameterCursor() {
		return procedureReflector.hasParameterCursor();
	}
	public List<ProcedureParameter> getParameterCursorList() {
		return procedureReflector.getParameterCursorList();
	}		
	
	public ProcedureParameter getParameter(String columnName) {
		return procedureReflector.getParameter(columnName);
	}
	
	public ProcedureParameter getParameter(int columnIndex) {
		return procedureReflector.getParameter(columnIndex);
	}	

	public Enumeration<String> getParameterNames() {
		return procedureReflector.getParameterNameEnum();
	}
	
	public int getParameterSize() {
		return procedureReflector.getParameterNames().length;
	}
	
	public List<ProcedureParameter> getParameterList() {
		return procedureReflector.getParameterList();
	}		

}
