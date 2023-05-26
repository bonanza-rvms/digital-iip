package com.bonanza.sjin.market.bithumb.result;

import com.bonanza.sjin.utils.DataUtil;

import lombok.Data;

@Data
public class BithumbTickerResult {
	private String type;
	private BithumbTickerContentResult content;
	
	@Data
	public static class BithumbTickerContentResult {
		private String symbol;
		private String tickType;
		private String date;
		private String time;
		private String openPrice;
		private String closePrice;
		private String lowPrice;
		private String highPrice;
		private String value;
		private String volume;
		private String sellVolume;
		private String buyVolume;
		private String prevClosePrice;
		private String chgRate;
		private String chgAmt;
		private String volumePower;
	}
	
	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
