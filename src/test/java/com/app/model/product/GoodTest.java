package com.app.model.product;

import com.app.model.currency.Currency;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;

public class GoodTest {

    private final LocalDateTime nowDate     = LocalDateTime.now();
    private final double nowPrice           = 1.5;
    private final LocalDateTime futureDate  = nowDate.plusDays(1);
    private final double futurePrice        = 3.55;

    private final Good goodMock = new Good(
        1,
        "Soup",
        Currency.GBP,
        new HashMap<LocalDateTime, Double>(){{
            put(nowDate, nowPrice);
            put(futureDate, futurePrice);
        }});

    @Test
    public void getPriceAsOf_NowDate(){
        Double price = goodMock.getPriceAsOf(nowDate);
        Assert.assertEquals(
                            nowPrice,
                            price.doubleValue(),
                            0.009d);
    }

    @Test
    public void getPriceAsOf_FutureDate(){
        Double price = goodMock.getPriceAsOf(futureDate);
        Assert.assertEquals(
                futurePrice,
                price.doubleValue(),
                0.009d);
    }

    @Test
    public void getPriceAsOf_BetweenDate_CloserToStart(){
        Double price = goodMock.getPriceAsOf(nowDate.plusSeconds(1));
        Assert.assertEquals(
                nowPrice,
                price.doubleValue(),
                0.009d);
    }

    @Test
    public void getPriceAsOf_BetweenDate_CloserToEnd(){
        Double price = goodMock.getPriceAsOf(futureDate.minusSeconds(1));
        Assert.assertEquals(
                nowPrice,
                price.doubleValue(),
                0.009d);
    }

    @Test
    public void getPriceAsOf_AfterFutureDate(){
        Double price = goodMock.getPriceAsOf(futureDate.plusSeconds(1));
        Assert.assertEquals(
                futurePrice,
                price.doubleValue(),
                0.009d);
    }

    @Test
    public void getPriceAsOf_BeforeNowDate(){
        Double price = goodMock.getPriceAsOf(nowDate.minusSeconds(1));
        Assert.assertEquals(null, price);
    }

    @Test(expected = Exception.class)
    public void getPriceAsOf_Null(){
        goodMock.getPriceAsOf(null);
    }
}
