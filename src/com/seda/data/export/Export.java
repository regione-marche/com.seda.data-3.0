/**
 * 
 */
package com.seda.data.export;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * @author f.ricci
 *
 */
public interface Export {

	public void export(ResultSet resultSet);

}
