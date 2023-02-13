/**
 * 
 */
package com.seda.data.procedure.result;

/**
 * @author f.ricci
 *
 */
public class ResultBounds {

	public final static int NO_ROW_OFFSET = 0;
	public final static int NO_ROW_LIMIT = Integer.MAX_VALUE;
	public final static ResultBounds DEFAULT = new ResultBounds();

	private int offset;
	private int limit;

	public ResultBounds() {
		this.offset = NO_ROW_OFFSET;
		this.limit = NO_ROW_LIMIT;
	}

	public ResultBounds(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}	

}
