/**
 * 
 */
package com.seda.data.procedure.result;

import java.util.ArrayList;
import java.util.List;

/**
 * @author f.ricci
 *
 */
public class ResultHandlerImpl implements ResultHandler {

	private final List list = new ArrayList();

	public void process(ResultContext context) {
		list.add(context.getResultObject());
	}

	public List getResultList() {
		return list;
	}
	
}
