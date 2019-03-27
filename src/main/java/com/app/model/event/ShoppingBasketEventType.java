package com.app.model.event;

/**
 *
 * All the different types of business events for Shopping Basket application
 *
 * */
public enum ShoppingBasketEventType implements IEventType {

    EXIT("exit \t\t\t Exit system"),
    PRICEBASKET("pricebasket \t Produce the total cost from the given list of goods. Arguments: [item1, item2, item3....]");

    String description;
    private ShoppingBasketEventType(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }
}
