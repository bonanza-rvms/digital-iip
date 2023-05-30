package com.bonanza.sjin.market.coinone.result;

import java.util.List;

import com.bonanza.sjin.utils.DataUtil;

import lombok.Data;

@Data
public class CoinOneMarketCode {
    private String result;
    private String error_code;
    private Long server_time;
    private List<CurrencyUnit> markets;
    
    @Data
    public static class CurrencyUnit {
        private String quote_currency;
        private String target_currency;
        private String price_unit;
        private String qty_unit;
        private String max_order_amount;
        private String max_price;
        private String max_qty;
        private String min_order_amount;
        private String min_price;
        private String min_qty;
        private List<String> order_book_units;
        private int maintenance_status;
        private int trade_status;
        private List<String> order_types;
    }
    
	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
