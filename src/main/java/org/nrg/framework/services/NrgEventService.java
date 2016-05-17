package org.nrg.framework.services;

import javax.inject.Inject;

import org.nrg.framework.event.EventI;
//import org.nrg.xdat.XDAT;
import org.springframework.stereotype.Service;

import reactor.bus.Bus;
import reactor.bus.Event;
import reactor.bus.EventBus;

/**
 * The Class XftEventService.
 */
@Service
public class NrgEventService {
	
	/** The _instance. */
	private static NrgEventService _instance;
	
	/**
	 * Instantiates a new xft event service.
	 */
	public NrgEventService() {
		_instance = this;
	}
	
	
	/** The event bus. */
	@Inject private EventBus eventBus;
	
	/**
	 * Trigger event.
	 *
	 * @param eventDesc the event desc
	 * @param event the event
	 * @param notifyClassListeners Notify class listeners?
	 */
	public void triggerEvent(String eventDesc, EventI event, boolean notifyClassListeners) {
		eventBus.notify(eventDesc,Event.wrap(event));
		if (notifyClassListeners) {
			eventBus.notify(event.getClass(),Event.wrap(event));
		}
	}
	
	/**
	 * Trigger event.
	 *
	 * @param eventDesc the event desc
	 * @param event the event
	 */
	public void triggerEvent(String eventDesc, EventI event) {
		triggerEvent(eventDesc,event,true);
	}
	
	/**
	 * Trigger event.
	 *
	 * @param event the event
	 */
	public void triggerEvent(EventI event) {
		eventBus.notify(event.getClass(),Event.wrap(event));
	}
	
	/**
	 * Trigger event.
	 *
	 * @param event the event
	 * @param replyTo the reply to
	 */
	public void triggerEvent(EventI event, Object replyTo) {
		if (replyTo == null) {
			throw new IllegalArgumentException("Event replyTo object cannot be null");
		}
		eventBus.notify(event.getClass(),Event.wrap(event, replyTo));
	}
	
	/**
	 * Trigger event.
	 *
	 * @param eventDesc the event desc
	 * @param event the event
	 * @param notifyClassListeners Notify class event listeners?
	 */
	@SuppressWarnings("rawtypes")
	public void triggerEvent(String eventDesc,Event event,boolean notifyClassListeners) {
		eventBus.notify(eventDesc,event);
		if (notifyClassListeners) {
			eventBus.notify(event.getClass(),Event.wrap(event));
		}
	}
	
	/**
	 * Trigger event.
	 *
	 * @param eventDesc the event desc
	 * @param event the event
	 */
	@SuppressWarnings("rawtypes")
	public void triggerEvent(String eventDesc,Event event) {
		triggerEvent(eventDesc,event,true);
	}
	
	/**
	 * Trigger event.
	 *
	 * @param event the event
	 */
	@SuppressWarnings("rawtypes")
	public void triggerEvent(Event event) {
		eventBus.notify(event.getClass(),event);
	}
	
	/**
	 * Send event.
	 *
	 * @param event the event
	 */
	@SuppressWarnings("rawtypes")
	public void sendEvent(Event event) {
		if (event.getReplyTo() == null) {
			throw new IllegalArgumentException("Event replyTo object cannot be null");
		}
		eventBus.send(event.getClass(),event);
	}
	
	/**
	 * Send event.
	 *
	 * @param eventDesc the event desc
	 * @param event the event
	 * @param notifyClassEventListeners Notify class listeners?
	 * @param replyTo the reply to
	 */
	@SuppressWarnings("rawtypes")
	public void sendEvent(String eventDesc, EventI event, Bus replyTo, boolean notifyClassListeners) {
		eventBus.send(eventDesc, Event.wrap(event), replyTo);
		if (notifyClassListeners) {
			eventBus.send(event.getClass(), Event.wrap(event), replyTo);
		}
	}
	
	/**
	 * Send event.
	 *
	 * @param eventDesc the event desc
	 * @param event the event
	 * @param replyTo the reply to
	 */
	@SuppressWarnings("rawtypes")
	public void sendEvent(String eventDesc, EventI event, Bus replyTo) {
		sendEvent(eventDesc, event, replyTo, true);
	}
	
	/**
	 * Send event.
	 *
	 * @param event the event
	 * @param replyTo the reply to
	 */
	@SuppressWarnings("rawtypes")
	public void sendEvent(EventI event, Bus replyTo) {
		eventBus.send(event.getClass(),Event.wrap(event), replyTo);
	}

}
