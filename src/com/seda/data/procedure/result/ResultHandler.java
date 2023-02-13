/**
 * 
 */
package com.seda.data.procedure.result;

import java.util.List;

/**
 * @author f.ricci
 *
 */
public interface ResultHandler {

	  void process(ResultContext context);

	  public List getResultList();
}
