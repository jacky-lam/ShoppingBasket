package com.app.model.event.parser;

/**
 *
 * Handles parsing args into business event objects
 *
 * */
public interface IEventParser<T> {

    T parseStringArg(String[] args) throws Exception;

}
