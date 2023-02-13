/**
 * 
 */
package com.seda.data.event;

import java.sql.Connection;

import com.seda.commons.event.Event;
import com.seda.commons.event.EventBus;
import com.seda.commons.event.HandlerRegistration;

/**
 * @author f.ricci
 *
 */
public class ConnectionOpenEvent extends Event<ConnectionOpenEvent.Handler>{
	/**
	 * Implemented by methods that handle ConnectionOpenEvent events.
	 */
	public interface Handler {
		/**
		 * Called when an {@link ConnectionOpenEvent} event is fired.
		 * The name of this method is whatever you want it.
		 *
		 * @param event an {@link ConnectionOpenEvent} instance
		 */
		void onConnectionOpen(ConnectionOpenEvent event);
	}

	private static final Type<ConnectionOpenEvent.Handler> TYPE =
		new Type<ConnectionOpenEvent.Handler>() {

			@Override
			public String toString() {
				return ConnectionOpenEvent.Handler.class.toString();
			}
		
	};

	/**
	 * Register a handler for ConnectionOpenEvent events on the event bus.
	 * 
	 * @param eventBus the {@link EventBus}
	 * @param handler an {@link ConnectionOpenEvent.Handler} instance
	 * @return an {@link HandlerRegistration} instance
	 */
	public static HandlerRegistration register(EventBus eventBus,
			ConnectionOpenEvent.Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
	
	/**
	 * Fire a ConnectionOpenEvent events on the event bus.
	 * 
	 * @param eventBus the {@link EventBus}
	 * @param opened {@link Connection} instance
	 */	
	public static void fire(EventBus eventBus, Connection connection) {
		eventBus.fireEvent(new ConnectionOpenEvent(connection));
	}    	

	private final Connection connection;

	public ConnectionOpenEvent(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Type<ConnectionOpenEvent.Handler> getAssociatedType() {
		return TYPE;
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onConnectionOpen(this);
	}
	
}