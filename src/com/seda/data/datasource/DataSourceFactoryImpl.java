/**
 * 
 */
package com.seda.data.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import com.seda.commons.reflection.MetaObject;
/**
 * @author f.ricci
 *
 */
public class DataSourceFactoryImpl implements DataSourceFactory {

	public final String DRIVER_KEY="driver.";
	
	protected DataSource dataSource;

	public DataSourceFactoryImpl() {
		this.dataSource = new DataSourceImpl();
	}

	public void setProperties(Properties properties) {
		Properties driverProperties = new Properties();
		MetaObject metaDataSource = MetaObject.forObject(dataSource);
		for (Object key : properties.keySet()) {
			String propertyName = (String) key;
			if (metaDataSource.hasSetter(retroCompatibility(propertyName))) {
				String value = (String) properties.get(propertyName);
				Object convertedValue = convertValue(metaDataSource, retroCompatibility(propertyName), value);
				metaDataSource.setValue(retroCompatibility(propertyName), convertedValue);
			} else if (propertyName.startsWith(DRIVER_KEY)){
				driverProperties.put(propertyName.substring(DRIVER_KEY.length()), properties.get(propertyName));
			} else {
				throw new DataSourceException("Unkown DataSource property: " + propertyName);
			}
		}
		if (driverProperties.size() > 0) {
			metaDataSource.setValue("driverProperties", driverProperties);
		}
	}

	private String retroCompatibility(String propertyName) {
		if ("user".equalsIgnoreCase(propertyName)) {
			return "username";
		} if ("autocommit".equalsIgnoreCase(propertyName)) {
			return "autoCommit";
		}
		return propertyName;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	private Object convertValue(MetaObject metaDataSource, String propertyName, String value) {
		Object convertedValue = value;
		Class<?> targetType = metaDataSource.getSetterType(propertyName);
		if (targetType == Integer.class || targetType == int.class) {
			convertedValue = Integer.valueOf(value);
		} else if (targetType == Long.class || targetType == long.class) {
			convertedValue = Long.valueOf(value);
		} else if (targetType == Boolean.class || targetType == boolean.class) {
			convertedValue = Boolean.valueOf(value);
		}
		return convertedValue;
	}

}
