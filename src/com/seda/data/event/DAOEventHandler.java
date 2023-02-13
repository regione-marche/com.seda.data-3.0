package com.seda.data.event;

import com.seda.commons.event.Event;
import com.seda.commons.event.HandlerRegistration;
import com.seda.commons.event.SimpleEventBus;
import com.seda.commons.event.Event.Type;

/**
 * This class wraps the DAO SimpleEventBus used to fire data access events.
 * 
 * @author f.ricci
 *
 */
public class DAOEventHandler {

	private SimpleEventBus eventBus;
	
	private final static DAOEventHandler INSTANCE;
	
	static {
		INSTANCE = new DAOEventHandler();
	}
	
	public DAOEventHandler(){
		eventBus=new SimpleEventBus();
	}
	
	public static DAOEventHandler instance() {
		return INSTANCE;
	}
	
	
	public <H> HandlerRegistration addHandler(Type<H> type, H handler) {

		return eventBus.addHandler(type, handler);
	}

	public <H> HandlerRegistration addHandlerToSource(final Event.Type<H> type, final Object source,
			final H handler) {

		return eventBus.addHandlerToSource(type, source, handler);
	}
	
	public void fire(Event<?> event) {
		eventBus.fireEvent(event);
	}
	
	public void fireEventFromSource(Event<?> event, Object source) {
		eventBus.fireEventFromSource(event, source);
	}
	
}
