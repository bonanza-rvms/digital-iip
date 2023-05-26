package com.bonanza.sjin.market.bithumb.enums;

import com.bonanza.sjin.config.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BithumbMarketUnit implements EnumInterface {

    KRW("KRW", "원화"),
    BTC("BTC", "비트코인"),
    USDT("USDT", "USDT");

    private String type;
    private String name;

    public boolean contains(String market) {
        return market.contains(type);
    }

    public static BithumbMarketUnit find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static BithumbMarketUnit findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }
}
