/**
 * 
 */
package com.seda.data.event.servlet;

import java.sql.Connection;

import com.seda.commons.event.EventBus;
import com.seda.commons.event.ResettableEventBus;
import com.seda.commons.event.SimpleEventBus;
import com.seda.data.event.ConnectionOpenEvent;

/**
 * @author f.ricci
 *
 */
public class DAOEventContext {

	private ResettableEventBus eventBus = null;
	
	public EventBus getEventBus() {
		return eventBus;
	}
	
	public void fireConnectionOpen(Connection connection) {
		ConnectionOpenEvent.fire(eventBus, connection);
	}
	
	public DAOEventContext () {
		this.eventBus = new ResettableEventBus(new SimpleEventBus());
	}

	public void remove() {
		eventBus.removeHandlers();
		eventBus=null;
	}
}
