package com.bonanza.sjin.market.bithumb.result;


import java.util.List;

import com.bonanza.sjin.utils.DataUtil;

import lombok.Data;

@Data
public class BithumbOrderBookResult {
    private String type;
    private Content content;
    
    @Data
    public static class Content {
        private Long datetime;
        private List<BithumbOrderBookResultUnit> list;
    }

    @Data
    public static class BithumbOrderBookResultUnit {
        private String symbol; // 매도 호가
        private String orderType;  // 주문타입 – bid / ask
        private String price; // 호가
        private String quantity; // 잔량
        private String total; // 건수
    }
	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
