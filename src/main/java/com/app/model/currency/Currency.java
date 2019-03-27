package com.app.model.currency;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 * Currency definition.
 * - Alternatively, could have used Joda-Money and lose this class
 *
 * */
public enum Currency {

    GBP("Great British Pounds", "£", "p"),
    USD("United States Dollar", "$", null); //did this for self testing sake

    private static DecimalFormat df = new DecimalFormat("###,###.00");
    private String name;
    private String currencySymbol;
    private String decimalCurrencySymbol;
    private Currency(String name, String currencySymbol, String decimalCurrencySymbol){
        this.name = name;
        this.currencySymbol = currencySymbol;
        this.decimalCurrencySymbol = decimalCurrencySymbol;
    }

    /**
     * Format the number into a humanly presentable number.
     * - If no decimal-currency was presented, must always show the original
     *
     * For example:
     * - GBP has penny symbol. Possible outputs: £1,234.56|£1.00|0p|-5p|-£1.00|-£1,234.56
     * - USD has no decimal specific symbols. Possible outputs: $1,234.56|$1.00|$0|-$0.05|-$1.00|-£1,234.56
     *
     * */
    public String format(double amount){

        df.setRoundingMode(RoundingMode.CEILING); //always round up penny

        boolean isNegative          = amount < 0;
        amount                      = Math.abs(amount);
        boolean isLessThanOne       = amount < 1;
        String amountStr            = df.format(amount);

        // show special case zero format
        if(amount == 0.00)
            return    (decimalCurrencySymbol != null ? "" : currencySymbol)
                    + 0
                    + (decimalCurrencySymbol != null ? decimalCurrencySymbol : "");

        if(decimalCurrencySymbol != null){
            if(amount < 0.1)
                amountStr = amountStr.substring(2); //handles displaying 1p (remove .0)
            else if(isLessThanOne)
                amountStr = amountStr.substring(1); //handles displaying 20p (remove .)
        }

        return (isNegative ? "-" : "")
                + (!isLessThanOne || decimalCurrencySymbol == null ? currencySymbol : "") //if no decimal-symbol was set: always use normal symbol
                + (isLessThanOne && decimalCurrencySymbol == null ? "0" : "") //put leading 0 for situations like $0.01
                + amountStr
                + (isLessThanOne && decimalCurrencySymbol != null ? decimalCurrencySymbol : "") //show decimal-symbol if < ABS(1)
        ;
    }
}
