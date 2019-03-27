package com.app.model.event.instance;

import com.app.model.basket.Basket;
import com.app.model.event.ShoppingBasketEventType;
import com.app.model.io.OutputHandler;
import com.app.model.product.Good;

import java.util.Map;

public class PriceBasketEvent extends Event {

    private final Basket basket;

    public PriceBasketEvent(OutputHandler outputHandler, Map<Good, Integer> basketItems) {
        super(ShoppingBasketEventType.PRICEBASKET, outputHandler);
        this.basket = new Basket(basketItems);
    }

    public Basket getBasket() {
        return basket;
    }

    //Leave equals to be pointer reference

    @Override
    public String toString(){
        return
            this.getClass().getSimpleName()
            +"={"
            + "eventType:"+eventType
            + ","+ basket
            + "}";
    }
}
