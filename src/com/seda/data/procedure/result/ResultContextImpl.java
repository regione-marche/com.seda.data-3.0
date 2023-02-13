/**
 * 
 */
package com.seda.data.procedure.result;

/**
 * @author f.ricci
 *
 */
public class ResultContextImpl implements ResultContext{

	private Object resultObject;
	private int resultCount;
	private boolean stopped;

	public ResultContextImpl() {
		resultObject = null;
		resultCount = 0;
		stopped = false;
	}

	public Object getResultObject() {
		return resultObject;
	}

	public int getResultCount() {
		return resultCount;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void nextObject(Object resultObject) {
		resultCount++;
		this.resultObject = resultObject;
	}

	public void stop() {
		this.stopped = true;
	}	
	
}
