package com.bonanza.sjin.listener;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bonanza.sjin.config.enums.BithumbConfig;
import com.bonanza.sjin.config.enums.TradeMarketType;
import com.bonanza.sjin.market.bithumb.result.BithumbTradeResult;
import com.bonanza.sjin.market.bithumb.result.BithumbTradeResult.BithumbTradeListResult;
import com.bonanza.sjin.market.jpa.rt.TradeSise;
import com.bonanza.sjin.market.type.BithumbSiseType;
import com.bonanza.sjin.repository.TradeSiseRepository;
import com.bonanza.sjin.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Qualifier("BithumbTrade")
public class BithumpWebSocketTradeListener extends WebSocketManager {

	@Autowired
	private TradeSiseRepository tradeSiseRepository;

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
    		BithumbTradeResult tradeResult = JsonUtil.fromJson(text, BithumbTradeResult.class);
    		List<TradeSise> tradeList = new ArrayList<TradeSise>();
    		for(BithumbTradeListResult item: tradeResult.getContent().getList()) {
    			TradeSise tradesise =  new TradeSise();
    			tradesise.setTrade_market(TradeMarketType.BITHUMB.getType());
    			tradesise.setCode(item.getSymbol());//TODO::변환 필요
    			tradesise.setAsk_bid("1".equals(item.getBuySellGb())?"ASK":"BID");
    			tradesise.setTrade_price(Double.parseDouble(item.getContPrice()));
    			tradesise.setTrade_volume(Double.parseDouble(item.getContQty()));
    			tradesise.setTrade_date(item.getContDtm().split(" ")[0]);
    			tradesise.setTrade_time(item.getContDtm().split(" ")[1].substring(0,8));
    			tradesise.setChange("up".equals(item.getUpdn())?"RISE":"dn".equals(item.getUpdn())?"FALL":"EVEN");
    			tradeList.add(tradesise);
    		}
    		tradeSiseRepository.saveAll(tradeList);
        }
    }
    
	@Override
    public void setParameter(List<String> codes) {
		super.setJson(JsonUtil.toJson(Type.of(BithumbSiseType.TRADE, codes)));
    }

    @Data(staticConstructor = "of")
    private static class Ticket {
        private final BithumbSiseType type;
        private final List<String> symbols; // market        
        private final List<String> tickTypes; // tickTypes        
    }
    
    @Data(staticConstructor = "of")
    private static class Type {
        private final BithumbSiseType type;
        private final List<String> symbols; // market        
    }
}
