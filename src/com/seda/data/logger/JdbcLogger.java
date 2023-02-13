/**
 * 
 */
package com.seda.data.logger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Base class for proxies class to do logging
 * 
 * @author f.ricci
 *
 */
public class JdbcLogger {

	  protected static final Set<String> SET_METHODS = new HashSet<String>();
	  protected static final Set<String> EXECUTE_METHODS = new HashSet<String>();
	  protected static final Set<String> RESULT_METHODS = new HashSet<String>();

	  private Map<Object, Object> columnMap = new HashMap<Object, Object>();

	  private List<Object> columnNames = new ArrayList<Object>();
	  private List<Object> columnValues = new ArrayList<Object>();

	  /**
	   * Default constructor
	   */
	  public JdbcLogger() {
	  }

	  static {
	    SET_METHODS.add("setString");
	    SET_METHODS.add("setInt");
	    SET_METHODS.add("setByte");
	    SET_METHODS.add("setShort");
	    SET_METHODS.add("setLong");
	    SET_METHODS.add("setDouble");
	    SET_METHODS.add("setFloat");
	    SET_METHODS.add("setTimestamp");
	    SET_METHODS.add("setDate");
	    SET_METHODS.add("setTime");
	    SET_METHODS.add("setArray");
	    SET_METHODS.add("setBigDecimal");
	    SET_METHODS.add("setAsciiStream");
	    SET_METHODS.add("setBinaryStream");
	    SET_METHODS.add("setBlob");
	    SET_METHODS.add("setBoolean");
	    SET_METHODS.add("setBytes");
	    SET_METHODS.add("setCharacterStream");
	    SET_METHODS.add("setClob");
	    SET_METHODS.add("setObject");
	    SET_METHODS.add("setNull");

	    EXECUTE_METHODS.add("execute");
	    EXECUTE_METHODS.add("executeUpdate");
	    EXECUTE_METHODS.add("executeQuery");

	    RESULT_METHODS.add("executeQuery"); //first ResultSet or null
	    RESULT_METHODS.add("execute");  // boolean if the first return value is resultset
	    RESULT_METHODS.add("getResultSet");  //ResultSet
	    RESULT_METHODS.add("getMoreResults"); //boolean 
	  }

	  protected void setColumn(Object key, Object value) {
	    columnMap.put(key, value);
	    columnNames.add(key);
	    columnValues.add(value);
	  }

	  protected Object getColumn(Object key) {
	    return columnMap.get(key);
	  }

	  protected String getParameterValueString() {
	    List<Object> typeList = new ArrayList<Object>(columnValues.size());
	    for (Object value : columnValues) {
	      if (value == null) {
	        typeList.add("null");
	      } else {
	        typeList.add(value + "(" + value.getClass().getSimpleName() + ")");
	      }
	    }
	    final String parameters = typeList.toString();
	    return parameters.substring(1, parameters.length() - 1);
	  }

	  protected String getColumnString() {
	    return columnNames.toString();
	  }

	  protected void clearColumnInfo() {
	    columnMap.clear();
	    columnNames.clear();
	    columnValues.clear();
	  }

	  protected String removeExcessiveBlank(String original) {
	    StringTokenizer blankStripper = new StringTokenizer(original);
	    StringBuffer buffer = new StringBuffer();
	    while (blankStripper.hasMoreTokens()) {
	      buffer.append(blankStripper.nextToken());
	      buffer.append(" ");
	    }
	    return buffer.toString();
	  }
	
	
}
