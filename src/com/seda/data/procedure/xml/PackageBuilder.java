/**
 * 
 */
package com.seda.data.procedure.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.seda.commons.reflection.MetaClass;
import com.seda.data.procedure.Procedure;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.parameter.CallParameter;
import com.seda.data.procedure.parameter.CallParameterMap;
import com.seda.data.procedure.parameter.Parameter;
import com.seda.data.procedure.parameter.ParameterMap;
import com.seda.data.procedure.result.Column;
import com.seda.data.procedure.result.ColumnLabel;
import com.seda.data.procedure.result.ColumnMap;
import com.seda.data.type.JdbcType;
import com.seda.data.type.TypeHandler;
/**
 * @author f.ricci
 *
 */

public class PackageBuilder extends BaseParser {

	private String currentCollection;
	private String resource;

	public PackageBuilder(SubSystem context, String resource) {
		super(context);
		this.resource = resource;
	}

	public String getCurrentCollection() {
		return currentCollection;
	}

	public void setCurrentCollection(String currentCollection) {
		if (currentCollection != null) {
			this.currentCollection = currentCollection;
		}
		if (this.currentCollection == null) {
			throw new ParserException("The package element requires a collection attribute to be specified.");
		}
	}

	public String applyCurrentNamespace(String base) {
		if (base == null) return null;
		if (base.contains(".")) return base;
		return currentCollection + "." + base;
	}

//	public ParameterMap addParameterMap(String id, Class parameterClass, List<Parameter> parameterList) {
//		id = applyCurrentNamespace(id);
//		ParameterMap.Factory parameterMapFactory = new ParameterMap.Factory(system, id, parameterClass, parameterList);
//		ParameterMap parameterMap = parameterMapFactory.getParameterMap();
//		system.addParameterMap(parameterMap);
//		return parameterMap;
//	}

//	public Parameter buildParameter(Class parameterType, String property, Class javaType, JdbcType jdbcType, 
//			String resultMap, ParameterDirection parameterDirection, Class typeHandler,	Integer numericScale) {
//
//		resultMap = applyCurrentNamespace(resultMap);
//		// Class parameterType = parameterMapBuilder.type();
//		Class javaTypeClass = resolveParameterJavaType(parameterType, property, javaType, jdbcType);
//		TypeHandler typeHandlerInstance = (TypeHandler) resolveInstance(typeHandler);
//
//		Parameter.Factory parameterFactory = new Parameter.Factory(system, property, javaTypeClass);
//		parameterFactory.jdbcType(jdbcType);
//		parameterFactory.resultMapId(resultMap);
//		parameterFactory.direction(parameterDirection);
//		parameterFactory.numericScale(numericScale);
//		parameterFactory.typeHandler(typeHandlerInstance);
//		return parameterFactory.getParameter();
//	}

	public ColumnMap addColumnMap(String id, Class type, String extend,	List<Column> columnList) {
		id = applyCurrentNamespace(id);
		extend = applyCurrentNamespace(extend);
		ColumnMap.Factory columnMapFactory = new ColumnMap.Factory(system, id, type, columnList);
		if (extend != null) {
			ColumnMap columnMap = system.getColumnMap(extend);
			columnList.addAll(columnMap.getColumnList());
		}
		ColumnMap columnMap = columnMapFactory.getColumnMap();
		system.addColumnMap(columnMap);
		return columnMap;
	}

	public Column buildColumn(Class resultType,	String property, String columnName,	Class javaType,	JdbcType jdbcType,
			String nestedSelect, String nestedColumnMap, Class typeHandler,	
			List<ColumnLabel> labels, String boolexp, CallParameterMap callParameterMap, String callParameter) {
		Column column = assembleColumn(resultType, property, columnName, javaType, jdbcType,nestedSelect,
				nestedColumnMap, typeHandler,labels,boolexp,callParameterMap, callParameter);
		return column;
	}

	public Procedure addMappedProcedure(String id, String specific, Integer fetchSize, Integer timeout, 
			String parameterMap, Class parameterType, String columnMap,	Class resultType, 
			CallParameterMap<String, CallParameter> callParameterMap) {

		String currentId = applyCurrentNamespace(id);
		Procedure.Factory callFactory = new Procedure.Factory(system, currentId);
		callFactory.name(id,specific);
		callFactory.resource(resource);
		callFactory.fetchSize(fetchSize);
		callFactory.callParameterMap(callParameterMap);
		setProcedureTimeout(timeout, callFactory);

//		setParameterMap(parameterMap, parameterType, callFactory);
		setParameterType(parameterType, callFactory);
		setColumnMap(columnMap, resultType, callFactory);

		Procedure procedure = callFactory.getMappedProcedure();
		system.addMappedProcedure(procedure);
		return procedure;
	}

	public CallParameter buildCallParameter(String name, String property, String column, String boolexp, boolean extra) {
		CallParameter.Factory factory = new CallParameter.Factory(name, column, property, boolexp, extra);
		return factory.getCallParameter();
	}
	
	
	private <T> T valueOrDefault(T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}

	private void setParameterType(Class parameterTypeClass, 
			Procedure.Factory mappedProcedureFactory) {

		if (parameterTypeClass != null) {
			List<Parameter> parameterList = new ArrayList<Parameter>();
			ParameterMap.Factory inlineParameterMapBuilder = new ParameterMap.Factory(
					system, mappedProcedureFactory.id() + "-Inline",
					parameterTypeClass, parameterList);
			mappedProcedureFactory.parameterMap(inlineParameterMapBuilder.getParameterMap());
		}
		
	}
//	private void setParameterMap(String parameterMap, Class parameterTypeClass, 
//			Procedure.Factory mappedProcedureFactory) {
//		parameterMap = applyCurrentNamespace(parameterMap);
//
//		if (parameterMap != null) {
//			mappedProcedureFactory.parameterMap(system.getParameterMap(parameterMap));
//		} else if (parameterTypeClass != null) {
//			List<Parameter> parameterList = new ArrayList<Parameter>();
//			ParameterMap.Factory inlineParameterMapBuilder = new ParameterMap.Factory(
//					system, mappedProcedureFactory.id() + "-Inline",
//					parameterTypeClass, parameterList);
//			mappedProcedureFactory.parameterMap(inlineParameterMapBuilder.getParameterMap());
//		}
//	}

	private void setColumnMap(String columnMap, Class resultType, Procedure.Factory mappedProcedureFactory) {
		columnMap = applyCurrentNamespace(columnMap);

		List<ColumnMap> columnMaps = new ArrayList<ColumnMap>();
		if (columnMap != null) {
			String[] resultMapNames = columnMap.split(",");
			for (String resultMapName : resultMapNames) {
				columnMaps.add(system.getColumnMap(resultMapName.trim()));
			}
		} else if (resultType != null) {
			ColumnMap.Factory inlineColumnMapBuilder = new ColumnMap.Factory(
					system,
					mappedProcedureFactory.id() + "-Inline",
					resultType,
					new ArrayList<Column>());
			columnMaps.add(inlineColumnMapBuilder.getColumnMap());
		}
		mappedProcedureFactory.columnMaps(columnMaps);
	}

	private void setProcedureTimeout(Integer timeout, Procedure.Factory mappedProcedureFactory) {
		if (timeout == null) {
			timeout = system.getDefaultCallTimeout();
		}
		mappedProcedureFactory.timeout(timeout);
	}

	private Column assembleColumn(Class resultType,	String property, String column,	Class javaType,
			JdbcType jdbcType, String nestedSelect, String nestedColumnMap, Class typeHandler,
			List<ColumnLabel> labels, String boolexp, CallParameterMap callParameterMap, String callParameter) {
		// Class resultType = resultMapBuilder.type();
		nestedColumnMap = applyCurrentNamespace(nestedColumnMap);
		Class javaTypeClass = resolveResultJavaType(resultType, property, javaType);
		TypeHandler typeHandlerInstance = (TypeHandler) resolveInstance(typeHandler);

		List<Column> composites = parseCompositeColumnName(column);
		if (composites.size() > 0) {
			Column first = composites.get(0);
			column = first.getName();
		}

		Column.Factory columnFactory = new Column.Factory(system, property, column, javaTypeClass);
		columnFactory.jdbcType(jdbcType);
		columnFactory.nestedCallId(applyCurrentNamespace(nestedSelect));
		columnFactory.nestedColumnMapId(applyCurrentNamespace(nestedColumnMap));
		columnFactory.typeHandler(typeHandlerInstance);
		columnFactory.labels(labels == null ? new ArrayList<ColumnLabel>() : labels);
		columnFactory.boolexp(boolexp);
		columnFactory.composites(composites);
		columnFactory.callParameterMap(callParameterMap==null ? new CallParameterMap("Column " + column) : callParameterMap);
		columnFactory.callParameter(callParameter);
		return columnFactory.getColumn();
	}

	private List<Column> parseCompositeColumnName(String columnName) {
		List<Column> composites = new ArrayList<Column>();
		if (columnName != null) {
			if (columnName.indexOf('=') > -1
					|| columnName.indexOf(',') > -1) {
				StringTokenizer parser = new StringTokenizer(columnName, "{}=, ", false);
				while (parser.hasMoreTokens()) {
					String property = parser.nextToken();
					String column = parser.nextToken();
					Column.Factory complexBuilder = new Column.Factory(system, property, column, system.getHandlerRegistry().getUnkownTypeHandler());
					composites.add(complexBuilder.getColumn());
				}
			}
		}
		return composites;
	}

	private Class resolveResultJavaType(Class resultType, String property, Class javaType) {
		if (javaType == null && property != null) {
			try {
				MetaClass metaResultType = MetaClass.forClass(resultType);
				javaType = metaResultType.getSetterType(property);
			} catch (Exception e) {
				//ignore, following null check statement will deal with the situation
			}
		}
		if (javaType == null) {
			javaType = Object.class;
		}
		return javaType;
	}

	private Class resolveParameterJavaType(Class resultType, String property, Class javaType, JdbcType jdbcType) {
		if (javaType == null) {
			if (JdbcType.CURSOR.equals(jdbcType)) {
				javaType = java.sql.ResultSet.class;
			} else {
				MetaClass metaResultType = MetaClass.forClass(resultType);
				javaType = metaResultType.getGetterType(property);
			}
		}
		if (javaType == null) {
			javaType = Object.class;
		}
		return javaType;
	}

}
