package com.bonanza.daip.listener;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bonanza.daip.config.enums.TradeMarketType;
import com.bonanza.daip.config.enums.UpbitConfig;
import com.bonanza.daip.market.jpa.rt.TradeSise;
import com.bonanza.daip.market.type.SiseType;
import com.bonanza.daip.repository.TradeSiseRepository;
import com.bonanza.daip.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Qualifier("UpbitTrade")
public class UpbitWebSocketTradeListener extends WebSocketManager{
	
	@Autowired
	private TradeSiseRepository tradeSiseRepository;
	
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
        log.debug("recvData::" +JsonUtil.fromJson(recvData, JsonNode.class).toPrettyString());
        TradeSise tradeResult = JsonUtil.fromJson(recvData, TradeSise.class);
        tradeResult.setTrade_market(TradeMarketType.UPBIT.getType());
        tradeSiseRepository.save(tradeResult);
    }

    @Override
    public void setParameter(List<String> codes) {
    	super.setJson(JsonUtil.toJson(List.of(Ticket.of(UUID.randomUUID().toString()), Type.of(SiseType.TRADE, codes))));
    }


    @Data(staticConstructor = "of")
    private static class Ticket {
        private final String ticket;
    }

    @Data(staticConstructor = "of")
    private static class Type {
        private final SiseType type;
        private final List<String> codes; // market
        private Boolean isOnlySnapshot = false;
        private Boolean isOnlyRealtime = true;
    }
    
}
