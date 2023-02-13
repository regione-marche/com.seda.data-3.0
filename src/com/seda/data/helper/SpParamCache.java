package com.seda.data.helper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.seda.commons.string.Pad;

/**
 * SpParamCache provides functions to leverage a static cache of procedure parameters, and the
 * ability to discover parameters for stored procedures at run-time
 * @author SEDA Lab
 */
public final class SpParamCache {
	
	private static boolean returnValue;
	private static int parameterCount;
	
	private static synchronized final boolean hasReturnValue() {
		return returnValue;
	}
	private static synchronized final void setReturnValue(boolean returnValue) {
		SpParamCache.returnValue = returnValue;
	}
	private static synchronized final int getParameterCount() {
		return parameterCount;
	}
	private static synchronized final void setParameterCount(int parameterCount) {
		SpParamCache.parameterCount = parameterCount;
	}
	private static synchronized final void incrementParameterCount() {
		SpParamCache.parameterCount++;
	}	

	/**
	 * It's a HashMap.
	 */
	private static Map<String, SpParamMetaData> paramCache = new HashMap<String, SpParamMetaData>();
	
	private static synchronized final Map<String, SpParamMetaData> getParamCache() {
		return paramCache;
	}
	
	/**
	 * Returns {@link SpParamMetaData}. It discovers only once avoiding to use connection every time.
	 * @param connection - The current {@link Connection} to pass.
	 * @param schemaPattern - The current schema to pass.
	 * @param procedureNamePattern - The current procedure to pass.
	 * @return <code>HelperParameterSetMetaData</code> - The {@link SpParamMetaData}.
	 * @throws SQLException In case of SQL Exception.
	 * @throws IllegalArgumentException In case of illegal argument.
	 * @throws HelperException In case of DAO Exception.
	 */
	public synchronized final static SpParamMetaData getParameterSet(final Connection connection, String schemaPattern, String procedureNamePattern) throws SQLException, HelperException {
        if( connection == null ) throw new HelperException(Messages.ARGUMENT_NULL.format("connection"));
        if( schemaPattern == null || schemaPattern.length() == 0 ) throw new HelperException(Messages.ARGUMENT_NULL.format("schemaPattern"));        
        if( procedureNamePattern == null || procedureNamePattern.length() == 0 ) throw new HelperException(Messages.ARGUMENT_NULL.format("procedureNamePattern"));
        
        String hashKey = connection.getMetaData().getURL() + ":" + schemaPattern + "." + procedureNamePattern;
        
        SpParamMetaData cachedParameters=null;
        
        if (getParamCache().containsKey(hashKey)) {
        	cachedParameters = getParamCache().get(hashKey);        	
        } else {
        	SpParamMetaData spParameters = discoverSpParameterSet(connection, schemaPattern, procedureNamePattern);
        	getParamCache().put(hashKey, spParameters);
        	cachedParameters = spParameters;
        }
        return cloneParameters(cachedParameters);
	}
	/**
	 * It discovers the set of parameters from a {@link DatabaseMetaData} object.
	 * @param connection - The current {@link Connection} to pass.
	 * @param schemaPattern - The current schema to pass.
	 * @param procedureNamePattern - The current procedure to pass.
	 * @return <code>String</code> - The CALL string.
	 * @throws IllegalArgumentException In case of illegal argument.
	 * @throws SQLException In case of SQL Exception.
	 * @throws HelperException In case of DAO Exception.
	 */
	private synchronized static SpParamMetaData discoverSpParameterSet(final Connection connection, final String schemaPattern, final String procedureNamePattern) throws IllegalArgumentException, SQLException, HelperException {
        discoverSpParameterCount(connection, schemaPattern, procedureNamePattern, false);
        return buildParameterSetMetaData(schemaPattern, procedureNamePattern);
	}
	
	private synchronized final static int discoverSpParameterCount(final Connection connection, final String schemaPattern, final String procedureNamePattern, boolean hasReturnValue) throws SQLException, HelperException {
		setParameterCount(0);
    	setReturnValue(false);		
        ResultSet parameters = connection.getMetaData().getProcedureColumns(null, schemaPattern, procedureNamePattern, "%");
        if (parameters!=null) {
            while (parameters.next()) {
            	switch (parameters.getShort(5)) {
				case DatabaseMetaData.procedureColumnIn:
				case DatabaseMetaData.procedureColumnOut:
				case DatabaseMetaData.procedureColumnInOut:					
					incrementParameterCount();					
					break;
				case DatabaseMetaData.procedureColumnReturn:
					// the return value, if any, is first					
					setReturnValue(true);					
				default:
					break;
				}
            }        	
        } else {
        	throw new HelperException(Messages.METADATA_UNSUPPORTED.format());
        }
        hasReturnValue=hasReturnValue();
        return getParameterCount();
	}
	/**
	 * Creates the CALL string.
	 * @param schemaPattern - The current schema to pass.
	 * @param procedureNamePattern - The current procedure to pass.
	 * @param hasReturnValue - If the procedure has or not return value.
	 * @param parmsLength - Parameters' length of procedure.
	 * @return <code>String</code> - The CALL string.
	 */
	public synchronized  final static String buildCallString(String schemaPattern, String procedureNamePattern, boolean hasReturnValue, int parmsLength) {
		StringBuffer discovered = new StringBuffer();
        discovered.append("{");
        discovered.append(hasReturnValue?"?=CALL ":"CALL ");
        discovered.append(schemaPattern);
        discovered.append(".");
        discovered.append(procedureNamePattern);
        discovered.append(parmsLength>0?" (" + Pad.literal("?,", parmsLength, true)+")}":"}");
        return discovered.toString();		
	}
	
	private synchronized static SpParamMetaData buildParameterSetMetaData(String schemaPattern, String procedureNamePattern) {
		return new SpParamMetaData(buildCallString(schemaPattern, procedureNamePattern, returnValue, parameterCount), parameterCount, returnValue);
	}
	
	/**
	 * Creates a new {@link String} object using originalParameters.
	 * @param originalParameters - String formated.
	 * @return <code>String</code> - Returns the {@link String} object.
	 */
	private synchronized static SpParamMetaData cloneParameters(SpParamMetaData originalParameters) {
		return originalParameters.clone();
	}
}
