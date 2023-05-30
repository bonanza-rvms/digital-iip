package com.bonanza.daip.task.service;

public interface CoinMarketCapService {

	/**
	 * 코인마켓 캡에서 코인정보를 조회하여
	 * 업데이트 합니다.
	 * @return
	 */
	String coinCheckIn();

	/**
	 * 코인정보를 조회 합니다.
	 * @return
	 */
	String coinMetaData();

}
