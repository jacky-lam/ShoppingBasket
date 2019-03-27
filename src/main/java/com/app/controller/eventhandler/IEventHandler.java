package com.app.controller.eventhandler;

/**
 *
 * Standard generic interface for processing events
 *
 */
public interface IEventHandler<T> {

    void processEvent(T event);

}
