/**
 * 
 */
package com.seda.data.procedure.xml;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.seda.commons.resource.ResourceManager;
import com.seda.commons.xparser.XNode;
import com.seda.commons.xparser.XPathParser;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.parameter.CallParameterMap;
import com.seda.data.procedure.result.Column;
import com.seda.data.procedure.result.ColumnLabel;
import com.seda.data.procedure.result.ColumnMap;
import com.seda.data.type.JdbcType;

/**
 * @author f.ricci
 *
 */
public class XMLPackageParser extends BaseParser {

	private XPathParser parser;
	private PackageBuilder packageBuilder;
	private String resource;

	public XMLPackageParser(Reader reader, SubSystem context, String resource, String collection) {
		this(reader, context, resource);
		this.packageBuilder.setCurrentCollection(collection);
	}

	public XMLPackageParser(Reader reader, SubSystem system, String resource) {
		super(system);
		this.packageBuilder = new PackageBuilder(system, resource);
		this.parser = new XPathParser(reader, true, system.getVariables(), new XMLEntityResolver());
		this.resource = resource;
	}

	public void parse() {
		if (!system.isResourceLoaded(resource)) {
			system.addLoadedResource(resource);
			contextElement(parser.evalNode("/package"));
			bindMapperForNamespace();
		}
	}

	private void contextElement(XNode context) {
		try {
			String collection = context.getStringAttribute("collection");
			packageBuilder.setCurrentCollection(collection);
//			parameterMapElement(context.evalNodes("/package/parameterMap"));
			columnMapElements(context.evalNodes("/package/columnMap"));
			buildStatementFromContext(context.evalNodes("/package/procedure"));
		} catch (Exception e) {
			throw new RuntimeException("Error parsing Package XML. Cause: " + e, e);
		}
	}

//	private void parameterMapElement(List<XNode> list) throws Exception {
//		for (XNode parameterMapNode : list) {
//			String id = parameterMapNode.getStringAttribute("id");
//			String type = parameterMapNode.getStringAttribute("type");
//			Class parameterClass = resolveClass(type);
//			List<XNode> parameterNodes = parameterMapNode.evalNodes("parameter");
//			List<Parameter> parameterMappings = new ArrayList<Parameter>();
//			for (XNode parameterNode : parameterNodes) {
//				String property = parameterNode.getStringAttribute("property");
//				String javaType = parameterNode.getStringAttribute("javaType");
//				String jdbcType = parameterNode.getStringAttribute("jdbcType");
//				String columnMap = parameterNode.getStringAttribute("columnMap");
//				String mode = parameterNode.getStringAttribute("mode");
//				String typeHandler = parameterNode.getStringAttribute("typeHandler");
//				Integer numericScale = parameterNode.getIntAttribute("numericScale", null);
//				ParameterDirection modeEnum = resolveParameterDirection(mode);
//				Class javaTypeClass = resolveClass(javaType);
//				JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
//				Class typeHandlerClass = resolveClass(typeHandler);
//				Parameter parameterMapping = packageBuilder.buildParameter(parameterClass, property, javaTypeClass, jdbcTypeEnum, columnMap, modeEnum, typeHandlerClass, numericScale);
//				parameterMappings.add(parameterMapping);
//			}
//			packageBuilder.addParameterMap(id, parameterClass, parameterMappings);
//		}
//	}


	private void columnMapElements(List<XNode> list) throws Exception {
		for (XNode columnMapNode : list) {
			columnMapElement(columnMapNode);
		}
	}

	private ColumnMap columnMapElement(XNode columnMapNode) throws Exception {
		return columnMapElement(columnMapNode, Collections.EMPTY_LIST);
	}

	private ColumnMap columnMapElement(XNode columnMapNode, List<Column> additionalColumnList) throws Exception {
		String id = columnMapNode.getStringAttribute("id",
				columnMapNode.getValueBasedIdentifier());
		String type = columnMapNode.getStringAttribute("type",
				columnMapNode.getStringAttribute("ofType",
						columnMapNode.getStringAttribute("resultType",
								columnMapNode.getStringAttribute("javaType"))));
		String extend = columnMapNode.getStringAttribute("extends");
		Class typeClass = resolveClass(type);
		List<Column> columnMap = new ArrayList<Column>();
		columnMap.addAll(additionalColumnList);
		List<XNode> resultChildren = columnMapNode.getChildren();
		for (XNode resultChild : resultChildren) {
			if ("constructor".equals(resultChild.getName())) {
				processConstructorElement(resultChild, typeClass, columnMap);
			} else {
				ArrayList<ColumnLabel> labels = new ArrayList<ColumnLabel>();
				columnMap.add(buildColumnFromContext(resultChild, typeClass, labels));
			}
		}
		return packageBuilder.addColumnMap(id, typeClass, extend, columnMap);
	}

	private void processConstructorElement(XNode resultChild, Class resultType, List<Column> columnList) throws Exception {
		List<XNode> argChildren = resultChild.getChildren();
		for (XNode argChild : argChildren) {
			ArrayList<ColumnLabel> labels = new ArrayList<ColumnLabel>();
			labels.add(ColumnLabel.CONSTRUCTOR);
			columnList.add(buildColumnFromContext(argChild, resultType, labels));
		}
	}

	private Column buildColumnFromContext(XNode context, Class resultType, ArrayList<ColumnLabel> labels) throws Exception {
		boolean isKey = context.getBooleanAttribute("key", false);
		if (isKey) labels.add(ColumnLabel.KEY);
		
		String property = context.getStringAttribute("property");
		String name = context.getStringAttribute("name");
		String javaType = context.getStringAttribute("javaType");
		String jdbcType = context.getStringAttribute("jdbcType");
		String nestedCall = context.getStringAttribute("call");
		String nestedColumnMap = context.getStringAttribute("columnMap",
				processNestedColumns(context, Collections.EMPTY_LIST));
		String callParameter = null;
		CallParameterMap callParameterMap=null;
		if ("link".equals(context.getName()) || "call".equals(context.getName())) {
			if (nestedCall!=null && nestedColumnMap!=null) {
				throw new ParserException("Attributes 'call' and 'columnMap' are mutually exclusive");
			}
			if (nestedCall!=null) {
				XMLCallParameterParser callParameterParser = new XMLCallParameterParser(system, packageBuilder, "Column "+(name==null?context.getName():name) + " nested " + (property==null?"":"property:"+property));
				callParameterMap=callParameterParser.parseParameterNodes(context);
				callParameter=context.getStringAttribute("parameter");
				if (callParameter!=null && callParameterMap.size()>0) {
					throw new ParserException("Attribute 'parameter' and 'parameter child nodes' are mutually exclusive");
				}
				if (callParameter==null && callParameterMap.isEmpty()) {
					throw new ParserException("For a nested call you at least specify the attribute parameter in "+context.getName() + " node");
				}
			}
		}
		
		String typeHandler = context.getStringAttribute("typeHandler");
		
		boolean isExtra = context.getBooleanAttribute("extra", false);
		if (isExtra) labels.add(ColumnLabel.EXTRA);

		String boolexp = null;
		if (!"list".equals(context.getName()) && 
				!"link".equals(context.getName()) && 
				!"call".equals(context.getName())) {
			boolexp = context.getStringAttribute("boolexp");
			if (boolexp!=null) {
				labels.add(ColumnLabel.BOOLEXP);
				javaType="boolean";
				typeHandler="com.seda.data.type.StringHandler";
			}
		}
		
		if ("list".equals(context.getName())) {
			javaType="Collection";
		}
		
		Class javaTypeClass = resolveClass(javaType);
		Class typeHandlerClass = resolveClass(typeHandler); //resolveClass(typeHandler)
		JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
		return packageBuilder.buildColumn(resultType, property, name, javaTypeClass, jdbcTypeEnum, nestedCall, nestedColumnMap, typeHandlerClass, labels, boolexp, callParameterMap, callParameter);
	}

	private String processNestedColumns(XNode context, List<Column> columnList) throws Exception {
		if ("link".equals(context.getName())
				|| "list".equals(context.getName())) {
			if (context.getStringAttribute("call") == null) {
				ColumnMap columnMap = columnMapElement(context, columnList);
				return columnMap.getId();
			}
		}
		return null;
	}

	private void buildStatementFromContext(List<XNode> list) {
		for (XNode context : list) {
			final XMLProcedureParser procedureParser = new XMLProcedureParser(system, packageBuilder);
			procedureParser.parseProcedureNode(context);
		}
	}	

	private void bindMapperForNamespace() {
		String collection = packageBuilder.getCurrentCollection();
		if (collection != null) {
			Class boundType = null;
			try {
				boundType = ResourceManager.classForName(collection);
			} catch (ClassNotFoundException e) {
				//ignore, bound type is not required
			}
		}
	}


}
