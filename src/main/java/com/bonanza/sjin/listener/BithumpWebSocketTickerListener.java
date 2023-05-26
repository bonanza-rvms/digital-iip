package com.bonanza.sjin.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bonanza.sjin.config.enums.BithumbConfig;
import com.bonanza.sjin.config.enums.TradeMarketType;
import com.bonanza.sjin.market.bithumb.result.BithumbTickerResult;
import com.bonanza.sjin.market.bithumb.result.BithumbTickerResult.BithumbTickerContentResult;
import com.bonanza.sjin.market.jpa.rt.TickerSise;
import com.bonanza.sjin.market.type.BithumbSiseType;
import com.bonanza.sjin.repository.TickerSiseRepository;
import com.bonanza.sjin.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Qualifier("BithumbTicker")
public class BithumpWebSocketTickerListener extends WebSocketManager {

	@Autowired
	private TickerSiseRepository tickerSiseRepository;

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
    		BithumbTickerResult tickerResult = JsonUtil.fromJson(text, BithumbTickerResult.class);
    		BithumbTickerContentResult item = tickerResult.getContent();
    		TickerSise tickersise = new TickerSise();
    		tickersise.setTrade_market(TradeMarketType.BITHUMB.getType());
    		tickersise.setCode(item.getSymbol());//TODO::코드 변경
    		tickersise.setTrade_date(item.getDate());
    		tickersise.setTrade_time(item.getTime());
    		tickersise.setOpening_price(Double.parseDouble(item.getOpenPrice()));
    		//종가?
    		tickersise.setLow_price(Double.parseDouble(item.getLowPrice()));
    		tickersise.setHigh_price(Double.parseDouble(item.getHighPrice()));
    		tickersise.setAcc_trade_price(Double.parseDouble(item.getValue()));
    		tickersise.setAcc_trade_volume(Double.parseDouble(item.getVolume()));
    		
    		tickersise.setAcc_ask_volume(Double.parseDouble(item.getSellVolume()));
    		tickersise.setAcc_bid_volume(Double.parseDouble(item.getBuyVolume()));
    		tickersise.setPrev_closing_price(Double.parseDouble(item.getPrevClosePrice()));
    		tickersise.setSigned_change_rate(Double.parseDouble(item.getChgRate()));
    		tickersise.setChange_rate(Double.parseDouble(item.getChgRate()));
    		tickersise.setSigned_change_price(Double.parseDouble(item.getChgAmt()));
    		tickersise.setTrade_volume(Double.parseDouble(item.getVolumePower()));//체결 강도
    		
    		tickerSiseRepository.save(tickersise);
        }
    }
    
    public void setParameter(List<String> codes, List<String> tickets) {
    	super.setJson(JsonUtil.toJson(Ticket.of(BithumbSiseType.TICKER, codes, tickets)));
    }
    
	@Override
    public void setParameter(List<String> codes) {
		super.setJson(JsonUtil.toJson(Type.of(BithumbSiseType.TICKER, codes)));
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
