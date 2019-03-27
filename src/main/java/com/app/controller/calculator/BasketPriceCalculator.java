package com.app.controller.calculator;

import com.app.model.basket.Basket;
import com.app.model.error.ShoppingBasketException;
import com.app.model.product.Good;
import com.app.model.product.Offer;
import com.app.model.currency.Currency;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Calculator to calculate the prices & offers applied to basket at specific point in time.
 * - Can rely on knowledge of the offer definitions.
 *
 * */
public class BasketPriceCalculator implements IAsOfCalculator{

    public final static Logger logger = Logger.getLogger(BasketPriceCalculator.class);

    private final Basket basket;
    private final Collection<Offer> offersInPriorityOrder;

    private double subTotal;                    //Total without offers applied
    private double total;                       //Total with offers applied
    private Map<Offer, Double> offersApplied;   //Key = Offer applied, Value = Total discounted
    private Currency currencyOutput;            //Currency the numbers represents (i.e. subtotal and total)
    /**
     * @param basket basket holding items to be calculated
     * @param offersInPriorityOrder known offers to be applied to items in basket. (Null = no offers)
     * */
    public BasketPriceCalculator(Basket basket, Collection<Offer> offersInPriorityOrder, Currency currencyOutput){
        this.basket                 = basket;
        this.offersInPriorityOrder  = offersInPriorityOrder;
        this.currencyOutput         = currencyOutput;
        this.offersApplied          = new HashMap<>();
        resetParameters();
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getTotal() {
        return total;
    }

    public Map<Offer, Double> getOffersApplied() {
        return offersApplied;
    }

    public Currency getCurrencyOutput() {
        return currencyOutput;
    }

    private void resetParameters(){
        total = 0;
        subTotal = 0;
        offersApplied.clear();
    }

    @Override
    public boolean calculate(LocalDateTime asOf) throws ShoppingBasketException {
        logger.info("Begin calculating basket price as-of " + asOf + ". From: " + this.toString());
        resetParameters();

        //calculate subtotal & clone basket (play with this later later)
        Map<Good, Integer> tmpBasket        = new HashMap<>();
        Map<Good, Integer> basketItems      = basket.getBasketItems();
        for(Good good : basketItems.keySet()){
            int quantity    = basketItems.get(good).intValue();
            subTotal        += good.getPriceAsOf(asOf).doubleValue() * ((double)quantity);
            tmpBasket.put(good, quantity);
        }

        total = subTotal + 0; //initial total

        //Go through all offers in priority order
        for (Offer offer: offersInPriorityOrder) {

            if(!dateWithinOfferPeriod(offer.getStartDate(), offer.getExpiryDate(), asOf)) //not within offer time-frame
                continue;

            int numOffersOccurred = getNumberOfTimesOfferOccurred(offer, tmpBasket);
            if(numOffersOccurred > 0){ //begin applying offer

                logger.debug("Found applicable offer: " + offer + ". Number of times: " + numOffersOccurred);
                Offer.DiscountType discountType = offer.getDiscountType();
                double discountAmount           = offer.getDiscountAmount();

                //Calculate discount amount from offer
                double offerAmountDiscounted = 0;
                Map<Good,Integer> discountedGoods = offer.getDiscountedGoods();
                for(Good goodDiscounting : discountedGoods.keySet()){

                    int discountQty         = numOffersOccurred * discountedGoods.get(goodDiscounting).intValue();

                    if(discountType == Offer.DiscountType.Percentage){
                        offerAmountDiscounted += goodDiscounting.getPriceAsOf(asOf) * discountAmount * discountQty;
                    }

                    int currentQtyInBasket  = tmpBasket.get(goodDiscounting).intValue();
                    if(currentQtyInBasket < discountQty)
                        throw new ShoppingBasketException("Error in cal: We are discounting too much that basket cannot support!"
                                                        +"\n Good: " + goodDiscounting.getName()
                                                        +"\n In Basket: " + currentQtyInBasket
                                                        +"\n Discounting Amount: " + discountQty); //there is a problem with code if reach here

                    //reduce basket-item because they've applied offer
                    tmpBasket.put(goodDiscounting, currentQtyInBasket - discountQty);
                }

                //record discount applied from offer
                logger.debug("Offer total: " + offerAmountDiscounted);
                offersApplied.put(offer, offerAmountDiscounted);

                //update total
                total -= offerAmountDiscounted;

                logger.debug("Temp Basket state: "+ tmpBasket);
            }
        }
        logger.info("Finished calculating basket price as-of " + asOf + ". From: " + this.toString());
        return true;
    }

    private boolean dateWithinOfferPeriod(LocalDateTime offerStart, LocalDateTime offerEnd, LocalDateTime asOf){

        boolean withinStart = offerStart == null || !offerStart.isAfter(asOf);
        boolean withinEnd   = offerEnd == null || !offerEnd.isBefore(asOf);

        return withinStart && withinEnd;
    }

    private int getNumberOfTimesOfferOccurred(Offer offer, Map<Good, Integer> tmpBasket){

        Map<Good,Integer> criteriaGoods = offer.getGoodCriteria();
        Map<Good,Integer> discountedGoods = offer.getDiscountedGoods();
        boolean canApplyDiscountOnCriteriaGoods = offer.canApplyDiscountOnCriteriaGoods();

        //Check number of times basket matches criteria set
        int smallestNumTimesMatchedCriteria = getTimesOfferOccurred(tmpBasket, canApplyDiscountOnCriteriaGoods,
                                              criteriaGoods, discountedGoods);

        //Check number of times basket matches discount set
        if(smallestNumTimesMatchedCriteria > 0){

            //only look at goods which we haven't seen yet in criteria
            List<Good> uncheckedGoodsInDiscountKey = discountedGoods.keySet().stream()
                                                    .filter(x -> !criteriaGoods.containsKey(x))
                                                    .collect(Collectors.toList());
            if(uncheckedGoodsInDiscountKey.size() > 0){
                Map uncheckedGoodsInDiscount = new HashMap();
                for(Good g : uncheckedGoodsInDiscountKey)
                    uncheckedGoodsInDiscount.put(g, discountedGoods.get(g));

                int smallestNumTimesMatchedGoods = getTimesOfferOccurred(tmpBasket, canApplyDiscountOnCriteriaGoods,
                                                   uncheckedGoodsInDiscount, new HashMap());
                smallestNumTimesMatchedCriteria = Math.min(smallestNumTimesMatchedCriteria, smallestNumTimesMatchedGoods);
            }
        }
        return smallestNumTimesMatchedCriteria;
    }


    private int getTimesOfferOccurred(Map<Good, Integer> tmpBasket, boolean excludeA, Map<Good,Integer> itemsA, Map<Good,Integer> itemsB){

        int smallestNumTimesMatchedCriteria = -1;

        for(Good goodForA : itemsA.keySet()){

            int amountA             = itemsA.get(goodForA).intValue();
            int amountB             = itemsB.containsKey(goodForA) ? itemsB.get(goodForA) : 0;
            int amountRequired      = Math.max(amountA, amountB);

            if(amountRequired == 0) //redundant requirement
                continue;

            int basketAmount        = tmpBasket.containsKey(goodForA) ? tmpBasket.get(goodForA) : 0;

            int numTimesMatchedCriteria;
            if(basketAmount != 0){
                if(excludeA)
                    numTimesMatchedCriteria = basketAmount / amountRequired;
                else
                    numTimesMatchedCriteria = basketAmount / (amountA + amountB);

                if(smallestNumTimesMatchedCriteria == -1) //first time
                    smallestNumTimesMatchedCriteria = numTimesMatchedCriteria;
                else
                    smallestNumTimesMatchedCriteria = Math.min(numTimesMatchedCriteria,smallestNumTimesMatchedCriteria);
            }
            else
                smallestNumTimesMatchedCriteria = 0;

            if(smallestNumTimesMatchedCriteria == 0)
                break;
        }
        return smallestNumTimesMatchedCriteria;
    }

    @Override
    public String toString(){
        return
            this.getClass().getSimpleName()
            +"={"
            + basket
            + ",offersInPriorityOrder:"+offersInPriorityOrder
            + ",subTotal:"+subTotal
            + ",total:"+total
            + ",offersApplied:"+offersApplied
            + "}";
    }
}
