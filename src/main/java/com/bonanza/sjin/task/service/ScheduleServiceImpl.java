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
import com.bonanza.sjin.market.upbit.candle.MinuteCandle;
import com.bonanza.sjin.market.upbit.client.UpbitCandleClient;
import com.bonanza.sjin.market.upbit.enums.MarketType;
import com.bonanza.sjin.market.upbit.enums.MinuteType;
import com.bonanza.sjin.market.upbit.result.MarketCode;
import com.bonanza.sjin.repository.CoinCodeRepository;
import com.bonanza.sjin.repository.FiveMinutesCandleRepository;
import com.bonanza.sjin.repository.OneMinutesCandleRepository;
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
    	String url = String.format("%s/public/v2/currencies", coinOneConfig.getApiUrl());
        ResponseEntity<String> response = restTemplate.exchange(URI.create(url), HttpMethod.GET, HttpEntity.EMPTY, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("StatusCode = " + response.getStatusCode().value());
        }
        String body = response.getBody();
		List<CoinCode> target = new ArrayList<CoinCode>();
		
		CoinOneMarketCode ccList = JsonUtil.fromJson(body, CoinOneMarketCode.class);
		if ("success".equals(ccList.getResult())) {
			for (CurrencyUnit code : ccList.getCurrencies()) {
				String codeNm = "KRW" + "-" + code.getSymbol(); // 업비트 방식이 메인
				CoinCode coin = coinCodeRepository.findByCode(codeNm);

				if (coin == null) {
					CoinCode item = new CoinCode();
					item.setCode(codeNm);
					item.setEnglish_name(code.getName());
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
    
    public void inquiryTradeByUpbit() throws Exception {
    	String url = String.format("%s/v1/trades/ticks", upbitConfig.getApiUrl());
        ResponseEntity<String> response = restTemplate.exchange(URI.create(url), HttpMethod.GET, HttpEntity.EMPTY, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("StatusCode = " + response.getStatusCode().value());
        }
        String body = response.getBody();
    }
}
