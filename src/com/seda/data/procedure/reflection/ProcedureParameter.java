/**
 * 
 */
package com.seda.data.procedure.reflection;

/**
 * 
 * @author f.ricci
 *
 */
public class ProcedureParameter {

	private String procedureCatalog;
	private String procedureSchema;
	private String procedureName;
	private String columnName;
	private Short columnType;
	private int dataType;
	private String typeName;
	private int precision;
	private int length;
	private short scale;
	private short radix;
	private short nullable;
	private String remarks;
	private int index;
	
	public String getProcedureCatalog() {
		return procedureCatalog;
	}
	public void setProcedureCatalog(String procedureCatalog) {
		this.procedureCatalog = procedureCatalog;
	}
	/**
	 * @return procedure schema (may be null) 
	 */
	public String getProcedureSchema() {
		return procedureSchema;
	}
	public void setProcedureSchema(String procedureSchema) {
		this.procedureSchema = procedureSchema;
	}
	/**
	 * 
	 * @return procedure name 
	 */
	public String getProcedureName() {
		return procedureName;
	}
	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}
	/**
	 * 
	 * @return column/parameter name 
	 */
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	/**
	 * procedureColumnUnknown - nobody knows <br/>
     * procedureColumnIn - IN parameter <br/>
     * procedureColumnInOut - INOUT parameter <br/> 
     * procedureColumnOut - OUT parameter <br/> 
     * procedureColumnReturn - procedure return value <br/> 
     * procedureColumnResult - result column in ResultSet <br/> 
	 * @return kind of column/parameter
	 */
	public Short getColumnType() {
		return columnType;
	}
	public void setColumnType(Short columnType) {
		this.columnType = columnType;
	}
	/**
	 *  
	 * @return SQL type from java.sql.Types
	 */
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	/**
	 * 
	 * @return SQL type name, for a UDT type the type name is fully qualified 
	 */
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	/**
	 * 
	 * @return precision 
	 */
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	/**
	 * 
	 * @return length in bytes of data 
	 */
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	/**
	 * 
	 * @return scale 
	 */
	public short getScale() {
		return scale;
	}
	public void setScale(short scale) {
		this.scale = scale;
	}
	/**
	 * 
	 * @return radix 
	 */
	public short getRadix() {
		return radix;
	}
	public void setRadix(short radix) {
		this.radix = radix;
	}
	/**
	 *  procedureNoNulls - does not allow NULL values<br/> 
     * 	procedureNullable - allows NULL values<br/> 
     *  procedureNullableUnknown - nullability unknown
	 * @return can it contain NULL.
	 */
	public short getNullable() {
		return nullable;
	}
	public void setNullable(short nullable) {
		this.nullable = nullable;
	}
	/**
	 * 
	 * @return comment describing parameter/column 
	 */
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	
	/**
	 * 
	 * @return the index position of the parameter/column
	 */
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return "ProcedureParameter [index=" + index + ", columnName="
				+ columnName + ", procedureCatalog=" + procedureCatalog
				+ ", procedureSchema=" + procedureSchema + ", procedureName="
				+ procedureName + ", columnType=" + columnType + ", dataType="
				+ dataType + ", typeName=" + typeName + ", precision="
				+ precision + ", length=" + length + ", scale=" + scale
				+ ", radix=" + radix + ", nullable=" + nullable + ", remarks="
				+ remarks + "]";
	}
	
//	PROCEDURE_CAT String => procedure catalog (may be null) 
//	PROCEDURE_SCHEM String => procedure schema (may be null) 
//	PROCEDURE_NAME String => procedure name 
//	COLUMN_NAME String => column/parameter name 
//	COLUMN_TYPE Short => kind of column/parameter: 
//	procedureColumnUnknown - nobody knows 
//	procedureColumnIn - IN parameter 
//	procedureColumnInOut - INOUT parameter 
//	procedureColumnOut - OUT parameter 
//	procedureColumnReturn - procedure return value 
//	procedureColumnResult - result column in ResultSet 
//	DATA_TYPE int => SQL type from java.sql.Types 
//	TYPE_NAME String => SQL type name, for a UDT type the type name is fully qualified 
//	PRECISION int => precision 
//	LENGTH int => length in bytes of data 
//	SCALE short => scale 
//	RADIX short => radix 
//	NULLABLE short => can it contain NULL. 
//	procedureNoNulls - does not allow NULL values 
//	procedureNullable - allows NULL values 
//	procedureNullableUnknown - nullability unknown 
//	REMARKS String => comment describing parameter/column 

}
