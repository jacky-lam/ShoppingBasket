package com.app.model.io;

import com.app.controller.eventhandler.IEventHandler;
import com.app.model.event.parser.IEventParser;
import org.apache.log4j.Logger;

import java.util.Scanner;

/**
 *
 * Generic Input scanner:
 * 1. Handles consuming the user's input via a Scanner.
 * 2. Parses the input into an array of strings, and feeds it into the parser to generate an object.
 * 3. The object is then processed by the event-handler.
 *
 * */
public class InputScanner implements Runnable {

    public final static Logger logger = Logger.getLogger(InputScanner.class);

    private final Scanner inputScanner;
    private final IEventHandler eventHandler;
    private final IEventParser eventParser;
    private final String welcomeMessage;
    private final String exitCommand;

    public InputScanner(IEventParser eventParser, IEventHandler eventHandler, String exitCommand, String welcomeMessage){
        inputScanner        = new Scanner(System.in);
        this.eventParser    = eventParser;
        this.eventHandler   = eventHandler;
        this.exitCommand    = exitCommand;
        this.welcomeMessage = welcomeMessage;
    }

    @Override
    public void run() {
        logger.info("Input scanner is beginning...");

        printWelcomeMessage();
        String line = fetchNextInput();
        while(!line.toUpperCase().startsWith(exitCommand)) {

            logger.info("Input retrieved: " + line);

            Object event = null;
            try {
                String[] args = parseInputIntoArray(line);
                event = eventParser.parseStringArg(args);
            } catch (Exception e) {
                logger.error("Failed to parse input: " + line, e);
                outputMessage("Error: " + e.getMessage());
            }

            if(event != null)
                eventHandler.processEvent(event);

            line = fetchNextInput();
        }

        logger.info("Terminating input scanner...");
    }

    private String fetchNextInput(){
        System.out.print("Input:");
        return inputScanner.nextLine();
    }

    private void printWelcomeMessage(){
        if(welcomeMessage != null)
            outputMessage(welcomeMessage);
    }

    private String[] parseInputIntoArray(String inputLine){
        String cleanedInput = inputLine.trim().replaceAll("\\s+", " ");
        return cleanedInput.split("\\s");
    }

    //Output message to user (assuming user does not see logger)
    private void outputMessage(String message){
        System.out.println(message);
    }
}
