/**
 * 
 */
package com.seda.data.procedure.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author f.ricci
 *
 */
public interface ParameterHandler {

	Object getParameterObject();

	void setParameters(PreparedStatement ps)
	throws SQLException;
	
}
