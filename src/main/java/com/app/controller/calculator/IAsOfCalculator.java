package com.app.controller.calculator;

import com.app.model.error.ShoppingBasketException;

import java.time.LocalDateTime;

/**
 * Calculator which calculates based upon specific date-time (temporal date involved)
 * */
public interface IAsOfCalculator {
    boolean calculate(LocalDateTime asOf) throws ShoppingBasketException;
}
