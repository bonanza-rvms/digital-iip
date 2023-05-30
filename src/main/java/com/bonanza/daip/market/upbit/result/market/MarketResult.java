package com.bonanza.daip.market.upbit.result.market;


import com.bonanza.daip.market.upbit.enums.MarketType;
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
