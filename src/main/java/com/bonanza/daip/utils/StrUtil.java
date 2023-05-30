package com.bonanza.daip.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrUtil {
	/**
	 * 오른쪽 패딩함수
	 * @param source
	 * @param totLen
	 * @param pad
	 * @return
	 */
	public static String rightPadWith(String source, int totLen, char pad) {
		String rtnString = new String();
		try {
			byte[] byteSrc = source.getBytes();
			int byteLen = totLen - byteSrc.length;

			if (byteLen < 0) {
				throw new Exception("totLen가 입력 스트링보다 작습니다!!");
			}
			rtnString = source + StringUtils.repeat(pad, byteLen);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return rtnString;
	}
	/**
	 * 왼쪽패딩
	 * @param source
	 * @param totLen
	 * @param pad
	 * @return
	 */
	public static String leftPadWith(String source, int totLen, char pad) {
		String rtnString = new String();
		try {
			byte[] byteSrc = source.getBytes();
			int byteLen = totLen - byteSrc.length;

			if (byteLen < 0) {
				throw new Exception("totLen가 입력 스트링보다 작습니다!!");
			}
			rtnString = StringUtils.repeat(pad, byteLen) + source;
		} catch (Exception e) {
			return null;
		}
		return rtnString;
	}
	public static String replace(String src, String org, String tar) {
		return replace(src, org, tar, true);
	}

	public static String replace(String src, String org, String tar, boolean all) {
		if (src == null) {
			return "";
		} else if (org != null && tar != null && src.indexOf(org) != -1) {
			String tmp1 = src;
			StringBuilder sbResult = new StringBuilder();
			int nIndex;
			if (all) {
				for (; (nIndex = tmp1.indexOf(org)) > -1; tmp1 = tmp1.substring(nIndex + org.length())) {
					sbResult.append(tmp1.substring(0, nIndex)).append(tar);
				}
				sbResult.append(tmp1);
			} else {
			}
			nIndex = src.indexOf(org);
			sbResult.append(src.substring(0, nIndex)).append(tar).append(src.substring(nIndex + org.length()));

			return sbResult.toString();
		} else {
			return src;
		}
	}

	/**
	 * 전각 반각 체크하는 함수입니다.
	 * @param i_strContents
	 * @return
	 */
	public static boolean isHalfWord(String i_strContents) {
		boolean value = true;
		byte[] byteArray = null;
		byteArray = i_strContents.getBytes();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] >= (byte) 0x81 && byteArray[i] <= (byte) 0x9f)
					|| (byteArray[i] >= (byte) 0xe0 && byteArray[i] <= (byte) 0xef)) {
				if ((byteArray[i + 1] >= (byte) 0x40 && byteArray[i + 1] <= (byte) 0x7e)
						|| (byteArray[i + 1] >= (byte) 0x80 && byteArray[i + 1] <= (byte) 0xfc)) {
					value = true;
				}
			}
		}
		return value;

	}

	/**
	 * 전각문자인지 체크하는 함수
	 * @param i_strContents
	 * @return
	 */
	public static boolean isFullWord(String i_strContents) {
		boolean value = true;
		byte[] byteArray = null;
		byteArray = i_strContents.getBytes();
		for (int i = 0; i < byteArray.length; i++) {
			if ((byteArray[i] >= (byte) 0x81 && byteArray[i] <= (byte) 0x9f)
					|| (byteArray[i] >= (byte) 0xe0 && byteArray[i] <= (byte) 0xef)) {
				if ((byteArray[i + 1] >= (byte) 0x40 && byteArray[i + 1] <= (byte) 0x7e)
						|| (byteArray[i + 1] >= (byte) 0x80 && byteArray[i + 1] <= (byte) 0xfc)) {
					i++;
				} else {
					value = false;
				}
			} else {
				value = false;
			}
		}
		return value;

	}
	/**
	 * 반각문자를 전각으로 변환
	 * @param s
	 * @return
	 */
	public static String toFullChar(String s) {
        if (s == null || "".equals(s)) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // 영문 알파벳 or 특수 문자
            if (c >= 0x21 && c <= 0x7e) {
                c += 0xfee0;
            }
            // 공백 (=space)
            else if (c == 0x20) {
                c = 0x3000;
            }
            sb.append(c);
        }
        return sb.toString();
	}

	/**
	 * 전각문자를 반각문자로 변환
	 * @param src
	 * @return
	 */
	public static String toHalfChar(String src) {
        if (src == null || "".equals(src)) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            // 영문 알파벳 or 특수 문자
            if (c >= '！' && c <= '～') {
                c -= 0xfee0;
            }
            // 공백 (=space)
            else if (c == '　') {
                c = 0x20;
            }
            sb.append(c);
        }
        return sb.toString();
	}
	/**
	 * 문자열을 반각으로 변환 및 공백을 제거
	 * @param src
	 * @return
	 */
	public static String removeEmptyToHalfChar(String src) {
		String owacNm = StrUtil.toHalfChar(src).replaceAll(" ", "");
		owacNm = owacNm.replaceAll("\\p{Z}", "");
		return owacNm;
	}
	/**
	 * 공백 제거 및 대소문자 변환
	 * @param src 문자열
	 * @param div 0 : 소문자, 1: 대문자
	 * @return
	 */
	public static String removeEmptyToCaseStr(String src, int div) {
		String result = StrUtil.removeEmptyToHalfChar(src);
		if( div == 0) {
			result = result.toLowerCase();
		}else if( div == 1) {
			result = result.toUpperCase();
		}
		return result;
	}
	
	/**
	 * 공백 및 null 제거
	 * @param str
	 * @return
	 */
	public static String nvl(String str) {
		return nvl(str, "");
	}
	
	/**
	 * 공백 및 null 제거
	 * @param str
	 * @return
	 */
	public static String nvl(String str, String dflt) {
		if(str == null) return dflt;
		
		str = str.trim();
		
		if("".equals(str)) return dflt;
		else return str;
	}
	
	/**
	 * Byte 길이만큼 데이터를 잘라내어 오른쪽에 ' '을 패딩하여 리턴합니다.
	 * @param sData 원본 데이터
	 * @param len 길이
	 * @param charSet 인코딩
	 * @return 변환된 값
	 * @throws UnsupportedEncodingException
	 */
	public static String getDataR(String sData, int len, String charSet){
		
		ByteArrayOutputStream stream = null;
		try {
			if(sData == null) sData = "";
			
			byte[] target;
			int bLen = sData.getBytes(charSet).length;
			
			if(bLen < len) {
				stream = new ByteArrayOutputStream(len);
				stream.write(sData.getBytes(charSet));
				stream.write(StringUtils.repeat(' ', len - bLen).getBytes(charSet));
				target = stream.toByteArray();
			}else {
				target = sData.getBytes(charSet);
			}
			
			sData = new String(target, 0, len, charSet);
		} catch (UnsupportedEncodingException e) {
			log.error("UnsupportedEncodingException ", e);
		} catch (IOException e) {
			log.error("IOException ", e);
		}finally {
			if(stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					log.error("IOException e",e);
				}
		}
		return sData;
	}
	/**
	 * Byte 길이만큼 데이터를 잘라내어 왼쪽에 '0'패딩하여 리턴합니다.
	 * @param sData 원본 데이터
	 * @param len 길이 
	 * @param charSet 인코딩
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getDataL(String sData, int len, String charSet){
		if(sData == null) sData = "0";
		ByteArrayOutputStream stream = null;
		try {
			
			byte[] target;
			int bLen = sData.getBytes(charSet).length;
			
			if(bLen < len) {
				stream = new ByteArrayOutputStream(len);
				stream.write(StringUtils.repeat('0', len - bLen).getBytes(charSet));
				stream.write(sData.getBytes(charSet));
				target = stream.toByteArray();
			}else {
				target = sData.getBytes(charSet);
			}
			sData = new String(target, 0, len, charSet);
		} catch (UnsupportedEncodingException e) {
			log.error("error ", e);
		} catch (IOException e) {
			log.error("error ", e);
		}finally {
			if(stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					log.error("IOException e",e);
				}
		}
		return sData;
	}
	   /**
     * 0-9 까지의 숫자외의 값을 제거합니다.
     * @param strValue
     * @return
     */
    public static String getNumberOnly(String strValue){
    	if(strValue == null) return "";
    	String noNumRegExp = new String("[^0-9]");
    	return strValue.replaceAll(noNumRegExp, "");
    }

    /**
     * Buffer에서 Number형 데이터를 가져와 리턴합니다.
     * 리턴할때 앞에 패딩되어 있는 0을 제거합니다.
     * @param buf Buffer
     * @param idx offSet
     * @param len 길이
     * @param charSet 인코딩
     * @return
     */
    public static String getBufferDataL(byte[] buf, int idx, int len, String charSet) {
    	String rlt = getBufferData(buf, idx, len, charSet).trim();
    	if(rlt == null || "".equals(rlt)) rlt = "0";
    	
    	DecimalFormat df = new DecimalFormat("0"); 

    	return df.format(new BigDecimal(rlt));
    }
    /**
     * Buffer에서 String 형 데이터를 가져와 리턴합니다.
     * @param buf Buffer
     * @param idx offSet
     * @param len 길이
     * @param charSet 인코딩
     * @return
     */
    public static String getBufferDataR(byte[] buf, int idx, int len, String charSet) {
    	return getBufferData(buf, idx, len, charSet).trim();
    }
    /**
     * Buffer에서 String 형 데이터를 가져와 리턴합니다.
     * @param buf Buffer
     * @param idx offSet
     * @param len 길이
     * @param charSet 인코딩
     * @return
     */
    public static String getBufferData(byte[] buf, int idx, int len, String charSet) {
    	String rlt = "";
    	try {
			rlt = new String(buf, idx, len, charSet);
		} catch (UnsupportedEncodingException e) {
			log.error("error getBufferData : ", e);
		}
    	return rlt;
    }
}
