package com.bonanza.daip.market.cmc.result;


import java.util.List;

import com.bonanza.daip.utils.DataUtil;

import lombok.Data;

@Data
public class ConinMaketCapIDMapResult {
    private Stauts status;
    private List<ContentData> data;
    
    @Data
    public static class Stauts {
        private String timestamp;
        private int error_code;
        private String error_message;
        private int elapsed;
        private int credit_count;
    }

    @Data
    public static class ContentData {
        private long id;
        private long rank;
        private String name;
        private String symbol;
        private String slug;
        private int is_active; 
        private String first_historical_data; 
        private String last_historical_data;
        private String platform; 
    }
	@Override
	public String toString() {

		return DataUtil.getJsonString(this);
	}
}
