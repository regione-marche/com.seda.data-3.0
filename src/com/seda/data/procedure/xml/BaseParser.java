/**
 * 
 */
package com.seda.data.procedure.xml;

import com.seda.data.procedure.SubSystem;
import com.seda.data.procedure.parameter.ParameterDirection;
import com.seda.data.type.AliasRegistry;
import com.seda.data.type.HandlerRegistry;
import com.seda.data.type.JdbcType;
import com.seda.data.type.TypeHandler;

/**
 * @author f.ricci
 *
 */
public class BaseParser {

	protected final SubSystem system;
	protected final AliasRegistry aliasRegistry;
	protected final HandlerRegistry handlerRegistry;

	public BaseParser(SubSystem system) {
		this.system = system;
		this.aliasRegistry = this.system.getAliasRegistry();
		this.handlerRegistry = this.system.getHandlerRegistry();
	}

	public SubSystem getProcedureSystem() {
		return system;
	}

	protected String stringValueOf(String value, String defaultValue) {
		return value == null ? defaultValue : value;
	}

	protected Boolean booleanValueOf(String value, Boolean defaultValue) {
		return value == null ? defaultValue : Boolean.valueOf(value);
	}

	protected Integer integerValueOf(String value, Integer defaultValue) {
		return value == null ? defaultValue : Integer.valueOf(value);
	}

	protected JdbcType resolveJdbcType(String alias) {
		if (alias == null) return null;
		try {
			return JdbcType.valueOf(alias);
		} catch (IllegalArgumentException e) {
			throw new ParserException("Error resolving JdbcType. Cause: " + e, e);
		}
	}

	//	  protected ResultSetType resolveResultSetType(String alias) {
	//	    if (alias == null) return null;
	//	    try {
	//	      return ResultSetType.valueOf(alias);
	//	    } catch (IllegalArgumentException e) {
	//	      throw new ContextBuilderException("Error resolving ResultSetType. Cause: " + e, e);
	//	    }
	//	  }

	protected ParameterDirection resolveParameterDirection(String alias) {
		if (alias == null) return null;
		try {
			return ParameterDirection.valueOf(alias);
		} catch (IllegalArgumentException e) {
			throw new ParserException("Error resolving ParameterDirection. Cause: " + e, e);
		}
	}

	protected TypeHandler resolveTypeHandler(String alias) {
		Class<?> aliasClass=resolveClass(alias);
		if (aliasClass==null) return null;
		try {
			return resolveTypeHandler(aliasClass);
		} catch (Exception e) {
			throw new ParserException("Error resolving type handler class . Cause: " + e, e);
		}
	}
	
	protected Class<?> resolveClass(String alias) {
		if (alias == null) return null;
		try {
			return resolveAlias(alias);
		} catch (Exception e) {
			throw new ParserException("Error resolving class . Cause: " + e, e);
		}
	}

	protected Object resolveInstance(String alias) {
		if (alias == null) return null;
		try {
			Class<?> type = resolveClass(alias);
			return type.newInstance();
		} catch (Exception e) {
			throw new ParserException("Error instantiating class. Cause: " + e, e);
		}
	}

	protected Object resolveInstance(Class<?> type) {
		if (type == null) return null;
		try {
			return type.newInstance();
		} catch (Exception e) {
			throw new ParserException("Error instantiating class. Cause: " + e, e);
		}
	}

	protected TypeHandler resolveTypeHandler(Class<?> aliasClass) {
		return handlerRegistry.getTypeHandler(aliasClass);
	}
	
	protected Class<?> resolveAlias(String alias) {
		return aliasRegistry.resolveAlias(alias);
	}	

}
