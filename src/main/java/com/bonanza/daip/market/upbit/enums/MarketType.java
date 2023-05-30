package com.bonanza.daip.market.upbit.enums;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bonanza.daip.config.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarketType implements EnumInterface {

    KRW_BTC("KRW-BTC", "원화 비트코인"),
    KRW_ETH("KRW-ETH", "원화 이더리움"),
    KRW_XRP("KRW-XRP", "원화 리플");

    private String type;
    private String name;

    public static final List<MarketType> marketTypeList = List.of(KRW_ETH);
//    public static final List<MarketType> marketTypeList = List.of(KRW_BTC, KRW_ETH, KRW_XRP);

    public static MarketType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static MarketType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }

    public boolean contains(MarketUnit marketUnit) {
        return StringUtils.contains(this.type, marketUnit.getType());
    }
}
