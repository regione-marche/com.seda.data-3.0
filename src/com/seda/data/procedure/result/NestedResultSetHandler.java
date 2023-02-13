package com.seda.data.procedure.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.seda.commons.reflection.MetaClass;
import com.seda.commons.reflection.MetaObject;
import com.seda.data.procedure.Procedure;
import com.seda.data.procedure.ProcedureBound;
import com.seda.data.procedure.executor.Executor;
import com.seda.data.procedure.executor.ExecutorException;
import com.seda.data.procedure.parameter.ParameterHandler;
import com.seda.data.type.TypeHandler;

public class NestedResultSetHandler extends ResultSetHandlerImpl {

	private final Map<BreakHandler, Set<BreakHandler>> localRowBreaks;
	private final Map<BreakHandler, Object> globalRowBreakValues;	
	
	public NestedResultSetHandler(Executor executor, Procedure mappedProcedure,
			ParameterHandler parameterHandler, 
			ProcedureBound boundCall, ResultBounds resultBounds) {
		super(executor, mappedProcedure, parameterHandler, boundCall,
				resultBounds);
	    localRowBreaks = new HashMap<BreakHandler, Set<BreakHandler>>();
	    globalRowBreakValues = new HashMap<BreakHandler, Object>();		
		ensureNoRowBounds(resultBounds);		
	}

	private void ensureNoRowBounds(ResultBounds resultBounds) {
		if (resultBounds != null && (resultBounds.getLimit() < ResultBounds.NO_ROW_LIMIT
				|| resultBounds.getOffset() > ResultBounds.NO_ROW_OFFSET)) {
			throw new ExecutorException("Mapped Calls with nested result mappings cannot be safely constrained by ResultBounds.");
		}
	}	

	//
	// Handle result set
	//
	protected void cleanUpAfterHandlingResultSet() {
		super.cleanUpAfterHandlingResultSet();
		globalRowBreakValues.clear();
	}	
	
	
	//
	// Handle rows
	//
	protected void handleRowValues(ResultSet rs, ColumnMap columnMap, ResultHandler resultHandler, ResultBounds resultBounds) throws SQLException {
		final ResultContext resultContext = new ResultContextImpl();
		skipRows(rs, resultBounds);
		while (hasNext(rs, resultContext, resultBounds)) {
			final BreakHandler rowBreak = createRowBreak(columnMap, rs);
			final boolean knownValue = globalRowBreakValues.containsKey(rowBreak);
			Object rowValue =  getRowValue(rs, columnMap, rowBreak);
			if (!knownValue) {
				resultContext.nextObject(rowValue);
				resultHandler.process(resultContext);
			}
		}
	}
	
	//
	// Get value from row
	//
	protected Object getRowValue(ResultSet rs, ColumnMap columnMap, BreakHandler rowBreak) throws SQLException {
		if (globalRowBreakValues.containsKey(rowBreak)) {
			final Object resultObject = globalRowBreakValues.get(rowBreak);
			final MetaObject metaObject = system.getMetaObject(resultObject);
			applyNestedResultMappings(rs, columnMap, metaObject);
			return resultObject;
		} else {
			final List<String> mappedColumnNames = new ArrayList<String>();
			final List<String> unmappedColumnNames = new ArrayList<String>();
			Object resultObject = createResultObject(rs, columnMap);
			if (resultObject != null && !handlerRegistry.hasTypeHandler(columnMap.getType())) {
				final MetaObject metaObject = system.getMetaObject(resultObject);
				loadMappedAndUnmappedColumnNames(rs, columnMap, mappedColumnNames, unmappedColumnNames);
				boolean foundValues = columnMap.getConstructorColumnList().size() > 0;
				foundValues = applyPropertyMappings(rs, columnMap, mappedColumnNames, metaObject) || foundValues;
				foundValues = applyNestedResultMappings(rs, columnMap, metaObject) || foundValues;
				resultObject = foundValues ? resultObject : null;
			}
			if (rowBreak != BreakHandler.NULL_BREAK) {
				globalRowBreakValues.put(rowBreak, resultObject);
			}
			return resultObject;
		}
	}
	
	//
	// Handle nested result map
	//
	private boolean applyNestedResultMappings(ResultSet rs, ColumnMap columnMap, MetaObject metaObject) {
		boolean foundValues = false;
		for (Column column: columnMap.getPropertyColumnList()) {
			final String nestedColumnMapId = column.getNestedColumnMapId();
			if (nestedColumnMapId != null) {
				try {
					final ColumnMap nestedColumnMap = system.getColumnMap(nestedColumnMapId);
					final Object collectionProperty = instantiateCollectionPropertyIfAppropriate(column, metaObject);

					final BreakHandler parentRowBreak = createRowBreak(columnMap, rs);
					final BreakHandler rowBreak = createRowBreak(nestedColumnMap, rs);
					final Set<BreakHandler> localRowBreaks = getRowValueCache(parentRowBreak);
					final boolean knownValue = localRowBreaks.contains(rowBreak);
					localRowBreaks.add(rowBreak);
					Object rowValue = getRowValue(rs, nestedColumnMap, rowBreak);

					if (rowValue != null) {
						if (collectionProperty != null && collectionProperty instanceof Collection) {
							if (!knownValue) {
								((Collection) collectionProperty).add(rowValue);
							}
						} else {
							if (column.isExtra()) {
								metaObject.setAttribute(column.getProperty(), rowValue);
							} else if (column.isBoolexp()){
									metaObject.setValue(column.getProperty(), column.applyBoolexp(rowValue));
							} else {
								metaObject.setValue(column.getProperty(), rowValue);								
							}
						}
						foundValues = true;
					}

				} catch (Exception e) {
					throw new ExecutorException("Error getting nested result map values for '" + column.getProperty() + "'.  Cause: " + e, e);
				}
			}
		}
		return foundValues;
	}

	private Set<BreakHandler> getRowValueCache(BreakHandler rowBreak) {
		Set<BreakHandler> breakSet = localRowBreaks.get(rowBreak);
		if (breakSet == null) {
			breakSet = new HashSet<BreakHandler>();
			localRowBreaks.put(rowBreak, breakSet);
		}
		return breakSet;
	}

	private Object instantiateCollectionPropertyIfAppropriate(Column column, MetaObject metaObject) {
		final String propertyName = column.getProperty();
		Class type = column.getJavaType();
		Object propertyValue=null;
		if (column.isExtra()) {
			if (metaObject.hasAttributes()) {
				propertyValue=metaObject.getAttribute(propertyName);
			} else {
				throw new ExecutorException("Error accessing attributes model for result '" + column.getProperty() + "'");				
			}
		} else {
			propertyValue = metaObject.getValue(propertyName);			
		}
		if (propertyValue == null) {
			if (type == null) {
				if (column.isExtra()) {
					type = metaObject.getAttributeType(propertyName);
				} else {
					type = metaObject.getSetterType(propertyName);					
				}
			}
			try {
				if (Collection.class.isAssignableFrom(type)) {
					propertyValue = objectFactory.create(type);
					if (column.isExtra()) {
						metaObject.setAttribute(propertyName, propertyValue);
					} else {
						metaObject.setValue(propertyName, propertyValue);						
					}
				}
			} catch (Exception e) {
				throw new ExecutorException("Error instantiating collection property for result '" + column.getProperty() + "'.  Cause: " + e, e);
			}
		}
		return propertyValue;
	}

	//
	// Create row breaker for nested column map
	//
	private BreakHandler createRowBreak(ColumnMap columnMap, ResultSet rs) throws SQLException {
		final BreakHandler breakHandler = new BreakHandler();
		List<Column> columnList = getColumnListForRowKey(columnMap);
		breakHandler.add(columnMap.getId());
		if (columnList.size() == 0) {
			if (Map.class.isAssignableFrom(columnMap.getType())) {
				createRowBreakForMap(rs, breakHandler);
			} else {
				createRowBreakForUnmappedProperties(columnMap, rs, breakHandler);
			}
		} else {
			createRowBreakForMappedProperties(rs, breakHandler, columnList);
		}
		if (breakHandler.getBreakCount() < 2) {
			return BreakHandler.NULL_BREAK;
		}
		return breakHandler;
	}

	private List<Column> getColumnListForRowKey(ColumnMap columnMap) {
		List<Column> columnList = columnMap.getKeyColumnList();
		if (columnList.size() == 0) {
			columnList = columnMap.getPropertyColumnList();
		}
		return columnList;
	}

	private void createRowBreakForMappedProperties(ResultSet rs, BreakHandler breakHandler, List<Column> columnList) {
		for (Column column : columnList) {
			if (column.getNestedCallId() == null && column.getNestedColumnMapId() == null) {
				final String columnName = column.getName();
				final TypeHandler typeHandler = column.getTypeHandler();
				if (columnName != null) {
					try {
						final Object value = typeHandler.getResult(rs, columnName);
						if (value != null) {
							breakHandler.add(columnName);
							breakHandler.add(value);
						}
					} catch (Exception e) {
						//ignore
					}
				}
			}
		}
	}

	private void createRowBreakForUnmappedProperties(ColumnMap columnMap, ResultSet rs, BreakHandler breakHandler) throws SQLException {
		final MetaClass metaType = MetaClass.forClass(columnMap.getType());
		final List<String> mappedColumnNames = new ArrayList<String>();
		final List<String> unmappedColumnNames = new ArrayList<String>();
		loadMappedAndUnmappedColumnNames(rs, columnMap, mappedColumnNames, unmappedColumnNames);
		for (String columnName : unmappedColumnNames) {
			if (metaType.findProperty(columnName) != null) {
				String value = rs.getString(columnName);
				if (value != null) {
					breakHandler.add(columnName);
					breakHandler.add(value);
				}
			}
		}
	}

	private void createRowBreakForMap(ResultSet rs, BreakHandler breakHandler) throws SQLException {
		final ResultSetMetaData rsmd = rs.getMetaData();
		final int columnCount = rsmd.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			final String columnName = system.isUseColumnLabel() ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i);
			final String value = rs.getString(columnName);
			if (value != null) {
				breakHandler.add(columnName);
				breakHandler.add(value);
			}
		}
	}	
	
	
}
