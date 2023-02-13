/**
 * 
 */
package com.seda.data.procedure.parameter;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * @author f.ricci
 *
 */
public enum ParameterDirection {
	IN(DatabaseMetaData.procedureColumnIn), 
	OUT(DatabaseMetaData.procedureColumnOut), 
	INOUT(DatabaseMetaData.procedureColumnInOut);
	
	public final int DIRECTION_CODE;
	private static Map<Integer, ParameterDirection> codeLookup = new HashMap<Integer, ParameterDirection>();

	static {
		for (ParameterDirection direction : ParameterDirection.values()) {
			codeLookup.put(direction.DIRECTION_CODE, direction);
		}
	}

	ParameterDirection(int code) {
		this.DIRECTION_CODE = code;
	}

	public static ParameterDirection forCode(int code)  {
		return codeLookup.get(code);
	}
	
	public static String getJDBCDirectionName(int direction) {
		switch (direction) {
		case DatabaseMetaData.procedureColumnIn:
			return "in";
		case DatabaseMetaData.procedureColumnInOut:
			return "inout";
		case DatabaseMetaData.procedureColumnOut:
			return "out";					
		case DatabaseMetaData.procedureColumnResult:
			return "result";
		case DatabaseMetaData.procedureColumnReturn:
			return "return";			
		case DatabaseMetaData.procedureColumnUnknown:
			return "unknown";									
		default:
			return "n/a";
		}
	}
}
