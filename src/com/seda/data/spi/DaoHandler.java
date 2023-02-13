/**
 * 
 */
package com.seda.data.spi;



import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.WebRowSet;

import com.seda.commons.string.Convert;
import com.seda.data.dao.DAOHelper;
import com.seda.data.helper.Helper;
import com.seda.data.helper.HelperException;
import com.seda.data.helper.Messages;
import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.WebRowSetImpl;
/**
 * @author Seda Lab
 *
 */
public abstract class DaoHandler {

	private String schema;
	private Connection connection;
	private ResultSet resultSet = null;
	private int sqlcode;
	private String code;	
	private String message;	
	private CachedRowSetImpl cachedRowSetImpl;
	private Map<String, CachedRowSetImpl> cachedRowSets;
	private WebRowSetImpl webRowSetImpl;
	private Map<String, WebRowSetImpl> webRowSets;	
	/**
	 * Returns the last method operation sqlcode or the last sqlcode set by DAO. <code>0</code> is the default
	 * @return <code>int</code> the sqlcode
	 */
	public final int sqlcode(){ return this.sqlcode; }
	/**
	 * Sets the last method operation sqlcode
	 * @param sqlcode the <code>int</code> that defines the sqlcode
	 */
	protected final void setSqlcode(int sqlcode) { this.sqlcode = sqlcode; }
	/**
	 * Returns the last method operation return code or the last return code set by DAO. <code>null</code> if no return code was set
	 * @return <code>String</code> the user return code
	 */	
	public final String rc(){ return this.code; }
	/**
	 * Sets the last method operation return code
	 * @param code The <code>String</code> that represents the user return code
	 */
	protected final void setRc(String code) { this.code = code; }
	/**
	 * Returns the last method operation return message or the last message set by DAO. <code>null</code> if no return message was set
	 * @return <code>int</code> The sqlcode
	 */	
	public final String message() { return this.message; }
	/**
	 * Sets the last method operation return message.
	 * @param code the <code>String</code> that represents the user return message
	 */	
	protected final void setMessage(String message) { this.message = message; }
	/**
	 * Returns the last {@link ResultSet} set by DAO. <code>null</code> if no ResultSet was set
	 * @return <code>int</code> the sqlcode
	 */	
	public final ResultSet getResultSet() { return this.resultSet; }
	/**
	 * Sets the ResultSet that DAO wants to return to the caller after a method operation
	 * @param resultSet the <code>ResultSet</code> 
	 */	
	protected final void setResultSet(ResultSet resultSet) { this.resultSet = resultSet;}
	/**
	 * Returns the schema of the database object used in the DAO fields' constructor
	 * @return <code>String</code> the schema of the database object
	 */
	protected String getSchema() { return this.schema; }
	/**
	 * Returns the {@link Connection} object used in the DAO fields' constructor
	 * @return <code>{@link Connection}</code> the Connection object
	 */	
	protected Connection getConnection() { return this.connection; }
	/**
	 * Returns a prepared CallableStatement object builded using DAO schema and provided routine name   
	 * 
	 * @param routine The name of the stored procedure that will be called
	 * @return <code>{@link CallableStatement}</code> the prepared statement
	 * @throws IllegalArgumentException in case of null input arguments
	 * @throws SQLException if a database access error occurs  
	 * @throws HelperException in case of unpaired number of parameters. In this method will never happen
	 */
	
	protected final CallableStatement prepareCall(String routine) throws IllegalArgumentException, SQLException, HelperException {
		return prepareCall(routine, -1);
	}
	
	/**
	 * Returns a prepared CallableStatement object builded using DAO schema and provided routine name
	 *  
	 * @param routine the name of the stored procedure that will called
	 * @param parameterCountExpected The number of expected parameter excluding the return value
	 * @return <code>{@link CallableStatement}</code> the prepared statement
	 * @throws IllegalArgumentException in case of null input arguments
	 * @throws SQLException if a database access error occurs 
	 * @throws HelperException in case of unpaired number of parameters
	 */
	protected final CallableStatement prepareCall(String routine, int parameterCountExpected) throws IllegalArgumentException, SQLException, HelperException {
		return Helper.prepareCall(getConnection(), getSchema(), routine, parameterCountExpected);
	}	
	/**
	 * Constructor of DAO object
	 * 
	 * @param connection The {@link Connection} object used in the DAO method operations
	 * @param schema the <code>String</code> that represents schema of the database objects
	 */
	public DaoHandler(Connection connection, String schema) {
		this.connection = connection;
		this.schema = schema;		
	}
	/**
	 * Returns the last {@link CachedRowSet} object filled from DAO. <code>null</code> if no CahcedRowSet was stored
	 * @return <code>{@link CachedRowSet}</code> the ChachedRowSet object
	 */
	public final CachedRowSet getCachedRowSet() {
		return cachedRowSetImpl;
	}
	/**
	 * Returns the last {@link CachedRowSetImpl} object filled from DAO. <code>null</code> if no CahcedRowSet was stored
	 * @return <code>{@link CachedRowSetImpl}</code> the ChachedRowSetImpl object
	 */
	public final CachedRowSetImpl getCachedRowSetImpl() {
		return cachedRowSetImpl;
	}	
	/**
	 * Returns the {@link CachedRowSet} object filled from DAO identified by the key. <code>null</code> if no CahcedRowSet was found. 
	 * @param key the key of the CachedRowSet in the HasMap 
	 * @return <code>{@link CachedRowSet}</code> the ChachedRowSet object
	 */
	public final CachedRowSet getCachedRowSet(String key) {
		return getCachedRowSets().get(key);
	}
	/**
	 * Returns the {@link CachedRowSetImpl} object filled from DAO identified by the key. <code>null</code> if no CahcedRowSet was found. 
	 * @param key the key of the CachedRowSetImpl in the HasMap 
	 * @return <code>{@link CachedRowSetImpl}</code> the ChachedRowSetImpl object
	 */
	public final CachedRowSetImpl getCachedRowSetImpl(String key) {
		return getCachedRowSets().get(key);
	}		
	/**
	 * Returns the last {@link WebRowSet} object filled from DAO. <code>null</code> if no WebRowSet was stored
	 * @return <code>{@link WebRowSet}</code> the WebRowSet object
	 */
	public final WebRowSet getWebRowSet() {
		return webRowSetImpl;
	}
	/**
	 * Returns the last {@link WebRowSetImpl} object filled from DAO. <code>null</code> if no WebRowSet was stored
	 * @return <code>{@link WebRowSet}</code> the WebRowSetImpl object
	 */
	public final WebRowSetImpl getWebRowSetImpl() {
		return webRowSetImpl;
	}	
	/**
	 * Returns the {@link WebRowSet} object filled from DAO identified by the key. <code>null</code> if no WebRowSet was found. 
	 * @param key the key of the WebRowSet in the HasMap
	 * @return <code>{@link WebRowSet}</code> the WebRowSet object
	 */
	public final WebRowSet getWebRowSet(String key) {
		return getWebRowSets().get(key);
	}
	/**
	 * Returns the {@link WebRowSetImpl} object filled from DAO identified by the key. <code>null</code> if no WebRowSet was found. 
	 * @param key the key of the WebRowSetImpl in the HasMap
	 * @return <code>{@link WebRowSet}</code> The WebRowSet object
	 */
	public final WebRowSet getWebRowSetImpl(String key) {
		return getWebRowSets().get(key);
	}	
	/**
	 * Returns the data, properties, and metadata for the last loaded WebRowSet object in XML format.
	 * @return <code>String</code> the xml data
	 * @throws SQLException if a database access error occurs
	 */
	public final String getWebRowSetXml() throws SQLException {
		return Convert.webRowSetToString(getWebRowSet());
	}
	/**
	 * Returns the data, properties, and metadata for the WebRowSet object, identified by the key, in XML format. 
	 * @param key key the key of the WebRowSet in the HasMap
	 * @return <code>String</code> the xml data
	 * @throws SQLException if a database access error occurs
	 */
	public final String getWebRowSetXml(String key) throws SQLException {
		return Convert.webRowSetToString(getWebRowSet(key));
	}	
	
	/**
	 * Returns the last {@link HashMap} object filled with all {@link CachedRowSetImpl} returned from a {@link Statement}. <code>null</code> if no CachedRowSet was stored in the HashMap.
	 * The key of the HashMap represents, if the ResultSet table name is null, a numeric integer in order of the ResultSet found in the Statement, or the table name of the ResultSet. 
	 * @return <code>Map<String, CachedRowSet></code> HashMap with the stored CachedRowSet
	 */
	public final Map<String, CachedRowSetImpl> getCachedRowSets(){
		return cachedRowSets;
	}
	/**
	 * Returns the last {@link HashMap} object filled with all {@link WebRowSetImpl} returned from a {@link Statement}. <code>null</code> if no WebRowSet was stored in the HashMap.
	 * The key of the HashMap represents, if the ResultSet table name is null, a numeric integer in order of the ResultSet found in the Statement, or the table name of the ResultSet. 
	 * @return <code>Map<String, WebRowSet></code> HashMap with the stored WebRowSet
	 */
	public final Map<String, WebRowSetImpl> getWebRowSets() {
		return webRowSets;
	}		
	/**
	 * Loads the current ResultSet of the provided {@link Statement} in a CachedRowSetImpl object.
	 * 
	 * @param statement the executed Statement
	 * @throws SQLException if a database access error occurs
	 */
	protected final void loadChachedRowSet(Statement statement) throws SQLException {
		loadChachedRowSet(statement.getResultSet());
	}
	/**
	 * Loads the current ResultSet of the provided {@link Statement} in a {@link WebRowSetImpl} object.
	 *  
	 * @param statement the executed Statement
	 * @throws SQLException if a database access error occurs
	 */
	protected final void loadWebRowSet(Statement statement) throws SQLException {
		loadWebRowSet(statement.getResultSet());
	}	
	/**
	 * Loads the provided {@link ResultSet} in a {@link CachedRowSetImpl} object. 
	 * 
	 * @param resultSet the ResultSet 
	 * @throws SQLException if a database access error occurs
	 */
	protected final void loadChachedRowSet(ResultSet resultSet) throws SQLException {
		// if rs is null means that there are no more results		
		if (resultSet==null) {
			throw new SQLException(Messages.ARGUMENT_NULL.format("ResultSet"));
		}
		try {
			// define a new cached rowset		
			cachedRowSetImpl = new CachedRowSetImpl();
			// fill data		
			cachedRowSetImpl.populate(resultSet);
		} finally {
			DAOHelper.closeIgnoringException(resultSet);
		}		
	}
	/**
	 * Loads the provided {@link ResultSet} in a {@link WebRowSetImpl} object.
	 * @param resultSet the ResultSet
	 * @throws SQLException if a database access error occurs
	 */
	protected final void loadWebRowSet(ResultSet resultSet) throws SQLException {
		// if rs is null means that there are no more results		
		if (resultSet==null) {
			throw new SQLException(Messages.ARGUMENT_NULL.format("ResultSet"));
		}
		try {
			// define a new cached rowset		
			webRowSetImpl = new WebRowSetImpl();
			// fill data		
			webRowSetImpl.populate(resultSet);			
		} finally {
			DAOHelper.closeIgnoringException(resultSet);
		}

	}	
	/**
	 * Loads the all ResultSet objects of the provided {@link Statement} in a HashMap of {@link CachedRowSetImpl} objects. 
     * The key of the HashMap represents a numeric integer in order of the ResultSet found in the Statement 
	 * @param statement the executed Statement
	 * @throws SQLException if a database access error occurs
	 */
	protected final void loadChachedRowSets(Statement statement) throws SQLException {
		// this prevents from NullPointerException		
		if (statement==null) return;
		// define a new rowset container
		cachedRowSets = new HashMap<String, CachedRowSetImpl>();
		// define a new table counter to give a table name
		int t=0;
		do {
			loadChachedRowSet(statement);
			// if no name has been set for the table 
			//if (getCachedRowSet().getTableName()==null) {
				// set the name according to a generic table counter
				getCachedRowSet().setTableName(String.valueOf(t++));
			//} 
			// put the cached rowset implementation into the hashmap				
			cachedRowSets.put(getCachedRowSet().getTableName(), getCachedRowSetImpl());				
		} while (statement.getMoreResults());		
	}
	/**
	 * Loads the all ResultSet objects of the provided {@link Statement} in a HashMap of {@link WebRowSetImpl} objects
     * The key of the HashMap represents a numeric integer in order of the ResultSet found in the Statement
	 * @param statement the executed Statement
	 * @throws SQLException if a database access error occurs
	 */
	protected final void loadWebRowSets(Statement statement) throws SQLException {
		// this prevents from NullPointerException		
		if (statement==null) return;
		// define a new rowset container
		webRowSets = new HashMap<String, WebRowSetImpl>();
		// define a new table counter to give a table name
		int t=0;
		do {
			loadWebRowSet(statement);
			// if no name has been set for the table 
			//if (getWebRowSet().getTableName()==null) {
				// set the name according to a generic table counter
				getWebRowSet().setTableName(String.valueOf(t++));
			//} 
			// put the cached rowset into the hashmap				
			webRowSets.put(getWebRowSet().getTableName(), getWebRowSetImpl());				
		} while (statement.getMoreResults());		
	}

	/**
	 * Executes a commit operation on the DAO database connection
	 * @see Helper#commit(Connection) 
	 */
	protected void commit() {
		Helper.commit(getConnection());
	}
	/**
	 * Executes a rollback operation on the DAO database connection
	 * @see Helper#rollback(Connection)
	 */
	protected void rollback() {
		Helper.rollback(getConnection());
	}	
	/**
	 * Override the base object finalize method
	 */
	public void finalize() {
		destroy();
	}
	/**
	 * Destroy cached statement
	 */
	public void destroy() {
		Helper.close(resultSet);
		if (cachedRowSetImpl!=null)
			Helper.close(cachedRowSetImpl);
		if (webRowSetImpl!=null)
			Helper.close(webRowSetImpl);
		cachedRowSets=null;
		webRowSets=null;	
	}
}
