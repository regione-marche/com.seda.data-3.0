/**
 * 
 */
package com.seda.data.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;

/**
 * This is a DAO support plus a cached row set management
 * @author Seda Lab
 *
 */
public class DAOCachedSupport extends DAOSupport{

	/**
	 * Returns the {@link CachedRowSet} object filled from result set.
	 * @return <code>{@link CachedRowSet}</code> the ChachedRowSet object
	 */
	protected final CachedRowSet getCachedRowSet(ResultSet resultSet) {
		return DAOHelper.getCachedRowSet(resultSet);
	}
		
	/**
	 * Loads all ResultSet objects of the provided {@link Statement} in a HashMap of {@link CachedRowSet} objects. 
     * The key of the HashMap represents a numeric integer in order of the ResultSet found in the Statement 
	 * @param statement the executed Statement
	 * @throws DAORuntimeException if a database access error occurs
	 */
	protected Map<String, CachedRowSet> getChachedRowSets(Statement statement) {
		// this prevents from NullPointerException		
		if (statement==null) 
			throw new NullPointerException("statement");
		// define a new rowset container
		Map<String, CachedRowSet> cachedRowSets = new HashMap<String, CachedRowSet>();
		// define a new table counter to give a table name
		int t=0;
		try {
			do {
				// put the cached rowset implementation into the hashmap				
				cachedRowSets.put(String.valueOf(t++), getCachedRowSet(statement.getResultSet()));				
			} while (statement.getMoreResults());			
		} catch (SQLException x) {
			throw new DAORuntimeException(x.getMessage(),x);
		}
		return cachedRowSets;
	}
	public DAOCachedSupport(DataSource datasource, String schema) {
		super(datasource, schema);
	}
	public DAOCachedSupport(Properties config, String schema) {
		super(config, schema);
	}

}
