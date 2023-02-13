/**
 * 
 */
package com.seda.data.procedure.parameter;

import java.util.Collections;
import java.util.List;

import com.seda.data.procedure.SubSystem;

/**
 * @author f.ricci
 *
 */
public class ParameterMap {

	private String id;
	private Class<?> type;
	private List<Parameter> parameterList;

	private ParameterMap() {
	}

	public static class Factory {
		private ParameterMap parameterMap = new ParameterMap();

		public Factory(SubSystem context, String id, Class type, List<Parameter> parameterList) {
			parameterMap.id = id;
			parameterMap.type = type;
			parameterMap.parameterList = parameterList;
		}

		public Class<?> type() {
			return parameterMap.type;
		}

		public ParameterMap getParameterMap() {
			//lock down collections
			parameterMap.parameterList = Collections.unmodifiableList(parameterMap.parameterList);
			return parameterMap;
		}
	}

	public String getId() {
		return id;
	}

	public Class<?> getType() {
		return type;
	}

	public List<Parameter> getParameterList() {
		return parameterList;
	}
	
	public int getSize() {
		return parameterList.size();
	}


}
