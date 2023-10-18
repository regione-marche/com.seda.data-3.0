/**
 * 
 */
package com.seda.data.procedure.reflection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seda.commons.logger.CustomLoggerManager;
import com.seda.commons.logger.LoggerWrapper;
import com.seda.data.logger.StatementLogger;
import com.seda.data.procedure.parameter.ParameterDirection;
import com.seda.data.procedure.transaction.Transaction;
import com.seda.data.type.JdbcType;

/**
 * @author f.ricci
 *
 */
public class ProcedureReflector {

	private static final LoggerWrapper log =  CustomLoggerManager.get(ProcedureReflector.class);

	private static final Object _locker = new Object();
	
	private static final Map<String, ProcedureReflector> reflectors = Collections.synchronizedMap(new HashMap<String, ProcedureReflector>());	
	private List<ProcedureParameter> refCursorList = new ArrayList<ProcedureParameter>();
	private Map<String, ProcedureParameter> parameterMap = new HashMap<String, ProcedureParameter>();
	private List<ProcedureParameter> parameterList = new ArrayList<ProcedureParameter>();	

	private String[] parameterNames = new String[0];
	private Enumeration<String> parameterNameEnum = Collections.enumeration(Collections.EMPTY_MAP.keySet());	
	
	private String hashKey;
	private ProcedureParameter returnColumn;
	private String sqlCall;
	
	private ProcedureReflector(String hashKey, Connection connection, String url, String catalog, String schema, String procedure) {
		this.hashKey = hashKey;
		addProcedureColumns(connection, url, catalog, schema, procedure);
		parameterNames = parameterMap.keySet().toArray(new String[parameterMap.keySet().size()]);
		parameterNameEnum = Collections.enumeration(parameterMap.keySet()); 
	}	
	
	private void addProcedureColumns(Connection connection,	String url, String catalog, String schema, String procedure) {
		String ref="'" + catalog + "'.'"+schema+"'.'"+procedure+"'";
		if (log.isDebugEnabled()) {
			log.debug("*> ProcedureColumn loop for "+ref+" at '"+url+"'");
		}
		try {
			ResultSet parameterMetaData = connection.getMetaData().getProcedureColumns(catalog, schema, procedure, "%");
			if (parameterMetaData!=null) {
				int index=1;
				StringBuffer call = new StringBuffer("CALL ");
				call.append(catalog==null?"":catalog.concat("."));
				if (schema==null && catalog==null) {
					call.append("");
				} else if (schema==null) {
					call.append(".");
				} else {
					call.append(schema.concat("."));
				}
				call.append(procedure).append("(");
				while (parameterMetaData.next()) {
					ProcedureParameter procedureColumn = new ProcedureParameter();
					procedureColumn.setProcedureCatalog(parameterMetaData.getString(1));
					procedureColumn.setProcedureSchema(parameterMetaData.getString(2));
					procedureColumn.setProcedureName(parameterMetaData.getString(3));
					procedureColumn.setColumnName(parameterMetaData.getString(4));
					procedureColumn.setColumnType(parameterMetaData.getShort(5));
					procedureColumn.setDataType(parameterMetaData.getInt(6));
					procedureColumn.setTypeName(parameterMetaData.getString(7));
					procedureColumn.setPrecision(parameterMetaData.getInt(8));
					procedureColumn.setLength(parameterMetaData.getInt(9));
					procedureColumn.setScale(parameterMetaData.getShort(10));
					procedureColumn.setRadix(parameterMetaData.getShort(11));
					procedureColumn.setNullable(parameterMetaData.getShort(12));
					procedureColumn.setRemarks(parameterMetaData.getString(13));
					procedureColumn.setIndex(index);
					if (procedureColumn.getColumnType()==DatabaseMetaData.procedureColumnReturn) {
						returnColumn=procedureColumn;
					} else if (procedureColumn.getColumnType()==DatabaseMetaData.procedureColumnResult) {
						refCursorList.add(procedureColumn);
					} else if (procedureColumn.getDataType()==JdbcType.OTHER.TYPE_CODE && procedureColumn.getTypeName().equals("REF CURSOR")) {
						// oracle ref cursor management
						procedureColumn.setDataType(JdbcType.CURSOR.TYPE_CODE);
						refCursorList.add(procedureColumn); 
					} else {
						parameterMap.put(procedureColumn.getColumnName(), procedureColumn);
						parameterList.add(procedureColumn);
					}
					if (log.isDebugEnabled()) {
						log.debug("*> "+ref+" #"+index+" name     : '" + procedureColumn.getColumnName());
						log.debug("*> "+ref+" #"+index+" type     : '" + procedureColumn.getTypeName());
						log.debug("*> "+ref+" #"+index+" direction: '" + ParameterDirection.getJDBCDirectionName(procedureColumn.getColumnType()));
					}
					call.append(parameterList.size()+refCursorList.size()>1?",?":"?");
					index++;
				}
				call.append(")");
				if (hasReturnValue()) {
					call.insert(0, "{?=");
				} else {
					call.insert(0, "{");
				}
				call.append("}");
				sqlCall=call.toString();
			} else {
				if (log.isDebugEnabled()) {
					log.debug("Connection '" + url + "' doesn't support procedure column metadata");
				}
			}
		} catch (SQLException x) {
			throw new ProcedureReflectorException("Procedure column metadata error.", x);
		}
	}

	public static ProcedureReflector forProcedure(Transaction transaction, String procedure) {
		return forProcedure(transaction.getConnection(), transaction.getSchema(), procedure);
	}	
	
	public static ProcedureReflector forProcedure(Connection connection, String schema, String procedure) {
		return forProcedure(connection, null, schema, procedure);
	}
	
	public static ProcedureReflector forProcedure(Connection connection, String catalog, String schema, String procedure) {
		String url=null;
		String username;
		DatabaseMetaData databaseMetaData = null; 
		try {
			databaseMetaData = connection.getMetaData();
			url = databaseMetaData.getURL();
			username=databaseMetaData.getUserName();
			if (schema==null && username!=null)
				schema=username.trim();
		} catch (SQLException e) {
			throw new ProcedureReflectorException("Connection doesn't support metadata.", e);
		} finally {
			username=null;
		}
		String hashKey = encodeHashKey(url, catalog, schema, procedure);
		synchronized (_locker) {
			ProcedureReflector cached = reflectors.get(hashKey);
			if (cached == null) {
				cached = new ProcedureReflector(hashKey, connection, url, catalog, schema,  procedure);
				reflectors.put(hashKey, cached);
			}
			return cached;			
		}
	}

	private static String encodeHashKey(String url, String catalog, String schema, String procedure) {
		return url.concat("#").concat(catalog==null?"nocatalog":catalog).concat("#").concat(schema==null?"noschema":schema).concat("#").concat(procedure);
	}
	
	public String getHashKey() {
		return hashKey;
	}	

	public String getSQLCall() {
		return sqlCall;
	}	
	
	public boolean hasReturnValue() {
		return !(returnColumn==null);
	}
	public boolean hasParameterCursor() {
		return (refCursorList.size()>0);
	}
	public List<ProcedureParameter> getParameterCursorList() {
		return refCursorList;
	}		
	
	public ProcedureParameter getParameter(String columnName) {
		if (parameterMap.containsKey(columnName)) 
			return parameterMap.get(columnName);
		return null;
	}
	
	public ProcedureParameter getParameter(int columnIndex) {
		return parameterList.get(columnIndex);
	}	

	public List<ProcedureParameter> getParameterList() {
		return parameterList;
	}		
	
	public Enumeration<String> getParameterNameEnum() {
		return this.parameterNameEnum;
	}
	public String[] getParameterNames() {
		return this.parameterNames;
	}	
	
}
