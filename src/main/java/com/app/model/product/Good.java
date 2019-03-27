package com.app.model.product;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import com.app.model.currency.Currency;
/**
 *
 * Representation model/definition of the goods being sold.
 *
 * - The ID represents the definition of a good (not instance of goods in basket).
 * - There can only be one unique ID per definition of good.
 * - Prices are temporal; could change over time.
 *
 * */
public class Good {

    private final int id;
    private final String name;
    private final Map<LocalDateTime, Double> temporalPrices;
    private final Currency priceCurrency;

    public Good(int id, String name, Currency priceCurrency, Map<LocalDateTime, Double> temporalPriceMap){
        this.id = id;
        this.name = name;
        this.priceCurrency = priceCurrency;
        this.temporalPrices = new TreeMap(temporalPriceMap); //sorted keys
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * @param asOf Get price of good at specific time
     * @return returns the price of good at the specific time. Will return null if no price was set, date is before earliest known price
     * */
    public Double getPriceAsOf(LocalDateTime asOf) {

        if(temporalPrices.size() == 0)
            return null;

        LocalDateTime[] orderedKey = temporalPrices.keySet().toArray(new LocalDateTime[temporalPrices.size()]);

        //improve performance by checking if date is closer to first or last of orderedKeys, and iterate from there
        Duration deltaStart = Duration.between(orderedKey[0], asOf).abs();
        Duration deltaEnd   = Duration.between(orderedKey[orderedKey.length-1], asOf).abs();
        boolean closerToEnd = deltaStart.compareTo(deltaEnd) != -1;

        int indexWant = -1;
        if(closerToEnd){

            //iterate backwards
            for(int i=orderedKey.length-1; i >= 0; i--){
                LocalDateTime key = orderedKey[i];
                if(key.isBefore(asOf) || key.equals(asOf)){
                    indexWant = i;
                    break;
                }
            }
        }
        else{

            //iterate forward
            for(int i=0; i < orderedKey.length; i++){
                LocalDateTime key = orderedKey[i];
                if(key.isAfter(asOf))
                    break;

                indexWant = i;
            }
        }

        if(indexWant == -1)
            return null;
        else
            return temporalPrices.get(orderedKey[indexWant]);
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    @Override
    public boolean equals(Object thatObj){

        if(!(thatObj instanceof Good))
            return false;

        Good that = (Good) thatObj;
        return that.getId() == this.getId();
    }

    @Override
    public String toString(){
        return
            this.getClass().getSimpleName()
            +"={"
            + "id:"+id
            + ",name:"+name
            + ",priceCurrency:"+priceCurrency
            + ",temporalPrices:"+temporalPrices
            + "}";
    }
}
