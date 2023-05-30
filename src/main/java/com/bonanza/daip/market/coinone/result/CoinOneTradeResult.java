package com.bonanza.daip.market.coinone.result;

import com.bonanza.daip.utils.DataUtil;

import lombok.Data;

@Data
public class CoinOneTradeResult {
    private String responseType;
    private String channel;
    private TradeTopic topic;
    private TradeData data;
    
    @Data
    public static class TradeTopic {
        private String priceCurrency;
        private String productCurrency;
    }
    @Data
    public static class TradeData {
    	private Long timestamp;
    	private String priceCurrency;
    	private String productCurrency;
    	private Boolean isSellerMaker;
    	private String price;
    	private String qty;
    	private Long seq;
    }
    
	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
