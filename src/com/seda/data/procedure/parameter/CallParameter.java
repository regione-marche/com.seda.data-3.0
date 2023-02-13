/**
 * 
 */
package com.seda.data.procedure.parameter;
/**
 * @author f.ricci
 *
 */
public class CallParameter {
	
	private String name;
	private String column;
	private String property;
	private String boolexp;
	private boolean extra;
	
	private CallParameter(){}
	
	public static class Factory {
		private CallParameter callParameter = new CallParameter();
		
		public Factory(String name, String column, String property, String boolexp, boolean extra) {
			callParameter.name=name;
			callParameter.column=column;
			callParameter.property=property;
			callParameter.boolexp=boolexp;
			callParameter.extra=extra;
		}
		
		public CallParameter getCallParameter() {
			return callParameter;
		}
	}

	public String getName() {
		return name;
	}

	public String getColumn() {
		return column;
	}

	public String getProperty() {
		return property;
	}
	
	public boolean isExtra() {
		return extra;
	}	
	
	public boolean isBoolexp() {
		return boolexp!=null;
	}
	
	public String getBoolexp() {
		return boolexp;
	}

	public Boolean applyBoolexp(Object value) {
		if (isBoolexp() && boolexp.equals(value)) return true;
		return false;
	}	
	
}
