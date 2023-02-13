/**
 * 
 */
package com.seda.data.procedure.xml;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.seda.commons.reflection.MetaClass;
import com.seda.commons.resource.ResourceManager;
import com.seda.commons.xparser.XNode;
import com.seda.commons.xparser.XPathParser;
import com.seda.data.datasource.DataSourceFactory;
import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.Plan;
import com.seda.data.procedure.transaction.TransactionFactory;
/**
 * @author f.ricci
 *
 */
public class XMLSystemParser extends BaseParser {

	private boolean parsed;
	private XPathParser parser;
	private String defaultPlan;	

	public XMLSystemParser(Reader reader) {
		this(reader, null, null);
	}

	public XMLSystemParser(Reader reader, String procedurePlan) {
		this(reader, procedurePlan, null);
	}

	public XMLSystemParser(Reader reader, String defaultPlan, Properties props) {
		super(new SubSystem());
		this.system.setVariables(props);
		this.parsed = false;
		this.defaultPlan = defaultPlan;
		this.parser = new XPathParser(reader, true, props, new XMLEntityResolver());
	}

	public SubSystem parse() {
		if (parsed) {
			throw new ParserException("Each MapperConfigParser can only be used once.");
		}
		parsed = true;
		parseSystem(parser.evalNode("/system"));
		return system;
	}

	private void parseSystem(XNode root) {
		try {
			aliasesElement(root.evalNode("aliases"));
			propertiesElement(root.evalNode("properties"));
			settingsElement(root.evalNode("settings"));
			procedurePlansElement(root.evalNode("plans"));
			packageElement(root.evalNode("packages"));
		} catch (Exception e) {
			throw new ParserException("Error parsing Procedure System. Cause: " + e, e);
		}
	}

	private void aliasesElement(XNode parent) {
		if (parent != null) {
			List<XNode> children = parent.getChildren();
			for (XNode child : children) {
				String name = child.getStringAttribute("name");
				String type = child.getStringAttribute("type");
				try {
					Class clazz = ResourceManager.classForName(type);
					if (name == null) {
						aliasRegistry.registerAlias(clazz);
					} else {
						aliasRegistry.registerAlias(name, clazz);
					}
				} catch (ClassNotFoundException e) {
					throw new ParserException("Error registering alias for '" + name + "'. Cause: " + e, e);
				}
			}
		}
	}

	private void propertiesElement(XNode node) throws Exception {
		if (node != null) {
			Properties defaults = node.getChildrenAsProperties();
			String resource = node.getStringAttribute("resource");
			String url = node.getStringAttribute("url");
			if (resource != null && url != null) {
				throw new ParserException("The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
			}
			if (resource != null) {
				defaults.putAll(ResourceManager.getResourceAsProperties(resource));
			} else if (url != null) {
				defaults.putAll(ResourceManager.getUrlAsProperties(url));
			}
			Properties vars = system.getVariables();
			if (vars != null) {
				defaults.putAll(vars);
			}
			parser.setVariables(defaults);
			system.setVariables(defaults);
		}
	}

	private void settingsElement(XNode node) throws Exception {
		if (node != null) {
			Properties props = node.getChildrenAsProperties();
			// Check that all settings are known to the configuration class
			for (Map.Entry entry : props.entrySet()) {
				MetaClass metaConfig = MetaClass.forClass(SubSystem.class);
				if (!metaConfig.hasSetter((String) entry.getKey())) {
					throw new ParserException("The setting " + entry.getKey() + " is not known.  Make sure you spelled it correctly (case sensitive).");
				}
			}
			system.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
			system.setDefaultCallTimeout(integerValueOf(props.getProperty("defaultCallTimeout"), null));
		}
	}

	private void procedurePlansElement(XNode node) throws Exception {
		if (node != null) {
			if (defaultPlan == null) {
				defaultPlan = node.getStringAttribute("default");
			}
			List<XNode> children = node.getChildren();
			for (XNode child : children) {
				String id = child.getStringAttribute("id");
				TransactionFactory txFactory = transactionManagerElement("JDBC");					
				DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
				Plan.Factory procedurePlanFactory = new Plan.Factory(id, txFactory, dsFactory.getDataSource());				
				if (isSpecifiedPlan(id)) {
					system.setDefaultPlan(procedurePlanFactory.getPlan());
				} 
				system.addMappedPlan(procedurePlanFactory.getPlan());
			}
		}
	}

	private TransactionFactory transactionManagerElement(String type) throws Exception {
		if (type != null) {
			TransactionFactory factory = (TransactionFactory) resolveClass(type).newInstance();
			return factory;
		}
		throw new ParserException("Plan declaration requires a TransactionFactory.");
	}

	private DataSourceFactory dataSourceElement(XNode node) throws Exception {
		if (node != null) {
			String type = node.getStringAttribute("type");
			Properties props = node.getChildrenAsProperties();
			DataSourceFactory factory = (DataSourceFactory) resolveClass(type).newInstance();
			factory.setProperties(props);
			return factory;
		}
		throw new ParserException("Plan declaration requires a DataSourceFactory.");
	}

	private void packageElement(XNode node) throws Exception {
		if (node != null) {
			List<XNode> children = node.getChildren();
			for (XNode child : children) {
				String resource = child.getStringAttribute("resource");
				String url = child.getStringAttribute("url");
				Reader reader;
				if (resource != null && url == null) {
					reader = ResourceManager.getResourceAsReader(resource);
					XMLPackageParser packageParser = new XMLPackageParser(reader, system, resource);
					packageParser.parse();
				} else if (url != null && resource == null) {
					reader = ResourceManager.getUrlAsReader(url);
					XMLPackageParser mapperParser = new XMLPackageParser(reader, system, url);
					mapperParser.parse();
				} else {
					throw new ParserException("A package element may only specify a url or resource, but not both.");
				}
			}
		}
	}

	private boolean isSpecifiedPlan(String id) {
		if (defaultPlan == null) {
			throw new ParserException("No procedurePlan specified.");
		} else if (id == null) {
			throw new ParserException("Plan requires an id attribute.");
		} else if (defaultPlan.equals(id)) {
			return true;
		}
		return false;
	}	

}
