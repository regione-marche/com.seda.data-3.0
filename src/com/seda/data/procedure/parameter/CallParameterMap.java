/**
 * 
 */
package com.seda.data.procedure.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author f.ricci
 *
 */
public class CallParameterMap<J extends String, K extends CallParameter> extends HashMap<J, K>{

	private String name;

	public CallParameterMap(String name, int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		this.name = name;
	}

	public CallParameterMap(String name, int initialCapacity) {
		super(initialCapacity);
		this.name = name;
	}

	public CallParameterMap(String name) {
		super();
		this.name = name;
	}

	public CallParameterMap(String name, Map<? extends J, ? extends K> m) {
		super(m);
		this.name = name;
	}	

	public List<CallParameter> getCallParameterList() {
		List<CallParameter> callParameterList = new ArrayList<CallParameter>(size());
		Enumeration enumKeys = Collections.enumeration(keySet());
		while (enumKeys.hasMoreElements()) {
			String key = (String) enumKeys.nextElement();
			callParameterList.add(get(key));
		}
		return callParameterList;
	}	
	
	public K put(J key, K value) {
		if (containsKey(key)) 
			throw new IllegalArgumentException(name + " already contains value for " + key);
		CallParameter callParameter = (CallParameter)value;
		if (containsName(callParameter.getName())) 
			throw new IllegalArgumentException(name + " already contains a call parameter name for " + key);

		return super.put(key, value);
	}
	
	public boolean containsName(String name) {
		return getByName(name)!=null;
	}
	public boolean containsColumn(String columnName) {
		return getByColumn(columnName)!=null;
	}	
	public boolean containsProperty(String property) {
		return getByProperty(property)!=null;
	}	
	public CallParameter getByName(String name) {
		if (name==null) return null;
		Enumeration enumKeys = Collections.enumeration(keySet());
		while (enumKeys.hasMoreElements()) {
			String key = (String)enumKeys.nextElement();
			CallParameter callParameter = get(key);
			if (name.equals(callParameter.getName())) {
				return callParameter;
			}
			
		}
		return null;
	}
	
	public CallParameter getByProperty(String property) {
		if (property==null) return null;
		Enumeration enumKeys = Collections.enumeration(keySet());
		while (enumKeys.hasMoreElements()) {
			String key = (String)enumKeys.nextElement();
			CallParameter callParameter = get(key);
			if (property.equals(callParameter.getProperty())) {
				return callParameter;
			}
			
		}
		return null;
	}		
	
	public CallParameter getByColumn(String columnName) {
		if (columnName==null) return null;
		Enumeration enumKeys = Collections.enumeration(keySet());
		while (enumKeys.hasMoreElements()) {
			String key = (String)enumKeys.nextElement();
			CallParameter callParameter = get(key);
			if (columnName.equals(callParameter.getColumn())) {
				return callParameter;
			}
			
		}
		return null;
	}	

}
