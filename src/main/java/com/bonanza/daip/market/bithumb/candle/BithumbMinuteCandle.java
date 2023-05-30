package com.bonanza.daip.market.bithumb.candle;


import java.util.List;

import com.bonanza.daip.utils.DataUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BithumbMinuteCandle {
    private String status;
    private List<String[]> data;
    
    @JsonProperty("trade_market")
    private String tradeMarket;
    
	@Override
	public String toString() {
		return DataUtil.getJsonString(this);
	}
}
