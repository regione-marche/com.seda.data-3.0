/**
 * 
 */
package com.seda.data.helper;

/**
 * The DriveInfo is used by {@link DriverInfoCache} to hold the connection's driver information 
 * @author Seda Lab
 *
 */
public class DriverInfo {

	private String name;
	private String version;
	private int majorVersion;
	private int minorVersion;
	/**
	 * Retrieves the name of this JDBC driver
	 *  
	 * @return <code>String</code> the JDBC driver name
	 * @see java.sql.DatabaseMetaData#getDriverName()
	 */
	public String getName() {
		return name;
	}
	/**
	 * Retrieves the version number of this JDBC driver as a String
	 * @return <code>String</code> the JDBC driver version
	 * @see java.sql.DatabaseMetaData#getDriverVersion() 
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * Retrieves this JDBC driver's major version number
	 *  
	 * @return <code>int</code> JDBC driver major version
	 * @see java.sql.DatabaseMetaData#getDriverMajorVersion()
	 */
	public int getMajorVersion() {
		return majorVersion;
	}
	/**
	 * Retrieves this JDBC driver's minor version number
	 *  
	 * @return <code>int</code> JDBC driver minor version number
	 * @see java.sql.DatabaseMetaData#getDriverMinorVersion()
	 */
	public int getMinorVersion() {
		return minorVersion;
	}
	/**
	 * Constructor for DriverInfo object
	 *  
	 * @param name the JDBC driver name
	 * @param version the JDBC driver version
	 * @param majorVersion the JDBC driver major version
	 * @param minorVersion the JDBC driver minor version number
	 * @see java.sql.DatabaseMetaData
	 */
	public DriverInfo(String name, String version, int majorVersion, int minorVersion) {
		this.name=name;
		this.version=version;
		this.majorVersion=majorVersion;
		this.minorVersion=minorVersion;
	}
	
	public DriverInfo clone() {
		return new DriverInfo(getName(),getVersion(),getMajorVersion(),getMinorVersion());
	}
}
