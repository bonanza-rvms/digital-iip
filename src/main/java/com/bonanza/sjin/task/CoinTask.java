package com.bonanza.sjin.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonanza.sjin.config.enums.BithumbConfig;
import com.bonanza.sjin.config.enums.CoinOneConfig;
import com.bonanza.sjin.config.enums.UpbitConfig;
import com.bonanza.sjin.market.jpa.CoinCode;
import com.bonanza.sjin.market.upbit.candle.MinuteCandle;
import com.bonanza.sjin.market.upbit.client.UpbitAllMarketClient;
import com.bonanza.sjin.market.upbit.client.UpbitCandleClient;
import com.bonanza.sjin.market.upbit.enums.MarketType;
import com.bonanza.sjin.market.upbit.enums.MarketUnit;
import com.bonanza.sjin.market.upbit.enums.MinuteType;
import com.bonanza.sjin.market.upbit.result.market.MarketResult;
import com.bonanza.sjin.repository.CoinCodeRepository;
import com.bonanza.sjin.task.service.ScheduleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableScheduling
public class CoinTask {

	private final ScheduleService scheduleService;

	@Autowired
	CoinCodeRepository coinCodeRepository;

	/**
	 * <pre>
	 *     코인 5분봉을 수집한다. (매 5분마다)
	 * </pre>
	 * 
	 * @throws Exception
	 */
//    @Scheduled(fixedDelay = 1000 * 60 * 5)
	public void collectGetCoinFiveMinutesCandles() throws Exception {
		try {
			log.info("[Demo Coin Scheduling] 코인 5분봉 수집 스케줄 시작");

			for (MarketType marketType : MarketType.marketTypeList) {
				log.info("코인 : {} 5분봉 수집 시작", marketType.getName());
				try {
					scheduleService.collectGetCoinCandles(MinuteType.FIVE, marketType);
				} catch (Exception e) {
					log.error("코인 : {} 5분봉 수집 중 에러가 발생\r\n 에러메시지 : {}", marketType.getName(), e.getMessage());
					// slackMessageService.scheduleErrorMessage(String.format("%s 5분봉 수집 중 에러발생!!
					// 에러메시지 : %s", marketType.getName(), e.getMessage())); TODO :: 텔레그램으로 변경
				} finally {
					log.info("코인 : {} 5분봉 수집 종료", marketType.getName());
				}
			}
			log.info("[Demo Coin Scheduling] 코인 5분봉 수집 스케줄 종료");
		} catch (Exception e) {
			log.error("e", e);
		} finally {
			// slackMessageService.scheduleErrorMessage("5분봉 수집 완료"); TODO :: 텔레그램으로 변경
		}
	}

	/**
	 * <pre>
	 *     코인 1분봉을 수집한다. (매 1분마다)
	 * </pre>
	 * 
	 * @throws Exception
	 */
//    @Scheduled(fixedDelay = 1000 * 60 * 1)
	public void collectGetCoinOneMinutesCandles() throws Exception {
		try {
			log.info("[Demo Coin Scheduling] 코인 1분봉 수집 스케줄 시작");

			for (MarketType marketType : MarketType.marketTypeList) {
				log.info("코인 : {} 1분봉 수집 시작", marketType.getName());
				try {
					scheduleService.collectGetCoinCandles(MinuteType.ONE, marketType);
				} catch (Exception e) {
					log.error("코인 : {} 1분봉 수집 중 에러가 발생\r\n 에러메시지 : {}", marketType.getName(), e.getMessage());
					// slackMessageService.scheduleErrorMessage(String.format("%s 5분봉 수집 중 에러발생!!
					// 에러메시지 : %s", marketType.getName(), e.getMessage())); TODO :: 텔레그램으로 변경
				} finally {
					log.info("코인 : {} 1분봉 수집 종료", marketType.getName());
				}
			}
			log.info("[Demo Coin Scheduling] 코인 1분봉 수집 스케줄 종료");
		} catch (Exception e) {
			log.error("e", e);
		} finally {
			// slackMessageService.scheduleErrorMessage("5분봉 수집 완료"); TODO :: 텔레그램으로 변경
		}
	}

	@Autowired
	private UpbitCandleClient upbitCandleClient;

	@Autowired
	private UpbitAllMarketClient upbitAllMarketClient;

//    @Scheduled(fixedDelay = 1000 * 60 * 1)
	public void tradeVolume() throws Exception {
		List<MarketResult> marketResults = upbitAllMarketClient.getAllMarketInfo(MarketUnit.KRW);

		marketResults.forEach(marketResult -> {
			MarketType market = marketResult.getMarket();

			log.info("======================= 종목 : {} 시작 =======================", market);
			List<MinuteCandle> minuteCandlesBofore = upbitCandleClient.getMinuteCandles(3, market, 200,
					LocalDateTime.now().minusMinutes(3));
			double beforeStdev = stdev(minuteCandlesBofore);

			List<MinuteCandle> minuteCandlesAfter = upbitCandleClient.getMinuteCandles(3, market, 200,
					LocalDateTime.now());
			double afterStdev = stdev(minuteCandlesAfter);

			if ((2.0 * beforeStdev) < afterStdev) {
				String nowFormat = LocalDateTime.now(ZoneId.of("Asia/Seoul"))
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				log.debug(String.format("%s 해당 시간에 거래량 급등 발생!! 종목 : %s", nowFormat, marketResult.getKoreanName()));
				// slackMessageService.scheduleErrorMessage(String.format("%s 해당 시간에 거래량 급등 발생!!
				// 종목 : %s", nowFormat, marketResult.getKoreanName()));
			}
			log.info("============ before: {}, after: {} ============",
					new BigDecimal(beforeStdev).setScale(2, RoundingMode.HALF_EVEN),
					new BigDecimal(afterStdev).setScale(2, RoundingMode.HALF_EVEN));
			log.info("======================= 종목 : {} 종료 =======================", market);

			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 표준 편차 구하기
	 * 
	 * @param minuteCandles
	 * @return
	 */
	private double stdev(List<MinuteCandle> minuteCandles) {
		SummaryStatistics statistics = new SummaryStatistics();
		for (MinuteCandle candle : minuteCandles) {
			statistics.addValue(candle.getCandleAccTradeVolume());
		}
		return statistics.getStandardDeviation();
	}

	/**
	 * 코인 코드 확인
	 * @throws Exception
	 */
	@Scheduled(cron = "0 10 9 * * *")
	public void checkCoinCode() throws Exception {
		// 업비트 코인 조회
		List<CoinCode> upbitCoins = scheduleService.collectCoinByUpbit();
		if (!upbitCoins.isEmpty()) {
			coinCodeRepository.saveAll(upbitCoins);
		}

		//////////////////////////////////////////////////////////////////////////
		// 빗썸 코인 조회
		String currencys[] = { "KRW", "BTC" };
		for (int i = 0; i < currencys.length; i++) {
			List<CoinCode> bithumbCoins = scheduleService.collectCoinByBithumb(currencys[i]);
			if (!upbitCoins.isEmpty()) {
				coinCodeRepository.saveAll(bithumbCoins);
			}
		}
		
		//////////////////////////////////////////////////////////////////////////
		// 코인원 코인코드 체크
		List<CoinCode> coinOneCoins = scheduleService.collectCoinByCoinOne();
		if (!upbitCoins.isEmpty()) {
			coinCodeRepository.saveAll(coinOneCoins);
		}
	}
}
