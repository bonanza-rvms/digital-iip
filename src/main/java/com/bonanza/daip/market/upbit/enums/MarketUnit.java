package com.bonanza.daip.market.upbit.enums;

import com.bonanza.daip.config.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarketUnit implements EnumInterface {

    KRW("KRW", "원화"),
    BTC("BTC", "비트코인"),
    USDT("USDT", "USDT");

    private String type;
    private String name;

    public boolean contains(String market) {
        return market.contains(type);
    }

    public static MarketUnit find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static MarketUnit findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }
}
