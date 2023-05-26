package com.bonanza.sjin.listener;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bonanza.sjin.config.enums.BithumbConfig;
import com.bonanza.sjin.config.enums.TradeMarketType;
import com.bonanza.sjin.market.bithumb.result.BithumbOrderBookResult;
import com.bonanza.sjin.market.bithumb.result.BithumbOrderBookResult.BithumbOrderBookResultUnit;
import com.bonanza.sjin.market.jpa.rt.OrderBookSise;
import com.bonanza.sjin.market.type.BithumbSiseType;
import com.bonanza.sjin.repository.OrderBookSiseRepository;
import com.bonanza.sjin.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Qualifier("BithumbOrder")
public class BithumpWebSocketOrderListener extends WebSocketManager {

	@Autowired
	private OrderBookSiseRepository orderBookSiseRepository;
	
	@Autowired
	private BithumbConfig bithumbProperties;
	
	@PostConstruct
	@Override
	public void init() {
		super.setWssUrl(bithumbProperties.getWssUrl());
		super.declare();
	}
	
	@Override
	public void receiveDataProc(String text) {
    	JsonNode node = JsonUtil.fromJson(text, JsonNode.class);
    	log.debug("receiveData::"+node.toPrettyString());
        if(node.has("type")) {
    		BithumbOrderBookResult orderResult = JsonUtil.fromJson(text, BithumbOrderBookResult.class);
    		List<OrderBookSise> list = new ArrayList<OrderBookSise>();
    		for(BithumbOrderBookResultUnit item:orderResult.getContent().getList()) {
        		OrderBookSise target = new OrderBookSise();
        		target.setTrade_market(TradeMarketType.BITHUMB.getType());
        		target.setCode(item.getSymbol());
        		target.setTimestamp(orderResult.getContent().getDatetime());
        		if("bid".equals(item.getOrderType())) {
        			target.setBid_price(Double.parseDouble(item.getPrice()));
        			target.setBid_size(Double.parseDouble(item.getQuantity()));
        			target.setTotal_bid_size(Double.parseDouble(item.getTotal()));
        			target.setAsk_price(0.0);
        			target.setAsk_size(0.0);
        			target.setTotal_ask_size(0.0);
        		}else {
        			target.setBid_price(0.0);
        			target.setBid_size(0.0);
        			target.setTotal_bid_size(0.0);
        			target.setAsk_price(Double.parseDouble(item.getPrice()));
        			target.setAsk_size(Double.parseDouble(item.getQuantity()));
        			target.setTotal_ask_size(Double.parseDouble(item.getTotal()));
        		}
        		list.add(target);
    		}
    		orderBookSiseRepository.saveAll(list);
        }
    }
	
	@Override
    public void setParameter(List<String> codes) {
		super.setJson(JsonUtil.toJson(Type.of(BithumbSiseType.ORDERBOOK, codes)));
    }
    
    @Data(staticConstructor = "of")
    private static class Type {
        private final BithumbSiseType type;
        private final List<String> symbols; // market        
    }
}
