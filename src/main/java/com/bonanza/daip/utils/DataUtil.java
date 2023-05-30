package com.bonanza.daip.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final ObjectMapper objectMapperField = new ObjectMapper();

	public static String getJsonString(Object obj) {
		try {

			return String
					.format("\n%s\n%s", obj.getClass().getSimpleName(),
							objectMapper
									.writer(new SimpleFilterProvider()
											.addFilter("logFilter",
													SimpleBeanPropertyFilter.serializeAllExcept("recordData",
															"RECORD_DATA", "FILE_CONT", "agreeRecord", "fileContent", "RECORDFILE")))
									.withDefaultPrettyPrinter().writeValueAsString(obj));
		} catch (JsonProcessingException jpe) {
			log.error(jpe.getMessage());
			return null;
		}
	}

	public static String getJsonStringField(Object obj) {
		try {

			return String
					.format("\n%s\n%s", obj.getClass().getSimpleName(),
							objectMapperField
								    .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
								    .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
									.writer(new SimpleFilterProvider()
											.addFilter("logFilter",
													SimpleBeanPropertyFilter.serializeAllExcept("recordData",
															"RECORD_DATA", "FILE_CONT", "agreeRecord", "fileContent", "RECORDFILE", "recordfile")))
									.withDefaultPrettyPrinter().writeValueAsString(obj));
		} catch (JsonProcessingException jpe) {
			log.error(jpe.getMessage());
			return null;
		}
	}

	public static void setDataR(byte[] arryByte, String str) throws UnsupportedEncodingException {
		setDataRPad(arryByte, str, (byte) ' ');
	}

	public static void setDataR(byte[] arryByte, String str, String encoding) throws UnsupportedEncodingException {
		setDataRPad(arryByte, str, (byte) ' ', encoding);
	}


	/** 필드값들을 설정하고 빈배열을 우측을 공백으로 채운다. */
	public static void setDataRPad(byte[] arryByte, String str, byte paddingChar) throws UnsupportedEncodingException {

		// 문자열 값이 널이면 전부 공백으로 채우기 위해
		if (str == null) {
			str = "";
		}

		// 필드에 채울 문자열을 바이트배열로 변환
		byte[] bytes = str.getBytes("EUC_KR");

		// 실제데이터 값의 바이트배열길이
		int endIdx = 0;

		// 실제데이터 바이트배열 설정
		if (arryByte.length >= bytes.length) {
			endIdx = bytes.length; // 필드의 배열이 실제데이터배열보다 크거나 같을 경우
		} else {
			endIdx = arryByte.length; // 필드의 배열이 실제데이터배열보다 작을 경우
		}

		// 필드배열에 실제데이터배열값으로 채운다.
		for (int i = 0; i < endIdx; i++) {
			arryByte[i] = bytes[i];
		}
		// 실제데이터값이 채워지지 않은 배열은 공백으로 채운다.
		for (int j = endIdx; j < arryByte.length; j++) {
			arryByte[j] = paddingChar;
		}
	}

	/** 필드값들을 설정하고 빈배열을 우측을 공백으로 채운다. */
	public static void setDataRPad(byte[] arryByte, String str, byte paddingChar, String encoding) throws UnsupportedEncodingException {

		// 문자열 값이 널이면 전부 공백으로 채우기 위해
		if (str == null) {
			str = "";
		}

		// 필드에 채울 문자열을 바이트배열로 변환
		byte[] bytes = str.getBytes(encoding);

		// 실제데이터 값의 바이트배열길이
		int endIdx = 0;

		// 실제데이터 바이트배열 설정
		if (arryByte.length >= bytes.length) {
			endIdx = bytes.length; // 필드의 배열이 실제데이터배열보다 크거나 같을 경우
		} else {
			endIdx = arryByte.length; // 필드의 배열이 실제데이터배열보다 작을 경우
		}

		// 필드배열에 실제데이터배열값으로 채운다.
		for (int i = 0; i < endIdx; i++) {
			arryByte[i] = bytes[i];
		}
		// 실제데이터값이 채워지지 않은 배열은 공백으로 채운다.
		for (int j = endIdx; j < arryByte.length; j++) {
			arryByte[j] = paddingChar;
		}
	}

	public static void setDataL(byte[] arryByte, String str) throws UnsupportedEncodingException {
		setDataLPad(arryByte, str, (byte) '0');
	}

	public static void setDataL(byte[] arryByte, String str, String encoding) throws UnsupportedEncodingException {
		setDataLPad(arryByte, str, (byte) '0', encoding);
	}

	/** 필드값들을 설정하고 빈배열을 좌측을 '0'으로 채운다. */
	public static void setDataLPad(byte[] arryByte, String str, byte paddingChar) throws UnsupportedEncodingException {

		// 문자열 값이 널이면 전부 공백으로 채우기 위해
		if (str == null) {
			str = "";
		}

		// 필드에 채울 문자열을 바이트배열로 변환
		byte[] bytes = str.getBytes("EUC_KR");

		// 실제데이터 값의 바이트배열길이
		int startIdx = 0;

		// 실제데이터 바이트배열 설정
		if (arryByte.length >= bytes.length) {
			startIdx = arryByte.length - bytes.length; // 필드의 배열이 실제데이터배열보다 크거나 같을 경우
			// 실제데이터값이 채워지지 않은 배열은 공백으로 채운다.
			for (int j = 0; j < startIdx; j++) {
				arryByte[j] = paddingChar;
			}
			for (int i = startIdx; i < arryByte.length; i++) {
				arryByte[i] = bytes[i - startIdx];
			}
		} else {
			startIdx = bytes.length - arryByte.length; // 필드의 배열이 실제데이터배열보다 작을 경우
			// 필드배열에 실제데이터배열값으로 채운다.
			for (int i = startIdx; i < bytes.length; i++) {
				arryByte[i - startIdx] = bytes[i];
			}
		}

	}

	/** 필드값들을 설정하고 빈배열을 좌측을 '0'으로 채운다. */
	public static void setDataLPad(byte[] arryByte, String str, byte paddingChar, String encoding) throws UnsupportedEncodingException {

		// 문자열 값이 널이면 전부 공백으로 채우기 위해
		if (str == null) {
			str = "";
		}

		// 필드에 채울 문자열을 바이트배열로 변환
		byte[] bytes = str.getBytes(encoding);

		// 실제데이터 값의 바이트배열길이
		int startIdx = 0;

		// 실제데이터 바이트배열 설정
		if (arryByte.length >= bytes.length) {
			startIdx = arryByte.length - bytes.length; // 필드의 배열이 실제데이터배열보다 크거나 같을 경우
			// 실제데이터값이 채워지지 않은 배열은 공백으로 채운다.
			for (int j = 0; j < startIdx; j++) {
				arryByte[j] = paddingChar;
			}
			for (int i = startIdx; i < arryByte.length; i++) {
				arryByte[i] = bytes[i - startIdx];
			}
		} else {
			startIdx = bytes.length - arryByte.length; // 필드의 배열이 실제데이터배열보다 작을 경우
			// 필드배열에 실제데이터배열값으로 채운다.
			for (int i = startIdx; i < bytes.length; i++) {
				arryByte[i - startIdx] = bytes[i];
			}
		}

	}

	public static String getData(byte[] arryByte) throws UnsupportedEncodingException {

		if (arryByte == null) {
			return "";
		}

		return new String(arryByte, "EUC-KR");

	}

	public static String getData(byte[] arryByte, String encoding) throws UnsupportedEncodingException {

		if (arryByte == null) {
			return "";
		}
		return new String(arryByte, encoding);
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

	public static String padLeft(String s, int n) {
		return String.format("%1$" + n + "s", s);
	}

	public static String substrL(String sData, int nLength) {
		int length = nLength;
		if(nLength > sData.length())
			length = sData.length();
		return sData.substring(0, length); 
	}

	public static String substrR(String sData, int nLength) {
		int start = sData.length() - nLength;
		int length = nLength;
		if(nLength > sData.length()) {
			start = 0;
			length = sData.length();
		}
		return sData.substring(start, length); 
	}

    /**
     * substrb 원하는 바이트 추출. 마지막 글자가 한글이고 추출 시 한글이 깨지는 경우 그 글자는 제외시킴
     * @param input 입력문자열
     * @param sIndex 추출 시작 인덱스. 바이트단위가 아님
     * @param byteSize 추출 바이트 길이. 추출 바이트 길이 보다 클 경우 차분은 스페이스로 패딩
     * @param byteLenKor 한글 한글자 바이트수
     * @return 추출 문자열
     */
    public static String substrb(String input, int sIndex, int byteSize, int byteLenKor) {
        if (input == null) return null;
        if (input.length() == 0 ) return input;
        if (sIndex < 0 || byteSize < 0 || byteLenKor > 3) return input;
        int iLen = 0;
        int iPrevSumBytes = 0;
        int iOneWordBytes = 0;

        do {
            iPrevSumBytes += iOneWordBytes;
            iOneWordBytes = (Character.getType(input.charAt(sIndex + iLen)) == Character.OTHER_LETTER) ? byteLenKor : 1;

            if (iPrevSumBytes + iOneWordBytes <= byteSize) {
                ++iLen;
            } else {
                break;
            }
        } while(sIndex + iLen < input.length());

        String strFmt = null;
        try {
            int iOutBytes = (input.substring(sIndex, sIndex + iLen)).getBytes("UTF-8").length;
            strFmt = (byteSize - iOutBytes > 0) ? "%-"+(byteSize - iOutBytes)+"s":"%s";
        } catch(UnsupportedEncodingException e) {
            return null;
        }

        return input.substring(sIndex, sIndex + iLen) + String.format(strFmt,"");
    }

	public static String calcCheckSum(String coAccountNum, String accountNum, String tranDtm, String apiType) {

		long lCoAccountNum = Long.parseLong(coAccountNum);
		long lAccountNum = Long.parseLong(accountNum);
		long lTranDate = Long.parseLong(tranDtm.substring(0, 8));
		long lTranTime = Long.parseLong(tranDtm.substring(8));
		long lApiType = Long.parseLong(apiType);

		long lCheckSum = (lCoAccountNum + lAccountNum + lTranDate + lTranTime) % (lApiType * 100);

		return String.format("%d", lCheckSum);
	}

	public static String calcCheckSumWoori(String tranDtm, String dpAccountNum, String tranAmt, String bankCodeStd, String wdAccountNum) {

		String tranDate = tranDtm.substring(2, 8);
		dpAccountNum = StringUtils.rightPad(dpAccountNum, 14, '0');
		tranAmt = StringUtils.leftPad(tranAmt, 13, '0');
		wdAccountNum = StringUtils.rightPad(wdAccountNum, 14, '0');
		
		String allTxt = String.format("%s%s%s%s%s", tranDate, dpAccountNum, tranAmt,  bankCodeStd, wdAccountNum);
		
		long sumAll = 0;
		for(char ch: allTxt.toCharArray()) {
			sumAll += Character.getNumericValue(ch);
		}

		long lTranAmt = Long.parseLong(tranAmt);
		long lRemain = lTranAmt % sumAll;

//		log.info("allText : [{}], sumAll : {}, remain : {}", allTxt, sumAll, lRemain);

		return String.format("%03d%03d", sumAll, lRemain);
	}

	public static String calcCheckSumKakao(String bizNo, String accountNum, String tranAmt, String tranNo, String tranDtm, String checkKey) {

		String sBizNo = bizNo.trim();
		String sAccountNum = accountNum.trim();
		long lTranAmt = Long.parseLong(tranAmt);
		String sTranAmt = String.format("%d", lTranAmt);
		String sTranNo = tranNo.trim();
		String tranDate = tranDtm.substring(2, 8);
		long lCheckKey = Long.parseLong(checkKey);

		String step1 = substrL(sTranAmt, 6);
		long step2 = Long.parseLong(sBizNo) + Long.parseLong(step1) + lCheckKey + 1;
		String step3 = substrR(sAccountNum, 6);
		long step4 = Long.parseLong(step3) + Long.parseLong(sTranNo) + 1;
		long step5 = step2 % 10000;
		long step6 = step4 % 10000;
		long step7 = step5 * step6;
		long step8 = step7 % 10000;
		String step9 = substrR(tranDate, 4);
		long step10 = step8 * Long.parseLong(step9) + 1;
		long step11 = step10 % 1000000;
		String step12 = StringUtils.leftPad(String.format("%d", step11), 6, '0');
		
		return step12;
	}

	public static String getSHA256(String input) {

		String toReturn = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update(input.getBytes("utf8"));
			toReturn = String.format("%064x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
			log.error("Exception", e);
		}

		return toReturn;
	}

	public static String getSHA512(String input) {

		String toReturn = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update(input.getBytes("utf8"));
			toReturn = String.format("%0128x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
			log.error("Exception", e);
		}

		return toReturn;
	}

	// 랜덤번호 생성
	public static String getRamdomNumber(int len) {
		char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
				'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		int idx = 0;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			idx = (int) (charSet.length * Math.random()); // 36 * 생성된 난수를 Int로 추출 (소숫점제거)
			sb.append(charSet[idx]);
		}
		return sb.toString();
	}

	/**
	 * 반각문자로 변경한다
	 *
	 * @param src 변경할값
	 * @return String 변경된값
	 */
	public static String toHalfChar(String src) {
		StringBuffer strBuf = new StringBuffer();
		char c = 0;
		int nSrcLength = src.length();
		for (int i = 0; i < nSrcLength; i++) {
			c = src.charAt(i);
			// 영문이거나 특수 문자 일경우.
			if (c >= '！' && c <= '～') {
				c -= 0xfee0;
			} else if (c == '　') {
				c = 0x20;
			}
			// 문자열 버퍼에 변환된 문자를 쌓는다
			strBuf.append(c);
		}
		return strBuf.toString();
	}

	/**
	 * 전각문자로 변경한다.
	 *
	 * @param src 변경할값
	 * @return String 변경된값
	 */
	public static String toFullChar(String src) {
		// 입력된 스트링이 null 이면 null 을 리턴
		if (src == null)
			return null;
		// 변환된 문자들을 쌓아놓을 StringBuffer 를 마련한다
		StringBuffer strBuf = new StringBuffer();
		char c = 0;
		int nSrcLength = src.length();
		for (int i = 0; i < nSrcLength; i++) {
			c = src.charAt(i);
			// 영문이거나 특수 문자 일경우.
			if (c >= 0x21 && c <= 0x7e) {
				c += 0xfee0;
			}
			// 공백일경우
			else if (c == 0x20) {
				c = 0x3000;
			}
			// 문자열 버퍼에 변환된 문자를 쌓는다
			strBuf.append(c);
		}
		return strBuf.toString();
	}


	public static int getKftcTimeOut() {

		int timeout = 60;
		return timeout;
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static byte[] hexByteToByteArray(byte[] s) {
		int len = s.length;
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s[i], 16) << 4) + Character.digit(s[i + 1], 16));
		}
		return data;
	}

	public static String byteArrayToHexString(byte[] bytes) {

		StringBuilder sb = new StringBuilder();

		for (byte b : bytes) {

			sb.append(String.format("%02X", b & 0xff));
		}

		return sb.toString();
	}

	@SuppressWarnings("serial")
	static final HashMap<Character, Integer> encTable = new HashMap<Character, Integer>() {
		{
			put('A', 35);
			put('B', 34);
			put('C', 33);
			put('D', 32);
			put('E', 31);
			put('F', 30);
			put('G', 29);
			put('H', 28);
			put('I', 27);
			put('J', 26);
			put('K', 25);
			put('L', 24);
			put('M', 23);
			put('N', 22);
			put('O', 21);
			put('P', 20);
			put('Q', 19);
			put('R', 18);
			put('S', 17);
			put('T', 16);
			put('U', 15);
			put('V', 14);
			put('W', 13);
			put('X', 12);
			put('Y', 11);
			put('Z', 10);
			put('0', 9);
			put('1', 8);
			put('2', 7);
			put('3', 6);
			put('4', 5);
			put('5', 4);
			put('6', 3);
			put('7', 2);
			put('8', 1);
			put('9', 0);
		}
	};

	@SuppressWarnings("serial")
	static final HashMap<Integer, Character> decTable = new HashMap<Integer, Character>() {
		{
			put(35, 'A');
			put(34, 'B');
			put(33, 'C');
			put(32, 'D');
			put(31, 'E');
			put(30, 'F');
			put(29, 'G');
			put(28, 'H');
			put(27, 'I');
			put(26, 'J');
			put(25, 'K');
			put(24, 'L');
			put(23, 'M');
			put(22, 'N');
			put(21, 'O');
			put(20, 'P');
			put(19, 'Q');
			put(18, 'R');
			put(17, 'S');
			put(16, 'T');
			put(15, 'U');
			put(14, 'V');
			put(13, 'W');
			put(12, 'X');
			put(11, 'Y');
			put(10, 'Z');
			put(9, '0');
			put(8, '1');
			put(7, '2');
			put(6, '3');
			put(5, '4');
			put(4, '5');
			put(3, '6');
			put(2, '7');
			put(1, '8');
			put(0, '9');
		}
	};

	public static String encryptKftc(String plainText, String vanCode, String tranDate, String senderName, int iMod) {
		StringBuilder sb = new StringBuilder();
		if(senderName.length() > 8)
			senderName =  senderName.substring(0, 8);
		String encKey = String.format("%s%s%s", vanCode, tranDate, StringUtils.rightPad(senderName, 8, 'Z'))
				.toUpperCase();
		String plainTextUp = plainText.toUpperCase();
		String encKeyUp = encKey.toUpperCase();
		for (int idx = 0; idx < plainTextUp.length(); idx++) {
			Character cP = plainTextUp.charAt(idx);
			Character cK = encKeyUp.charAt(idx);
			int iP = encTable.get(cP);
			int iK = encTable.get(cK);
			int iC = (iP + iK) % iMod;
			char cD = decTable.get(iC);
			sb.append(cD);
		}
		return sb.toString();
	}

	public static String decryptKftc(String encText, String vanCode, String tranDate, String senderName, int iMod) {
		StringBuilder sb = new StringBuilder();
		if(senderName.length() > 8)
			senderName =  senderName.substring(0, 8);
		String encKey = String.format("%s%s%s", vanCode, tranDate, StringUtils.rightPad(senderName, 8, 'Z'))
				.toUpperCase();
		String encTextUp = encText.toUpperCase();
		for (int idx = 0; idx < encTextUp.length(); idx++) {
			Character cC = encTextUp.charAt(idx);
			Character cK = encKey.charAt(idx);
			int iC = encTable.get(cC);
			int iK = encTable.get(cK);
			int iP = ((iC + iMod) - iK) % iMod;
			char cP = decTable.get(iP);
			sb.append(cP);
		}
		return sb.toString();
	}
	
	/**
	 * 랜덤숫자를 만들어 리턴합니다.
	 * @param targetLen 길이
	 * @return 랜덤숫자
	 */
	public static String randomDigit(int targetLen) {
		
		int leftLimit = 48; // numeral '0'
		int rightLimit = 57; // letter '9'=57, 'z'=122
		
		if(targetLen == 0) targetLen = 3;
		
		Random random = new Random();
		String generatedString = random.ints(leftLimit,rightLimit + 1)
//				  .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				  .filter(i -> (i <= 57))
				  .limit(targetLen)
				  .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				  .toString();
		return generatedString;
		
	}
	
}
