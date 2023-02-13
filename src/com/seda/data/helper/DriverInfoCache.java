/**
 * 
 */
package com.seda.data.helper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * DriverInfoCache provides functions to leverage a static cache of database connectivity driver information, and the
 * ability to discover driver information for connections at run-time 
 * @author Seda Lab
 *
 */
public final class DriverInfoCache {
	/**
	 * It's a HashMap.
	 */
	private static Map<String, DriverInfo> driverInfoCache = new HashMap<String, DriverInfo>();
	
	private static synchronized final Map<String, DriverInfo> getDriverInfoCache() {
		return driverInfoCache;
	}
	
	/**
     * Returns {@link DriverInfo} object. It discovers only once avoiding to use connection every time 
	 * @param connection a valid database connection
	 * @return {@link DriverInfo} the DriverInfo object containing minimal driver information
	 * @throws SQLException if a database access error occurs
	 * @throws HelperException for a bad argument
	 */
	public synchronized final static DriverInfo getDriverInfo(final Connection connection) throws SQLException, HelperException {
        if( connection == null ) throw new HelperException(Messages.ARGUMENT_NULL.format("connection"));
        
        String hashKey = connection.getMetaData().getURL();
        
        DriverInfo driverInfo=null;
        
        if (getDriverInfoCache().containsKey(hashKey)) {
        	driverInfo = getDriverInfoCache().get(hashKey);        	
        } else {
        	DriverInfo newDriverInfo = discoverDriverInfo(connection);
        	getDriverInfoCache().put(hashKey, newDriverInfo);
        	driverInfo = newDriverInfo;
        }
        return cloneDriverInfo(driverInfo);
	}
	
	/**
	 * It discovers the set of database driver metadata from the {@link DatabaseMetaData} object.
	 * @param connection a valid database connection
	 * @return {@link DriverInfo} the DriverInfo object containing minimal driver information
	 * @throws SQLException if a database access error occurs
	 */
	private synchronized static DriverInfo discoverDriverInfo(final Connection connection) throws SQLException {
		DatabaseMetaData databaseMetaData = connection.getMetaData();
		try {
			return new DriverInfo(databaseMetaData.getDriverName(),
					databaseMetaData.getDriverVersion(),
					databaseMetaData.getDriverMajorVersion(),
					databaseMetaData.getDriverMinorVersion()				
					); 			
		} finally {
			databaseMetaData=null;
		}
	}
	
	/**
	 * Creates a new {@link DriverInfo} object using originalDriverInfo.
	 * @param originalDriverInfo the original DriverInfo object
	 * @return <code>String</code> - Returns the {@link String} object.
	 */
	private synchronized static DriverInfo cloneDriverInfo(DriverInfo originalDriverInfo) {
		return originalDriverInfo.clone();
	}	
}
