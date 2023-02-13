/**
 * 
 */
package com.seda.data.procedure;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.seda.commons.reflection.MetaObject;
import com.seda.commons.reflection.factory.ObjectFactory;
import com.seda.commons.reflection.factory.ObjectFactoryImpl;
import com.seda.commons.reflection.wrapper.ObjectWrapperFactory;
import com.seda.commons.reflection.wrapper.ObjectWrapperFactoryImpl;
import com.seda.data.datasource.DataSourceFactoryImpl;
import com.seda.data.datasource.JndiDataSourceFactory;
import com.seda.data.datasource.PooledDataSourceFactory;
import com.seda.data.procedure.executor.Executor;
import com.seda.data.procedure.executor.ExecutorHandlerImpl;
import com.seda.data.procedure.parameter.ParameterHandler;
import com.seda.data.procedure.parameter.ParameterHandlerImpl;
import com.seda.data.procedure.parameter.ParameterMap;
import com.seda.data.procedure.result.ColumnMap;
import com.seda.data.procedure.result.NestedResultSetHandler;
import com.seda.data.procedure.result.ResultBounds;
import com.seda.data.procedure.result.ResultHandler;
import com.seda.data.procedure.result.ResultSetHandler;
import com.seda.data.procedure.result.ResultSetHandlerImpl;
import com.seda.data.procedure.statement.CallableStatementHandler;
import com.seda.data.procedure.statement.StatementHandler;
import com.seda.data.procedure.transaction.Transaction;
import com.seda.data.procedure.transaction.TransactionFactoryImpl;
import com.seda.data.type.AliasRegistry;
import com.seda.data.type.HandlerRegistry;
/**
 * @author f.ricci
 *
 */
public class SubSystem {

	protected Plan defaultPlan;
	
	protected Integer defaultCallTimeout;
	protected boolean useColumnLabel = true;

	protected Properties variables = new Properties();
	protected ObjectFactory objectFactory = new ObjectFactoryImpl();
	protected ObjectWrapperFactory objectWrapperFactory = new ObjectWrapperFactoryImpl();	
	protected final HandlerRegistry handlerRegistry = new HandlerRegistry();
	protected final AliasRegistry aliasRegistry = new AliasRegistry();

	protected final Map<String, Procedure> mappedProcedures = new UniqueMap<String, Procedure>("Mapped Procedure collection");
	protected final Map<String, ColumnMap> columnMaps = new UniqueMap<String, ColumnMap>("Result Column Maps collection");
	protected final Map<String, ParameterMap> parameterMaps = new UniqueMap<String, ParameterMap>("Parameter Maps collection");	
	protected final Map<String, Plan> mappedPlan = new UniqueMap<String, Plan>("Mapped Plan collection");	

	protected final Set<String> loadedResources = new HashSet<String>();	

	public void addLoadedResource(String resource) {
		loadedResources.add(resource);
	}

	public boolean isResourceLoaded(String resource) {
		return loadedResources.contains(resource);
	}	

	public Plan getDefaultPlan() {
		return defaultPlan;
	}

	public void setDefaultPlan(Plan defaultPlan) {
		this.defaultPlan = defaultPlan;
	}

	public SubSystem(Plan defaultPlan) {
		this();
		this.defaultPlan = defaultPlan;
	}

	public SubSystem() {  
		aliasRegistry.registerAlias("JDBC", TransactionFactoryImpl.class.getName());
		aliasRegistry.registerAlias("jndi", JndiDataSourceFactory.class.getName());
		aliasRegistry.registerAlias("pool", PooledDataSourceFactory.class.getName());
		aliasRegistry.registerAlias("direct", DataSourceFactoryImpl.class.getName());
	}

	public Integer getDefaultCallTimeout() {
		return defaultCallTimeout;
	}

	public void setDefaultCallTimeout(Integer defaultCallTimeout) {
		this.defaultCallTimeout = defaultCallTimeout;
	}

	public boolean isUseColumnLabel() {
		return useColumnLabel;
	}

	public void setUseColumnLabel(boolean useColumnLabel) {
		this.useColumnLabel = useColumnLabel;
	}

	public Properties getVariables() {
		return variables;
	}

	public void setVariables(Properties variables) {
		this.variables = variables;
	}

	public HandlerRegistry getHandlerRegistry() {
		return handlerRegistry;
	}

	public AliasRegistry getAliasRegistry() {
		return aliasRegistry;
	}

	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}

	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	public ObjectWrapperFactory getObjectWrapperFactory() {
		return objectWrapperFactory;
	}

	public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
		this.objectWrapperFactory = objectWrapperFactory;
	}

	public MetaObject getMetaObject(Object object) {
		return MetaObject.forObject(object,objectFactory,objectWrapperFactory);
	}

	public ParameterHandler getParameterHandler(Procedure mappedProcedure, Object parameter, ProcedureBound boundCall) {
		ParameterHandler parameterHandler = new ParameterHandlerImpl(mappedProcedure, parameter, boundCall);
		return parameterHandler;
	}	

	public ResultSetHandler getResultSetHandler(Executor executor, Procedure mappedProcedure, ResultBounds rowBounds, ParameterHandler parameterHandler, ProcedureBound boundCall) {
		ResultSetHandler resultSetHandler = mappedProcedure.hasNestedResultMaps() ?
				new NestedResultSetHandler(executor, mappedProcedure, parameterHandler, boundCall, rowBounds)
		: new ResultSetHandlerImpl(executor, mappedProcedure, parameterHandler, boundCall, rowBounds);
				return resultSetHandler;
	}

	public StatementHandler getStatementHandler(Executor executor, Procedure mappedProcedure, Object parameterObject, ResultBounds rowBounds, ResultHandler resultHandler) {
		StatementHandler statementHandler = new CallableStatementHandler(executor, mappedProcedure, parameterObject, rowBounds, resultHandler);
		return statementHandler;
	}

	public Executor getExecutor(Transaction transaction) {
		return new ExecutorHandlerImpl(this,transaction);
	}

	public void addColumnMap(ColumnMap rm) {
		columnMaps.put(rm.getId(), rm);
	}

	public Collection<String> getColumnMapNames() {
		return columnMaps.keySet();
	}

	public Collection<ColumnMap> getColumnMaps() {
		return columnMaps.values();
	}

	public ColumnMap getColumnMap(String id) {
		return columnMaps.get(id);
	}

	public boolean hasColumnMap(String id) {
		return columnMaps.containsKey(id);
	}

	public void addParameterMap(ParameterMap pm) {
		parameterMaps.put(pm.getId(), pm);
	}

	public Collection<String> getParameterMapNames() {
		return parameterMaps.keySet();
	}

	public Collection<ParameterMap> getParameterMaps() {
		return parameterMaps.values();
	}

	public ParameterMap getParameterMap(String id) {
		return parameterMaps.get(id);
	}

	public boolean hasParameterMap(String id) {
		return parameterMaps.containsKey(id);
	}

	public void addMappedProcedure(Procedure procedure) {
		mappedProcedures.put(procedure.getId(), procedure);
	}

	public Collection<String> getMappedProcedureNames() {
		return mappedProcedures.keySet();
	}

	public Collection<Procedure> getMappedProcedures() {
		return mappedProcedures.values();
	}

	public Procedure getMappedProcedure(String id) {
		return mappedProcedures.get(id);
	}

	public void addMappedPlan(Plan plan) {
		mappedPlan.put(plan.getId(), plan);
	}

	public Collection<String> getMappedPlanNames() {
		return mappedPlan.keySet();
	}

	public Collection<Plan> getMappedPlans() {
		return mappedPlan.values();
	}

	public Plan getMappedPlan(String id) {
		return mappedPlan.get(id);
	}	
	
	protected static class UniqueMap<J extends String, K extends Object> extends HashMap<J, K> {

		private static final long serialVersionUID = 3104909777155519916L;

		private String name;

		public UniqueMap(String name, int initialCapacity, float loadFactor) {
			super(initialCapacity, loadFactor);
			this.name = name;
		}

		public UniqueMap(String name, int initialCapacity) {
			super(initialCapacity);
			this.name = name;
		}

		public UniqueMap(String name) {
			super();
			this.name = name;
		}

		public UniqueMap(String name, Map<? extends J, ? extends K> m) {
			super(m);
			this.name = name;
		}

		public K put(J key, K value) {
			if (containsKey(key)) throw new IllegalArgumentException(name + " already contains value for " + key);
			if (key.contains(".")) {
				final String shortKey = getShortName(key);
				if (super.get(shortKey) == null) {
					super.put((J)shortKey, value);
				} else {
					super.put((J)shortKey, (K)new Ambiguity(shortKey));
				}
			}
			return super.put(key, value);
		}

		public K get(Object key) {
			K value = super.get(key);
			if (value == null) {
				value = super.get(getShortName((J)key));
				if (value == null) {
					throw new IllegalArgumentException(name + " does not contain value for " + key);
				}
			}
			if (value instanceof Ambiguity) {
				throw new IllegalArgumentException(((Ambiguity)value).getSubject()
						+ " is ambiguous in " + name + " (try using the full name including the namespace, or rename one of the entries)");
			}
			return value;
		}

		private String getShortName(J key) {
			final String[] keyparts = key.split("\\.");
			final String shortKey = keyparts[keyparts.length-1];
			return shortKey;
		}

		protected static class Ambiguity {
			private String subject;
			public Ambiguity(String subject) {
				this.subject = subject;
			}
			public String getSubject() {
				return subject;
			}
		}
	}
}
