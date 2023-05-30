package com.bonanza.daip.market.bithumb.enums;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bonanza.daip.config.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BithumbMarketType implements EnumInterface {

    KRW_BTC("BTC_KRW", "원화 비트코인"),
    KRW_ETH("ETH_KRW", "원화 이더리움");


    private String type;
    private String name;

    public static final List<BithumbMarketType> marketTypeList = List.of(KRW_BTC,KRW_ETH);

    public static BithumbMarketType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static BithumbMarketType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }
    public boolean contains(BithumbMarketUnit marketUnit) {
        return StringUtils.contains(this.type, marketUnit.getType());
    }
}
