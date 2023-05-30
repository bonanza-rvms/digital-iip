package com.bonanza.sjin.task.service;


import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.util.UriEncoder;

import com.bonanza.sjin.config.enums.BithumbConfig;
import com.bonanza.sjin.config.enums.CoinOneConfig;
import com.bonanza.sjin.config.enums.TradeMarketType;
import com.bonanza.sjin.config.enums.UpbitConfig;
import com.bonanza.sjin.market.bithumb.candle.BithumbMinuteCandle;
import com.bonanza.sjin.market.bithumb.client.BithumbCandleClient;
import com.bonanza.sjin.market.bithumb.enums.BithumbMarketType;
import com.bonanza.sjin.market.coinone.result.CoinOneMarketCode;
import com.bonanza.sjin.market.coinone.result.CoinOneMarketCode.CurrencyUnit;
import com.bonanza.sjin.market.jpa.CoinCode;
import com.bonanza.sjin.market.jpa.candle.FiveMinutesCandle;
import com.bonanza.sjin.market.jpa.candle.OneMinutesCandle;
import com.bonanza.sjin.market.jpa.rt.TradeSise;
import com.bonanza.sjin.market.upbit.candle.MinuteCandle;
import com.bonanza.sjin.market.upbit.client.UpbitCandleClient;
import com.bonanza.sjin.market.upbit.enums.MarketType;
import com.bonanza.sjin.market.upbit.enums.MinuteType;
import com.bonanza.sjin.market.upbit.result.MarketCode;
import com.bonanza.sjin.market.upbit.result.TradeResult;
import com.bonanza.sjin.repository.CoinCodeRepository;
import com.bonanza.sjin.repository.FiveMinutesCandleRepository;
import com.bonanza.sjin.repository.OneMinutesCandleRepository;
import com.bonanza.sjin.repository.TradeSiseRepository;
import com.bonanza.sjin.utils.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final FiveMinutesCandleRepository fiveMinutesCandleRepository;
    private final OneMinutesCandleRepository oneMinutesCandleRepository;
    private final UpbitCandleClient upbitCandleClient;
    private final BithumbCandleClient bithumbCandleClient;
    
    private final RestTemplate restTemplate;
    
	private final UpbitConfig upbitConfig;

	private final BithumbConfig bithumbConfig;

	private final CoinOneConfig coinOneConfig;
	
	@Autowired
	private TradeSiseRepository tradeSiseRepository;
	
	@Autowired
	CoinCodeRepository coinCodeRepository;

    @Override
    public void collectGetCoinCandles(MinuteType minuteType, MarketType market) throws Exception {
        int minute = minuteType.getMinute();
        LocalDateTime nextTo = LocalDateTime.now().minusMinutes(minute); // 현재 만들어지는 분봉에 의해서 값이 왜곡된다.
        
        log.debug("nextTo : " + nextTo);
        
        /************************************************************************************/
        //업비트
        boolean flag = true;
        log.debug("업비트 캔들 조회 시작");
        while (flag) {
            int size = 0;
            long start = System.currentTimeMillis();
            List<MinuteCandle> minuteCandles = upbitCandleClient.getMinuteCandles(minute, market, 20, nextTo);
            for (MinuteCandle candle : minuteCandles) {
            	candle.setTradeMarket(TradeMarketType.UPBIT.getType());
                switch (minuteType) {
                    case FIVE -> {
                        if (!fiveMinutesCandleRepository.existsByTimestampAndMarket(market.getType(), candle.getTimestamp(), TradeMarketType.UPBIT.getType())) {
                        	log.debug("candle ::" + candle);
                            fiveMinutesCandleRepository.save(FiveMinutesCandle.of(candle));
                            size ++;
                        } else {
                            flag = false;
                        }
                    } case ONE -> {
                        if (!oneMinutesCandleRepository.existsByTimestampAndMarket(market.getType(), candle.getTimestamp(), TradeMarketType.UPBIT.getType())) {
                        	oneMinutesCandleRepository.save(OneMinutesCandle.of(candle));
                            size ++;
                        } else {
                            flag = false;
                        }
                    }
                    default -> throw new RuntimeException("There is not a operate minute candle type : " + minuteType.name());
                }
            }
            nextTo = minuteCandles.get(minuteCandles.size() - 1).getCandleDateTimeUtc();
            long end = System.currentTimeMillis();
            log.debug("nextTo ::"+ nextTo);
            log.debug("type :{}, timestamp:{}",market.getType(), minuteCandles.get(minuteCandles.size() - 1).getTimestamp());
            log.debug("flag ::"+ flag);
            log.debug("minuteCandles cnt ::"+ minuteCandles.size());
            log.debug("========== %s초 =========== 사이즈 : " + (end - start) / 1000.0, size);
            Thread.sleep(1000);
        }
        /************************************************************************************/
        //빗썸
        log.debug("빗썸 캔들 조회 시작");
        flag = false; 
    	int size = 0;
    	long start = System.currentTimeMillis();
    	BithumbMinuteCandle minuteCandles = bithumbCandleClient.getMinuteCandles(minute, BithumbMarketType.KRW_BTC);
    	for (String[] candle : minuteCandles.getData()) {
    		long timestamp = Long.parseLong(candle[0]);
    		MinuteCandle item = new MinuteCandle();
    		item.setTimestamp(timestamp);
    		item.setMarket(market.getType());
    		item.setTradeMarket(TradeMarketType.BITHUMB.getType());
    		item.setOpeningPrice(Double.parseDouble(candle[1]));
    		item.setTradePrice(Double.parseDouble(candle[2]));
    		item.setHighPrice(Double.parseDouble(candle[3]));
    		item.setLowPrice(Double.parseDouble(candle[4]));
    		item.setCandleAccTradeVolume(Double.parseDouble(candle[5]));
    		
    		switch (minuteType) {
    		case FIVE -> {
    			if (!fiveMinutesCandleRepository.existsByTimestampAndMarket(market.getType(), timestamp, TradeMarketType.BITHUMB.getType())) {
    				fiveMinutesCandleRepository.save(FiveMinutesCandle.of(item));
    				size ++;
    			}else {
    				flag = true;
    				break;
    			}
    		} case ONE -> {
    			if (!oneMinutesCandleRepository.existsByTimestampAndMarket(market.getType(), timestamp, TradeMarketType.BITHUMB.getType())) {
    				oneMinutesCandleRepository.save(OneMinutesCandle.of(item));
    				size ++;
    			}else {
    				flag = true; 
    				break;
    			}
    		}
    		default -> throw new RuntimeException("There is not a operate minute candle type : " + minuteType.name());
    		}
    		if(flag) break;	//데이터가 존재하면 정지
    	}
    	long end = System.currentTimeMillis();
    	log.debug("minuteCandles cnt ::"+ minuteCandles.getData().size());
    	log.debug("========== %s초 =========== 사이즈 : " + (end - start) / 1000.0, size);
    }
    /**
     * 업비크 코인목록을 조회하여 합니다.
     * @throws Exception
     */
    @Override
    public List<CoinCode> collectCoinByUpbit() throws Exception {
    	String url = String.format("%s/v1/market/all?isDetails=false", upbitConfig.getApiUrl());
        ResponseEntity<String> response = restTemplate.exchange(URI.create(url), HttpMethod.GET, HttpEntity.EMPTY, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("StatusCode = " + response.getStatusCode().value());
        }
        
        String body = response.getBody();

		List<CoinCode> target = new ArrayList<CoinCode>();
		MarketCode[] list = JsonUtil.fromJson(body, MarketCode[].class);
		for (MarketCode code : list) {
			CoinCode coin = coinCodeRepository.findByCode(code.getMarket());

			//원화마켓만 대상
			if(!code.getMarket().contains("KRW")) continue; 
			
			if (coin == null) {
				CoinCode item = new CoinCode();
				item.setCode(code.getMarket());
				item.setKorean_name(code.getKorean_name());
				item.setEnglish_name(code.getEnglish_name());
				item.setUpbit(code.getMarket());
				target.add(item);
			} else if(StringUtils.isBlank(coin.getUpbit())) {
				coin.setKorean_name(code.getKorean_name());
				coin.setEnglish_name(code.getEnglish_name());
				coin.setUpbit(code.getMarket());
				target.add(coin);
			}
		
		}
		return target;
    }
    /**
     * 빗썸 코인목록을 조회하여 합니다.
     * @throws Exception
     */
    @Override
    public List<CoinCode> collectCoinByBithumb(String cur) throws Exception {
    	String url = String.format("%s/public/ticker/ALL_%s", bithumbConfig.getApiUrl(), cur);
        ResponseEntity<String> response = restTemplate.exchange(URI.create(url), HttpMethod.GET, HttpEntity.EMPTY, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("StatusCode = " + response.getStatusCode().value());
        }
        
        String body = response.getBody();
        
        JSONParser jsonParser = new JSONParser();
		JSONObject node = (JSONObject) jsonParser.parse(body);
		List<CoinCode> target = new ArrayList<CoinCode>();

		if (node.containsKey("status") && "0000".equals(node.get("status").toString())) {
			JSONObject item = (JSONObject) node.get("data");
			Set<String> keys = item.keySet();

			for (String fieldName : keys) {
				String codeNm = cur + "-" + fieldName; // 업비트 방식이 메인
				String BithCodeNm = fieldName + "_" + cur; // 빗썸 코드 방식
				CoinCode coin = coinCodeRepository.findByCode(codeNm);
				if (coin == null) {
					CoinCode cc = new CoinCode();
					cc.setCode(codeNm);
					cc.setBithumb(BithCodeNm);
					target.add(cc);
				} else if (StringUtils.isBlank(coin.getBithumb())) {
					coin.setBithumb(BithCodeNm);
					target.add(coin);
				}
			}
		}
		
		return target;
    }
    
    /**
     * 코인원 코인목록을 조회하여 합니다.
     * @throws Exception
     */
    @Override
    public List<CoinCode> collectCoinByCoinOne() throws Exception {
    	String url = String.format("%s/public/v2/markets/KRW", coinOneConfig.getApiUrl());
        ResponseEntity<String> response = restTemplate.exchange(URI.create(url), HttpMethod.GET, HttpEntity.EMPTY, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("StatusCode = " + response.getStatusCode().value());
        }
        String body = response.getBody();
		List<CoinCode> target = new ArrayList<CoinCode>();
		
		CoinOneMarketCode ccList = JsonUtil.fromJson(body, CoinOneMarketCode.class);
		if ("success".equals(ccList.getResult())) {
			for (CurrencyUnit code : ccList.getMarkets()) {
				
				//원화마켓만 대상
				if(!code.getQuote_currency().contains("KRW")) continue;
				
				String codeNm = "KRW" + "-" + code.getTarget_currency(); // 업비트 방식이 메인
				CoinCode coin = coinCodeRepository.findByCode(codeNm);
				
				if (coin == null || StringUtils.isBlank(coin.getCode())) {
					CoinCode item = new CoinCode();
					item.setCode(codeNm);
					item.setCoinone(codeNm);
					target.add(item);
					
				} else if (StringUtils.isBlank(coin.getCoinone())) {
					coin.setCoinone(codeNm);
					target.add(coin);
				}
				
				
			}
			if (!target.isEmpty()) {
				coinCodeRepository.saveAll(target);
			}
		}
		
		return target;
    }
    
    /**
     * 업비트 체결내역 조회
     * @param market 마켓 코드 (ex. KRW-BTC)
     * @param count 체결 개수
     * @param dayAgo 최근 체결 날짜 기준 7일 이내의 이전 데이터 조회 가능. 비워서 요청 시 가장 최근 체결 날짜 반환. (범위: 1 ~ 7))
     * @param to 마지막 체결 시각. 형식 : [HHmmss 또는 HH:mm:ss]. 비워서 요청시 가장 최근 데이터
     * @return
     * @throws Exception
     */
    public List<TradeResult> inquiryTradeByUpbit(MarketType market, int count, int dayAgo, String to) throws Exception {
    	String url = String.format("%s/v1/trades/ticks", upbitConfig.getApiUrl());
    	String queryString = String.format("market=%s&count=%s&to=%s&daysAgo=%s",
                market.getType(),
                count,
                to,
                dayAgo);
        String path = String.format("%s?%s", url, UriEncoder.encode(queryString));
        
        ResponseEntity<String> response = restTemplate.exchange(URI.create(path), HttpMethod.GET, HttpEntity.EMPTY, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("StatusCode = " + response.getStatusCode().value());
        }
        String body = response.getBody();
        
        return JsonUtil.listFromJson(body, TradeResult.class);
    }
    
    
    public void tradeChecker(TradeMarketType marketType, MarketType market) throws Exception {
        String nextTo = "";
        
        log.debug("nextTo : " + nextTo);
        
        /************************************************************************************/
        //업비트
        boolean flag = true;
        log.debug("업비트 최근 체결 내역 체크 시작");
        while (flag) {
            int size = 0;
            long start = System.currentTimeMillis();
            List<TradeResult> tradeResult = this.inquiryTradeByUpbit(market, 20, 1, nextTo);
            for (TradeResult trade : tradeResult) {
            	
            	if (!tradeSiseRepository.existsBySequentialIdAndMarket(trade.getMarket(),
            			trade.getTimestamp(), marketType.getType(), trade.getSequential_id())) {
            		log.debug("trade ::" + trade);
            		
            		TradeSise sise = new TradeSise();
            		sise.setTrade_market(marketType.getType());
            		sise.setCode(trade.getMarket());
            		sise.setTrade_date(trade.getTrade_date_utc());
            		sise.setTrade_time(trade.getTrade_time_utc());
            		sise.setTimestamp(trade.getTimestamp());
            		sise.setTrade_price(Double.parseDouble(trade.getTrade_price().toString()));
            		sise.setTrade_volume(Double.parseDouble(trade.getTrade_volume().toString()));
            		sise.setPrev_closing_price(Double.parseDouble(trade.getPrev_closing_price().toString()));
            		sise.setChange_price(Double.parseDouble(trade.getChange_price().toString()));
            		sise.setAsk_bid(trade.getAsk_bid());
            		
            		tradeSiseRepository.save(sise);
            		size ++;
            	} else {
            		flag = false;
            	}
            }
            nextTo = tradeResult.get(tradeResult.size() - 1).getTrade_time_utc();
            long end = System.currentTimeMillis();
            log.debug("nextTo ::"+ nextTo);
            log.debug("type :{}, timestamp:{}",market.getType(), tradeResult.get(tradeResult.size() - 1).getTimestamp());
            log.debug("flag ::"+ flag);
            log.debug("minuteCandles cnt ::"+ tradeResult.size());
            log.debug("========== %s초 =========== 사이즈 : " + (end - start) / 1000.0, size);
            Thread.sleep(1000);
        }
    }
}
