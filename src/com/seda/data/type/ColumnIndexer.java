/**
 * 
 */
package com.seda.data.type;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author f.ricci
 *
 */
public class ColumnIndexer {

	ResultSet savedResultSet;
	ResultSetMetaData metaData;
	Map<String,Integer> columnIndexLookup;
	
	public ColumnIndexer(ResultSet resultSet) throws SQLException {
		this.savedResultSet=resultSet;
		loadNewMetaData();
	}
	
	public ResultSetMetaData getResultSetMetaData(ResultSet resultSet) throws SQLException {
		if (!resultSet.equals(savedResultSet)) {
			savedResultSet=resultSet;
			loadNewMetaData();				
		}
		return metaData;
	}

	public int lookUpIndex(String columnName) {
		return columnIndexLookup.get(columnName);
	}
	
	private void loadNewMetaData() throws SQLException {
		metaData=savedResultSet.getMetaData();
		loadColumnIndexLookup();		
	}
	
	private void loadColumnIndexLookup() throws SQLException {
		columnIndexLookup = new HashMap<String,Integer>();
		int count = metaData.getColumnCount();
		for (int i=1; i <= count; i++) {
			String name = metaData.getColumnName(i);
			columnIndexLookup.put(name,i);
		}
	}
	
}
