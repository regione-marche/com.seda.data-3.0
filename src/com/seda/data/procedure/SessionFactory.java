/**
 * 
 */
package com.seda.data.procedure;

/**
 * @author f.ricci
 *
 */
public interface SessionFactory {

	  Session open();	
	
	  Session open(String schema);
	  
	  Session open(String planId, String schema);	  

	  Session open(boolean autoCommit);	  
	  
	  Session open(String schema, boolean autoCommit);
	  
	  Session open(String planId, String schema, boolean autoCommit);	  
	  
	  Session open(String planId, String catalog, String schema, boolean autoCommit);
	  
	  SubSystem getSystemConfig();
}
