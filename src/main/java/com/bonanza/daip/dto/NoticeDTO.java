package com.bonanza.daip.dto;

import lombok.Data;

/**
 * 공지사항 대한 정의
 * @author Jin
 *
 */
@Data
public class NoticeDTO {
	private String title;
	private String link;
	private String regDt;
	private String count;
}
