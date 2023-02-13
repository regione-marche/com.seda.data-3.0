/**
 * 
 */
package com.seda.data.type;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.seda.commons.string.Pad;
/**
 * Gives the ability to get a string representation of data column
 * @author f.ricci
 *
 */
public class TypeFormatter {

	/**
	 * @return an array of integer for a tabular data representation for a specified result set metadata.  
	 */
	public static int[] getTabularLength(ResultSetMetaData metaData, boolean compareNames) {
		// define the tabular column length array; 
		//+ 1 because the column base index is 1
		// so will ignore the zero base index
		int[] tabSize;
		try {
			tabSize = new int[metaData.getColumnCount() + 1];
			// loop through the result set metadata
			// column base index start from 1
		   	for (int i = 1, j=metaData.getColumnCount()+1; i < j; i++) {
				switch (metaData.getColumnType(i)) {
				case java.sql.Types.FLOAT:
				case java.sql.Types.DOUBLE:
				case java.sql.Types.DECIMAL:
					// precision +1 for the decimal dot
					if (compareNames)
						tabSize[i] = Math.max(metaData.getColumnName(i).length(), metaData.getPrecision(i)+1);					
					else
						tabSize[i] = metaData.getPrecision(i)+1;
					break;
				default:
					if (compareNames)
						tabSize[i] = Math.max(metaData.getColumnName(i).length(), metaData.getColumnDisplaySize(i));					
					else
						tabSize[i] = metaData.getColumnDisplaySize(i);
				}
			}			
		} catch (SQLException x) {
			throw new TypeException(x.getMessage(), x);
		}

	   	return tabSize;
	}
	
	/**
	 * Cuts or appends the required bytes
	 * 
	 * @param strData		The String to be processed
	 * @param finalLen		The length to be set for the output String 
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(String strData, int finalLen) {
		return Pad.right(strData==null?"":strData, finalLen);
	} // format(String, int)
	/**
	 * Returns a String representation of the int data
	 * 
	 * @param intData		The int data to be processed
	 * @param finalLen		The length to be set for the output String 
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(int intData, int finalLen) {
		return Pad.left(String.valueOf(intData), finalLen);
	}  // format(int, int)
	/**
	 * 
	 * Returns a String representation of the Integer data 
	 * 
	 * @param integerData	The Integer data to be processed
	 * @param finalLen		The length to be set for the output String
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(Integer integerData, int finalLen) {
		if (integerData==null) return format("",finalLen);
	    return format(integerData.intValue(), finalLen);		
	} // format(Integer, int)
	/**
	 * 
	 * Returns a String representation of the double data
	 * 
	 * @param doubData		The double data to be processed
	 * @param precision		The precision digits
	 * @param scale			The scale digits
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(double doubData, int precision, int scale) {
	    return format(doubData,precision, scale, precision+1);
	} // format(double, int, int)
	/**
	 * 
	 * Returns a String representation of the double data
	 * 
	 * @param doubData		The double data to be processed
	 * @param precision		The precision digits
	 * @param scale			The scale digits
	 * @param finalLen		The final length of the string 
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(double doubData, int precision, int scale, int finalLen) {
	    BigDecimal decData = new BigDecimal(doubData);
	    decData = decData.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
	    return format(decData, precision, scale, finalLen);
//	    if (finalLen < precision + 1) finalLen = precision + 1;	    
//	    return Pad.left(decData.toString(), finalLen);
	} // format(double, int, int, int)	
	/**
	 * 
	 * Returns a String representation of the float data
	 * 
	 * @param floatData		The float data to be processed
	 * @param precision		The precision digits
	 * @param scale			The scale digits
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(float floatData, int precision, int scale) {
	    return format(floatData, precision, scale, precision + 1);
	} // format(float, int, int)
	/**
	 * 
	 * Returns a String representation of the float data
	 * 
	 * @param floatData		The float data to be processed
	 * @param precision		The precision digits
	 * @param scale			The scale digits
	 * @param finalLen		The final length of the string 
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(float floatData, int precision, int scale, int finalLen) {
	    BigDecimal decData = new BigDecimal(floatData);
	    decData = decData.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
	    return format(decData, precision, scale, finalLen);
//	    if (finalLen < precision + 1) finalLen = precision + 1;
//	    return Pad.left(decData.toString(), finalLen);
	} // format(float, int, int, int)		
	/**
	 * 
	 *  Returns a String representation of the BigDecimal data
	 * 
	 * @param decData		The BigDecimal data to be processed
	 * @param precision		The precision digits 
	 * @param scale			The scale digits
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(BigDecimal decData, int precision, int scale) {
		return format(decData, precision, scale, precision + 1);
	} // format(BigDecimal, int, int)
	/**
	 * 
	 *  Returns a String representation of the BigDecimal data
	 * 
	 * @param decData		The BigDecimal data to be processed
	 * @param precision		The precision digits 
	 * @param scale			The scale digits
	 * @param finalLen		The final length of the string 
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(BigDecimal decData, int precision, int scale, int finalLen) {
		if (decData==null) return format("",finalLen);
	    decData = decData.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
	    if (finalLen < precision + 1) finalLen = precision + 1;
	    DecimalFormat df = new DecimalFormat();
	    df.setGroupingUsed(false);
	    df.setMinimumFractionDigits(scale);
	    df.setMaximumFractionDigits(scale);
	    return Pad.left(df.format(decData), finalLen);
//	    return Pad.left(decData.toString(), finalLen);		    
	} // format(BigDecimal, int, int, int)	
	/**
	 * 
	 * Returns a String representation of the Double data
	 * 
	 * @param doubleData		The Double data to be processed
	 * @param precision			The precision digits
	 * @param scale				The scale digits
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(Double doubleData, int precision, int scale) {
		if (doubleData==null) return format("",precision+1);
	    return (format(doubleData.doubleValue(), precision, scale));		
	} // format(Double, int, int) 
	/**
	 * 
	 * Returns a String representation of the Float data
	 * 
	 * @param floatData		The Float data to be processed
	 * @param precision			The precision digits
	 * @param scale				The scale digits
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(Float floatData, int precision, int scale) {
		if (floatData==null) return format("",precision+1);
	    return (format(floatData.floatValue(), precision, scale));		
	} // format(Float, int, int) 	
	/**
	 * 
	 * Returns a String representation of the Long data
	 * 
	 * @param longData		The long data to be processed
	 * @param finalLen		The length to be set for the output String
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(long longData, int finalLen) {
	    return Pad.left(String.valueOf(longData), finalLen);
	} // format(long, int) 
	/**
	 * 
	 * Returns a String representation of the Long data
	 * 
	 * @param longData		    The Long data to be processed
	 * @param finalLen			The length to be set for the output String
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */
	public static String format(Long longData, int finalLen) {
		if (longData==null) return format("", finalLen);
	    return (format(longData.longValue(), finalLen));		
	} // format(Long, int) 	
	/**
	 * 
	 * Returns a String representation of the Sql Type Date
	 * 
	 * @param sqlDate		The sql data to be processed
	 * @param finalLen		The length to be set for the output String
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */	
	public static String format(Date sqlDate, int finalLen) {
	    return format(sqlDate==null?"":sqlDate.toString(),finalLen);
	} // format(sql.Date, int)
	/**
	 * 
	 * Returns a String representation of the Sql Type Time
	 * 
	 * @param sqlTime		The sql time to be processed
	 * @param finalLen		The length to be set for the output String
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */		
	public static String format(Time sqlTime, int finalLen) {
	    return format(sqlTime==null?"":sqlTime.toString(),finalLen);
	} // format(sql.Time, int)
	/**
	 * 
	 * Returns a String representation of the Sql Type Timestamp
	 * 
	 * @param sqlTimestamp	The sql timestamp to be processed
	 * @param finalLen		The length to be set for the output String
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */		
	public static String format(Timestamp sqlTimestamp, int finalLen) {
	    return format(sqlTimestamp==null?"":Pad.right(sqlTimestamp.toString(), 26, '0'),finalLen);
	} // format(sql.Timestamp, int)
	
	/**
	 * 
	 * Returns a String rappresentation of the boolean data
	 * 
	 * @param b				The boolean to be processed
	 * @param finalLen		The length to be set for the output String
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */		
	public static String format(boolean b, int finalLen) {
	    return format(Boolean.toString(b),finalLen);
	} // format(boolean, int)		
	/**
	 * 
	 * Returns a String rappresentation of the Boolean data
	 * 
	 * @param b				The Boolean object to be processed
	 * @param finalLen		The length to be set for the output String
	 * @return <code>String</code> opportunely formatted
	 * @throws Exception
	 */		
	public static String format(Boolean b, int finalLen) {
		if (b==null) return format("",finalLen);
	    return format(b.toString(),finalLen);
	} // format(boolean, int)	
	
}
