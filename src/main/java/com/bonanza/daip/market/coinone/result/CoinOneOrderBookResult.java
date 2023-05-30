package com.bonanza.daip.market.coinone.result;


import com.bonanza.daip.utils.DataUtil;

import lombok.Data;

@Data
public class CoinOneOrderBookResult {
    private String responseType; // DATA로 고정
    private String channel; // ORDERBOOK_V2 고정
    private OrderTopic topic; // 구독 topic 정보
    private OrderData data; // 구독 data 정보

    @Data
    public static class OrderTopic {
        private String priceCurrency;  // 마켓 기준 통화 (예: KRW)
        private String productCurrency; // 조회한 종목의 심볼 (예: BTC)
        private String unit; // 조회하고자 하는 오더북 단위 (0은 기본 오더북 호출)
    }

    @Data
    public static class OrderData {
    	private Long timestamp; // 타임스탬프 (millisecond)
    	private String priceCurrency; // 마켓 기준 통화 (예: KRW)
    	private String productCurrency; // 조회한 종목의 심볼 (예: BTC)
    	private String unit; // 오더북 모아보기 단위 (숫자 형식)
        private OrderBid[] bid; // 매수 오더북
        private OrderAsk[] ask; // 매도 오더북
    	private int seq; // timestamp 내의 일련번호
    }

    @Data
    public static class OrderBid {
    	private String price; // 매수 가격
    	private String qty; // 해당 가격에 걸린 총 주문량 (productCurrency 기준)
    }

    @Data
    public static class OrderAsk {
    	private String price; // 매도 가격
    	private String qty; // 해당 가격에 걸린 총 주문량 (productCurrency 기준)
    }

	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
