/**
 * 
 */
package com.seda.data.procedure.parameter;

import com.seda.data.procedure.SubSystem;
import com.seda.data.type.JdbcType;
import com.seda.data.type.TypeHandler;
import com.seda.data.type.HandlerRegistry;

/**
 * @author f.ricci
 *
 */
public class Parameter {

	private SubSystem context;

	private String property;
	private ParameterDirection direction;
	private Class<?> javaType = Object.class;
	private JdbcType jdbcType;
	private Integer numericScale;
	private TypeHandler typeHandler;
	private String resultMapId;
	private String jdbcTypeName;
	private String boolexp;
	private boolean extra;

	private Parameter() {
	}

	public static class Factory {

		private Parameter parameter = new Parameter();

		public Factory(SubSystem context, String property, TypeHandler typeHandler) {
			parameter.context = context;
			parameter.property = property;
			parameter.typeHandler = typeHandler;
			parameter.direction = ParameterDirection.IN;
		}

		public Factory(SubSystem context, String property, Class<?> javaType) {
			parameter.context = context;
			parameter.property = property;
			parameter.javaType = javaType;
			parameter.direction = ParameterDirection.IN;
		}

		public Factory boolexp(String boolexp) {
			parameter.boolexp=boolexp;
			return this;
		}
		public Factory extra(boolean extra) {
			parameter.extra=extra;
			return this;
		}
		public Factory direction(ParameterDirection direction) {
			parameter.direction = direction;
			return this;
		}

		public Factory javaType(Class<?> javaType) {
			parameter.javaType = javaType;
			return this;
		}

		public Factory jdbcType(JdbcType jdbcType) {
			parameter.jdbcType = jdbcType;
			return this;
		}

		public Factory numericScale(Integer numericScale) {
			parameter.numericScale = numericScale;
			return this;
		}

		public Factory resultMapId(String resultMapId) {
			parameter.resultMapId = resultMapId;
			return this;
		}

		public Factory typeHandler(TypeHandler typeHandler) {
			parameter.typeHandler = typeHandler;
			return this;
		}

		public Factory jdbcTypeName(String jdbcTypeName) {
			parameter.jdbcTypeName = jdbcTypeName;
			return this;
		}
		
		public Parameter getParameter() {
			resolveTypeHandler();
			return parameter;
		}

		private void resolveTypeHandler() {
			if (parameter.typeHandler == null) {
				if (parameter.javaType != null) {
					SubSystem context = parameter.context;
					HandlerRegistry typeHandlerRegistry = context.getHandlerRegistry();
					parameter.typeHandler = typeHandlerRegistry.getTypeHandler(parameter.javaType, parameter.jdbcType);
				}
			}
		}

	}

	public String getProperty() {
		return property;
	}

	public ParameterDirection getDirection() {
		return direction;
	}

	public Class<?> getJavaType() {
		return javaType;
	}

	public JdbcType getJdbcType() {
		return jdbcType;
	}

	public Integer getNumericScale() {
		return numericScale;
	}

	public TypeHandler getTypeHandler() {
		return typeHandler;
	}

	public String getResultMapId() {
		return resultMapId;
	}

	public String getJdbcTypeName() {
		return jdbcTypeName;
	}
	
	public boolean isExtra() {
		return extra;
	}
	
	public boolean isBoolexp() {
		return boolexp!=null;
	}
	
	public String getBoolexp() {
		return boolexp;
	}
	
	public Boolean applyBoolexp(Object value) {
		if (isBoolexp() && boolexp.equals(value)) return true;
		return false;
	}	
}
