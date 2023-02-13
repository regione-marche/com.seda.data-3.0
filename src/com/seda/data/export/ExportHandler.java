/**
 * 
 */
package com.seda.data.export;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.seda.commons.string.Pad;
import com.seda.data.type.HandlerRegistry;
import com.seda.data.type.ResolverTypes;
import com.seda.data.type.TypeFormatter;
import com.seda.data.type.TypeHandler;
/**
 * @author f.ricci
 *
 */
public abstract class ExportHandler implements Export {

	private char cdel = ' ';
	private char sdel = ' ';
	private boolean header=false;
	private boolean headerSep=false;
	private boolean isTabular=false;
	
	private ResultSetMetaData metaData;
	private ResultSet resultSet;
	private int columnCount;
	private int columnCountBound;
	
	private ArrayList<TypeHandler> typeHandlers;
	private int[] tabularSizes;
	
	public void setTabular(boolean tabular) {this.isTabular=tabular;}
	public void setCharacterDelimiter(char cdel) {this.cdel=cdel;}	
	public void setStringDelimiter(char sdel) {this.sdel=sdel;}	
	public void setHeader(boolean header) {this.header = header;}
	public void setHeaderSeparator(boolean headerSep) {this.headerSep = headerSep;}
	
	public final void export(PreparedStatement statement) {
		try {
			do {
				export(statement.getResultSet());
			} while (statement.getMoreResults());			
		} catch (SQLException e) {
			throw new ExportException(e.getMessage(), e);
		}

	}
	
	public final void export(ResultSet resultSet) {
		this.resultSet=resultSet;
		String[] headerRows=doOpen();
		if (headerRows!=null) {
			for (int i = 0; i < headerRows.length; i++) {
				printRow(headerRows[i]);
			}			
		}
		doLoop();
	}
	
	public String[] open(ResultSet resultSet) {
		this.resultSet=resultSet;
		return doOpen();
	}
	
	public String[] open(ResultSet resultSet, boolean header) {
		this.header=header;
		return open(resultSet);
	}
	
	public String[] open(ResultSet resultSet, boolean header, boolean headerSep) {
		this.headerSep=headerSep;
		return open(resultSet, header);
	}		

	public String fetch() {
		try {
			return doFetch(resultSet.next());
		} catch (SQLException e) {
			throw new ExportException(e.getMessage(), e);
		}
	}

	public String fetch(boolean hasNext) {
		return doFetch(hasNext);
	}		

	private String[] doOpen() {
		try {
			String[] headerRows=null;
			metaData = resultSet.getMetaData();
			columnCount = metaData.getColumnCount();
			columnCountBound=columnCount+1;
			if (isTabular)
				tabularSizes = TypeFormatter.getTabularLength(metaData, header);
			if (header) {
				headerRows=printHeadColumns();
			}
			typeHandlers = discoveryTypeHandler();
			return headerRows;
		} catch (SQLException e) {
			throw new ExportException(e.getMessage(), e);
		}
	}
	
	private String doFetch(boolean hasNext) {
		try {
			if (hasNext) {
				StringBuffer rowBuffer = new StringBuffer();
				for (int i = 1; i < columnCountBound; i++) {
					String columnData=null;
					if (isTabular)
						columnData = typeHandlers.get(i).getResult(resultSet, metaData.getColumnName(i), tabularSizes[i]);
					else 
						columnData = resultSet.getString(i);
					rowBuffer.append(columnData);
					if (i!=columnCount) rowBuffer.append(cdel);
				}
				return rowBuffer.toString();
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new ExportException(e.getMessage(), e);
		}
	}	
	
	private void doLoop() {
		try {		
			String row=null;
			while ((row = doFetch(resultSet.next()))!=null) {
				printRow(row);
			}
		} catch (SQLException e) {
			throw new ExportException(e.getMessage(), e);
		}		
	}

	private String[] printHeadColumns() throws SQLException {
    	String[] headerRows = new String[2];		
		StringBuffer rowBuffer = new StringBuffer();
    	for (int i = 1; i < columnCountBound; i++) {
    		if (isTabular) 
    			rowBuffer.append(TypeFormatter.format(metaData.getColumnName(i),tabularSizes[i]));
    		else 
    			rowBuffer.append(metaData.getColumnName(i));
    		if (i!=columnCount) rowBuffer.append(cdel);
		}
    	headerRows[0] = rowBuffer.toString();
    	if (headerSep && isTabular) {
        	rowBuffer = new StringBuffer();
    		for (int i = 1; i < columnCountBound; i++) {
    			rowBuffer.append(Pad.literal("-", tabularSizes[i]));
        		if (i!=columnCount) rowBuffer.append(cdel);
    		}		    	
    		headerRows[1] = rowBuffer.toString();    		
    	}
    	if (headerRows[1]==null) return new String[]{headerRows[0]};
    	return headerRows;
	}

	private ArrayList<TypeHandler> discoveryTypeHandler() throws SQLException {
		ResolverTypes resolverTypes = new ResolverTypes(new HandlerRegistry());
		ArrayList<TypeHandler> typeHandlerList = new ArrayList<TypeHandler>(metaData.getColumnCount()+1) ;
		typeHandlerList.add(null);
		for (int i = 1; i < columnCountBound; i++) {
			typeHandlerList.add(resolverTypes.resolveTypeHandler(metaData, i));
		}
		return typeHandlerList;
	}

	protected abstract void printRow(String row);
	
}
