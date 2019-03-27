package com.app.model.currency;


import org.junit.Assert;
import org.junit.Test;

public class CurrencyTest {

    @Test
    public void testAllWithDecimalSymbol() {
        double[] input = {
            999999999876.321
            ,1234.521
            ,1234.55
            ,123.99
            ,1
            ,0.1
            ,0.02
            ,0
            ,-0.05
            ,-0.1
            ,-1
            ,-1234.03
            ,-1234.521
            ,-987987987987.9999
        };
        String[] expected = {
                "£999,999,999,876.33"
                ,"£1,234.53"
                ,"£1,234.55"
                ,"£123.99"
                ,"£1.00"
                ,"10p"
                ,"2p"
                ,"0p"
                ,"-5p"
                ,"-10p"
                ,"-£1.00"
                ,"-£1,234.03"
                ,"-£1,234.53"
                ,"-£987,987,987,988.00"
        };

        for(int i=0; i < input.length; i++){
            String outputStr    = Currency.GBP.format(input[i]);
            String expectedStr  = expected[i];
            Assert.assertEquals("Not correct format returned for GBP",
                                expectedStr,
                                outputStr);
        }
    }

    @Test
    public void testAllWithoutDecimalSymbol() {
        double[] input = {
                999999999876.321
                ,1234.521
                ,1234.55
                ,123.99
                ,1
                ,0.1
                ,0.02
                ,0
                ,-0.05
                ,-0.1
                ,-1
                ,-1234.03
                ,-1234.521
                ,-987987987987.9999
        };
        String[] expected = {
                "$999,999,999,876.33"
                ,"$1,234.53"
                ,"$1,234.55"
                ,"$123.99"
                ,"$1.00"
                ,"$0.10"
                ,"$0.02"
                ,"$0"
                ,"-$0.05"
                ,"-$0.10"
                ,"-$1.00"
                ,"-$1,234.03"
                ,"-$1,234.53"
                ,"-$987,987,987,988.00"
        };

        for(int i=0; i < input.length; i++){
            String outputStr    = Currency.USD.format(input[i]);
            String expectedStr  = expected[i];
            Assert.assertEquals("Not correct format returned for USD",
                    expectedStr,
                    outputStr);
        }
    }
}