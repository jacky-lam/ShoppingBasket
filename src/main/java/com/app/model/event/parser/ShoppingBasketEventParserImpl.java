package com.app.model.event.parser;

import com.app.controller.dao.GoodManagerDAO;
import com.app.model.error.ShoppingBasketException;
import com.app.model.event.instance.Event;
import com.app.model.event.instance.PriceBasketEvent;
import com.app.model.event.ShoppingBasketEventType;
import com.app.model.io.OutputHandler;
import com.app.model.product.Good;

import java.util.*;

/**
 *  Shopping Basket Event Parser
 *  - relies on knowing the Good definitions
 *
 * */
public class ShoppingBasketEventParserImpl implements IEventParser<Event> {

    private final GoodManagerDAO goodManager;

    public ShoppingBasketEventParserImpl(GoodManagerDAO goodManager){
        this.goodManager = goodManager;
    }

    @Override
    public Event parseStringArg(String[] inputArgs) throws ShoppingBasketException {

        if(inputArgs == null)
            throw new ShoppingBasketException("Cannot parse null arguments");
        if(inputArgs.length == 0)
            throw new ShoppingBasketException("Cannot parse 0 arguments");

        ShoppingBasketEventType eventCommand = null;
        try {
            eventCommand = ShoppingBasketEventType.valueOf(inputArgs[0].toUpperCase());
        }
        catch (Exception e){
            throw new ShoppingBasketException("Invalid command '" + inputArgs[0] +"'."
                                            +" Valid commands " + Arrays.asList(ShoppingBasketEventType.values()));
        }

        if(eventCommand != null){
            switch(eventCommand){
                case PRICEBASKET:
                    return createPriceBasketEvent(inputArgs);
                case EXIT:
                    return null; //shouldn't get here (could have created an event to handle this)
            }
        }
        return null;
    }

    private PriceBasketEvent createPriceBasketEvent(String[] args) throws ShoppingBasketException{

        OutputHandler output = new OutputHandler(); //new output handler
        Map<Good, Integer> items = new HashMap();

        if(args != null){
            for(int i=1; i < args.length; i++){
                String itemName = args[i];

                Good g = itemName != null ? goodManager.getGoodByName(itemName) : null;
                if(g == null)
                    throw new ShoppingBasketException("Unrecognised item at index " + i +" '" + itemName + "'."
                                                    + " Available items: " + goodManager.getAllGoodNames());

                int prevValue = items.containsKey(g) ? items.get(g).intValue() : 0;
                items.put(g, prevValue + 1);
            }
        }

        return new PriceBasketEvent(output, items);
    }
}
