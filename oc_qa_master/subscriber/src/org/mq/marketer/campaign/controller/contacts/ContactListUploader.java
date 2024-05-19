package org.mq.marketer.campaign.controller.contacts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Contacts;
import org.mq.marketer.campaign.beans.CustomFieldData;
import org.mq.marketer.campaign.beans.MLCustomFields;
import org.mq.marketer.campaign.beans.MailingList;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.ContactsDaoForDML;
import org.mq.marketer.campaign.dao.CustomFieldDataDao;
import org.mq.marketer.campaign.dao.CustomFieldDataDaoForDML;
import org.mq.marketer.campaign.dao.MLCustomFieldsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.Utility;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataIntegrityViolationException;

import au.com.bytecode.opencsv.CSVReader;

@SuppressWarnings("unchecked")
public class ContactListUploader implements ApplicationContextAware {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private ApplicationContext context;
	public void setApplicationContext(ApplicationContext context) throws BeansException {
			this.context = context;
	}
	
	
	
	public List uploadContactList(ContactsDao contactsDao,ContactsDaoForDML contactsDaoForDML,CustomFieldDataDao cfDataDao,CustomFieldDataDaoForDML cfDataDaoForDML,MailingList ml,
			String path,char delimiter,List fieldIndexList,Map<String,String> cfMap,Map<String,String> genFieldMap, Users user){
		
		
		logger.debug("path ::"+path +">>>and fieldIndexList is ::"+fieldIndexList +">>> and cfMap is ::"+cfMap);
		
		
		List result = new ArrayList();
		int totLines = 0, uploaded = 0, dataLines = 0, existCount = 0, invalidCount = 0;
		StringBuffer existSB = new StringBuffer();
		StringBuffer invalidSB = new StringBuffer();
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(path),delimiter);
			
			String [] nextLine;
			Contacts contact = null;
			CustomFieldData customFieldData = null;
		    
			Class strArg[] = new Class[]{String.class};
			Class longArg[] = new Class[]{Long.class};
			Class intArg[] = new Class[]{Integer.TYPE};
			Class calArg[] = new Class[]{Calendar.class};
			
			List<Method> contMethList = new ArrayList<Method>();
			contMethList.add(Contacts.class.getMethod("setEmailId",strArg));
			contMethList.add(Contacts.class.getMethod("setFirstName",strArg));
			contMethList.add(Contacts.class.getMethod("setLastName",strArg));
			contMethList.add(Contacts.class.getMethod("setAddressOne",strArg));
			contMethList.add(Contacts.class.getMethod("setAddressTwo",strArg));
			contMethList.add(Contacts.class.getMethod("setCity",strArg));
			contMethList.add(Contacts.class.getMethod("setState",strArg));
			contMethList.add(Contacts.class.getMethod("setCountry",strArg));
			contMethList.add(Contacts.class.getMethod("setPin",intArg));
			contMethList.add(Contacts.class.getMethod("setPhone",longArg));
			
			contMethList.add(Contacts.class.getMethod("setGender",strArg));
			contMethList.add(Contacts.class.getMethod("setBirthDay",calArg));
			contMethList.add(Contacts.class.getMethod("setAnniversary",calArg));

			int emailIndex = getEmailIndex(fieldIndexList);
			int phoneIndex = getPhoneIndex(fieldIndexList);
			if(logger.isDebugEnabled()) logger.debug("Email Index : " + emailIndex);
			 logger.debug("Phone Index : " + phoneIndex);
			if(emailIndex == -1 && phoneIndex == -1) {
				result.add(null);
				result.add(existSB);
				result.add(invalidSB);//TODO 
				return result;
			}
			//TODO need to remove the hard coded string and use constants class
			logger.debug("ML optin status is : "+ ml.getCheckDoubleOptin());
			//String status =  ml.getCheckDoubleOptin() == true?("Optin pending"):("Active");
			
			//logger.debug("Status is :"+ status);
			
			
			MLCustomFieldsDao mlCfDataDao	= (MLCustomFieldsDao)context.getBean("mlCustomFieldsDao");
			
			Map<Integer, MLCustomFields> mlCustMap = new HashMap<Integer, MLCustomFields>();
			
			if(ml.isCustField()) {
				List<MLCustomFields> cfDataList = mlCfDataDao.findAllByList(ml);
				for (MLCustomFields mlCustField : cfDataList) {
					logger.debug("ml customField index from DB ::"+mlCustField.getFieldIndex());
					mlCustMap.put(mlCustField.getFieldIndex(), mlCustField);
				}
			} //if
			
			/********new code*********/
			String existContact = "";
			
//			while ((nextLine = reader.readNext()) != null) {
				
			FileReader fileReader = new FileReader(path);
			BufferedReader br = new BufferedReader(fileReader);
			
			String lineStr=null;
			
			while((lineStr = br.readLine())!= null) {
//				lineStr += ",\"0\"";
				if(lineStr.trim().length()==0) continue;
				
				nextLine = parse(lineStr);// lineStr.split(csvDelemiterStr);
				
				logger.info("nextLine length is::"+nextLine);
				
				
				
			/**********************************/	
				totLines++;
				dataLines++;
				int lineTokenCount = nextLine.length;
				
				//Manditory fields not existe ignore the record.(Email and Phone)
				if(lineTokenCount==1 && nextLine[0]!=null && nextLine[0].trim().equals("")) {
			    	if(logger.isDebugEnabled()) logger.debug("Empty line");
			    	invalidSB.append(totLines + "  \r\n   ");
					invalidCount++;
			    	continue;
			    }
				
				String email = null;
				if(emailIndex !=-1) {
					
					email = nextLine[emailIndex].trim();
				}
				
				String phone = "";
				
				if(phoneIndex !=-1) {
					
					//Regex for Phone
					phone = Utility.phoneParse(nextLine[phoneIndex].trim(),user !=null ? user.getUserOrganization() : null );
					
					try {
						Long.parseLong(phone);
					} catch (Exception e) {
						phone = "";
					}
				}
				
				//Email and Phone Validation
				if(!Utility.validateEmail(email) && (phone == null || phone.trim().length() == 0)) {
					if(logger.isDebugEnabled()) logger.debug("Invalid Email: " + email);
					
					String msg = ""+totLines;
					if(emailIndex !=-1) {
						 msg = msg + "  " + nextLine[emailIndex].trim();
						
//						email = nextLine[emailIndex].trim();
					}
					if(phoneIndex != -1) {
						msg = msg+","+nextLine[phoneIndex].trim();
					}
					invalidSB.append( msg+ "\r\n   ");
					/*if(msg.trim().length()  > 0) {
					}
*/					invalidCount++;
					continue;
				}
				
				
				
				
				//logger.debug("<<<<<Before contact ");
				contact  = contactsDao.findContactByValues(email, phone, null, user.getUserId() );
//				contact  = contactsDao.findContctsFromListId(email, ml.getListId());
				if(contact != null) {
					//logger.debug("contact alreday exists ignore it..."+contact.getEmailId());
					
					if(email != null && email.trim().length() >0) {
						if(phone!= null && phone.trim().length() >0){
							existContact = email+","+phone;
						}else {
							existContact = email;
						}
					}else {
						if(phone != null) existContact = phone;
					}
					existSB.append(totLines + "  " + existContact +"\r\n   ");
					existCount++;
//					continue;
					
				}else  {
					contact = new Contacts(ml,MyCalendar.getNewCalendar(), Constants.CONT_STATUS_PURGE_PENDING);
					
					//added after implementation for contact's optin medium
					contact.setOptinMedium(Constants.CONTACT_OPTIN_MEDIUM_ADDEDMANUALLY);
				}
				
				
				
				Method tempMethod;
				int tempInt;
				if(emailIndex > lineTokenCount){
					if(logger.isDebugEnabled()) logger.debug("Line tokens are less than the email Index");
					invalidSB.append(totLines + "  \r\n   ");
					invalidCount++;
					continue;
				}
				
				
				//logger.info("Field Index List :"+ fieldIndexList);
				
				for (int i = 0; i < fieldIndexList.size(); i++) {
					
					if(i>= lineTokenCount || nextLine[i].trim().length()==0) continue;
					
					tempInt = (Integer)fieldIndexList.get(i)-1;
					
					// TODO : checks for middle custom fields, data is -1.
					if(tempInt < 0 ) {
						continue;
					}
					
					tempMethod = contMethList.get(tempInt);
					if(i < lineTokenCount ) {
						Object [] params = null; 
						
						
						String tempDateFormat;
						
						
						if(tempInt==8) {
							try {
								params = new Object[]{Integer.parseInt(nextLine[i].trim())};
							} catch (Exception e) {
								if(logger.isDebugEnabled()) logger.debug("Invalid data for Pin : " + nextLine[i].trim());
								continue;
							}
						}
						else if(tempInt==9) {
							try {
								//TODO
								//params = new Object[]{Long.parseLong(nextLine[i].trim())};
								params = new Object[]{Long.parseLong(phone)};
								contact.setMobileStatus(Constants.CON_MOBILE_STATUS_ACTIVE);
							} catch (Exception e) {
								if(logger.isDebugEnabled()) logger.debug("Invalid data for Phone : " + nextLine[i].trim());
								contact.setMobileStatus(null);
								continue;
							}
						}else if(tempInt == 11 && genFieldMap.get("BirthDay") != null) { //BirthDay
							
							
							logger.debug("Birth Day format is : "+genFieldMap.get("BirthDay"));
							
							
							tempDateFormat	=genFieldMap.get("BirthDay");
							String dataStr = nextLine[i].trim();
							if(CustomFieldValidator.validateDate(dataStr ,"Date" ,tempDateFormat)){
								logger.debug("dataStr is:: "+dataStr);
								
								Date date; 
								DateFormat formatter = new SimpleDateFormat(tempDateFormat);
								date = (Date)formatter.parse(dataStr); 
								logger.debug("Date is >>>"+date);
								Calendar cal = Calendar.getInstance();
								cal.setTime(date);
								logger.debug("cal time is ::"+cal.getTime());
								params = new Object[]{cal};
								logger.debug("params  ::"+params);
								
							}
							
						}else if(tempInt == 12 && genFieldMap.get("Anniversary") != null) { //Anniversay
							
							logger.debug("Birth Day format is : "+genFieldMap.get("Anniversary"));
							tempDateFormat	=genFieldMap.get("Anniversary");
							String dataStr = nextLine[i].trim();
							
							if(CustomFieldValidator.validateDate(dataStr ,"Date" ,tempDateFormat)){
								logger.debug("dataStr is:: "+dataStr);
								
								Date date; 
								DateFormat formatter = new SimpleDateFormat(tempDateFormat);
								date = (Date)formatter.parse(dataStr); 
								logger.debug("Date is >>>"+date);
								Calendar cal = Calendar.getInstance();
								cal.setTime(date);
								logger.debug("cal time is ::"+cal.getTime());
								params = new Object[]{cal};
								logger.debug("params  ::"+params);
								
							}

						}
						else if(tempInt < 11 || tempInt > 12) {
							//logger.debug("tempINT value is::"+tempInt);
							params = new Object[]{nextLine[i].trim()};
						}
						if(params  != null) {
							
							tempMethod.invoke(contact, params);
						}
					}
				} //for default variables settings
				
				boolean isContactCreated = false;
				
				try {
					
					if(contact.getExternalId() == null) {
						contact.setExternalId("-1");
					} 
					
					contactsDaoForDML.saveOrUpdate(contact);
					isContactCreated = true;
					uploaded++;
				} catch (DataIntegrityViolationException dive) {
					existSB.append(totLines + "  " + email + "\r\n   ");
					existCount++;
					if(logger.isDebugEnabled()) logger.error("DataIntegrityViolationException1 : " + dive.getMessage());
				}catch (Exception e) {
					invalidSB.append(totLines + "  " + email + "\r\n   ");
					invalidCount++;
					if(logger.isDebugEnabled()) logger.error("Invalid email1 : " + e.getMessage());
				}
				
				if(cfMap!=null && isContactCreated) {
					customFieldData = new CustomFieldData(contact);
					Set<String> keys = cfMap.keySet();
					
					logger.debug(" keys size is ::"+keys.size());
					
					String dt,data,value;
					String[] cfInfo;
					boolean isCFDataSet = false;
					
					//new Code
					MLCustomFields tempCF;
					
					for (String key: keys) {
						//logger.debug("key value is ::"+key);
						value = cfMap.get(key);
						
						//logger.debug("value is >>"+value);
						
						cfInfo = value.split(":");
						dt = cfInfo[2];
						int cfIndex = Integer.parseInt(cfInfo[0])-1;
						
						//logger.debug("cfIndex is >>"+cfIndex);
						
						if(lineTokenCount<=cfIndex) continue;
						data = nextLine[cfIndex].trim();
						
						//logger.debug("data is >>>"+data);
						tempCF = mlCustMap.get(Integer.parseInt(""+key.charAt(key.length()-1)));
						
						logger.debug("tempCF >>>"+tempCF);
						
						tempMethod = CustomFieldData.class.getMethod("setCust" + 
								key.substring("Custom Field".length()),strArg);
						logger.debug("tempCF.getDataType()>>>"+tempCF.getDataType()+" ::tempCF.getFormat() "+tempCF.getFormat() +" :: data is >>>"+data);
						//if selected type is Date 
						if(tempCF.getDataType().contains("Date")) {
							
							logger.debug("dateFormat is ::"+tempCF.getFormat());
							
							if(CustomFieldValidator.validateDate(data, tempCF.getDataType(), tempCF.getFormat())) {
								Object [] params = new Object[]{data};
								tempMethod.invoke(customFieldData, params);
								isCFDataSet = true;
							}
						}
						else if(CustomFieldValidator.validate(data,dt)){
							Object [] params = new Object[]{data};
							tempMethod.invoke(customFieldData, params);
							isCFDataSet = true;
						}
					} // for cfMap
					
					if(isCFDataSet) {
						try {
							cfDataDaoForDML.saveOrUpdate(customFieldData);
						} catch (Exception e) {
							if(logger.isDebugEnabled()) 
								logger.debug("Problem while saving custom field : ", (Throwable)e);
						}
					}
					
					keys = null;
				} // if(cfMap!=null)
				
				contact = null;
				customFieldData = null;
				//if(totLines%1000==0) {  System.gc(); }
			} // while reading csv file
			
		} catch (FileNotFoundException e) {
			if(logger.isDebugEnabled()) logger.debug("CSV file not found : ", (Throwable)e);
		} catch (Exception e) {
			if(logger.isDebugEnabled()) logger.debug("Exception : ", (Throwable)e);
		} finally{
			try {
				if(reader!=null) reader.close();
			} catch (Exception e) {
			}
		}
		
		result.add(uploaded + ":" + dataLines + ":" + existCount + ":" + invalidCount);
		result.add(existSB);
		result.add(invalidSB);
		//System.gc();
		return result;
	} // uploadContactList
	
	private int getEmailIndex(List<Integer> fieldList){
		for (int i = 0 ; i <fieldList.size() ; i++) {
			if(fieldList.get(i)==1)
				return i;
		}
		return -1;
	} // getEmailIndex
	private int getPhoneIndex(List<Integer> fieldList){
		for (int i = 0 ; i <fieldList.size() ; i++) {
			if(fieldList.get(i)==10)
				return i;
		}
		return -1;
	} // getPhoneIndex
	
	
	private final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");  
	private ArrayList<String> allMatches = new ArrayList<String>();        
	private Matcher matcher = null;
	
	public String[] parse(String csvLine) {
        matcher = csvPattern.matcher(csvLine);
        allMatches.clear();
        String match;
        while (matcher.find()) {
                match = matcher.group(1);
                if (match!=null) {
                        allMatches.add(match);
                }
                else {
                        allMatches.add(matcher.group(2));
                }
        }

        if (allMatches.size() > 0) {
                return allMatches.toArray(new String[allMatches.size()]);
        }
        else {
                return new String[0];
        }                       
    }  // parse 
	/*private final Pattern phonePattern = Pattern.compile("\\b1?\\s*\\(?([0-9]{3})\\)?[-. *]*([0-9]{3})[-. ]*([0-9]{4})\\b");  
	
	public String phoneParse(String csvLine) {
        matcher = phonePattern.matcher(csvLine);
        String poneMatch = "";
        while (matcher.find()) {
        	poneMatch = matcher.group(1)+matcher.group(2)+matcher.group(3);
                
        }
       return poneMatch;
                             
    }  // phoneParse 
*/	
	
	
	
}
