package com.bonanza.sjin.market.upbit.result;

import com.bonanza.sjin.utils.DataUtil;

import lombok.Data;

@Data
public class MarketCode {
    private String market;
    private String korean_name;
    private String english_name;
    
	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
