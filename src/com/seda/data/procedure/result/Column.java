/**
 * 
 */
package com.seda.data.procedure.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.parameter.CallParameterMap;
import com.seda.data.type.HandlerRegistry;
import com.seda.data.type.JdbcType;
import com.seda.data.type.TypeHandler;

/**
 * @author f.ricci
 *
 */
public class Column {

	private SubSystem system;
	private String property;
	private String name;
	private Class<?> javaType;
	private JdbcType jdbcType;
	private TypeHandler typeHandler;
	private String nestedColumnMapId;
	private List<ColumnLabel> labels;
	private List<Column> composites;
	private String boolexp;
	
	private String nestedCallId;
	private CallParameterMap callParameterMap;
	private String callParameter;	
	
	
	private Column() {
	}

	public static class Factory {
		private Column resultMapping = new Column();

		public Factory(SubSystem context, String property, String name, TypeHandler typeHandler) {
			resultMapping.system = context;
			resultMapping.property = property;
			resultMapping.name = name;
			resultMapping.typeHandler = typeHandler;
			resultMapping.labels = new ArrayList<ColumnLabel>();
			resultMapping.composites = new ArrayList<Column>();
			resultMapping.callParameterMap = new CallParameterMap("Column " +name);
		}

		public Factory(SubSystem context, String property, String name, Class<?> javaType) {
			resultMapping.system = context;
			resultMapping.property = property;
			resultMapping.name = name;
			resultMapping.javaType = javaType;
			resultMapping.labels = new ArrayList<ColumnLabel>();			
			resultMapping.composites = new ArrayList<Column>();
		}

		public Factory(SubSystem context, String property) {
			resultMapping.system = context;
			resultMapping.property = property;
			resultMapping.labels = new ArrayList<ColumnLabel>();			
			resultMapping.composites = new ArrayList<Column>();
		}

		public Factory javaType(Class<?> javaType) {
			resultMapping.javaType = javaType;
			return this;
		}

		public Factory jdbcType(JdbcType jdbcType) {
			resultMapping.jdbcType = jdbcType;
			return this;
		}

		public Factory nestedColumnMapId(String nestedColumnMapId) {
			resultMapping.nestedColumnMapId = nestedColumnMapId;
			return this;
		}

		public Factory nestedCallId(String nestedCallId) {
			resultMapping.nestedCallId = nestedCallId;
			return this;
		}

		public Factory typeHandler(TypeHandler typeHandler) {
			resultMapping.typeHandler = typeHandler;
			return this;
		}

		public Factory composites(List<Column> composites) {
			resultMapping.composites = composites;
			return this;
		}
		public Factory callParameterMap(CallParameterMap callParameterMap) {
			resultMapping.callParameterMap = callParameterMap;
			return this;
		}
		public Factory callParameter(String callParameter) {
			resultMapping.callParameter = callParameter;
			return this;
		}		
		public Factory labels(List<ColumnLabel> labels) {
			resultMapping.labels = labels;
			return this;
		}
		public Factory boolexp(String booleexp) {
			resultMapping.boolexp=booleexp;
			return this;
		}
		public Column getColumn() {
			//lock down collections
			resultMapping.composites = Collections.unmodifiableList(resultMapping.composites);
			resolveTypeHandler();
			return resultMapping;
		}

		private void resolveTypeHandler() {
			if (resultMapping.typeHandler == null) {
				if (resultMapping.javaType != null) {
					SubSystem context = resultMapping.system;
					HandlerRegistry handlerRegistry = context.getHandlerRegistry();
					resultMapping.typeHandler = handlerRegistry.getTypeHandler(resultMapping.javaType, resultMapping.jdbcType);
				}
			}
		}

		public Factory name(String name) {
			resultMapping.name = name;
			return this;
		}
	}

	public String getProperty() {
		return property;
	}

	public String getName() {
		return name;
	}

	public Class<?> getJavaType() {
		return javaType;
	}

	public JdbcType getJdbcType() {
		return jdbcType;
	}

	public TypeHandler getTypeHandler() {
		return typeHandler;
	}

	public String getNestedColumnMapId() {
		return nestedColumnMapId;
	}

	public String getNestedCallId() {
		return nestedCallId;
	}

	public List<ColumnLabel> getLabels() {
		return labels;
	}	

	public List<Column> getComposites() {
		return composites;
	}
	
	public CallParameterMap getCallParameterMap() {
		return callParameterMap;
	}	

	public String getCallParameter() {
		return callParameter;
	}		
	
	public boolean isExtra() {
		return labels.contains(ColumnLabel.EXTRA);
	}		

	public boolean isBoolexp() {
		return labels.contains(ColumnLabel.BOOLEXP);
	}
	
	public Boolean applyBoolexp(Object value) {
		if (isBoolexp() && boolexp.equals(value)) return true;
		return false;
	}		
	
	public boolean isCompositeResult() {
		return this.composites != null && !this.composites.isEmpty();
	}
	
	public boolean hasCompositeParameter() {
		return this.callParameterMap != null && !this.callParameterMap.isEmpty();
	}	

	@Override
	public String toString() {
		return "Column [name=" + name + ", composites=" + composites
		+ ", javaType=" + javaType + ", jdbcType=" + jdbcType
		+ ", labels=" + labels + ", nestedCallId=" + nestedCallId
		+ ", nestedColumnMapId=" + nestedColumnMapId + ", property="
		+ property + ", typeHandler=" + typeHandler + "]";
	}	
}
