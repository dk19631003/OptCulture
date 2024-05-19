package org.mq.optculture.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.mq.marketer.campaign.general.Constants;

public class OptCultureUtils {

	public static String getParameterJsonValue(HttpServletRequest request) throws IOException{
		String jsonValue = request.getParameter(OCConstants.JSON_VALUE);
		if(jsonValue == null || jsonValue.trim().isEmpty()){
			InputStream is = request.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			char[] cbuf = new char[1024];
			int bytesRead = 0;
			StringBuffer sb = new StringBuffer();
			while((bytesRead = br.read(cbuf)) > 0){
				sb.append(cbuf, 0, bytesRead);
			}
			jsonValue = sb.toString();
			br.close();
		}
		return jsonValue;
	}
	
	public static  String getParameterDRJsonValue(HttpServletRequest request) throws IOException{
		String jsonValue = request.getParameter(OCConstants.JSON_DR_VALUE);
		if(jsonValue == null || jsonValue.trim().isEmpty()){
			InputStream is = request.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			char[] cbuf = new char[1024];
			int bytesRead = 0;
			StringBuffer sb = new StringBuffer();
			while((bytesRead = br.read(cbuf)) > 0){
				sb.append(cbuf, 0, bytesRead);
			}
			jsonValue = sb.toString();
			br.close();
		}
		return jsonValue;
	}

	/**
	 * Extracts card number from %B97417441669724512345^?;9741744166972451234=? sort of Strings.
	 * These strings are part of request data from POS system
	 * @param cardNumber
	 * @return
	 */
	public static String parseCardNumber(String cardNumber){
		Pattern digitPattern = Pattern.compile("(\\d+)");  // %B97417441669724512345^?;9741744166972451234=?
		Matcher matcher = digitPattern.matcher(cardNumber);
		String parsedCard = "";
		while(matcher.find()){
			if(parsedCard.length() == 15 || parsedCard.length() == 16 ) break;
			parsedCard += matcher.group(1).trim();
        } // while
		return parsedCard;
	}
	
	public static Long validateCardNumber(String cardNumber) throws Exception {

		String card = null;
		card = cardNumber;
		Pattern digitPattern = Pattern.compile("(\\d+)");  // %B974174416697245^?;974174416697245=?
		Matcher matcher = null;
		Long cardLong = null;
		try {
			String cardNum = "";
			matcher = digitPattern.matcher(card);
			while (matcher.find()) {
				if(cardNum.length() == 15 || cardNum.length() == 16 ) break;
				cardNum += matcher.group(1).trim();
			} // while
			card = cardNum;
			cardLong = Long.parseLong(card);
		} catch(NumberFormatException e) {
			return null;
		}
		return cardLong;
	}
	
	
	
	public static String validateOCLtyCardNumber(String cardNumber) throws Exception {

		String card = null;
		card = cardNumber;
		Pattern digitPattern = Pattern.compile("(\\d+)");  // %B974174416697245^?;974174416697245=?
		Matcher matcher = null;
		//Long cardLong = null;
		try {
			String cardNum = "";
			matcher = digitPattern.matcher(card);
			while (matcher.find()) {
				//if(cardNum.length() == 15 || cardNum.length() == 16 ) break;
				cardNum += matcher.group(1).trim();
			} // while
			card = cardNum;
			//cardLong = Long.parseLong(card);
		} catch(NumberFormatException e) {
			return null;
		}
		return card;
	}
	public static String validateOCLtyDCardNumber(String cardNumber, String validationRule) throws Exception {
		if(cardNumber != null && validationRule !=null){
			String dynamic[] = validationRule.split(Constants.ADDR_COL_DELIMETER);
			if(dynamic[0].equals("A")){
				if(cardNumber.matches("^[a-zA-Z0-9]{"+dynamic[1]+"}$")){
					return cardNumber;
				}
				return null;
				
			}else if(dynamic[0].equals("N")){
				if(cardNumber.matches("\\d{"+dynamic[1]+"}")){
					return cardNumber;
				}
				return null;
			}
			
		}

		return null;
	}
	
}
