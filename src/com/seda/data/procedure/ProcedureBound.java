/**
 * 
 */
package com.seda.data.procedure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seda.commons.reflection.MetaObject;
import com.seda.data.procedure.parameter.Parameter;
import com.seda.data.procedure.reflection.MetaProcedure;
/**
 * @author f.ricci
 *
 */
public class ProcedureBound {

	private String hashKey;
	private String sql;
	private List<Parameter> parameterList;
	private Object parameter;
	private Map<String, Object> additionalParameters;
	private MetaObject metaParameters;

	public ProcedureBound(SubSystem system, MetaProcedure metaProcedure, List<Parameter> parameterList, Object parameterObject) {
		this.hashKey = metaProcedure.getHashKey();
		this.sql = metaProcedure.getSQLCall();
		this.parameterList = parameterList;
		this.parameter = parameterObject;
		this.additionalParameters = new HashMap<String, Object>();
		this.metaParameters = system.getMetaObject(additionalParameters);
	}

	public String getHashKey() {
		return hashKey;
	}	
	
	public String getSql() {
		return sql;
	}

	public List<Parameter> getParameterList() {
		return parameterList;
	}

	public Object getParameter() {
		return parameter;
	}

	public boolean hasAdditionalParameter(String name) {
		return metaParameters.hasGetter(name);
	}

	public void setAdditionalParameter(String name, Object value) {
		metaParameters.setValue(name, value);
	}

	public Object getAdditionalParameter(String name) {
		return metaParameters.getValue(name);
	}	

}
