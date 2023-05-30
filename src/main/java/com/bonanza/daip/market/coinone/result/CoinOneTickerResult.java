package com.bonanza.daip.market.coinone.result;


import com.bonanza.daip.utils.DataUtil;

import lombok.Data;

@Data
public class CoinOneTickerResult {
    private String responseType; // DATA로 고정
    private String channel; // ORDERBOOK_V2 고정
    private TickerTopic topic; // 구독 topic 정보
    private TickerData data; // 구독 data 정보

    @Data
    public static class TickerTopic {
        private String priceCurrency;  // 마켓 기준 통화 (예: KRW)
        private String productCurrency; // 조회한 종목의 심볼 (예: BTC)
        private String timezone; // 시간대 (UTC, KST, RELATIVE)
    }

    @Data
    public static class TickerData {
    	private Long timestamp; // 타임스탬프 (millisecond)
        private String priceCurrency; // 마켓 기준 통화 (예: KRW)
        private String productCurrency; // 조회한 종목의 심볼 (예: BTC)
        private String timezone; // UTC/KST/RELATIVE
        private String priceVolume; // 금일 거래량 (priceCurrency)
        private String productVolume; // 금일 거래량 (productCurrency)
        private String high; //	금일 최고가
        private String low; // 금일 최저가
        private String open; //	금일 시가
        private String close; // 금일 종가
        private String yesterdayPriceVolume; // 전일 거래량 (priceCurrency)
        private String yesterdayProductVolume; // 전일 거래량 (productCurrency)
        private String yesterdayHigh; // 전일 최고가
        private String yesterdayLow;	// 전일 최저가
        private String yesterdayOpen;	// 전일 시가
        private String yesterdayClose;	// 전일 종가
        private int seq;	// 타임스탬프 내의 일련번호
    }

	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
