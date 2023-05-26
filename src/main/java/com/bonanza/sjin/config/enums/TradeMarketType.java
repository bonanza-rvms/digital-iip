package com.bonanza.sjin.config.enums;


import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeMarketType implements EnumInterface {
	UPBIT("UB", "업비트"),
	BITHUMB("BT", "빗썸"),
	COINONE("CO", "코인원");
	
	private String type;
	private String name;
	
	public static TradeMarketType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static TradeMarketType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}
}
