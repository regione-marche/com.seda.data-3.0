/**
 * 
 */
package com.seda.data.datasource;

import java.sql.Connection;

/**
 * @author f.ricci
 *
 */
public interface UserConnectionWrapperFactory {

	/**
	 * @param connection
	 * @return the proxied connection
	 */
	public Connection createWrapper(Connection connection);
	
}
