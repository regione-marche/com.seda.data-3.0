/**
 * 
 */
package com.seda.data.procedure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.seda.data.procedure.parameter.CallParameter;
import com.seda.data.procedure.parameter.CallParameterMap;
import com.seda.data.procedure.parameter.Parameter;
import com.seda.data.procedure.parameter.ParameterMap;
import com.seda.data.procedure.reflection.MetaProcedure;
import com.seda.data.procedure.result.ColumnMap;
import com.seda.data.procedure.transaction.Transaction;

/**
 * @author f.ricci
 *
 */
public class Procedure {

	private String resource;
	private SubSystem system;
	private String id;
	private String name;	
	private Integer fetchSize;
	private Integer timeout;
	private ParameterMap parameterMap;
	private List<ColumnMap> columnMapList;
	private boolean hasNestedResultMaps;
	private ProcedureSource procedureSource;
	private CallParameterMap callParameterMap;

	public String getResource() {
		return resource;
	}

	public SubSystem getSubSystem() {
		return system;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}	
	
	public boolean hasNestedResultMaps() {
		return hasNestedResultMaps;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public ParameterMap getParameterMap() {
		return parameterMap;
	}

	public CallParameterMap getCallParameterMap() {
		return callParameterMap;
	}	
	
	public List<ColumnMap> getColumnMapList() {
		return columnMapList;
	}

	public ProcedureBound getProcedureBound(Transaction transaction, Object parameter) {
		MetaProcedure metaProcedure = MetaProcedure.forProcedure(transaction, name);
		ProcedureBound procedureBound = procedureSource.bind(metaProcedure,parameter);		
		List<Parameter> parameterList = procedureBound.getParameterList();
		if (parameterList == null || parameterList.size() <= 0) {
			procedureBound = new ProcedureBound(system, metaProcedure, parameterMap.getParameterList(), parameter);
		}
		return procedureBound;
	}	
	
	private Procedure() {}

	public static class Factory {
		private Procedure procedure = new Procedure();

		public Factory(SubSystem system, String id) {
			procedure.system = system;
			procedure.id = id;
			procedure.name = id;
			procedure.parameterMap = new ParameterMap.Factory(system, "parameterMap", Object.class, new ArrayList<Parameter>()).getParameterMap();
			procedure.columnMapList = new ArrayList<ColumnMap>();
			procedure.timeout = system.getDefaultCallTimeout();
			procedure.callParameterMap = new CallParameterMap<String, CallParameter>(id);
		}

		public String id() {
			return procedure.id;
		}		
		
		public Factory name(String id, String name) {
			procedure.name=name==null?id:name;
			return this;
		}		
		
		public Factory resource(String resource) {
			procedure.resource = resource;
			return this;
		}

		public Factory parameterMap(ParameterMap parameterMap) {
			procedure.parameterMap = parameterMap;
			return this;
		}

		public Factory columnMaps(List<ColumnMap> columnMaps) {
			procedure.columnMapList = columnMaps;
			for (ColumnMap columnMap : columnMaps) {
				procedure.hasNestedResultMaps = procedure.hasNestedResultMaps || columnMap.hasNestedResultMaps();
			}
			return this;
		}

		public void callParameterMap(CallParameterMap callParameterMap ) {
			procedure.callParameterMap=callParameterMap;
		}		
		
		public Factory fetchSize(Integer fetchSize) {
			procedure.fetchSize = fetchSize;
			return this;
		}

		public Factory timeout(Integer timeout) {
			procedure.timeout = timeout;
			return this;
		}

		public Procedure getMappedProcedure() {
			assert procedure.system != null;
			assert procedure.id != null;
			procedure.procedureSource = new ProcedureSource(procedure.system, procedure.callParameterMap);
			procedure.columnMapList = Collections.unmodifiableList(procedure.columnMapList);
			return procedure;
		}

	}

	@Override
	public String toString() {
		return "Procedure [id=" + id + ", name=" + name + ", columnMapList="
				+ columnMapList + ", fetchSize=" + fetchSize
				+ ", hasNestedResultMaps=" + hasNestedResultMaps
				+ ", callParameterMap=" + callParameterMap + ", parameterMap="
				+ parameterMap + ", procedureSource=" + procedureSource
				+ ", resource=" + resource + ", system=" + system
				+ ", timeout=" + timeout + "]";
	}

}
