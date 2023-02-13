/**
 * 
 */
package com.seda.data.procedure.xml;

import com.seda.commons.xparser.XNode;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.parameter.CallParameterMap;
/**
 * @author f.ricci
 *
 */
public class XMLProcedureParser extends BaseParser {

	private PackageBuilder packageBuilder;

	public XMLProcedureParser(SubSystem configuration, PackageBuilder packageBuilder) {
		super(configuration);
		this.packageBuilder = packageBuilder;
	}

	public void parseProcedureNode(XNode context) {
		String id = context.getStringAttribute("id");
		String specific = context.getStringAttribute("name");		    
		Integer fetchSize = context.getIntAttribute("fetchSize", null);
		Integer timeout = context.getIntAttribute("timeout", null);
		String parameterMap = context.getStringAttribute("parameterMap");
		String parameterType = context.getStringAttribute("parameterType");
		Class parameterTypeClass = resolveClass(parameterType);
		String columnMap = context.getStringAttribute("columnMap");
		String resultType = context.getStringAttribute("resultType");
		Class resultTypeClass = resolveClass(resultType);
		
		XMLCallParameterParser callParameterParser = new XMLCallParameterParser(system, packageBuilder, id);
		CallParameterMap callParameterMap = callParameterParser.parseParameterNodes(context);
		
		packageBuilder.addMappedProcedure(id, specific, fetchSize, timeout, parameterMap, parameterTypeClass, columnMap, resultTypeClass, callParameterMap);
	}


}
