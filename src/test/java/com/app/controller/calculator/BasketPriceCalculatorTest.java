package com.app.controller.calculator;

import com.app.model.basket.Basket;
import com.app.model.currency.Currency;
import com.app.model.error.ShoppingBasketException;
import com.app.model.product.Good;
import com.app.model.product.Offer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;


public class BasketPriceCalculatorTest {
    //Every apples are 10% off
    @Test
    public void calculate_IncludeCriteriaGoods() throws ShoppingBasketException {
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);

        Offer o = new Offer(new HashMap<Good,Integer>(){{put(apple, 1);}},
                            new HashMap<Good,Integer>(){{put(apple, 1);}},
                            Offer.DiscountType.Percentage,
                            0.1d,
                            true,
                            null,
                            null);
        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(apple, 3);}});
        Collection<Offer> offers    = Arrays.asList(o);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("Only one offer should have been applied", 1, cal.getOffersApplied().size() );
        Assert.assertEquals("Offer should be exactly 30p from 3 apples", 0.3d, cal.getOffersApplied().get(o).doubleValue(),0.001);
    }

    //Buy 1 apple, get 2 for free. (discount on good, exclude self)
    @Test
    public void calculate_DiscountOnSameGoods_ExcludeCriteria() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);
        Offer o = new Offer(new HashMap<Good,Integer>(){{put(apple, 1);}},
                            new HashMap<Good,Integer>(){{put(apple, 2);}},
                            Offer.DiscountType.Percentage,
                            1.0d,
                            false,
                            null,
                            null);
        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(apple, 3);}});
        Collection<Offer> offers    = Arrays.asList(o);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("Only one offer should have been applied", 1, cal.getOffersApplied().size() );
        Assert.assertEquals("Offer should be exactly £2, from 2 free apples", 2d, cal.getOffersApplied().get(o).doubleValue(),0.001);
    }

    //For every bread, the bread itself and 2 apples are half price. (discount on self & others goods)
    @Test
    public void calculate_DiscountOnMultipleGoods_IncludeCriteria() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);
        Good bread = mockGood(2, "Bread", Currency.GBP, 3.50);
        Offer o = new Offer(new HashMap<Good,Integer>(){{put(bread, 1);}},
                            new HashMap<Good,Integer>(){{put(bread, 1); put(apple, 2);}},
                            Offer.DiscountType.Percentage,
                            0.5d,
                            true,
                            null,
                            null);
        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(bread,3); put(apple, 50);}});
        Collection<Offer> offers    = Arrays.asList(o);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("Only one offer should have been applied", 1, cal.getOffersApplied().size() );
        Assert.assertEquals("Offer should be exactly £8.25, 3 half price bread (1.75 each) and 6 half price apples (0.5 each)",
                            8.25d, cal.getOffersApplied().get(o).doubleValue(),0.001);
    }

    @Test
    public void calculate_DiscountOnMultipleGoods_IncludeCriteria2() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);
        Good bread = mockGood(2, "Bread", Currency.GBP, 3.50);
        Offer o = new Offer(new HashMap<Good,Integer>(){{put(bread, 1);}},
                            new HashMap<Good,Integer>(){{put(bread, 1); put(apple, 2);}},
                            Offer.DiscountType.Percentage,
                            0.5d,
                            true,
                            null,
                            null);
        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(bread,50); put(apple, 3);}});
        Collection<Offer> offers    = Arrays.asList(o);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("Only one offer should have been applied", 1, cal.getOffersApplied().size() );
        Assert.assertEquals("Offer should be exactly £2.75, 1 half price bread (1.75 each) and 2 half price apples (0.5 each)",
                2.75d, cal.getOffersApplied().get(o).doubleValue(),0.001);
    }

    //Buy 2 bread and 1 Soup, get 2 apples for free. (discount on others goods not related to criteria)
    @Test
    public void calculate_DiscountOnSingleGoods_MultipleCriteria() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);
        Good bread = mockGood(2, "Bread", Currency.GBP, 3.50);
        Good soup = mockGood(3, "Soup", Currency.GBP, 2.00);

        Offer o = new Offer(new HashMap<Good,Integer>(){{put(bread, 2); put(soup, 1);}},
                            new HashMap<Good,Integer>(){{put(apple, 2);}},
                            Offer.DiscountType.Percentage,
                            1.0d,
                            true,
                            null,
                            null);
        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(bread,4); put(soup,5); put(apple, 50);}});
        Collection<Offer> offers    = Arrays.asList(o);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("Only one offer should have been applied", 1, cal.getOffersApplied().size() );
        Assert.assertEquals("Offer should be exactly £4.00, 4 apples free",
                4.00d, cal.getOffersApplied().get(o).doubleValue(),0.001);
    }

    //Buy 2 bread and 1 Soup, get 2 apples for free. (no soup bought)
    @Test
    public void calculate_NoMatchingCriteria() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);
        Good bread = mockGood(2, "Bread", Currency.GBP, 3.50);
        Good soup = mockGood(3, "Soup", Currency.GBP, 2.00);

        Offer o = new Offer(new HashMap<Good,Integer>(){{put(bread, 2); put(soup, 1);}},
                            new HashMap<Good,Integer>(){{put(apple, 2);}},
                            Offer.DiscountType.Percentage,
                            1.0d,
                            true,
                            LocalDateTime.MIN,
                            null);
        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(bread,5); put(apple, 50);}}); //no soup
        Collection<Offer> offers    = Arrays.asList(o);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("No offer should have been applied", 0, cal.getOffersApplied().size() );
    }
    //Buy 1 bread, get 2 apples free. (but only have 1 apple in basket: no discount)
    @Test
    public void calculate_NotEnoughDiscountGoods() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);
        Good bread = mockGood(2, "Bread", Currency.GBP, 3.50);

        Offer o = new Offer(new HashMap<Good,Integer>(){{put(bread, 1);}},
                            new HashMap<Good,Integer>(){{put(apple, 2);}},
                            Offer.DiscountType.Percentage,
                            1.0d,
                            true,
                            null,
                            null);
        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(bread,5); put(apple, 1);}}); //not enough apples
        Collection<Offer> offers    = Arrays.asList(o);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("No offer should have been applied", 0, cal.getOffersApplied().size() );
    }

    //No offers (null)
    @Test
    public void calculate_NoOffers() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);
        Good bread = mockGood(2, "Bread", Currency.GBP, 3.50);

        Offer o = new Offer(null,
                            null,
                            Offer.DiscountType.Percentage,
                            1.0d,
                            true,
                            LocalDateTime.MIN,
                            null);
        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(bread,5); put(apple, 1);}}); //not enough apples
        Collection<Offer> offers    = Arrays.asList(o);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("No offer should have been applied", 0, cal.getOffersApplied().size() );
    }

    //Expired offer (before)
    public void calculate_ExpiredOffers() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);
        Offer o = new Offer(new HashMap<Good,Integer>(){{put(apple, 1);}},
                            new HashMap<Good,Integer>(){{put(apple, 1);}},
                            Offer.DiscountType.Percentage,
                            1.0d,
                            true,
                            LocalDateTime.MIN,
                            LocalDateTime.now().minusDays(1));
        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(apple, 1);}}); //not enough apples
        Collection<Offer> offers    = Arrays.asList(o);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("No offer should have been applied", 0, cal.getOffersApplied().size() );
    }

    //Future offers - not started
    public void calculate_FutureOffersNotStartedYet() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);
        Offer o = new Offer(new HashMap<Good,Integer>(){{put(apple, 1);}},
                            new HashMap<Good,Integer>(){{put(apple, 1);}},
                            Offer.DiscountType.Percentage,
                            1.0d,
                            true,
                            LocalDateTime.now().plusDays(1),
                            LocalDateTime.now().plusDays(2));
        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(apple, 1);}}); //not enough apples
        Collection<Offer> offers    = Arrays.asList(o);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("No offer should have been applied", 0, cal.getOffersApplied().size());
    }

    //Multiple offers - dont collide - Every apples are 10% off, Every Bread are 10% off
    @Test
    public void calculate_MultipleOfferDontCollide_TestTotalSubTotal() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);
        Good bread = mockGood(2, "Bread", Currency.GBP, 3.00);

        Offer o = new Offer(new HashMap<Good,Integer>(){{put(apple, 1);}},
                            new HashMap<Good,Integer>(){{put(apple, 1);}},
                            Offer.DiscountType.Percentage,
                            0.1d,
                            true,
                            null,
                            null);
        Offer o2 = new Offer(new HashMap<Good,Integer>(){{put(bread, 1);}},
                            new HashMap<Good,Integer>(){{put(bread, 1);}},
                            Offer.DiscountType.Percentage,
                            0.1d,
                            true,
                            null,
                            null);

        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(apple, 3); put(bread, 2);}});
        Collection<Offer> offers    = Arrays.asList(o, o2);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("Only one offer should have been applied", 2, cal.getOffersApplied().size() );


        Assert.assertEquals("Offer should be exactly 90p from 3 apples (10p each), 2 breads (30p each)",
                            0.9d, cal.getSubTotal() - cal.getTotal(),0.001);
    }

    //Multiple offers - do collide.
    // Priority 1 = Every 5 apples are 50% off
    // Priority 2 = Every 2 apple are 10% off
    @Test
    public void calculate_MultipleOfferDoCollide_TestTotalSubTotal() throws ShoppingBasketException{
        Good apple = mockGood(1, "Apple", Currency.GBP, 1.00);

        Offer o = new Offer(new HashMap<Good,Integer>(){{put(apple, 5);}},
                new HashMap<Good,Integer>(){{put(apple, 5);}},
                Offer.DiscountType.Percentage,
                0.5d,
                true,
                LocalDateTime.now().minusDays(1),
                null);
        Offer o2 = new Offer(new HashMap<Good,Integer>(){{put(apple, 2);}},
                new HashMap<Good,Integer>(){{put(apple, 2);}},
                Offer.DiscountType.Percentage,
                0.1d,
                true,
                LocalDateTime.now().minusDays(2),
                null);

        Basket basket               = new Basket(new HashMap<Good,Integer>(){{put(apple, 13);}});
        Collection<Offer> offers    = Arrays.asList(o, o2);
        BasketPriceCalculator cal   = new BasketPriceCalculator(basket, offers, Currency.GBP);

        Assert.assertTrue("Calculator should have calculated successfully", cal.calculate(LocalDateTime.now()));

        Assert.assertEquals("Only one offer should have been applied", 2, cal.getOffersApplied().size() );


        Assert.assertEquals("Offer should be exactly £2.70 from: 10 apples (50p each), 2 apple 10% off (10p each)",
                5.2d, cal.getSubTotal() - cal.getTotal(),0.001);
    }

    private Good mockGood(int id, String name, Currency currency, double price){
        return new Good(1, name, currency, new HashMap<LocalDateTime, Double>(){{put(LocalDateTime.MIN, price);}});
    }
}
