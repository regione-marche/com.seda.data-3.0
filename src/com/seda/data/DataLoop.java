/**
 * 
 */
package com.seda.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.seda.commons.string.Pad;
/**
 * @author dbadm
 *
 */
public class DataLoop {

	// This is the array containing the tabular column size 
	// computed from metadata of the last result set
	private int[] columnSize; 
	
	public final static int LOOP_SYSOUT = 1;
	public final static int LOOP_LIST = 3;

	private int loop = LOOP_SYSOUT; // default to standard output
	private List<String> dataList;
	
	public int getLoop() {
		return loop;
	}
	public void setLoop(int loop) {
		this.loop = loop;
	}

	public List<String> getDataList() {
		if (dataList==null) dataList=new ArrayList<String>();
		return dataList;
	}
	
	// methods to print to standard out
	public void loopToGrid(PreparedStatement prepared, boolean printHeader) throws SQLException {
		// loop through result set and print catalog statistics of the executed job
        loopToGrid(prepared, ' ', printHeader);
	}
	
	public void loopToGrid(PreparedStatement prepared, char coldel, boolean printHeader) throws SQLException {
		do {
			ResultSet rs = prepared.getResultSet();
			// loop through result set and print catalog statistics of the executed job
	        loopToGrid(rs, coldel, printHeader);
		} while (prepared.getMoreResults());
		// bisogna aggiunegre il loop anche degli update counts, vedere mokabyte

	}
	
	public void loopToGrid(ResultSet resultSet, boolean printHeader) throws SQLException {
        loopToGrid(resultSet, ' ', printHeader);		
	}
	
	public void loopToGrid(ResultSet resultSet, char coldel, boolean printHeader) throws SQLException {
		// check for a not valid result set
		//*if (resultSet==null) return;
		// get metadata from result set to prepare the grid
        ResultSetMetaData metaData = resultSet.getMetaData();
        // calculate tabSize array information
        buildTabSize(metaData, printHeader);
        // Print the header if necessary
        if (printHeader) {
        	printHeader(metaData,coldel);
        	printHeaderSeparator(metaData, '-');        	
        }
        // loop through the result set
        while (resultSet.next()) {
        	StringBuffer rowBuffer = new StringBuffer();
    		// column base index start from 1
        	for (int i = 1, j=metaData.getColumnCount()+1; i < j; i++) {
        		// choose the appropriate length between column size and header size
				switch (metaData.getColumnType(i)) {
				case Types.CHAR:
				case Types.VARCHAR:
					rowBuffer.append(DataFormat.format(resultSet.getString(i), columnSize[i]));
					break;
				case Types.INTEGER :
					rowBuffer.append(DataFormat.format(resultSet.getInt(i), columnSize[i]));
					break;					
				case Types.BIGINT :
					rowBuffer.append(DataFormat.format(resultSet.getLong(i), columnSize[i]));
					break;			
				case Types.FLOAT:
					rowBuffer.append(DataFormat.format(resultSet.getFloat(i), metaData.getPrecision(i), metaData.getScale(i), columnSize[i]));
					break;								
				case Types.DOUBLE:
					rowBuffer.append(DataFormat.format(resultSet.getDouble(i), metaData.getPrecision(i), metaData.getScale(i), columnSize[i]));
					break;					
				case Types.DECIMAL:
					rowBuffer.append(DataFormat.format(resultSet.getBigDecimal(i), metaData.getPrecision(i), metaData.getScale(i), columnSize[i]));
					break;					
				case Types.DATE:
					rowBuffer.append(DataFormat.format(resultSet.getDate(i), columnSize[i]));
					break;					
				case Types.TIMESTAMP:
					rowBuffer.append(DataFormat.format(resultSet.getTimestamp(i), columnSize[i]));
					break;					
				case Types.TIME:
					rowBuffer.append(DataFormat.format(resultSet.getTime(i), columnSize[i]));
					break;					
				default:
					break;
				}
				// append column delimiter
				rowBuffer.append(coldel);
			}
        	print(rowBuffer.toString());
//        	System.out.println(rowBuffer.toString()); //write line to standard out
        }
        metaData = null;
	}

	private void buildTabSize(ResultSetMetaData metaData, boolean header) throws SQLException {
		// define the tabular column length array; 
		//+ 1 because the column base index is 1
		// so will ignore the zero base index
		columnSize = new int[metaData.getColumnCount() + 1];
		// loop through the result set metadata
		// column base index start from 1
	   	for (int i = 1, j=metaData.getColumnCount()+1; i < j; i++) {
			switch (metaData.getColumnType(i)) {
			case Types.FLOAT:
			case Types.DOUBLE:
			case Types.DECIMAL:
				// precision +1 for the decimal dot
				if (header)
					columnSize[i] = Math.max(metaData.getColumnName(i).length(), metaData.getPrecision(i)+1);					
				else
					columnSize[i] = metaData.getPrecision(i)+1;
				break;
			default:
				if (header)
					columnSize[i] = Math.max(metaData.getColumnName(i).length(), metaData.getColumnDisplaySize(i));					
				else
					columnSize[i] = metaData.getColumnDisplaySize(i);
			}
		}
	}	
	private void printHeader(ResultSetMetaData metaData, char coldel) throws SQLException {
		// define the row buffer
        StringBuffer headBuffer = new StringBuffer();		
    	// column header
		// column base index start from 1
    	for (int i = 1, j=metaData.getColumnCount()+1; i < j; i++) {
			headBuffer.append(DataFormat.format(metaData.getColumnName(i),columnSize[i])).append(coldel);
		}
    	print(headBuffer.toString());
//    	System.out.println(headBuffer.toString());
    	headBuffer=null;
	}
	
	private void printHeaderSeparator(ResultSetMetaData metaData, char c) throws SQLException {
		// define the row buffer		
        StringBuffer sepBuffer = new StringBuffer();		
	   	// row header separator        	
		// column base index start from 1
    	for (int i = 1, j=metaData.getColumnCount()+1; i < j; i++) {
			sepBuffer.append(Pad.literal(String.valueOf(c),columnSize[i])).append(' ');
		}
    	print(sepBuffer.toString());
//    	System.out.println(sepBuffer.toString());
    	sepBuffer=null;
	}		
	
	private void print(String row) {
		switch (loop) {
		case 3:
			getDataList().add(row);
			break;
		default:
			System.out.println(row);
			break;
		}
	}
}
