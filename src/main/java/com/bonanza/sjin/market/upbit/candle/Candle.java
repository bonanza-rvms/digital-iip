package com.bonanza.sjin.market.upbit.candle;



import com.bonanza.sjin.utils.NumberUtils;

public interface Candle {

    Double getTradePrice();

    Double getOpeningPrice();

    Double getLowPrice();

    Double getHighPrice();

    default boolean isBetween(Candle before) {
        return NumberUtils.between(before.getTradePrice(), before.getOpeningPrice(), this.getTradePrice());
    }
}
