package com.app.controller.eventhandler;

import com.app.controller.calculator.BasketPriceCalculator;
import com.app.controller.dao.GoodManagerDAO;
import com.app.controller.dao.OfferManagerDAO;
import com.app.model.currency.Currency;
import com.app.model.error.ShoppingBasketException;
import com.app.model.event.instance.Event;
import com.app.model.event.instance.PriceBasketEvent;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;

public class ShoppingBasketEventHandlerImpl implements IEventHandler<Event>{

    public final static Logger logger = Logger.getLogger(ShoppingBasketEventHandlerImpl.class);

    private final GoodManagerDAO goodManager;
    private final OfferManagerDAO offerManager;

    public ShoppingBasketEventHandlerImpl(GoodManagerDAO goodManager, OfferManagerDAO offerManager){
        this.goodManager = goodManager;
        this.offerManager = offerManager;
    }

    @Override
    public void processEvent(Event event){
        logger.info("Retrieved event: " + event);

        //find out which method to process the event
        if(event instanceof PriceBasketEvent)
            handlePriceBasketEvent((PriceBasketEvent)event);
        else
           logger.error(new ShoppingBasketException("Unhandled event: " + event));
    }


    private void handlePriceBasketEvent(PriceBasketEvent event){
        logger.info("Begin processing event: " + event);

        BasketPriceCalculator cal = new BasketPriceCalculator(event.getBasket(), offerManager.getOffersInPriorityOrder(), Currency.GBP);

        boolean result = false;
        try {
            result = cal.calculate(LocalDateTime.now());
        }
        catch (Exception e){  //just incase: catch any run-time error due to mathematical/operational errors
            logger.error("Something went wrong calculating the basket: " + e.getMessage(), e);
        }

        //normally would pass output back to response object. But for now, we print the output
        if(result)
            event.getOutputHandler().handleOutput(cal);
        else
            event.getOutputHandler().handleOutput("The calculator was not able to price the basket. Please check the server logs.");

    }
}
