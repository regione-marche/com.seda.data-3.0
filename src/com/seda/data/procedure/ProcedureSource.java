/**
 * 
 */
package com.seda.data.procedure;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.seda.commons.reflection.MetaClass;
import com.seda.data.procedure.parameter.CallParameter;
import com.seda.data.procedure.parameter.CallParameterMap;
import com.seda.data.procedure.parameter.Parameter;
import com.seda.data.procedure.parameter.ParameterDirection;
import com.seda.data.procedure.reflection.MetaProcedure;
import com.seda.data.procedure.reflection.ProcedureParameter;
import com.seda.data.procedure.xml.BaseParser;
import com.seda.data.type.JdbcType;

/**
 * @author f.ricci
 *
 */
public class ProcedureSource extends BaseParser {
	
	CallParameterMap<String, CallParameter> callParameterMap;
	
	public ProcedureSource(SubSystem system) {
		super(system);
	}

	public ProcedureSource(SubSystem system, CallParameterMap callParameterMap) {
		super(system);
		if (callParameterMap==null) this.callParameterMap = new CallParameterMap("");
		else this.callParameterMap=callParameterMap;
	}	
	
	public ProcedureBound bind(MetaProcedure metaProcedure, Object parameterObject) {
		List<Parameter> parameterList = new ArrayList<Parameter>(metaProcedure.getParameterSize());
		Class parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
		for (ProcedureParameter parameter : metaProcedure.getParameterList()) {
			parameterList.add(buildParameter(parameter,parameterType));
		}
		return new ProcedureBound(system, metaProcedure, parameterList, parameterObject);
	}

	private Parameter buildParameter(ProcedureParameter parameter, Class parameterType) {
	    MetaClass metaClass = MetaClass.forClass(parameterType);
	    CallParameter callParameter = resolveCallParameter(parameter.getColumnName());
	    if (callParameter==null)
	    	throw new RuntimeSystemException("Parameter " + parameter.getColumnName() + " doesn't resolve a valid property in " + parameterType.getName() + " caused by a missing or incorrect call parameter");
	    String propertyName = callParameter.getProperty();
//	    String propertyName = resolvePropertyName(parameter.getColumnName());
//	    if (propertyName==null)
//	    	throw new RuntimeSystemException("Parameter " + parameter.getColumnName() + " doesn't resolve a valid property in " + parameterType.getName() + " caused by a missing or incorrect call parameter");
	    Class propertyType;
	    if (handlerRegistry.hasTypeHandler(parameterType)) {
	    	propertyType = parameterType;
	    } else if ("CURSOR".equals(JdbcType.forCode(parameter.getDataType()).name())) {
	    	propertyType = java.sql.ResultSet.class;
	    } else if (metaClass.hasGetter(propertyName)) {
	    	propertyType = metaClass.getGetterType(propertyName);
	    } else {
	    	propertyType = Object.class;
	    }
	    
	    Parameter.Factory parameterFactory = new Parameter.Factory(system, propertyName, propertyType);
	    parameterFactory.extra(callParameter.isExtra());
	    parameterFactory.boolexp(callParameter.getBoolexp());
	    parameterFactory.jdbcType(JdbcType.forCode(parameter.getDataType()));
	    parameterFactory.direction(ParameterDirection.forCode(parameter.getColumnType()));
	    parameterFactory.numericScale(Integer.valueOf(parameter.getScale()));
		return parameterFactory.getParameter();
	}
	
//	private String resolvePropertyName(String parameterName) {
//		String propertyName=null;
//		if (callParameterMap.containsName(parameterName)) {
//			propertyName=callParameterMap.getByName(parameterName).getProperty();	
//		}
//		return propertyName==null?propertyName:propertyName;
//	}
	private CallParameter resolveCallParameter(String parameterName) {
		CallParameter callParameter=null;
		if (callParameterMap.containsName(parameterName)) {
			callParameter=callParameterMap.getByName(parameterName);	
		}
		return callParameter;
	}
}
