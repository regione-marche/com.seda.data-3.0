/**
 * 
 */
package com.seda.data.procedure.result;

/**
 * @author f.ricci
 *
 */
public interface ResultContext {

	  Object getResultObject();

	  int getResultCount();

	  void nextObject(Object resultObject);	  
	  
	  boolean isStopped();

	  void stop();	
	
}
