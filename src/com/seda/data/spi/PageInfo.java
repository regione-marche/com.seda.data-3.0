/**
 * 
 */
package com.seda.data.spi;

import java.io.Serializable;

/**
 * PageInfo gives the ability to store the information about result set paging
 * @author Seda Lab
 *
 */
public class PageInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private int firstRow;
	private int lastRow;
	private int rowsPerPage;
	private int numRows;
	private int numPages;
	private int pageNumber;
	
	public int getFirstRow() {
		return firstRow;
	}
	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}
	public int getLastRow() {
		return lastRow;
	}
	public void setLastRow(int lastRow) {
		this.lastRow = lastRow;
	}
	public int getRowsPerPage() {
		return rowsPerPage;
	}
	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}
	public int getNumRows() {
		return numRows;
	}
	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}
	public int getNumPages() {
		return numPages;
	}
	public void setNumPages(int numPages) {
		this.numPages = numPages;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public String toString() {
		return "[PageInfo: " +
				" firstRow=" + firstRow +
				" ,lastRow=" + lastRow +
				" ,rowsPerPage=" + rowsPerPage +
				" ,numRows=" + numRows +
				" ,numPages=" + numPages +
				" ,pageNumber=" + pageNumber +
				"]";
	}
	
	public String toJSON() {
		return "\"pageInfo\" : {" +
		" \"firstRow\": " + firstRow +
		" ,\"lastRow\": " + lastRow +
		" ,\"rowsPerPage\": " + rowsPerPage +
		" ,\"numRows\": " + numRows +
		" ,\"numPages\": " + numPages +
		" ,\"pageNumber\": " + pageNumber +
		"}";
}
	
	
}
