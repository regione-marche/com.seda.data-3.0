/**
 * 
 */
package com.seda.data.datasource;

import java.util.Properties;

import javax.sql.DataSource;

/**
 * @author f.ricci
 *
 */
public interface DataSourceFactory {


	void setProperties(Properties props);

	DataSource getDataSource();	

}
