/**
 * 
 */
package com.seda.data.event.servlet;

/**
 * @author f.ricci
 *
 */
public class DAOEventLocal {

	public final static ThreadLocal<DAOEventContext> LOCAL = new ThreadLocal<DAOEventContext>();
	
	public static void set(DAOEventContext user) {
		LOCAL.set(user);
    }

    public static void unset() {
    	DAOEventContext context = LOCAL.get();
    	if (context!=null) {
    		context.remove();
    	}
    	LOCAL.remove();
    }

    public static DAOEventContext get() {
        return LOCAL.get();
    }
	
}
