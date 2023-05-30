package com.bonanza.daip.market.cmc.result;


import com.bonanza.daip.utils.DataUtil;

import lombok.Data;

@Data
public class ConinMaketCapCoinInfo {
    private Stauts status;
    private String data;
    
    @Data
    public static class Stauts {
        private String timestamp;
        private int error_code;
        private String error_message;
        private int elapsed;
        private int credit_count;
        private String notice;
    }

	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
