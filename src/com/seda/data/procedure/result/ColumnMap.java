/**
 * 
 */
package com.seda.data.procedure.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.seda.data.procedure.SubSystem;
/**
 * @author f.ricci
 *
 */
public class ColumnMap {

	private String id;
	private Class<?> type;
	private List<Column> columnList;
	private List<Column> keyColumnList;
	private List<Column> constructorColumnList;
	private List<Column> propertyColumnList;
	private Set<String> mappedColumns;
	private boolean hasNestedResultMaps;

	public String getId() {
		return id;
	}

	public boolean hasNestedResultMaps() {
		return hasNestedResultMaps;
	}

	public Class<?> getType() {
		return type;
	}


	public List<Column> getColumnList() {
		return columnList;
	}

	public List<Column> getConstructorColumnList() {
		return constructorColumnList;
	}

	public List<Column> getPropertyColumnList() {
		return propertyColumnList;
	}

	public List<Column> getKeyColumnList() {
		return keyColumnList;
	}

	public Set<String> getMappedColumns() {
		return mappedColumns;
	}

	public void forceNestedResultMaps() {
		hasNestedResultMaps = true;
	}	
	
	private ColumnMap() {
	}

	public static class Factory {
		private ColumnMap columnMap = new ColumnMap();

		public Factory(SubSystem context, String id, Class<?> type, List<Column> columnList) {
			columnMap.id = id;
			columnMap.type = type;
			columnMap.columnList = columnList;
		}

		public Class<?> type() {
			return columnMap.type;
		}

		public ColumnMap getColumnMap() {
			columnMap.mappedColumns = new HashSet<String>();
			columnMap.keyColumnList = new ArrayList<Column>();
			columnMap.constructorColumnList = new ArrayList<Column>();
			columnMap.propertyColumnList = new ArrayList<Column>();
			for (Column column : columnMap.columnList) {
				columnMap.hasNestedResultMaps = columnMap.hasNestedResultMaps || column.getNestedColumnMapId() != null;
				final String columnName = column.getName();
				if (columnName != null) {
					columnMap.mappedColumns.add(columnName.toUpperCase(Locale.ENGLISH));
				} else if (column.isCompositeResult()) {
					List<Column> composites = column.getComposites();
					for (Column compositeResultMapping: composites) {
						final String compositeColumn = compositeResultMapping.getName();
						if (compositeColumn != null) {
							columnMap.mappedColumns.add(compositeColumn.toUpperCase(Locale.ENGLISH));
						}
					}
				}

				if (column.getLabels().contains(ColumnLabel.CONSTRUCTOR)) {
					columnMap.constructorColumnList.add(column);
				} else {
					columnMap.propertyColumnList.add(column);
				}
				if (column.getLabels().contains(ColumnLabel.KEY)) {
					columnMap.keyColumnList.add(column);
				}				
			}
			if (columnMap.keyColumnList.isEmpty()) {
				columnMap.keyColumnList.addAll(columnMap.columnList);
			}
			// lock all collections
			columnMap.columnList = Collections.unmodifiableList(columnMap.columnList);
			columnMap.keyColumnList = Collections.unmodifiableList(columnMap.keyColumnList);
			columnMap.constructorColumnList = Collections.unmodifiableList(columnMap.constructorColumnList);
			columnMap.propertyColumnList = Collections.unmodifiableList(columnMap.propertyColumnList);
			columnMap.mappedColumns = Collections.unmodifiableSet(columnMap.mappedColumns);
			return columnMap;
		}
	}

	@Override
	public String toString() {
		return "ColumnMap [id=" + id + ", type=" + type + ", columnList="
				+ columnList + ", constructorColumnList="
				+ constructorColumnList + ", hasNestedResultMaps="
				+ hasNestedResultMaps + ", keyColumnList=" + keyColumnList
				+ ", mappedColumns=" + mappedColumns + ", propertyColumnList="
				+ propertyColumnList + "]";
	}

}
