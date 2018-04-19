/*
 * framework: org.nrg.framework.services.NrgEventService
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.services;

import org.nrg.framework.event.EventI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.bus.Bus;
import reactor.bus.Event;
import reactor.bus.EventBus;

/**
 * The Class NrgEventService.
 */
@Service
public class NrgEventService {
	/**
	 * Instantiates a new xft event service.
     *
     * @param eventBus    The event bus for the service.
	 */
	@Autowired
	public NrgEventService(final EventBus eventBus) {
		_eventBus = eventBus;
	}

	/** The event bus. */
	private EventBus _eventBus;
	
	/**
	 * Trigger event.
	 *
	 * @param eventDesc the event desc
	 * @param event the event
	 * @param notifyClassListeners Notify class listeners?
	 */
	public void triggerEvent(String eventDesc, EventI event, boolean notifyClassListeners) {
		log.debug("Triggering event {}: {}", event, eventDesc);
		_eventBus.notify(eventDesc, Event.wrap(event));
		if (notifyClassListeners) {
			_eventBus.notify(event.getClass(), Event.wrap(event));
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
		log.debug("Triggering event {}", event);
		_eventBus.notify(event.getClass(), Event.wrap(event));
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
		log.debug("Triggering event {}", event);
		_eventBus.notify(event.getClass(), Event.wrap(event, replyTo));
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
		log.debug("Triggering event {}: {}", event, eventDesc);
		_eventBus.notify(eventDesc, event);
		if (notifyClassListeners) {
			_eventBus.notify(event.getClass(), Event.wrap(event));
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
		log.debug("Triggering event {}: {}", event, eventDesc);
		triggerEvent(eventDesc,event,true);
	}
	
	/**
	 * Trigger event.
	 *
	 * @param event the event
	 */
	@SuppressWarnings("rawtypes")
	public void triggerEvent(Event event) {
		log.debug("Triggering event {}", event);
		_eventBus.notify(event.getClass(), event);
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
		log.debug("Triggering event {}", event);
		_eventBus.send(event.getClass(), event);
	}
	
	/**
	 * Send event.
	 *
	 * @param eventDesc the event desc
	 * @param event the event
	 * @param notifyClassListeners Notify class listeners?
	 * @param replyTo the reply to
	 */
	@SuppressWarnings("rawtypes")
	public void sendEvent(String eventDesc, EventI event, Bus replyTo, boolean notifyClassListeners) {
		_eventBus.send(eventDesc, Event.wrap(event), replyTo);
		if (notifyClassListeners) {
			_eventBus.send(event.getClass(), Event.wrap(event), replyTo);
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
		_eventBus.send(event.getClass(), Event.wrap(event), replyTo);
	}

	private final Logger log = LoggerFactory.getLogger(NrgEventService.class);
}
