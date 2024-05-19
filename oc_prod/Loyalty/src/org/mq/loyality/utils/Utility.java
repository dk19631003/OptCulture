package org.mq.loyality.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.hbmbean.UserOrganization;
import org.mq.loyality.common.hbmbean.Users;

public class Utility {
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
public static Set<String> findCoupPlaceholders(String content) {
		
		String cfpattern = "\\[([^\\[]*?)\\]";
		
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();

		try {
			while(m.find()) {
	
				ph = m.group(1); //.toUpperCase()
				//logger.info("Ph holder :" + ph);
	
				
				 if(ph.startsWith("CC_")) {
					 totalPhSet.add(ph);
				 }
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			return totalPhSet;
		}
		
		return totalPhSet;
	}
	
public static String phoneParse(String csvLine, UserOrganization userOrganization,Users user ) {
/*	if(userOrganization == null ){
		logger.info("User does not belong to any organization.", "color:red;","top");
		return null ;

	}
//	logger.debug("Validating Moblie Number"+csvLine +"      UserOranization.... "+userOrganization.getOrganizationName());
	while(csvLine.startsWith("0")){
					csvLine =  csvLine.substring(1);
				}
			
	Pattern phonePattern = null;
	String poneMatch = null;
	if(userOrganization.isMobilePattern()){
		//For US we need to have pattern
		phonePattern = Pattern.compile("\\b(1?|91?|92?)\\s*\\(?([0-9]{3})\\)?[-. *]*([0-9]{3})[-. ]*([0-9]{4})\\b");
		Matcher matcher = phonePattern.matcher(csvLine);
		while (matcher.find()) {
			poneMatch = matcher.group(1)+matcher.group(2)+matcher.group(3)+matcher.group(4);

		}
		if( poneMatch != null ) {

			if( (poneMatch.startsWith("91") && poneMatch.startsWith("910000000000") ) ||
					(poneMatch.startsWith("92") && poneMatch.startsWith("920000000000") ) || 
					(poneMatch.startsWith("1") && poneMatch.trim().length() == 10 )   || 
					poneMatch.startsWith("0000000000")  || 
					poneMatch.startsWith("10000000000") ||
					(poneMatch.startsWith("9") && poneMatch.length() == 11	)) {
				//	System.out.println("Returning NULL ");
				return null;
			}
		}

		return poneMatch;
	}
	else{
		//default pattern \\b(1?|91?|92?|971?)[0-9]{7,9}\\b
		if(csvLine.startsWith("00") || csvLine.startsWith("0")  ) {
			String prefix = csvLine.startsWith("00") ? csvLine.substring(0,2)  : csvLine.substring(0,1); 
			csvLine = csvLine.substring(prefix.length());
		}

		if(csvLine.startsWith("971")) {
		//	phonePattern = Pattern.compile("\\b(971?|)[0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
			phonePattern = Pattern.compile("\\b(971)[0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
			Matcher matcher = phonePattern.matcher(csvLine);
			//String poneMatch = null;
			while (matcher.find()) {

				poneMatch = matcher.group();
				//       System.out.println(",........"+poneMatch);    
			}
			if( poneMatch != null ) {

				if((poneMatch.startsWith("971") && (poneMatch.startsWith("9710000000") || poneMatch.startsWith("97100000000") 
						|| poneMatch.startsWith("971000000000")) )) {
					//System.out.println("Returning NULL ");
					return null;
				}
			}
		}else if(!(csvLine.startsWith("971"))){
			
			phonePattern = Pattern.compile("^\\d{9}$");
			Matcher matcher = phonePattern.matcher(csvLine);
			//String poneMatch = null;
			while (matcher.find()) {

				poneMatch = matcher.group();
				//       System.out.println(",........"+poneMatch);    
			}
		//	poneMatch = csvLine;
		}

		return poneMatch;


	}
*/
	try {
		String userCountryCarrier=user.getCountryCarrier().toString();
	if(userOrganization == null ){
		logger.info("User does not belong to any organization.", "color:red;","top");
		return null ;

	}
	if(!userOrganization.isRequireMobileValidation()) return csvLine;
//	logger.debug("Validating Moblie Number"+csvLine +"      UserOranization.... "+userOrganization.getOrganizationName());
	csvLine= csvLine.replaceAll("[- ()]", "");//APP-117
	while(csvLine.startsWith("0")){
		csvLine =  csvLine.substring(1);
	}
	Pattern phonePattern = null;
	String poneMatch = null;
	if(userOrganization.isMobilePattern()){
		//For US we need to have pattern
		phonePattern = Pattern.compile("\\b(1?|91?|92?)\\s*\\(?([0-9]{3})\\)?[-. *]*([0-9]{3})[-. ]*([0-9]{4})\\b");
		Matcher matcher = phonePattern.matcher(csvLine);
		while (matcher.find()) {
			poneMatch = matcher.group(1)+matcher.group(2)+matcher.group(3)+matcher.group(4);

		}
		if( poneMatch != null ) {

			if( (poneMatch.startsWith("91") && poneMatch.startsWith("910000000000") ) ||
					(poneMatch.startsWith("92") && poneMatch.startsWith("920000000000") ) || 
					(poneMatch.startsWith("1") && poneMatch.trim().length() == 10 )   || 
					poneMatch.startsWith("0000000000")  || 
					poneMatch.startsWith("10000000000") ||
					(poneMatch.startsWith("9") && poneMatch.length() == 11	)) {
				//	System.out.println("Returning NULL ");
				return null;
			}
		}

		return poneMatch;
	}
	else{
		//default pattern \\b(1?|91?|92?|971?)[0-9]{7,9}\\b
		if(csvLine.startsWith("00") || csvLine.startsWith("0")  ) {
			String prefix = csvLine.startsWith("00") ? csvLine.substring(0,2)  : csvLine.substring(0,1); 
			csvLine = csvLine.substring(prefix.length());
		}
		//Here Code is changed for including countries Qatar, Kuwait

		/*//if(csvLine.startsWith("971")) {
		//	phonePattern = Pattern.compile("\\b(971?|)[0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
		//	phonePattern = Pattern.compile("\\b(971)[0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
			phonePattern = Pattern.compile("\\b(971?|9?74?|965)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
			Matcher matcher = phonePattern.matcher(csvLine);
			//String poneMatch = null;
			while (matcher.find()) {

				poneMatch = matcher.group();
				//       System.out.println(",........"+poneMatch);    
			}
			if( poneMatch != null ) {

				if((poneMatch.startsWith("971") && (poneMatch.startsWith("9710000000") || poneMatch.startsWith("97100000000") 
						|| poneMatch.startsWith("971000000000")) )) {
					//System.out.println("Returning NULL ");
					return null;
				}
			}
		}else if(!(csvLine.startsWith("971"))){
			
			phonePattern = Pattern.compile("^\\d{9}$");
			Matcher matcher = phonePattern.matcher(csvLine);
			//String poneMatch = null;
			while (matcher.find()) {

				poneMatch = matcher.group();
				//       System.out.println(",........"+poneMatch);    
			}
		//	poneMatch = csvLine;
		}

		return poneMatch;


	}

}  // phoneParse
*/	
		if((csvLine.startsWith(userCountryCarrier)||csvLine.startsWith("+"+userCountryCarrier))) {
			
			//phonePattern = Pattern.compile("\\b(\\+?)(971?|974?|965?)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+(userOrganization.getMinNumberOfDigits()-1)+","+(userOrganization.getMaxNumberOfDigits()-1)+"})\\b");
			phonePattern = Pattern.compile("\\b(\\+?)("+userCountryCarrier+"?)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+(userOrganization.getMinNumberOfDigits()-1)+","+(userOrganization.getMaxNumberOfDigits()-1)+"})\\b");
			Matcher matcher = phonePattern.matcher(csvLine);
			//String poneMatch = null;
			while (matcher.find()) { 
				poneMatch = matcher.group();

			}
			
			if(csvLine.length() == 11){
				//phonePattern = Pattern.compile("\\b(\\+?)(971?|974?|965?)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"})\\b");
				phonePattern = Pattern.compile("\\b(\\+?)("+userCountryCarrier+"?)\\s*\\(?([0-9]{1})\\)?[-. *]*([0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"})\\b");
				Matcher m= phonePattern.matcher(csvLine);
				//String poneMatch = null;
				while (m.find()) { 
					poneMatch = m.group();
				}
			}
			
			if( poneMatch != null ) {

				if((poneMatch.startsWith(userCountryCarrier) && (poneMatch.startsWith(userCountryCarrier+"0000000") 
						|| poneMatch.startsWith(userCountryCarrier+"00000000") 
						|| poneMatch.startsWith(userCountryCarrier+"000000000")) )) {
					return null;
				}
			}
		}else if(!csvLine.startsWith(userCountryCarrier)){
			
			phonePattern = Pattern.compile("\\b[0-9]{"+userOrganization.getMinNumberOfDigits()+","+userOrganization.getMaxNumberOfDigits()+"}\\b");
			Matcher matcher = phonePattern.matcher(csvLine);
			//String poneMatch = null;
			while (matcher.find()) {

				poneMatch = matcher.group();
				//System.out.println(",........"+poneMatch);    
			}
		//	poneMatch = csvLine;
		}  

	}

	return poneMatch;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		logger.error("Exception in phone parse ",e);
		return null;
	} 

}  // phoneParse 

public static Map<String, Object> validateMobile(Users user, String phone) {
	Map<String, Object> resultMap = new HashMap<String, Object>();
	
	logger.info("phone is============>"+phone);
	if(phone!=null && phone.trim().length() !=0){
	phone = phone.trim();
	UserOrganization organization=  user!=null ? user.getUserOrganization() : null ;
	phone = phoneParse(phone, organization,user);
	if(phone != null && !phone.startsWith(user.getCountryCarrier().toString())
			&& phone.length() >= user.getUserOrganization().getMinNumberOfDigits() && phone.length() <=user.getUserOrganization().getMaxNumberOfDigits()) {
		phone = user.getCountryCarrier().toString() + phone;
		logger.info("phone is============>"+phone);
	}
	try {
		Long.parseLong(phone);
		resultMap.put("phone", phone);
		resultMap.put("isValid", true);
	} catch (Exception e) {
		resultMap.put("phone", phone);
		resultMap.put("isValid", false);
		return resultMap;
	}
	}
	
	return resultMap;
}

/**
 * This method use to display decimal or currency value into US format with Comma Separated format (eg: $ 921,921.50) .
 * @param numberToFormatted
 * @return decimalFormat
 */
public static String getAmountInUSFormat(Object numberToFormatted) {
	logger.info("entered in getAmountInUSFormat");
	String usFormatValue ="$ ";
	try {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		if (numberFormat instanceof DecimalFormat) {
			DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
			decimalFormat.applyPattern("#0.00");
			decimalFormat.setGroupingUsed(true);
			decimalFormat.setGroupingSize(3);

			usFormatValue = usFormatValue + decimalFormat.format(numberToFormatted);
		}//if

	} catch (Exception e) {
		usFormatValue = usFormatValue + numberToFormatted;
		logger.error("Exception While formatting the Number ",e);
	}
	logger.info("completed  getAmountInUSFormat");
	return usFormatValue;
}//getAmountInUSFormat

}
	
