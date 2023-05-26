package com.bonanza.sjin.market.upbit.enums;

import com.bonanza.sjin.config.enums.EnumInterface;
import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrdSideType implements EnumInterface {

    ASK("ask", "매도"),
    BID("bid", "매수");

    private String type;
    private String name;

    public static OrdSideType find(String type) {
        return EnumInterface.find(type, values());
    }

    @JsonCreator
    public static OrdSideType findToNull(String type) {
        return EnumInterface.findToNull(type, values());
    }

}
