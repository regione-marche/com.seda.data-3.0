/**
 * 
 */
package com.seda.data.procedure.result;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.seda.commons.reflection.MetaObject;
import com.seda.commons.reflection.factory.ObjectFactory;
import com.seda.data.procedure.Procedure;
import com.seda.data.procedure.ProcedureBound;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.executor.Executor;
import com.seda.data.procedure.executor.ExecutorException;
import com.seda.data.procedure.parameter.CallParameter;
import com.seda.data.procedure.parameter.Parameter;
import com.seda.data.procedure.parameter.ParameterDirection;
import com.seda.data.procedure.parameter.ParameterHandler;
import com.seda.data.type.HandlerRegistry;
import com.seda.data.type.TypeHandler;
/**
 * @author f.ricci
 *
 */
public class ResultSetHandlerImpl implements ResultSetHandler {

	protected final Executor executor;
	protected final SubSystem system;
	protected final Procedure mappedProcedure;
	protected final ResultBounds resultBounds;
	protected final ParameterHandler parameterHandler;
	protected final ResultHandler resultHandler;
	protected final ProcedureBound boundProcedure;
	protected final HandlerRegistry handlerRegistry;
	protected final ObjectFactory objectFactory;	

	public ResultSetHandlerImpl(Executor executor, Procedure mappedProcedure, ParameterHandler parameterHandler, ProcedureBound boundProcedure, ResultBounds resultBounds) {
		this.executor = executor;
		this.system = mappedProcedure.getSubSystem();
		this.mappedProcedure = mappedProcedure;
		this.resultBounds = resultBounds;
		this.parameterHandler = parameterHandler;
		this.boundProcedure = boundProcedure;
		this.handlerRegistry = system.getHandlerRegistry();
		this.objectFactory = system.getObjectFactory();
		this.resultHandler = new ResultHandlerImpl();
	}	


	// ***********************
	// Handle output parameter
	// ***********************
	public void processOutputParameters(CallableStatement cs) throws SQLException {
		final Object parameterObject = parameterHandler.getParameterObject();
		final MetaObject metaParam = system.getMetaObject(parameterObject);
		final List<Parameter> parameterList = boundProcedure.getParameterList();
		for (int i = 0; i < parameterList.size(); i++) {
			final Parameter parameter = parameterList.get(i);
			if (parameter.getDirection() == ParameterDirection.OUT || parameter.getDirection() == ParameterDirection.INOUT) {
				if ("java.sql.ResultSet".equalsIgnoreCase(parameter.getJavaType().getName())) {
					throw new ExecutorException("ResultSet type handler was not yet implemented" );
				} else {
					final TypeHandler typeHandler = parameter.getTypeHandler();
					if (typeHandler == null) {
						throw new ExecutorException("Type handler was null on parameter mapping for property " + parameter.getProperty() + ".  " +
						"It was either not specified and/or could not be found for the javaType / jdbcType combination specified.");
					}
					Object value = typeHandler.getResult(cs, i + 1);
					if (parameter.isExtra() && metaParam.hasAttributes()) {
						metaParam.setAttribute(parameter.getProperty(), value);
					} else if (parameter.isBoolexp()) {
						metaParam.setValue(parameter.getProperty(), parameter.applyBoolexp(value));
					} else {
						metaParam.setValue(parameter.getProperty(), value);						
					}
//					metaParam.setValue(parameter.getProperty(), typeHandler.getResult(cs, i + 1));
				}
			}
		}
	}

	// *******************
	// Handler result sets
	// *******************
	public List processResultSets(Statement stmt) throws SQLException {
		final List multipleResults = new ArrayList();
		final List<ColumnMap> resultMaps = mappedProcedure.getColumnMapList();
		int resultMapCount = resultMaps.size();
		int resultSetCount = 0;
		ResultSet rs = stmt.getResultSet();
		validateResultMapsCount(rs,resultMapCount);
		while (rs != null && resultMapCount > resultSetCount) {
			final ColumnMap resultMap = resultMaps.get(resultSetCount);
			handleResultSet(rs, resultMap, multipleResults);
			rs = getNextResultSet(stmt);
			cleanUpAfterHandlingResultSet();
			resultSetCount++;
		}
		return collapseSingleResultList(multipleResults);
	}

	protected void cleanUpAfterHandlingResultSet() {
		resultHandler.getResultList().clear();
	}

	protected void validateResultMapsCount(ResultSet rs, int resultMapCount) {
		if (rs != null && resultMapCount < 1) {
			throw new ExecutorException(
					"A procedure was run and no Result Maps were found for the Mapped Statement '"
					+ mappedProcedure.getId()
					+ "'.  It's seem that neither a Result Type nor a Result Map was specified.");
		}
	}

	protected void handleResultSet(ResultSet rs, ColumnMap resultMap, List multipleResults) throws SQLException {
		handleRowValues(rs, resultMap, resultHandler, resultBounds);
		multipleResults.add(new ArrayList(resultHandler.getResultList()));		
	}

	protected List collapseSingleResultList(List multipleResults) {
		if (multipleResults.size() == 1) {
			return (List) multipleResults.get(0);
		} else {
			return multipleResults;
		}
	}

	// *****************
	// Handle rows
	// *****************
	protected void handleRowValues(ResultSet rs, ColumnMap resultMap, ResultHandler resultHandler, ResultBounds resultBounds) throws SQLException {
		final ResultContextImpl resultContext = new ResultContextImpl();
		skipRows(rs, resultBounds);
		while (hasNext(rs, resultContext, resultBounds)) {
			Object rowValue = getRowValue(rs, resultMap, null);
			resultContext.nextObject(rowValue);
			resultHandler.process(resultContext);
		}
	}

	protected boolean hasNext(ResultSet rs, ResultContext context, ResultBounds resultBounds) throws SQLException {
		return rs.next() && context.getResultCount() < resultBounds.getLimit() && !context.isStopped();
	}

	protected void skipRows(ResultSet rs, ResultBounds resultBounds) throws SQLException {
		if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
			if (resultBounds.getOffset() != 0) {
				rs.absolute(resultBounds.getOffset());
			}
		} else {
			for (int i = 0; i < resultBounds.getOffset(); i++) rs.next();
		}
	}

	protected ResultSet getNextResultSet(Statement stmt) throws SQLException {
		// Making this method tolerant of bad JDBC drivers
		try {
			if (stmt.getConnection().getMetaData().supportsMultipleResultSets()) {
				// Crazy Standard JDBC way of determining if there are more results
				if (!((!stmt.getMoreResults()) && (stmt.getUpdateCount() == -1))) {
					return stmt.getResultSet();
				}
			}
		} catch (Exception e) { }
		return null;
	}

	// ******************
	// Get value from row
	// ******************
	protected Object getRowValue(ResultSet rs, ColumnMap resultMap, BreakHandler breakHandler) throws SQLException {
		final List<String> mappedColumnNames = new ArrayList<String>();
		final List<String> unmappedColumnNames = new ArrayList<String>();
		Object resultObject = createResultObject(rs, resultMap);
		if (resultObject != null && !handlerRegistry.hasTypeHandler(resultMap.getType())) {
			final MetaObject metaObject = system.getMetaObject(resultObject);
			loadMappedAndUnmappedColumnNames(rs, resultMap, mappedColumnNames, unmappedColumnNames);
			boolean foundValues = resultMap.getConstructorColumnList().size() > 0;
			foundValues = applyPropertyMappings(rs, resultMap, mappedColumnNames, metaObject) || foundValues;
			resultObject = foundValues ? resultObject : null;
			return resultObject;
		}
		return resultObject;
	}

	//
	// Result Columns
	//
	protected boolean applyPropertyMappings(ResultSet rs, ColumnMap resultMap, List<String> mappedColumnNames, MetaObject metaObject) throws SQLException {
		boolean foundValues = false;
		final List<Column> columnList = resultMap.getPropertyColumnList();
		for (Column column : columnList) {
			final String columnName = column.getName();
			if ((columnName != null && mappedColumnNames.contains(columnName.toUpperCase(Locale.ENGLISH))) ||
					column.getNestedCallId()!=null || 
					column.isCompositeResult()) {
				Object value = getPropertyMappingValue(rs, metaObject, column);
				if (value != null) {
					final String property = column.getProperty();					
					if (column.isExtra() && metaObject.hasAttributes()) {
						metaObject.setAttribute(property, value);
					} else if (column.isBoolexp()){
						metaObject.setValue(property, column.applyBoolexp(value));
					} else {
						metaObject.setValue(property, value);						
					}
					foundValues = true;
				}
			}
		}
		return foundValues;
	}

	protected Object getPropertyMappingValue(ResultSet rs, MetaObject metaResultObject, Column propertyColumn) throws SQLException {
		final TypeHandler typeHandler = propertyColumn.getTypeHandler();
		if (propertyColumn.getNestedCallId() != null) {
			return getNestedCallMappingValue(rs, metaResultObject, propertyColumn);
		} else if (typeHandler != null) {
			final String column = propertyColumn.getName();
			return typeHandler.getResult(rs, column);
		}
		return null;
	}

	protected boolean applyAutomaticMappings(ResultSet rs, List<String> unmappedColumnNames, MetaObject metaObject) throws SQLException {
		boolean foundValues = false;
		for (String columnName : unmappedColumnNames) {
			final String property = metaObject.findProperty(columnName);
			if (property != null) {
				final Class propertyType = metaObject.getSetterType(property);
				if (handlerRegistry.hasTypeHandler(propertyType)) {
					final TypeHandler typeHandler = handlerRegistry.getTypeHandler(propertyType);
					final Object value = typeHandler.getResult(rs, columnName);
					if (value != null) {
						metaObject.setValue(property, value);
						foundValues = true;
					}
				}
			}
		}
		return foundValues;
	}

	protected void loadMappedAndUnmappedColumnNames(ResultSet rs, ColumnMap resultMap, List<String> mappedColumnNames, List<String> unmappedColumnNames) throws SQLException {
		mappedColumnNames.clear();
		unmappedColumnNames.clear();
		final ResultSetMetaData rsmd = rs.getMetaData();
		final int columnCount = rsmd.getColumnCount();
		final Set<String> mappedColumns = resultMap.getMappedColumns();
		for (int i = 1; i <= columnCount; i++) {
			final String columnName = system.isUseColumnLabel() ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i);
			final String upperColumnName = columnName.toUpperCase(Locale.ENGLISH);
			if (mappedColumns.contains(upperColumnName)) {
				mappedColumnNames.add(upperColumnName);
				mappedColumnNames.add(columnName);
			} else {
				unmappedColumnNames.add(upperColumnName);
				unmappedColumnNames.add(columnName);
			}
		}
	}
	
	//
	// Instantiation and constructor mapping
	//
	protected Object createResultObject(ResultSet rs, ColumnMap resultMap) throws SQLException {
		final Class resultType = resultMap.getType();
		final List<Column> constructorColumnList = resultMap.getConstructorColumnList();
		if (handlerRegistry.hasTypeHandler(resultType)) {
			return createPrimitiveResultObject(rs, resultMap);
		} else if (constructorColumnList.size() > 0) {
			return createParameterizedResultObject(rs, resultType, constructorColumnList);
		} else {
			return objectFactory.create(resultType);
		}
	}

	protected Object createParameterizedResultObject(ResultSet rs, Class resultType, List<Column> constructorColumnList) throws SQLException {
		boolean foundValues = false;
		final List<Class> parameterTypes = new ArrayList<Class>();
		final List<Object> parameterValues = new ArrayList<Object>();
		for (Column constructorColumn : constructorColumnList) {
			final Class parameterType = constructorColumn.getJavaType();
			final TypeHandler typeHandler = constructorColumn.getTypeHandler();
			final String column = constructorColumn.getName();
			final Object value = typeHandler.getResult(rs, column);
			parameterTypes.add(parameterType);
			parameterValues.add(value);
			foundValues = value != null || foundValues;
		}
		return foundValues ? objectFactory.create(resultType, parameterTypes, parameterValues) : null;
	}

	protected Object createPrimitiveResultObject(ResultSet rs, ColumnMap resultMap) throws SQLException {
		final Class resultType = resultMap.getType();
		final String columnName;
		if (resultMap.getColumnList().size() > 0) {
			final List<Column> resultMappingList = resultMap.getColumnList();
			final Column column = resultMappingList.get(0);
			columnName = column.getName();
		} else {
			final ResultSetMetaData rsmd = rs.getMetaData();
			columnName = system.isUseColumnLabel() ? rsmd.getColumnLabel(1) : rsmd.getColumnName(1);
		}
		final TypeHandler typeHandler = handlerRegistry.getTypeHandler(resultType);
		return typeHandler.getResult(rs, columnName);
	}

	//
	// Nested Call
	//
	protected Object getNestedCallMappingValue(ResultSet rs, MetaObject metaResultObject, Column propertyColumn) throws SQLException {
		final String nestedCallId = propertyColumn.getNestedCallId();
		final String property = propertyColumn.getProperty();
		final Procedure nestedCall = system.getMappedProcedure(nestedCallId);
		final Class nestedCallParameterType = nestedCall.getParameterMap().getType();
		final Object nestedCallParameterObject = prepareParameterForNestedQuery(rs, propertyColumn, nestedCallParameterType);
		Object value = null;
		if (nestedCallParameterObject != null) {
			final ResultLoader resultLoader = new ResultLoader(system, executor, nestedCall, nestedCallParameterObject, propertyColumn.getJavaType());
			value = resultLoader.loadResult();
		}
		return value;
	}

	protected Object prepareParameterForNestedQuery(ResultSet rs, Column column, Class parameterType) throws SQLException {
		if (column.hasCompositeParameter()) {
			return prepareCompositeKeyParameter(rs, column, parameterType);
		} else {
			return prepareSimpleKeyParameter(rs, column, parameterType);
		}
	}

	protected Object prepareSimpleKeyParameter(ResultSet rs, Column column, Class parameterType) throws SQLException {
		final TypeHandler typeHandler;
		if (handlerRegistry.hasTypeHandler(parameterType)) {
			typeHandler = handlerRegistry.getTypeHandler(parameterType);
		} else {
			typeHandler = handlerRegistry.getUnkownTypeHandler();
		}
		return typeHandler.getResult(rs, column.getCallParameter());
	}

	protected Object prepareCompositeKeyParameter(ResultSet rs, Column column, Class parameterType) throws SQLException {
		final Object parameterObject = instantiateParameterObject(parameterType);
		final MetaObject metaObject = system.getMetaObject(parameterObject);
		List<CallParameter> callParameterList = column.getCallParameterMap().getCallParameterList();
		for (CallParameter callParameter : callParameterList) {
			final Class propType = metaObject.getSetterType(callParameter.getProperty());
			final TypeHandler typeHandler = handlerRegistry.getTypeHandler(propType);
			final String columnName=callParameter.getColumn();
			if (columnName==null)
				throw new ExecutorException("CallParameter column name can not be null in a composite key for nested call, column " + column);
			final Object propValue = typeHandler.getResult(rs, columnName);

			if (callParameter.isExtra() && metaObject.hasAttributes()) {
				metaObject.setAttribute(callParameter.getProperty(), propValue);
			} else if (callParameter.isBoolexp()) {
				metaObject.setValue(callParameter.getProperty(), callParameter.applyBoolexp(propValue));
			} else {			
				metaObject.setValue(callParameter.getProperty(), propValue);
			}
		}
		return parameterObject;
	}

	protected Object instantiateParameterObject(Class parameterType) {
		if (parameterType == null) {
			return new HashMap();
		} else {
			return objectFactory.create(parameterType);
		}
	}	

}
