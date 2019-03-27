package com.app.model.product;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * An offer provides discount towards item(s). Conditions:
 *  1. Within offer's start-end date.
 *  2. Has the item required (criteria) in basket.
 *  3. Must have all the target (discounting) items in basket .
 *
 * - Can configure if discount can be applied to goods in criteria.
 *
 * */
public class Offer {

    public enum DiscountType{ Percentage } //TODO: in future introduce "amount"

    private final Map<Good,Integer> goodCriteria;           //items required for offer to be triggered
    private final Map<Good,Integer> discountedGoods;        //items to be discounted
    private final DiscountType discountType;                //discount is based off % or exact amount
    private final double discountAmount;                    //Percentage > 0.1 = 10%
    private final boolean canApplyDiscountOnCriteriaGoods;  //the items that were used in the criteria, can be included in the discount
    private final LocalDateTime startDate;                  //offer start date (null = beginning of time)
    private final LocalDateTime expiryDate;                 //offer end date (null = till end of time)

    public Offer(Map<Good,Integer> goodCriteria, Map<Good,Integer> discountedGoods,
                 DiscountType discountType, double discountAmount,
                 boolean canApplyDiscountOnCriteriaGoods,
                 LocalDateTime startDate, LocalDateTime expiryDate){
        this.goodCriteria                    = goodCriteria == null ? new HashMap() : goodCriteria;
        this.discountedGoods                 = discountedGoods == null ? new HashMap() : discountedGoods;
        this.discountType                    = discountType;
        this.discountAmount                  = discountAmount;
        this.startDate                       = startDate;
        this.expiryDate                      = expiryDate;
        this.canApplyDiscountOnCriteriaGoods = canApplyDiscountOnCriteriaGoods;
    }

    public Map<Good,Integer> getGoodCriteria() {
        return goodCriteria;
    }

    public Map<Good,Integer> getDiscountedGoods() {
        return discountedGoods;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public boolean canApplyDiscountOnCriteriaGoods() {
        return canApplyDiscountOnCriteriaGoods;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    @Override
    public int hashCode(){

        int result = 17; //prime num

        result = 31 * result + goodCriteria.hashCode();
        result = 31 * result + discountedGoods.hashCode();
        result = 31 * result + discountType.hashCode();

        long doubleFieldBits = Double.doubleToLongBits(discountAmount);
        result = 31 * result + (int)(doubleFieldBits ^ (doubleFieldBits >>> 32));

        result = 31 * result + (canApplyDiscountOnCriteriaGoods ? 1 : 0);
        if(startDate != null)
            result = 31 * result + startDate.hashCode();
        if(expiryDate != null)
            result = 31 * result + expiryDate.hashCode();

        return result;
    }

    @Override
    public boolean equals(Object thatObj){

        if(!(thatObj instanceof Offer))
            return false;

        Offer that = (Offer) thatObj;
        return
               areEqual(this.getDiscountedGoods(), that.getDiscountedGoods())
            && areEqual(this.getGoodCriteria(), that.getGoodCriteria())
            && this.getDiscountType() == that.getDiscountType()
            && this.getDiscountAmount() == that.getDiscountAmount()
            && this.canApplyDiscountOnCriteriaGoods() == that.canApplyDiscountOnCriteriaGoods()
            && this.getStartDate() == null ? that.getStartDate()==null : this.getStartDate().isEqual(that.getStartDate())
            && this.getExpiryDate() == null ? that.getExpiryDate()==null : this.getExpiryDate().isEqual(that.getExpiryDate())
            ;
    }

    @Override
    public String toString(){
        return
            this.getClass().getSimpleName()
            +"={"
            + "goodCriteria:"+goodCriteria
            + ",discountedGoods:"+discountedGoods
            + ",discountType:"+discountType
            + ",discountAmount:"+discountAmount
            + ",canApplyDiscountOnCriteriaGoods:"+canApplyDiscountOnCriteriaGoods
            + ",startDate:"+startDate
            + ",expiryDate:"+expiryDate
            + "}";
    }

    private boolean areEqual(Map<Good, Integer> first, Map<Good, Integer> second) {
        if (first.size() != second.size())
            return false;

        return first.entrySet().stream()
                .allMatch(e -> e.getValue().equals(second.get(e.getKey())));
    }
}
