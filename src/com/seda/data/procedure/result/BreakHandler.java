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
public class BreakHandler {

	public static final BreakHandler NULL_BREAK = new BreakHandler();

	private static final int DEFAULT_MULTIPLYER = 29;
	private static final int DEFAULT_HASHCODE = 13;

	private int multiplier;
	private int hashcode;
	private long checksum;
	private int count;
	private List breakList;

	public BreakHandler() {
		this.hashcode = DEFAULT_HASHCODE;
		this.multiplier = DEFAULT_MULTIPLYER;
		this.count = 0;
		this.breakList = new ArrayList();
	}

	public BreakHandler(Object[] objects) {
		this();
		addAll(objects);
	}

	public int getBreakCount() {
		return breakList.size();
	}

	public void add(Object object) {
		int baseHashCode = object == null ? 1 : object.hashCode();

		count++;
		checksum += baseHashCode;
		baseHashCode *= count;

		hashcode = multiplier * hashcode + baseHashCode;

		breakList.add(object);
	}

	public void addAll(Object[] objects) {
		for (Object o : objects) {
			add(o);
		}
	}

	public boolean equals(Object object) {
		if (this == object) return true;
		if (!(object instanceof BreakHandler)) return false;

		final BreakHandler breaker = (BreakHandler) object;

		if (hashcode != breaker.hashcode) return false;
		if (checksum != breaker.checksum) return false;
		if (count != breaker.count) return false;

		for (int i = 0; i < breakList.size(); i++) {
			Object thisObject = breakList.get(i);
			Object thatObject = breaker.breakList.get(i);
			if (thisObject == null) {
				if (thatObject != null) return false;
			} else {
				if (!thisObject.equals(thatObject)) return false;
			}
		}
		return true;
	}

	public int hashCode() {
		return hashcode;
	}

	public String toString() {
		StringBuffer returnValue = new StringBuffer().append(hashcode).append(':').append(checksum);
		for (int i = 0; i < breakList.size(); i++) {
			returnValue.append(':').append(breakList.get(i));
		}

		return returnValue.toString();
	}


}
