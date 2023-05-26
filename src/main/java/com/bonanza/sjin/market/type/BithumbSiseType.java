package com.bonanza.sjin.market.type;

import com.bonanza.sjin.config.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BithumbSiseType implements EnumInterface {
	TICKER("ticker", "현재가"),
    TRADE("transaction", "체결"),
    ORDERBOOK("orderbookdepth", "호가"),
	ORDERBOOKSNAP("orderbooksnapshot", "호가(스냅샷)");

    private String type;
    private String name;
    
    public static BithumbSiseType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static BithumbSiseType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }
}
