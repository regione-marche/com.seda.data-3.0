/**
 * 
 */
package com.seda.data.datasource;
/**
 * @author f.ricci
 *
 */
public class PooledDataSourceFactory extends DataSourceFactoryImpl {

	public PooledDataSourceFactory() { 
		this.dataSource = new PooledDataSource();
	}

}
