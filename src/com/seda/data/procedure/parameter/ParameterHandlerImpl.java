/**
 * 
 */
package com.seda.data.procedure.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.seda.commons.reflection.MetaObject;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.Procedure;
import com.seda.data.procedure.ProcedureBound;
import com.seda.data.type.TypeHandler;
import com.seda.data.type.HandlerRegistry;
/**
 * @author f.ricci
 *
 */
public class ParameterHandlerImpl implements ParameterHandler {

	private final HandlerRegistry handlerRegistry;

	private final Procedure mappedProcedure;
	private final Object parameterObject;
	private ProcedureBound procedureBound;
	private SubSystem system;

	public ParameterHandlerImpl(Procedure mappedProcedure, Object parameterObject, ProcedureBound procedureBound) {
		this.mappedProcedure = mappedProcedure;
		this.system = mappedProcedure.getSubSystem();
		this.handlerRegistry = mappedProcedure.getSubSystem().getHandlerRegistry();
		this.parameterObject = parameterObject;
		this.procedureBound = procedureBound;
	}

	public Object getParameterObject() {
		return parameterObject;
	}

	public void setParameters(PreparedStatement ps) throws SQLException {
		List<Parameter> parameterList = procedureBound.getParameterList();
		if (parameterList != null) {
			MetaObject metaObject = parameterObject == null ? null : system.getMetaObject(parameterObject);
			for (int i = 0; i < parameterList.size(); i++) {
				Parameter parameter = parameterList.get(i);
				if (parameter.getDirection() != ParameterDirection.OUT) {
					Object value;
					String propertyName = parameter.getProperty();

					if (parameterObject == null) {
						value = null;
					} else if (handlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else if (procedureBound.hasAdditionalParameter(propertyName)) {
						value = procedureBound.getAdditionalParameter(propertyName);
					} else if (parameter.isExtra() && metaObject.hasAttributes()) {
						value = metaObject == null ? null : metaObject.getAttribute(propertyName);
					} else {
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}
					
					TypeHandler typeHandler = parameter.getTypeHandler();
					if (typeHandler == null) {
						throw new ParameterException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedProcedure.getId());
					}
					
					if (parameter.isBoolexp() && value instanceof Boolean) {
						boolean boolValue = (Boolean) value;
						typeHandler.setParameter(ps, i + 1, boolValue?parameter.getBoolexp():" ", parameter.getJdbcType());
					} else {
						typeHandler.setParameter(ps, i + 1, value, parameter.getJdbcType());						
					}
				}
			}
		}
	}

}
