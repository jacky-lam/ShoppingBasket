package com.app.controller.dao;

import com.app.model.currency.Currency;
import com.app.model.product.Good;
import com.app.model.product.Offer;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

public class OfferManagerDAOTest {

    private final GoodManagerDAO goodManager = getMockGoodManager();

    /**
     *
     * Priority order is based on start date
     *
     */
    @Test
    public void validatePriorityOrder(){

        OfferManagerDAO offerManager = new OfferManagerDAO(goodManager);

        Map<Good,Integer> mockTargetGoods = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        //now
        Offer offerNow = new Offer(mockTargetGoods,
                                    mockTargetGoods,
                                    Offer.DiscountType.Percentage,
                                    10.0d,
                                    true,
                                    now,
                                    null);
        //back 1 day from now
        Offer offerBack1 = new Offer(mockTargetGoods,
                                    mockTargetGoods,
                                    Offer.DiscountType.Percentage,
                                    9.0d,
                                    true,
                                    now.minusDays(1),
                                    null);
        //forward 1 day from now
        Offer offerPlus1 = new Offer(mockTargetGoods,
                                    mockTargetGoods,
                                    Offer.DiscountType.Percentage,
                                    11.0d,
                                    true,
                                    now.plusDays(1),
                                    null);
        //back 2 day from now
        Offer offerBack2 = new Offer(mockTargetGoods,
                                    mockTargetGoods,
                                    Offer.DiscountType.Percentage,
                                    8.0d,
                                    true,
                                    now.minusDays(2),
                                    null);
        //null start date
        Offer offerNull = new Offer(mockTargetGoods,
                                    mockTargetGoods,
                                    Offer.DiscountType.Percentage,
                                    0.0d,
                                    true,
                                    null,
                                    null);
        offerManager.addOffer(offerNow);
        offerManager.addOffer(offerBack1);
        offerManager.addOffer(offerPlus1);
        offerManager.addOffer(offerBack2);
        offerManager.addOffer(offerNull);

        Set<Offer> order = offerManager.getOffersInPriorityOrder();
        List<String> actual = order.stream()
                                .map(x->x.getStartDate() != null ? x.getStartDate().toString() : "NULL")
                                .collect(Collectors.toList());

        List<String> expected = new ArrayList(){{
            add(offerPlus1.getStartDate().toString());
            add(offerNow.getStartDate().toString());
            add(offerBack1.getStartDate().toString());
            add(offerBack2.getStartDate().toString());
            add("NULL");
        }};

        Assert.assertEquals("Priority order based on start-date", expected, actual);
    }



    private GoodManagerDAO getMockGoodManager(){
        GoodManagerDAO goodManager = new GoodManagerDAO();
        goodManager.addGood(new Good(
                1,
                "Soup",
                Currency.GBP,
                new HashMap<LocalDateTime, Double>(){{
                    put(LocalDateTime.now(), 1.1d);
                }}
        ));
        goodManager.addGood(new Good(
                2,
                "Bread",
                Currency.USD,
                new HashMap<LocalDateTime, Double>(){{
                    put(LocalDateTime.now(), 1.1d);
                }}
        ));
        return goodManager;
    }
}
