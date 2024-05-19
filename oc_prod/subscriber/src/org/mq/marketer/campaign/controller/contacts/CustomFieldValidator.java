package org.mq.marketer.campaign.controller.contacts;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
@SuppressWarnings("unused")
public class CustomFieldValidator {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public static boolean validate(String data,String datatype){
		boolean isValid = false;
		try{
			if(data.equalsIgnoreCase("null") || data.equalsIgnoreCase("")) {
				isValid = true;
			}else if(datatype.equalsIgnoreCase("String")){
				isValid = true;
			}else if(datatype.equalsIgnoreCase("Date")) {
				String dtFormat = PropertyUtil.getPropertyValue("customFiledDateFormat");
				DateFormat format = new SimpleDateFormat(dtFormat);
				format.setLenient(false);
				Date date = format.parse(data);
				isValid = true;
			}else if(datatype.equalsIgnoreCase("Number")){
				Long l = Long.parseLong(data);
				isValid = true;
			}else if(datatype.equalsIgnoreCase("Double")){
				Double d = Double.parseDouble(data);
				isValid = true;
			}else if(datatype.equalsIgnoreCase("boolean")){
				if(data.equalsIgnoreCase("yes") || data.equalsIgnoreCase("no") || data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false") || data.equalsIgnoreCase("null") ){
					isValid = true;
				}
			}
		}catch(java.lang.NumberFormatException nfe){
			isValid = false;
		}catch (java.text.ParseException pe){
			isValid = false;
		}catch (IllegalArgumentException e) {
	    	isValid = false;
	    }catch(Exception e){
			isValid = false;
		}
		return isValid;
	}
	
	public static boolean validateDate(String data,String datatype, String dateFormat){
		boolean isValid = false;
		logger.info("--data >>"+data +":: dataType is>>"+datatype +" :: dateFormat is ::"+dateFormat);
		try{
			 if(data != null && datatype.toLowerCase().startsWith("date")) {
				//String usdateFormat = PropertyUtil.getPropertyValue("customFiledDateFormat"); //mm/dd/yyyy
				DateFormat format = new SimpleDateFormat(dateFormat);
				format.setLenient(false);
				Date date = format.parse(data);
				isValid = true;
			}
		}catch(java.lang.NumberFormatException nfe){
			isValid = false;
		}catch (java.text.ParseException pe){
			isValid = false;
		}catch (IllegalArgumentException e) {
	    	isValid = false;
	    }catch(Exception e){
			isValid = false;
		}
		return isValid;
	} // validateDate
	
	
	public static boolean validateDate(String data, String dateFormat) {
		boolean isValid = false;
		try{
			
			//String usdateFormat = PropertyUtil.getPropertyValue("customFiledDateFormat"); //mm/dd/yyyy
			if(data != null && dateFormat != null) {
				
				DateFormat format = new SimpleDateFormat(dateFormat);
//				format.setLenient(false);
				Date date = format.parse(data);
				isValid = true;
			}
		}catch(java.lang.NumberFormatException nfe){
			isValid = false;
		}catch (java.text.ParseException pe){
			isValid = false;
		}catch (IllegalArgumentException e) {
	    	isValid = false;
	    	logger.error("Exception ::", e);
	    }catch(Exception e){
			isValid = false;
		}
		return isValid;
	} // validateDate
	
}
