package com.app;

import com.app.controller.dao.GoodManagerDAO;
import com.app.controller.dao.OfferManagerDAO;
import com.app.controller.eventhandler.ShoppingBasketEventHandlerImpl;
import com.app.controller.eventhandler.IEventHandler;
import com.app.model.event.parser.ShoppingBasketEventParserImpl;
import com.app.model.event.parser.IEventParser;
import com.app.model.event.ShoppingBasketEventType;
import com.app.model.io.InputScanner;
import com.google.inject.*;

/**
 *
 * Personally prefer to have all the injection dependency managed in one file; easier to maintain
 *
 * */
public class ShoppingBasketAssemblyModule implements Module {

    public final static String BIND_NAME = "SHOPPING_BASKET";

    public ShoppingBasketAssemblyModule(){

    }

    @Override
    public void configure(Binder binder) {
        binder.bind(String.class).toInstance(BIND_NAME);
    }

    @Provides
    @Singleton
    @Inject
    public GoodManagerDAO goodManagerDAO() {
        GoodManagerDAO gm = new GoodManagerDAO();
        gm.loadAllGoods();
        return gm;
    }

    @Provides
    @Singleton
    @Inject
    public OfferManagerDAO offerManagerDAO(GoodManagerDAO goodManager) {
        OfferManagerDAO om = new OfferManagerDAO(goodManager);
        om.loadAllOffers();
        return om;
    }

    @Provides
    @Singleton
    @Inject
    public IEventHandler eventHandler(GoodManagerDAO goodManager, OfferManagerDAO offerManager) {
        return new ShoppingBasketEventHandlerImpl(goodManager, offerManager);
    }

    @Provides
    @Singleton
    @Inject
    public IEventParser eventParser(GoodManagerDAO goodManager) {
        return new ShoppingBasketEventParserImpl(goodManager);
    }

    @Provides
    @Singleton
    @Inject
    public InputScanner inputScanner(IEventParser eventParser, IEventHandler eventHandler) {

        //message could be persisted somewhere, for now hardcoded
        String welcomeMessage = "Welcome to the shopping basket system!\n"
            +"Please enter the commands you wish the system to process.\n"
            +"\tAvailable commands:\n"
            + "\t" + ShoppingBasketEventType.PRICEBASKET.getDescription() +"\n"
            + "\t" + ShoppingBasketEventType.EXIT.getDescription()
            ;

        return new InputScanner(eventParser, eventHandler, ShoppingBasketEventType.EXIT.toString(), welcomeMessage);
    }

}
