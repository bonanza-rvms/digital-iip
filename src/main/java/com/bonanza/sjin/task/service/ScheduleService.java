package com.bonanza.sjin.task.service;


import java.util.List;

import com.bonanza.sjin.market.jpa.CoinCode;
import com.bonanza.sjin.market.upbit.enums.MarketType;
import com.bonanza.sjin.market.upbit.enums.MinuteType;

public interface ScheduleService {
    void collectGetCoinCandles(MinuteType minute, MarketType market) throws Exception ;

	/**
	 * 업비크 코인목록을 조회하여 합니다.
	 * @throws Exception
	 */
	List<CoinCode> collectCoinByUpbit() throws Exception;

	/**
	 * 빗썸 코인목록을 조회하여 합니다.
	 * @throws Exception
	 */
	List<CoinCode> collectCoinByBithumb(String cur) throws Exception;

	/**
	 * 코인원 코인목록을 조회하여 합니다.
	 * @throws Exception
	 */
	List<CoinCode> collectCoinByCoinOne() throws Exception;
}
