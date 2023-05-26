package com.bonanza.sjin.market.upbit.result.market;


import com.bonanza.sjin.market.upbit.enums.MarketType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MarketResult {
    private MarketType market;
    @JsonProperty("korean_name")
    private String koreanName;
    @JsonProperty("english_name")
    private String englishName;
}
