package com.app.controller.dao;

import com.app.model.product.Good;
import com.app.model.product.Offer;
import org.apache.log4j.Logger;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 *
 * Manage the interaction of adding/changing the offer's state (currently in memory only)
 *  - relies on knowing the Good definitions.
 *  - prioritise offers in desc-order of its starting-date (Offers that starts later has higher priority).
 *
 * */
public class OfferManagerDAO {

    public final static Logger logger = Logger.getLogger(OfferManagerDAO.class);

    private final Set<Offer> offers; // in-memory cache (first item has highest priority)
    private final GoodManagerDAO goodManager;

    public OfferManagerDAO(GoodManagerDAO goodManager){
        offers = new TreeSet<>((s1, s2) -> {
            LocalDateTime s1Date = s1.getStartDate();
            LocalDateTime s2Date = s2.getStartDate();

            if(s1Date == null && s2Date != null){
                return 1;
            }
            else if(s1Date != null && s2Date == null){
                return -1;
            }
            else if(s1Date != null && s2Date != null){
                return -1 * s1Date.compareTo(s2Date);
            }
            return 0;
        });
        this.goodManager = goodManager;
    }

    public Set<Offer> getOffersInPriorityOrder() {
        return offers;
    }

    /*
     * Load all offers into memory
     * */
    public void loadAllOffers(){
        //TODO: Load data from database & pass to factory class to validate & parse the data.
        //TODO: - for now, assume data is legit

        LocalDate startOfWeek   = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endOfWeek     = startOfWeek.plusDays(7);

        logger.info("Loading all offers...");
        offers.clear();

        // Apples have a 10% discount off their normal price this week
        addOffer(new Offer( new HashMap<Good, Integer>(){{put(goodManager.getGoodByName("Apples"), 1);}},
                            new HashMap<Good, Integer>(){{put(goodManager.getGoodByName("Apples"), 1);}},
                            Offer.DiscountType.Percentage,
                            0.1d,
                            true,
                            startOfWeek.atStartOfDay(),
                            endOfWeek.atTime(LocalTime.MAX))
        );

        // Buy 2 tins of soup and get a loaf of bread for half price
        addOffer(new Offer( new HashMap<Good, Integer>(){{put(goodManager.getGoodByName("Soup"), 2);}},
                            new HashMap<Good, Integer>(){{put(goodManager.getGoodByName("Bread"), 1);}},
                            Offer.DiscountType.Percentage,
                            0.5d,
                            false,
                            null,
                            null)
        );
        logger.info("Loaded offers: " + offers);
    }

    public void addOffer(Offer o){
        offers.add(o);
    }

}
