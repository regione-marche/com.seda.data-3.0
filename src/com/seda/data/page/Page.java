/**
 * 
 */
package com.seda.data.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
/**
 * This class represents the base class for all data collection that must be returned to the client.
 * The Page class represents the sub set of row for a result set and all attributes for a simple paging
 * management.
 * @author Seda Lab
 *
 */
public class Page<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<T> rows;
	private PageMetaData metaData;
	private HashMap<String, Object> properties;
	private Class<T> type;
	
	public HashMap<String, Object> getProperties() {
		return properties;
	}
	public Object getProperty(String key) {
		return properties.get(key);
	}
	public Object putProperty(String key, Object value) {
		return properties.put(key, value);
	}
	
//	private void setType() {
//		type=(Class<T>) ((ParameterizedType) getClass()
//                .getGenericSuperclass()).getActualTypeArguments()[0];
//	}
//	
//	public Class<T> getType(){
//		return type;
//	}
	/**
	 * This is a representation of an empty data page 
	 */
	@Deprecated
	public static Page EMPTY_PAGE = new Page();

	public Page<T> getNewEmptyPage() {
		return new Page<T>(new ArrayList<T>());
	}
	
	public Page<T> getInstance() {
		return this;
	}
	
	public Page() {
		this.metaData = new PageMetaData();
		this.rows = new ArrayList<T>();
		this.properties=new HashMap<String, Object>();
//		setType();
	}
	
	public Page (List<T> rows) {
		this.rows = new ArrayList<T>(rows);
		this.metaData = new PageMetaData();
		this.properties=new HashMap<String, Object>();
//		setType();
	}
	
	/**
	 * Return the list containing all rows in this page 
	 * @return <code>List</code> all rows in this page
	 */
	public final List<T> getRows() {
		return this.rows;
	}
	
	/**
	 * Appends the specified row to the end of this page 
	 * @param row row to be appended to this page
	 */
//	protected final void addRow(Object row) {
//		rows.add(row);
//	}
	/**
	 * Inserts the specified row at the specified position in this page. 
	 * Shifts the row currently at that position (if any) and any subsequent rows to the right 
	 * (adds one to their indices).
	 * 
	 * @param index index at which the specified row is to be inserted
	 * @param row row to be inserted
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size()).
	 */
//	protected final void addRow(int index, Object row) {
//		rows.add(index, row);
//	}
	
	/**
	 * Returns the row at the specified position in this page
	 * @param index index of row to return
	 * @return the row at the specified position in this page
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
	 */
	protected final Serializable getRow(int index) { 
		return rows.get(index);
	}
	/**
	 * Returns the number of elements in this page
	 * @return this page size
	 */
	public final int getSize() { return rows.size(); }
	
	/**
	 * 
	 * @see PageMetaData
	 */
	public PageMetaData getPageMetaData() {
		return metaData;
	}
	/**
	 * @see PageMetaData#getFirstRow()
	 */
	public int getFirstRow() {
		return metaData.getFirstRow();
	}
	/**
	 * @see PageMetaData#getLastRow()
	 */	
	public int getLastRow() {
		return metaData.getLastRow();
	}
	/**
	 * @see PageMetaData#getPageNumber()
	 */
	public int getPageNumber() {
		return metaData.getPageNumber();
	}
	/**
	 * @see PageMetaData#getPageSize()
	 */
	public int getPageSize() {
		return metaData.getPageSize();
	}
	/**
	 * @see PageMetaData#getTotalPages()
	 */
	public int getTotalPages() {
		return metaData.getTotalPages();
	}
	/**
	 * @see PageMetaData#getTotalRows()
	 */
	public int getTotalRows() {
		return metaData.getTotalRows();
	}
	/**
	 * @see PageMetaData#setFirstRow(int)
	 */
	public void setFirstRow(int firstRow) {
		metaData.setFirstRow(firstRow);
	}
	/**
	 * @see PageMetaData#setLastRow(int)
	 */
	public void setLastRow(int lastRow) {
		metaData.setLastRow(lastRow);
	}
	/**
	 * @see PageMetaData#setPageNumber(int)
	 */
	public void setPageNumber(int pageNumber) {
		metaData.setPageNumber(pageNumber);
	}
	/**
	 * @see PageMetaData#setPageSize(int)
	 */
	public void setPageSize(int pageSize) {
		metaData.setPageSize(pageSize);
	}
	/**
	 * @see PageMetaData#setTotalPages(int)
	 */
	public void setTotalPages(int totalPages) {
		metaData.setTotalPages(totalPages);
	}
	/**
	 * @see PageMetaData#setTotalRows(int)
	 */
	public void setTotalRows(int totalRows) {
		metaData.setTotalRows(totalRows);
	}
	/**
	 * @see PageMetaData#toJSON()
	 */
	public String metaDataToJSON() {
		return metaData.toJSON();
	}
	/**
	 * @see PageMetaData#toString()
	 */
	public String metaDataToString() {
		return metaData.toString();
	}
	@Override
	public String toString() {
		StringBuffer buf=new StringBuffer();
		buf.append("Page [metaData=" + metaData + ", rows=[ ");
		for (Iterator<T> iterator = rows.iterator(); iterator.hasNext();) {
			buf.append(iterator.next().toString()).append(",");
		}
		buf.deleteCharAt(buf.length()-1);
		buf.append("]]");
		return buf.toString();
	}
	
}
