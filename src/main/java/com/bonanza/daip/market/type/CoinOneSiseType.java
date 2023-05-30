package com.bonanza.daip.market.type;

import com.bonanza.daip.config.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CoinOneSiseType implements EnumInterface {
	TICKER("TICKER", "현재가"),
    TRADE("TRADE", "체결"),
    ORDERBOOK("ORDERBOOK_V2", "호가");

    private String type;
    private String name;
    
    public static CoinOneSiseType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static CoinOneSiseType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }
}
