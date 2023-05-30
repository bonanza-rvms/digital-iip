package com.bonanza.daip.market.upbit.result;


import java.util.List;

import com.bonanza.daip.utils.DataUtil;

import lombok.Data;

@Data
public class OrderBookResult {
    private String type; // orderbook
    private String code; // KRW-BTC (market)
    private Long timestamp;
    private Double total_ask_size; // 호가 매도 총 잔량
    private Double total_bid_size; // 호가 매수 총 잔량
    private List<OrderBookUnit> orderbook_units; // 호가
    public String stream_type; // TODO enum SNAPSHOT : 스냅샷, REALTIME : 실시간

    @Data
    public static class OrderBookUnit {
        private Double ask_price; // 매도 호가
        private Double bid_price; // 매수 호가
        private Double ask_size; // 매도 잔량
        private Double bid_size; // 매수 잔량
    }
	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
