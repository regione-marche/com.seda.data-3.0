package com.seda.data.datasource;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JndiDataSourceFactory implements DataSourceFactory {

	public static final String INITIAL_CONTEXT = "initial.context";
	public static final String DATA_SOURCE = "data.source";
	public static final String JNDI_PREFIX = "jndi.";

	private DataSource dataSource;

	public void setProperties(Properties properties) {
		try {
			InitialContext initCtx = null;
			Hashtable env = getEnvProperties(properties);
			if (env == null) {
				initCtx = new InitialContext();
			} else {
				initCtx = new InitialContext(env);
			}

			if (properties.containsKey(INITIAL_CONTEXT) && properties.containsKey(DATA_SOURCE)) {
				Context ctx = (Context) initCtx.lookup(properties.getProperty(INITIAL_CONTEXT));
				dataSource = (DataSource) ctx.lookup(properties.getProperty(DATA_SOURCE));
			} else if (properties.containsKey(DATA_SOURCE)) {
				dataSource = (DataSource) initCtx.lookup(properties.getProperty(DATA_SOURCE));
			}

			if (dataSource!=null) {
				DataSourceWrapper dataSourceWrapper = new DataSourceWrapper(dataSource);
				dataSource=dataSourceWrapper.getDataSourceProxy();
			}
		} catch (NamingException e) {
			throw new DataSourceException("There was an error configuring JndiDataSource. Cause: " + e, e);
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	private static Hashtable getEnvProperties(Map properties) {
		Hashtable contextProperties = null;
		Iterator keys = properties.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String value = (String) properties.get(key);
			if (key.startsWith(JNDI_PREFIX)) {
				if (contextProperties == null) {
					contextProperties = new Properties();
				}
				contextProperties.put(key.substring(JNDI_PREFIX.length()), value);
			}
		}
		return contextProperties;
	}

}
