/**
 * 
 */
package com.seda.data.event;

import java.sql.Connection;

import com.seda.data.dao.DAOHelper;
import com.seda.data.event.servlet.DAOEventContext;
import com.seda.data.event.servlet.DAOEventLocal;

/**
 * @author f.ricci
 *
 */
public class DAOEventProxy {

	
	public static void dispatch(Connection connection) {
		// before returning connection fire the open connection event
		if (DAOHelper.isInServletContainer()) {
			DAOEventContext context = DAOEventLocal.get();
			if (context!=null) {
				context.fireConnectionOpen(connection);
			}
		} else {
			DAOEventHandler.instance().fire(new ConnectionOpenEvent(connection));				
		}	
	}
	
}
