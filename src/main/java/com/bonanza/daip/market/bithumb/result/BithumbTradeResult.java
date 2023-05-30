package com.bonanza.daip.market.bithumb.result;

import java.util.List;

import com.bonanza.daip.utils.DataUtil;

import lombok.Data;

@Data
public class BithumbTradeResult {
	private String type;
	private BithumbTradeContentResult content;
	
	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
	
	@Data
	public static class BithumbTradeContentResult {
    	private List<BithumbTradeListResult> list;
    }
	
    @Data
    public static class BithumbTradeListResult {
    	private String symbol;
    	private String buySellGb;
    	private String contPrice;
    	private String contQty;
    	private String contAmt;
    	private String contDtm;
    	private String updn;
    }
}
