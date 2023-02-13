/**
 * 
 */
package com.seda.data.procedure.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seda.commons.xparser.XNode;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.parameter.CallParameter;
import com.seda.data.procedure.parameter.CallParameterMap;

/**
 * @author f.ricci
 *
 */
public class XMLCallParameterParser extends BaseParser {

	private PackageBuilder packageBuilder;
	private String procedureId;
	
	public XMLCallParameterParser(SubSystem configuration, PackageBuilder packageBuilder, String procedureId) {
		super(configuration);
		this.packageBuilder = packageBuilder;
		this.procedureId=procedureId;
	}

	public CallParameterMap parseParameterNodes(XNode context) {
		CallParameterMap callParameterMap = new CallParameterMap<String, CallParameter>("CallParameter collection for '" + procedureId + "'");
		
		List<XNode> resultChildren = context.getChildren();
		for (XNode resultChild : resultChildren) {
			if ("parameter".equals(resultChild.getName())) {
				String property = resultChild.getStringAttribute("property");
				String name = resultChild.getStringAttribute("name");
				String column = resultChild.getStringAttribute("column");
				String boolexp = resultChild.getStringAttribute("boolexp", null);
				Boolean extra = resultChild.getBooleanAttribute("extra", false);
				String key =resultChild.getStringAttribute("name",
						resultChild.getValueBasedIdentifier()); 

				callParameterMap.put(key, buildCallParameterFromContext(property, name, column, boolexp, extra));
			}
		}
		return callParameterMap;
	}

	private CallParameter buildCallParameterFromContext(String property, String name,
			String column, String boolexp, boolean extra) {
		return packageBuilder.buildCallParameter(name, property, column, boolexp, extra);
	}	
	
}
