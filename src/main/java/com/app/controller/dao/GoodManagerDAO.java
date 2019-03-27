package com.app.controller.dao;

import com.app.model.currency.Currency;
import com.app.model.product.Good;
import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * Manage the interaction of adding/changing the good's state (currently in memory only)
 *
 * */
public class GoodManagerDAO {

    public final static Logger logger = Logger.getLogger(GoodManagerDAO.class);

    private Map<String,Good> goodsByName; // in-memory cache

    public GoodManagerDAO(){
        goodsByName = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    /*
    * Load all goods into memory
    * */
    public void loadAllGoods(){
        //TODO: Load data from database & pass to factory class to validate & parse the data.
        //TODO: - for now, assume data is legit

        logger.info("Loading all goods...");
        Set<Good> goodSet = new HashSet();
        goodSet.add(new Good(1, "Soup",   Currency.GBP, new HashMap<LocalDateTime, Double>(){{put(LocalDateTime.MIN, 0.65);}}));
        goodSet.add(new Good(2, "Bread",  Currency.GBP, new HashMap<LocalDateTime, Double>(){{put(LocalDateTime.MIN, 0.80);}}));
        goodSet.add(new Good(3, "Milk",   Currency.GBP, new HashMap<LocalDateTime, Double>(){{put(LocalDateTime.MIN, 1.30);}}));
        goodSet.add(new Good(4, "Apples", Currency.GBP, new HashMap<LocalDateTime, Double>(){{put(LocalDateTime.MIN, 1.00);}}));
        logger.info("Loaded goods: " + goodSet);

        goodsByName.clear();
        for(Good g: goodSet)
            addGood(g);
    }

    public void addGood(Good g){
        goodsByName.put(g.getName(), g);
    }

    public Good getGoodByName(String name){
        return goodsByName.get(name);
    }

    public Collection<String> getAllGoodNames(){
        return new ArrayList(goodsByName.keySet());
    }
}
