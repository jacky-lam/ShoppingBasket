package com.app.model.io;

import com.app.controller.calculator.BasketPriceCalculator;
import com.app.model.currency.Currency;
import com.app.model.product.Good;
import com.app.model.product.Offer;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

/**
 *
 * Placeholder class that manages outputting data to the user.
 * - For now just output to console.
 *
 * TODO: in future we could have different types of outputs.
 *
 * */
public class OutputHandler {

    private static final DecimalFormat df = new DecimalFormat("#.####"); //show maximum 4 dp

    public OutputHandler(){
        df.setRoundingMode(RoundingMode.CEILING);
    }

    public void handleOutput(String msg) {
        System.out.println(msg);
    }

    public void handleOutput(BasketPriceCalculator cal) {

        StringBuilder sb = new StringBuilder();
        Currency currencyOutput = cal.getCurrencyOutput();

        sb.append("Subtotal: ").append(currencyOutput.format(cal.getSubTotal())).append("\n");

        Map<Offer, Double> offerApplied = cal.getOffersApplied();
        for(Offer offer : offerApplied.keySet()){

            //offer description
            boolean isFirst = true;
            Map<Good,Integer> discountedGoods = offer.getDiscountedGoods();
            for(Good good : discountedGoods.keySet()){
                if(!isFirst)
                    sb.append(", ");
                sb.append(good.getName());
                isFirst = false;
            }

            //offer discount
            switch(offer.getDiscountType()){
                case Percentage:
                    sb.append(" ");
                    sb.append(df.format(offer.getDiscountAmount() * -100));
                    sb.append("% off:");
                    break;
            }

            //offer total
            Double offerTotalAmount = offerApplied.get(offer);
            if(offerTotalAmount != null)
                sb.append(" ").append(currencyOutput.format(offerTotalAmount));
            else
                sb.append(" ").append(currencyOutput.format(0));

            sb.append("\n");
        }
        if(offerApplied.size() == 0)
            sb.append("(No offers available)\n");

        sb.append("Total: ").append(currencyOutput.format(cal.getTotal())).append("\n");

        System.out.println(sb.toString());
    }

}
