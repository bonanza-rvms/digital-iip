package com.bonanza.sjin.market.coinone.result;

import java.util.List;

import com.bonanza.sjin.utils.DataUtil;

import lombok.Data;

@Data
public class CoinOneMarketCode {
    private String result;
    private String error_code;
    private Long server_time;
    private List<CurrencyUnit> currencies;
    
    @Data
    public static class CurrencyUnit {
        private String name;
        private String symbol;
        private String deposit_status;
        private String withdraw_status;
        private Long deposit_confirm_count;
        private Long max_precision;
        private String deposit_fee;
        private String withdrawal_min_amount;
        private String withdrawal_fee;
    }
    
	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
