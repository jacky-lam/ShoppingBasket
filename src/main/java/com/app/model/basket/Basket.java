package com.app.model.basket;

import com.app.model.product.Good;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 *
 * Basket holds information about the items in the basket only
 *  - Does not care about the time or pricing models
 *
 * */
public class Basket {

    private final Map<Good, Integer> basketItems;

    public Basket(Map<Good, Integer> basketItems){
        this.basketItems = basketItems;
    }

    public ImmutableMap<Good, Integer> getBasketItems() {
        return ImmutableMap.copyOf(basketItems);
    }

    //Leave equals to be pointer reference

    @Override
    public String toString(){
        return
            this.getClass().getSimpleName()
            +"={"
            + basketItems
            + "}";
    }
}
