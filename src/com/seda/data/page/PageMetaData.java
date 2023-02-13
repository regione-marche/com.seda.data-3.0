/**
 * 
 */
package com.seda.data.page;

import java.io.Serializable;

/**
 * This class contains the general information about the page meta data.
 * These are page information regardless the paging management.
 * @author Seda Lab
 *
 */
public class PageMetaData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int firstRow;
	private int lastRow;
	private int totalPages;
	private int totalRows;
	private int pageSize;
	private int pageNumber;
	/**
	 * Returns the number representing the position of this first row in the source result set
	 */
	public int getFirstRow() {return firstRow;}
	/**
	 * Sets the number of this first row page from the source result set
	 */
	public void setFirstRow(int firstRow) {this.firstRow = firstRow;}
	/**
	 * Returns the number representing the position of this last row in the source result set
	 * 
	 */
	public int getLastRow() {return lastRow;}
	/**
	 * Sets the number of this last row page from the source result set 
	 */
	public void setLastRow(int lastRow) {this.lastRow = lastRow;}		
	
	
	/**
	 * Returns the computed number of page in according to the row page size
	 */
	public int getTotalPages() {
		return totalPages;
	}
	/**
	 * Sets the number of pages from the source result set
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	/**
	 * Returns the total number of rows contained in the source result set
	 */
	public int getTotalRows() {
		return totalRows;
	}
	/**
	 * Sets the total number of rows from the source result set
	 */
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	/**
	 * Returns the total number of rows that composes a single page
	 * @return this page size
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * Sets the number of rows that composes a single page
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * Returns the logical page number of this page in the source result set
	 */
	public int getPageNumber() {return pageNumber;}
	/**
	 * Sets the logical number of this page from the source result set
	 */
	public void setPageNumber(int pageNumber) {this.pageNumber = pageNumber;}		
	
	@Override
	public String toString() {
		return "Paging [firstRow=" + firstRow + ", lastRow=" + lastRow
				+ ", pageNumber=" + pageNumber + ", pageSize=" + pageSize
				+ ", totalPages=" + totalPages + ", totalRows=" + totalRows
				+ "]";
	}
	public String toJSON() {
		return "\"paging\" : {" +			
		" \"totalPages\": " + totalPages +
		" ,\"totalRows\": " + totalRows +
		" ,\"pageSize\": " + pageSize +
		" ,\"first\": " + firstRow +
		" ,\"last\": " + lastRow +
		"}";			
	}	
}
