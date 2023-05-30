package com.bonanza.daip.listener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bonanza.daip.config.enums.CoinOneConfig;
import com.bonanza.daip.config.enums.TradeMarketType;
import com.bonanza.daip.market.coinone.result.CoinOneTradeResult;
import com.bonanza.daip.market.jpa.rt.TradeSise;
import com.bonanza.daip.market.type.CoinOneSiseType;
import com.bonanza.daip.repository.TradeSiseRepository;
import com.bonanza.daip.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;

@Slf4j
@Component
@Qualifier("CoinOneTrade")
public class CoinOneWebSocketTradeListener extends WebSocketManager{
	
	@Autowired
	private TradeSiseRepository tradeSiseRepository;
	
	@Autowired
	private CoinOneConfig coinOneProperties;
	
	private Timer pingTimer;
	
	@PostConstruct
	@Override
	public void init() {
		super.setWssUrl(coinOneProperties.getWssUrl());
		this.declare2();
	}

	/**
	 * PING을 수동으로 전송하기 때문에 재선언
	 */
	public void declare2() {
		super.setClient(new OkHttpClient.Builder()
				.readTimeout(0, TimeUnit.MILLISECONDS)
				.build());
		super.setRequest(new Request.Builder().url(super.getWssUrl())
				.build());
	}
	
	/**
	 * 수신데이터 처리
	 */
	@Override
    public void receiveDataProc(String recvData) {
        log.debug("recvData::" +JsonUtil.fromJson(recvData, JsonNode.class).toPrettyString());
        CoinOneTradeResult tradeResult = JsonUtil.fromJson(recvData, CoinOneTradeResult.class);
        
        if("DATA".equals(tradeResult.getResponseType())) {
        	TradeSise item = new TradeSise();
        	item.setTrade_market(TradeMarketType.COINONE.getType());
        	item.setTimestamp(tradeResult.getData().getTimestamp());
        	item.setCode(tradeResult.getData().getPriceCurrency() + "-" + tradeResult.getData().getProductCurrency());
        	item.setTrade_price(Double.parseDouble(tradeResult.getData().getPrice()));
        	item.setTrade_volume(Double.parseDouble(tradeResult.getData().getQty()));
        	item.setSequential_id(tradeResult.getData().getSeq());
        	
        	tradeSiseRepository.save(item);
        }
    }

    @Override
    public void setParameter(List<String> codes) {
    	//코인 목록을 저장
    	super.setJson(JsonUtil.toJson(List.of(codes)));
    }
    
    /**
     * PING 전송을 위한 Timer
     */
	public void startPingTimer() {
		pingTimer = new Timer();
		pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendPingMessage();
            }
        }, 0, 60 * 1000); // 1분마다 실행 (밀리초 단위)
	}
	
	/**
	 * PING 메시지 전송
	 */
	public void sendPingMessage() {
		log.debug("sendData1 ::" + JsonUtil.toJson(Ping.of("PING")));
		super.getWebSocket().send(JsonUtil.toJson(Ping.of("PING")));
	}
	
	/**
	 * 코인원은 PING을 보내는 타입이 달라 재설정
	 * 한꺼번에 여러코인을 요청할 수 없어서 반복문을통해 하나씩 구독 요청
	 */
    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
    	//PING 전송
    	sendPingMessage();
    	
    	//PING TIMER 시작
    	this.startPingTimer();
    	
    	//코인 거래체결 구독 시작
    	String[] list = JsonUtil.fromJson(super.getJson(), String[].class);
    	for(String item : list) {
    		log.debug("sendData2 ::" + JsonUtil.toJson(Ticket.of(Body.of(CoinOneSiseType.TRADE.getType(), Topic.of("KRW", item)))));
    		String sendData = JsonUtil.toJson(Ticket.of(Body.of(CoinOneSiseType.TRADE.getType(), Topic.of("KRW", item))));
    		super.getWebSocket().send(sendData);
    	}
    }
    
    
    @Data(staticConstructor = "of")
    private static class Ping {
        private final String requestType;
    }


    @Data(staticConstructor = "of")
    private static class Ticket {
        private final String requestType = "SUBSCRIBE";
        private final Body body;
    }
    
    @Data(staticConstructor = "of")
    private static class Body {
        private final String channel;
        private final Topic topic;
    }

    @Data(staticConstructor = "of")
    private static class Topic {
        private final String priceCurrency;
        private final String productCurrency;
    }
}
