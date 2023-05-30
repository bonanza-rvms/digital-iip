package com.bonanza.daip.task;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bonanza.daip.task.service.NoticeCheckService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableScheduling
public class NoticeTask {
	private final NoticeCheckService noticeCheckService;
	
	
//  @Scheduled(fixedDelay = 1000 * 60 * 5)
  public void collectGetNotice() throws Exception { 
	  
	  //업비트
//	  noticeCheckService.upbitNotiProcess();
	  
	  //빗썸
//	  noticeCheckService.bithumbNotiProcess();
	  
	  //코인원 - 스크래핑, 크롤링 막고 있음
//	  noticeCheckService.coninoneNotiProcess();
  }
  
}
