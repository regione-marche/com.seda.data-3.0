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
import javax.sql.rowset.WebRowSet;

import com.seda.commons.string.Convert;
/**
 * This is a DAO support plus a web row set management
 * @author Seda Lab
 *
 */
public class DAOWebSupport extends DAOSupport {

	/**
	 * Returns the {@link WebRowSet} object filled from ResultSet.
	 * @return <code>{@link WebRowSet}</code> the WebRowSet object
	 */
	protected final WebRowSet getWebRowSet(ResultSet resultSet) {
		return DAOHelper.getWebRowSet(resultSet);
	}

	/**
	 * Returns the String object containing xml information about webrowset filled from provided resultset
	 */
	protected final String getWebRowSetXml(ResultSet resultSet) {
		String webRowSetXml=null;
		try {
			webRowSetXml=Convert.webRowSetToString(getWebRowSet(resultSet));
		} catch (SQLException e) {
			throw new DAORuntimeException(e.getMessage(), e);
		}
		return webRowSetXml;
	}
	
	
	/**
     * Loads all ResultSet objects of the provided {@link Statement} in a HashMap of {@link WebRowSet} objects. 
     * The key of the HashMap represents a numeric integer in order of the ResultSet found in the Statement 
	 * @param statement the executed Statement 
	 * @throws DAORuntimeException in case of database error
	 */
	protected final Map<String, WebRowSet> getWebRowSets(Statement statement) {
		// this prevents from NullPointerException		
		if (statement==null) 
			throw new NullPointerException("statement");
		// define a new rowset container
		Map<String, WebRowSet> webRowSets = new HashMap<String, WebRowSet>();
		// define a new table counter to give a table name
		int t=0;
		try {
			do {
				webRowSets.put(String.valueOf(t++), getWebRowSet(statement.getResultSet()));				
			} while (statement.getMoreResults());			
		} catch (SQLException x) {
			throw new DAORuntimeException(x.getMessage(),x);
		}
		return webRowSets;
	}

	public DAOWebSupport(DataSource datasource, String schema) {
		super(datasource, schema);
	}

	public DAOWebSupport(Properties config, String schema) {
		super(config, schema);
	}

}
