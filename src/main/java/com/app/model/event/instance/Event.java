package com.app.model.event.instance;

import com.app.model.event.IEventType;
import com.app.model.io.OutputHandler;

public abstract class Event {

    protected final IEventType eventType;
    protected final OutputHandler outputHandler;

    public Event(IEventType eventType, OutputHandler outputHandler){
        this.eventType      = eventType;
        this.outputHandler  = outputHandler;
    }

    public IEventType getEventType() {
        return eventType;
    }

    public OutputHandler getOutputHandler() {
        return outputHandler;
    }
}
