package com.bonanza.daip.listener;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bonanza.daip.config.enums.TradeMarketType;
import com.bonanza.daip.config.enums.UpbitConfig;
import com.bonanza.daip.market.jpa.rt.TickerSise;
import com.bonanza.daip.market.type.SiseType;
import com.bonanza.daip.repository.TickerSiseRepository;
import com.bonanza.daip.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Qualifier("UpbitTicker")
public class UpbitWebSockeTickertListener extends WebSocketManager{
	
	@Autowired
	private TickerSiseRepository tickerSiseRepository;
	
	@Autowired
	private UpbitConfig upbitProperties;
	
	@PostConstruct
	@Override
	public void init() {
		super.setWssUrl(upbitProperties.getWssUrl());
		super.declare();
	}
	
    @Override
    public void receiveDataProc(String recvData) {
        log.debug("recvData :: "+JsonUtil.fromJson(recvData, JsonNode.class).toPrettyString());
        
        //수신 데이터 파싱 및 DB 저장
        TickerSise tickerResult = JsonUtil.fromJson(recvData, TickerSise.class);
        tickerResult.setTrade_market(TradeMarketType.UPBIT.getType());
        tickerSiseRepository.save(tickerResult);
    }

    @Data(staticConstructor = "of")
    private static class Type {
        private final SiseType type;
        private final List<String> codes; // market
        private Boolean isOnlySnapshot = false;
        private Boolean isOnlyRealtime = true;
    }

    @Override
    public void setParameter(List<String> codes) {
    	super.setJson(JsonUtil.toJson(List.of(Ticket.of(UUID.randomUUID().toString()), Type.of(SiseType.TICKER, codes))));
    }
    
    @Data(staticConstructor = "of")
    protected static class Ticket {
        private final String ticket;
    }
}
