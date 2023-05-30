package com.bonanza.sjin.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bonanza.sjin.config.enums.TradeMarketType;
import com.bonanza.sjin.config.enums.UpbitConfig;
import com.bonanza.sjin.market.jpa.rt.OrderBookSise;
import com.bonanza.sjin.market.type.SiseType;
import com.bonanza.sjin.market.upbit.result.OrderBookResult;
import com.bonanza.sjin.market.upbit.result.OrderBookResult.OrderBookUnit;
import com.bonanza.sjin.repository.OrderBookSiseRepository;
import com.bonanza.sjin.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Qualifier("UpbitOrder")
public class UpbitWebSockeOrdertListener extends WebSocketManager{
	
	@Autowired
	private OrderBookSiseRepository orderBookSiseRepository;

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
        log.debug("recvData::"+JsonUtil.fromJson(recvData, JsonNode.class).toPrettyString());
    	OrderBookResult orderBookResult= JsonUtil.fromJson(recvData, OrderBookResult.class);
    	
    	List<OrderBookSise> list = new ArrayList<OrderBookSise>();
    	for(OrderBookUnit item:orderBookResult.getOrderbook_units()) {
    		OrderBookSise target = new OrderBookSise();
    		target.setTrade_market(TradeMarketType.UPBIT.getType());
    		target.setCode(orderBookResult.getCode());
    		target.setTimestamp(orderBookResult.getTimestamp());
    		target.setTotal_ask_size(orderBookResult.getTotal_ask_size());
    		target.setTotal_bid_size(orderBookResult.getTotal_bid_size());
    		target.setAsk_price(item.getAsk_price());
    		target.setBid_price(item.getBid_price());
    		target.setAsk_size(item.getAsk_size());
    		target.setBid_size(item.getBid_size());
    		list.add(target);
    	}
    	orderBookSiseRepository.saveAll(list);
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

    @Override
    public void setParameter(List<String> codes) {
    	super.setJson(JsonUtil.toJson(List.of(Ticket.of(UUID.randomUUID().toString()), Type.of(SiseType.ORDERBOOK, codes))));
    }
}
