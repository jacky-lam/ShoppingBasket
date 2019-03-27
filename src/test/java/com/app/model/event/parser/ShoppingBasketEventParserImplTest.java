package com.app.model.event.parser;

import com.app.controller.dao.GoodManagerDAO;
import com.app.model.currency.Currency;
import com.app.model.event.instance.Event;
import com.app.model.event.instance.PriceBasketEvent;
import com.app.model.event.ShoppingBasketEventType;
import com.app.model.product.Good;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ShoppingBasketEventParserImplTest {

    private final LocalDateTime nowDate             = LocalDateTime.now();
    private final GoodManagerDAO goodManager        = getMockGoodManager();
    private final IEventParser<Event> eventParser   = new ShoppingBasketEventParserImpl(goodManager);

    @Test
    public void parseStringArg_ValidPriceBasketWithOneGood() throws Exception{
        String[] arg    = {ShoppingBasketEventType.PRICEBASKET.toString(), "Soup"};
        Event event     = eventParser.parseStringArg(arg);

        Assert.assertTrue("Should have created instance of " + PriceBasketEvent.class.getSimpleName(),
                            event instanceof PriceBasketEvent);

        PriceBasketEvent pbEvent        = (PriceBasketEvent) event;
        Map<Good, Integer> basketItems  = pbEvent.getBasket().getBasketItems();

        Assert.assertEquals("Should have one type of good in basket",
                            1,
                            basketItems.size());
        for(Good good : basketItems.keySet()) {
            Assert.assertEquals("There should only be one Soup in basket",
                    "Soup,1",
                    good.getName()+","+basketItems.get(good).intValue()
                    );
        }
    }

    @Test
    public void parseStringArg_ValidPriceBasketWithDuplicateGoods() throws Exception{
        String[] arg    = {ShoppingBasketEventType.PRICEBASKET.toString(), "Bread", "Bread"};
        Event event     = eventParser.parseStringArg(arg);

        Assert.assertTrue("Should have created instance of " + PriceBasketEvent.class.getSimpleName(),
                            event instanceof PriceBasketEvent);

        PriceBasketEvent pbEvent        = (PriceBasketEvent) event;
        Map<Good, Integer> basketItems  = pbEvent.getBasket().getBasketItems();

        Assert.assertEquals("Should have 1 types of goods in basket",
                            1,
                            basketItems.size());
        for(Good good : basketItems.keySet()) {
            Assert.assertEquals("There should be two Bread in basket",
                    "Bread,2",
                    good.getName()+","+basketItems.get(good).intValue()
            );
        }
    }

    @Test
    public void parseStringArg_ValidPriceBasketWithMultipleGoods() throws Exception{
        String[] arg    = {ShoppingBasketEventType.PRICEBASKET.toString(), "Soup", "Bread", "Soup", "Bread", "Soup"};
        Event event     = eventParser.parseStringArg(arg);

        Assert.assertTrue("Should have created instance of " + PriceBasketEvent.class.getSimpleName(),
                            event instanceof PriceBasketEvent);

        PriceBasketEvent pbEvent        = (PriceBasketEvent) event;
        Map<Good, Integer> basketItems  = pbEvent.getBasket().getBasketItems();

        Assert.assertEquals("Should have 2 types of goods in basket",
                            2,
                            basketItems.size());

        for(Good good : basketItems.keySet()) {
            switch(good.getName()){
                case "Soup":
                    Assert.assertEquals("There should be three Soups in basket",
                            "Soup,3",
                            good.getName()+","+basketItems.get(good).intValue()
                    );
                    break;
                case "Bread":
                    Assert.assertEquals("There should be two Breads in basket",
                            "Bread,2",
                            good.getName()+","+basketItems.get(good).intValue()
                    );
                    break;
                default:
                    throw new Exception("Unexpected good found in basket: " + good);
            }
        }
    }

    @Test
    public void parseStringArg_ValidPriceBasketWitNoGoods() throws Exception{
        String[] arg    = {ShoppingBasketEventType.PRICEBASKET.toString()};
        Event event     = eventParser.parseStringArg(arg);

        Assert.assertTrue("Should have created instance of " + PriceBasketEvent.class.getSimpleName(),
                            event instanceof PriceBasketEvent);

        PriceBasketEvent pbEvent        = (PriceBasketEvent) event;
        Map<Good, Integer> basketItems  = pbEvent.getBasket().getBasketItems();
        Assert.assertEquals("Should have 0 items in basket",
                            0,
                            basketItems.size());
    }

    @Test(expected = Exception.class)
    public void parseStringArg_InvalidCommand() throws Exception{
        String[] arg    = {"SomethingInvalid"};
        eventParser.parseStringArg(arg);
    }

    @Test(expected = Exception.class)
    public void parseStringArg_NullArgs() throws Exception{
        eventParser.parseStringArg(null);
    }

    private GoodManagerDAO getMockGoodManager(){
        GoodManagerDAO goodManager = new GoodManagerDAO();
        goodManager.addGood(new Good(
                1,
                "Soup",
                Currency.GBP,
                new HashMap<LocalDateTime, Double>(){{
                    put(nowDate, 1.1d);
                }}
        ));
        goodManager.addGood(new Good(
                2,
                "Bread",
                Currency.USD,
                new HashMap<LocalDateTime, Double>(){{
                    put(nowDate, 1.1d);
                }}
        ));
        return goodManager;
    }
}
