/**
 * 
 */
package com.seda.data.helper;

/**
 * The SpParamMetaData is used by {@link SpParamCache} to hold the stored procedure's parameter description
 * 
 * @author Seda Lab
 *
 */
public class SpParamMetaData {

	private String parameterSet;
	private int parameterCount;
	private boolean returnValue;
	/**
	 * Returns buffered callable statement string that will be prepared
	 * 
	 * @return <code>String</code> The callable string
	 */
	public String getParameterSet() {
		return parameterSet;
	}
	/**
	 * Stores callable statement string that will be prepared
	 * 
	 * @param parameterSet the callable string
	 */
	public void setParameterSet(String parameterSet) {
		this.parameterSet = parameterSet;
	}
	/**
	 * Returns the number of parameters defined for the callable statement
	 * 
	 * @return <code>int</code> the number of parameters
	 */
	public int getParameterCount() {
		return parameterCount;
	}
	/**
	 * Stores the number of parameters defined for the callable statement
	 * 
	 * @param parameterCount the number of parameters
	 */
	public void setParameterCount(int parameterCount) {
		this.parameterCount = parameterCount;
	}
	/**
	 * Returns <code>true</code> if the callable statement includes a return value
	 * @return <code>boolean</code> 
	 */
	public boolean hasReturnValue() {
		return returnValue;
	}
	/**
	 * Stores the boolean value in according of the existence of a return value for the callable statement
	 * @param returnValue 
	 */
	public void setReturnValue(boolean returnValue) {
		this.returnValue = returnValue;
	}
	/**
	 * Unique constructor of SpParamMetaData object
	 * @param parameterSet the string of the callable statement
	 * @param parameterCount the number of parameter used in the statement
	 * @param returnValue the boolean value in according of the existence of a return value
	 */
	public SpParamMetaData(String parameterSet, int parameterCount,
			boolean returnValue) {
		this.parameterSet = parameterSet;
		this.parameterCount = parameterCount;
		this.returnValue = returnValue;
	}
	
	public SpParamMetaData clone() {
		return new SpParamMetaData(getParameterSet(),getParameterCount(), hasReturnValue());		
	}
}
