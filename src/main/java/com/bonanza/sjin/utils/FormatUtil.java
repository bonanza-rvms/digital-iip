package com.bonanza.sjin.utils;

import java.text.DecimalFormat;

/**
 * 데이터 포맷팅 처리하는 클래스입니다.
 * 계좌번호, 금액 등
 * @author Jin
 *
 */
public class FormatUtil {
	   /**
     * 숫자에 컴마표기
     * @param nVal
     * @return
     */
    public static String getMoney(int nVal) {
        String displayForm = new DecimalFormat("#,###").format(nVal);
        return displayForm;
    }
    /**
     * 숫자에 컴마표기
     * @param nVal
     * @return
     */
    public static String getMoney(long nVal) {
        String displayForm = new DecimalFormat("#,###").format(nVal);
        return displayForm;
    }
    /**
     * 숫자에 컴마표기
     * @param nVal
     * @return
     */
    public static String getMoney(double nVal) {
        String displayForm = new DecimalFormat("#,###").format(nVal);
        return displayForm;
    }
    /**
     * 숫자에 컴마표기
     * @param strVal
     * @return
     */
    public static String getMoney(String strVal) {
        return getMoney(strVal,"0");
    }
    /**
     * 숫자에 컴마표기
     * @param strVal
     * @param defValue
     * @return
     */
    public static String getMoney(String strVal, String defVal){
    	if(null == strVal || "".equals(strVal.trim()) || "".equals(strVal.trim().replaceAll(",","")) ){
       		return defVal;
        }
        strVal = strVal.trim().replaceAll(",","");
        double nVal = Double.parseDouble(strVal);
        String displayForm = new DecimalFormat("#,###").format(nVal);
        return displayForm;
    }    
    
    /**
     * 끝 4자리를 제외한 나머지 숫자를 마스킹하여 리턴합니다.
     * @param acctNo
     * @return
     */
    public static String getMaskAcct(String acctNo) {
    	String rlt = "";
    	if(acctNo == null) return rlt;
    	int len = acctNo.length();
    	if(len < 5) {
    		return acctNo;
    	}else {
    		for(int i=0; i<len-4; i++) {
    			rlt += "*";
    		}
    		rlt = rlt + acctNo.substring(len-4);
    	}
    	
    	return rlt;
    }
    
    /**
     * 숫자에 컴마표기 (소수점 2자리)
     * @param strVal
     * @return
     */
    public static  String getDecimal(String strVal) {
        
    	strVal = getDecimal(strVal,2);
        return strVal;
    }
    
	/**
	 * 숫자에 컴마표기 (소수점자리)
	 * @param strVal
	 * @param decimal 소수점 자리수
	 * @return
	 */
    public static  String getDecimal(String strVal, int decimal) {
       
        DecimalFormat dFormatter = null;
        StringBuffer format = new StringBuffer();
        
        format.append("#,##0");
        
        if(0 < decimal){
        	format.append(".");
        }
        
        for(int i=0; i < decimal; i++){
        	format.append("0");
        }
        
        dFormatter  = new DecimalFormat(format.toString());
        
        strVal = dFormatter.format(Double.parseDouble(strVal));

        return strVal;
    }
}
