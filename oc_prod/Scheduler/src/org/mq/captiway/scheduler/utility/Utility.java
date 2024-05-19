package org.mq.captiway.scheduler.utility;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.ExternalSMTPEventsProcessingThread;
import org.mq.captiway.scheduler.ExternalSMTPSpamEventsDestructor;
import org.mq.captiway.scheduler.beans.Bounces;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.ContactParentalConsent;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.EmailClient;
import org.mq.captiway.scheduler.beans.EmailQueue;
import org.mq.captiway.scheduler.beans.ExternalSMTPEvents;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.OrganizationStores;
import org.mq.captiway.scheduler.beans.POSMapping;
import org.mq.captiway.scheduler.beans.RetailProSalesCSV;
import org.mq.captiway.scheduler.beans.SMSSettings;
import org.mq.captiway.scheduler.beans.SkuFile;
import org.mq.captiway.scheduler.beans.SuppressedContacts;
import org.mq.captiway.scheduler.beans.UserOrganization;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.campaign.ExternalSMTPSpamEventsQueue;
import org.mq.captiway.scheduler.dao.BouncesDao;
import org.mq.captiway.scheduler.dao.BouncesDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignReportDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignSentDaoForDML;
import org.mq.captiway.scheduler.dao.ContactParentalConsentDao;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.ContactsDaoForDML;
import org.mq.captiway.scheduler.dao.CouponCodesDao;
import org.mq.captiway.scheduler.dao.CouponsDao;
import org.mq.captiway.scheduler.dao.CouponsDaoForDML;
import org.mq.captiway.scheduler.dao.EmailClientDao;
import org.mq.captiway.scheduler.dao.EmailQueueDao;
import org.mq.captiway.scheduler.dao.EmailQueueDaoForDML;
import org.mq.captiway.scheduler.dao.ExternalSMTPEventsDao;
import org.mq.captiway.scheduler.dao.ExternalSMTPEventsDaoForDML;
import org.mq.captiway.scheduler.dao.OrgSMSkeywordsDao;
import org.mq.captiway.scheduler.dao.OrganizationStoresDao;
import org.mq.captiway.scheduler.dao.POSMappingDao;
import org.mq.captiway.scheduler.dao.SMSSettingsDao;
import org.mq.captiway.scheduler.dao.SuppressedContactsDao;
import org.mq.captiway.scheduler.dao.SuppressedContactsDaoForDML;
import org.mq.captiway.scheduler.dao.UsersDao;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;


    /**
		unescape()
		encodeUrl()
		decodeUrl()
		mergeImages()
		mergeCouponWithTemplate()
	*/
@SuppressWarnings({ "unchecked", "serial" })
public class Utility 
{
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	
	public static String unescape(String s) {
		StringBuffer sbuf = new StringBuffer ();
		try {
				int l  = s.length() ;
				int ch = -1 ; 
				int b, sumb = 0;
				for (int i = 0, more = -1 ; i < l ; i++) {
				  /* Get next byte b from URL segment s */
				  switch (ch = s.charAt(i)) {
				case '%':
				  ch = s.charAt (++i) ;
				  int hb = (Character.isDigit ((char) ch) 
						? ch - '0'
						: 10+Character.toLowerCase((char) ch) - 'a') & 0xF ;
				  ch = s.charAt (++i) ;
				  int lb = (Character.isDigit ((char) ch)
						? ch - '0'
						: 10+Character.toLowerCase ((char) ch)-'a') & 0xF ;
				  b = (hb << 4) | lb ;
				  break ;
				case '+':
				  b = ' ' ;
				  break ;
				default:
				  b = ch ;
				  }
				  /* Decode byte b as UTF-8, sumb collects incomplete chars */
				  if ((b & 0xc0) == 0x80) {			// 10xxxxxx (continuation byte)
				sumb = (sumb << 6) | (b & 0x3f) ;	// Add 6 bits to sumb
				if (--more == 0) sbuf.append((char) sumb) ; // Add char to sbuf
				  } else if ((b & 0x80) == 0x00) {		// 0xxxxxxx (yields 7 bits)
				sbuf.append((char) b) ;			// Store in sbuf
				  } else if ((b & 0xe0) == 0xc0) {		// 110xxxxx (yields 5 bits)
				sumb = b & 0x1f;
				more = 1;				// Expect 1 more byte
				  } else if ((b & 0xf0) == 0xe0) {		// 1110xxxx (yields 4 bits)
				sumb = b & 0x0f;
				more = 2;				// Expect 2 more bytes
				  } else if ((b & 0xf8) == 0xf0) {		// 11110xxx (yields 3 bits)
				sumb = b & 0x07;
				more = 3;				// Expect 3 more bytes
				  } else if ((b & 0xfc) == 0xf8) {		// 111110xx (yields 2 bits)
				sumb = b & 0x03;
				more = 4;				// Expect 4 more bytes
				  } else /*if ((b & 0xfe) == 0xfc)*/ {	// 1111110x (yields 1 bit)
				sumb = b & 0x01;
				more = 5;				// Expect 5 more bytes
				  }
				  /* We don't test if the UTF-8 encoding is well-formed */
				}
		}  catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error(" ** Exception : Unescape failed for string: "+s);
			if(logger.isErrorEnabled()) logger.error(" ** Exception : "+e.getMessage());
			return s;
		}
		return sbuf.toString();	
	}

	public static boolean isModifiedContact(Contacts inputContact, Contacts dbContact) {
		
		if(	inputContact!=null && dbContact!=null)
		{
			if( (inputContact.getFirstName()!=null && !inputContact.getFirstName().isEmpty() && (dbContact.getFirstName() ==null || !dbContact.getFirstName().equalsIgnoreCase(inputContact.getFirstName())))||
					(inputContact.getLastName()!=null && !inputContact.getLastName().isEmpty() && (dbContact.getLastName() ==null || !dbContact.getLastName().equalsIgnoreCase(inputContact.getLastName())))||
					(inputContact.getMobilePhone()!=null && !inputContact.getMobilePhone().isEmpty() && (dbContact.getMobilePhone() ==null ||!dbContact.getMobilePhone().equalsIgnoreCase(inputContact.getMobilePhone())))||
					(inputContact.getEmailId()!=null && !inputContact.getEmailId().isEmpty() && (dbContact.getEmailId() ==null || !dbContact.getEmailId().equalsIgnoreCase(inputContact.getEmailId())))||
					(inputContact.getAddressOne()!=null && !inputContact.getAddressOne().isEmpty() && (dbContact.getAddressOne() ==null || !dbContact.getAddressOne().equalsIgnoreCase(inputContact.getAddressOne())))||
					(inputContact.getAddressTwo()!=null && !inputContact.getAddressTwo().isEmpty() && (dbContact.getAddressTwo() ==null || !dbContact.getAddressTwo().equalsIgnoreCase(inputContact.getAddressTwo())))||
					(inputContact.getCity()!=null && !inputContact.getCity().isEmpty() && (dbContact.getCity() ==null || !dbContact.getCity().equalsIgnoreCase(inputContact.getCity())))||
					(inputContact.getZip()!=null && !inputContact.getZip().isEmpty() && (dbContact.getZip() ==null || !dbContact.getZip().equalsIgnoreCase(inputContact.getZip())))||
					(inputContact.getCountry()!=null && !inputContact.getCountry().isEmpty() && (dbContact.getCountry() ==null || !dbContact.getCountry().equalsIgnoreCase(inputContact.getCountry())))||
					(inputContact.getState()!=null && !inputContact.getState().isEmpty() && (dbContact.getState() ==null || !dbContact.getState().equalsIgnoreCase(inputContact.getState())))||
					(inputContact.getHomeStore()!=null && !inputContact.getHomeStore().isEmpty() && (dbContact.getHomeStore() ==null || !dbContact.getHomeStore().equalsIgnoreCase(inputContact.getHomeStore())))||
					(inputContact.getGender()!=null && !inputContact.getGender().isEmpty() && (dbContact.getGender() ==null || !dbContact.getGender().equalsIgnoreCase(inputContact.getGender())))||
					//(inputContact.getBirthDay()!=null  && (dbContact.getBirthDay() ==null || !dbContact.getBirthDay().equals(inputContact.getBirthDay())))||
					//(inputContact.getAnniversary()!=null  && (dbContact.getAnniversary() ==null || !dbContact.getAnniversary().equals(inputContact.getAnniversary())))||
					//(inputContact.getCreatedDate()!=null  && (dbContact.getCreatedDate() ==null || !dbContact.getCreatedDate().equals(inputContact.getCreatedDate())))||
					(inputContact.getBirthDay()!=null && (dbContact.getBirthDay() ==null || !(MyCalendar.calendarToString(dbContact.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR).equals(MyCalendar.calendarToString(inputContact.getBirthDay(), MyCalendar.FORMAT_DATETIME_STYEAR)))))||
					(inputContact.getAnniversary()!=null && (dbContact.getAnniversary() ==null || !(MyCalendar.calendarToString(dbContact.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR).equals(MyCalendar.calendarToString(inputContact.getAnniversary(), MyCalendar.FORMAT_DATETIME_STYEAR)))))||
					(inputContact.getCreatedDate()!=null && (dbContact.getCreatedDate() ==null || !(MyCalendar.calendarToString(dbContact.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR).equals(MyCalendar.calendarToString(inputContact.getCreatedDate(), MyCalendar.FORMAT_DATETIME_STYEAR)))))||
					(inputContact.getUdf1()!=null && !inputContact.getUdf1().isEmpty() && (dbContact.getUdf1() ==null || !dbContact.getUdf1().equalsIgnoreCase(inputContact.getUdf1())))||
					(inputContact.getUdf2()!=null && !inputContact.getUdf2().isEmpty() && (dbContact.getUdf2() ==null || !dbContact.getUdf2().equalsIgnoreCase(inputContact.getUdf2())))||
					(inputContact.getUdf3()!=null && !inputContact.getUdf3().isEmpty() && (dbContact.getUdf3() ==null || !dbContact.getUdf3().equalsIgnoreCase(inputContact.getUdf3())))||
					(inputContact.getUdf4()!=null && !inputContact.getUdf4().isEmpty() && (dbContact.getUdf4() ==null || !dbContact.getUdf4().equalsIgnoreCase(inputContact.getUdf4())))||
					(inputContact.getUdf5()!=null && !inputContact.getUdf5().isEmpty() && (dbContact.getUdf5() ==null || !dbContact.getUdf5().equalsIgnoreCase(inputContact.getUdf5())))||
					(inputContact.getUdf6()!=null && !inputContact.getUdf6().isEmpty() && (dbContact.getUdf6() ==null || !dbContact.getUdf6().equalsIgnoreCase(inputContact.getUdf6())))||
					(inputContact.getUdf7()!=null && !inputContact.getUdf7().isEmpty() && (dbContact.getUdf7() ==null || !dbContact.getUdf7().equalsIgnoreCase(inputContact.getUdf7())))||
					(inputContact.getUdf8()!=null && !inputContact.getUdf8().isEmpty() && (dbContact.getUdf8() ==null || !dbContact.getUdf8().equalsIgnoreCase(inputContact.getUdf8())))||
					(inputContact.getUdf9()!=null && !inputContact.getUdf9().isEmpty() && (dbContact.getUdf9() ==null || !dbContact.getUdf9().equalsIgnoreCase(inputContact.getUdf9())))||
					(inputContact.getUdf10()!=null && !inputContact.getUdf10().isEmpty() && (dbContact.getUdf10() ==null || !dbContact.getUdf10().equalsIgnoreCase(inputContact.getUdf10())))||
					(inputContact.getUdf11()!=null && !inputContact.getUdf11().isEmpty() && (dbContact.getUdf11() ==null || !dbContact.getUdf12().equalsIgnoreCase(inputContact.getUdf11())))||
					(inputContact.getUdf12()!=null && !inputContact.getUdf12().isEmpty() && (dbContact.getUdf12() ==null || !dbContact.getUdf12().equalsIgnoreCase(inputContact.getUdf12())))||
					(inputContact.getUdf13()!=null && !inputContact.getUdf13().isEmpty() && (dbContact.getUdf13() ==null || !dbContact.getUdf13().equalsIgnoreCase(inputContact.getUdf13())))||
					(inputContact.getUdf14()!=null && !inputContact.getUdf14().isEmpty() && (dbContact.getUdf14() ==null || !dbContact.getUdf14().equalsIgnoreCase(inputContact.getUdf14())))||
					(inputContact.getUdf15()!=null && !inputContact.getUdf15().isEmpty() && (dbContact.getUdf15() ==null || !dbContact.getUdf15().equalsIgnoreCase(inputContact.getUdf15()))))
					
					
					
					
				/*||	inputContact.getPhone()!=dbContact.getPhone() || inputContact.getEmailId()!=dbContact.getEmailId() || inputContact.getAddressOne()!=dbContact.getAddressOne() 
				|| inputContact.getAddressTwo()!=dbContact.getAddressTwo() || inputContact.getCity()!=dbContact.getCity() || inputContact.getState()!=dbContact.getState()
				|| inputContact.getPin()!=dbContact.getPin() || inputContact.getCountry()!=dbContact.getCountry() || inputContact.getBirthDay()!=dbContact.getBirthDay()
				|| inputContact.getAnniversary()!=dbContact.getAnniversary() || inputContact.getGender()!=dbContact.getGender() || inputContact.getZip()!=dbContact.getZip()
		        || inputContact.getHomeStore()!=dbContact.getHomeStore() || inputContact.getCreatedDate()!=dbContact.getCreatedDate() || inputContact.getUdf1()!=dbContact.getUdf1()
		        || inputContact.getUdf2()!=dbContact.getUdf2() || inputContact.getUdf3()!=dbContact.getUdf3() || inputContact.getUdf4()!=dbContact.getUdf4()
		        || inputContact.getUdf5()!=dbContact.getUdf5() || inputContact.getUdf6()!=dbContact.getUdf6() || inputContact.getUdf7()!=dbContact.getUdf7()
		        || inputContact.getUdf8()!=dbContact.getUdf8() || inputContact.getUdf9()!=dbContact.getUdf9() || inputContact.getUdf10()!=dbContact.getUdf10()
		        || inputContact.getUdf11()!=dbContact.getUdf11() || inputContact.getUdf12()!=dbContact.getUdf12() || inputContact.getUdf13()!=dbContact.getUdf13()
		        || inputContact.getUdf14()!=dbContact.getUdf14() || inputContact.getUdf15()!=dbContact.getUdf15())
			*/{
			return true;
			}
			else {
			return false;
			}
		}
		return false;
		}  
	public static String encodeUrl(String url) throws EncoderException, DecoderException{
		URLCodec encoder = new URLCodec();
		String result = encoder.encode(url);
		return result;
	}

	public static String decodeUrl(String url) throws EncoderException, DecoderException{
		URLCodec encoder = new URLCodec();
		String result = encoder.decode(url);
		return result;
	}
	
	/**
	 * Replaces space with '%20' 
	 * @param urlStr - Url string with spaces
	 * @return Returns converted String
	 */
	public static String encodeSpace(String urlStr) {
		return StringUtils.replace(urlStr, " ", "%20");
	}

	
	
public static String getOnlyUserName(String userFullName) {
		
	if (userFullName == null || userFullName.indexOf("__org__")==-1) return userFullName;
	
	try {
		userFullName = userFullName.substring(0, userFullName.lastIndexOf("__org__"));
	}
	catch(Exception e) {
		logger.error("** Exception : ",e);
	}
	return userFullName;
	
	
}//end block
	
public static String getOnlyOrgId(String userFullName) {
	if (userFullName == null || userFullName.indexOf("__org__")==-1) return userFullName;
	try{
		userFullName = userFullName.substring(userFullName.lastIndexOf("__org__")+7);
	}
	catch(Exception e) {
		logger.error("** Exception : ",e);
	}
	return userFullName;
	
	
}//end block

 /*public static File mergeImages(File imageOne, File imageTwo,int x, int y,String destinPath) throws Exception{
	
	logger.debug("just entered");
	logger.info("co-ordinates: x-"+x +", y- "+y);
	logger.info("destination path: "+destinPath);
	File outImage = null;
	String ext = FileUtil.getFileNameExtension(imageOne);
	String destExt = FileUtil.getFileNameExtension(destinPath);
	if(!ext.equalsIgnoreCase(destExt)){
		String path = FileUtil.getFileNameNoExtension(destinPath);
		destinPath = path + "." + ext;
	}
	BufferedImage input = ImageIO.read(imageOne);
	BufferedImage input1 = ImageIO.read(imageTwo);
	int h2 = input1.getHeight();
	int w2 = input1.getWidth();
	int starty = y;
	int startx = x;

	for (int j=starty; j<starty+h2; j++) {
	  for (int i=startx; i<startx+w2; i++) {
		int pixColor = input1.getRGB(i-startx,j- starty);
		input.setRGB(i,j,pixColor);
	  }
	}
	logger.info("Positioning completed");
	outImage = new File(destinPath);
	ImageIO.write(input, ext, outImage);
	logger.info(" images merged successfully");
	
	logger.debug("Exiting");
	return outImage;
	
  }
	public static String mergeCouponWithTemplate(String path,String htmlTextOriginal){

		try{
			logger.debug(" just entered ");
			String replString = "<div name=\"RMCouponDiv\"><img src=\""+path+"\"/></div>";
			String HtmlCouponDivId= PropertyUtil.getPropertyValue("HtmlCouponDivId");
			logger.info("HtmlCouponDivId is"+HtmlCouponDivId);
			//logger.debug("-- mergeCouponWithTemplate --");
			htmlTextOriginal = htmlTextOriginal.replaceAll( HtmlCouponDivId, replString );
			logger.debug("Exiting");
			return htmlTextOriginal;
			
			
		}catch(Exception e){
           logger.error("** Exception mergeCouponWithTemplate is "+ e.getMessage()+" **");
			logger.debug("Exception in mergeCouponWithTemplate  :"+e);
			return htmlTextOriginal;
		}
	}
	*/
	
	public static long DateDiff(Date from, Date to){
		long diff = from.getTime() - to.getTime();
		return (diff / (1000 * 60 * 60 * 24));
	}
	
	public static Object makeCopy(Object obj) {
		
		if(obj==null) return null;
		Object retObj=null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			retObj = ois.readObject();
			ois.close();
		}catch(Exception e) {
			
		}
		return retObj;
	}
	
	
	
	public static String HexToString(String str) {
		int size = str.length()/4;
		String[] charTokensArr = new String[size];
		StringBuffer buffer = new StringBuffer();
		int j = 0;
		int k =0;
		char c ;
		
		for(int i=0; i<str.length(); i+=4){
			
			charTokensArr[j] = str.substring(i, i+4);
			 k= Integer.parseInt(charTokensArr[j],16);
			    if(logger.isDebugEnabled()) logger.debug("Decimal:="+ k);
			    c = (char)k;
			    ++j;
			    buffer.append(c);
		}
			/*++j;
		}
		
		 for(int i=0; i<charTokensArr.length; i++){
			 
			    k= Integer.parseInt(charTokensArr[i],16);
			    if(logger.isDebugEnabled()) logger.debug("Decimal:="+ k);
			    c = (char)k;
			    buffer.append(c);
		 }*/
		 
		return buffer.toString();
		
		
	}//HexToString
	
	public static String stringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer strBuffer = new StringBuffer();
        String tempStr;
        for (int i = 0; i < chars.length; i++) {
        	
        	tempStr = Integer.toHexString((int) chars[i]);
        	if(logger.isDebugEnabled()) logger.debug(chars[i]+" = "+tempStr);
        	if(tempStr.length()==1) {
        		tempStr = "000"+tempStr;
        	}
        	else if(tempStr.length()==2) {
        		tempStr = "00"+tempStr;
        	}
        	else if(tempStr.length()==3) {
        		tempStr = "0"+tempStr;
        	}

            strBuffer.append(tempStr);
        }
        return strBuffer.toString().toUpperCase();
    }//stringToHex
	


	public static long calCountOfContacts(long num, int perc) {
		
		long count = Math.round((perc/100f)*num);
		
		
		
		return count;
		
		
		
		
		
	}

	
	
	//Url Shortning
	//***********Generate 32 bit cipherText(encrypted Text) of given URL using  MessageDigest5(MD5) Algo
	private static String MD5Algo(String inputUrl) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			 byte[] messageDigest = md.digest(inputUrl.getBytes());
			 
//			 logger.debug("md5 lengtyh ==="+messageDigest.length);
			 BigInteger number = new BigInteger(1, messageDigest);
			 String hashtext = number.toString(16);
			   
			   // Now we need to zero pad it if you actually want the full 32 chars.
			   while (hashtext.length() < 32) {
				   
			       hashtext = "0" + hashtext;
			   }
			   return hashtext;
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("Exception ::error occured while generating the MD5 algoitham",e);
			return null;
		}
	} // MD5Algo
	
	//****return list of Strings  each string contain 6-digit length ****//
	
	/*public static List<String> getSixDigitURLCode(String inputURL) {
		
		try {
			List<String> sixDigitStrList = new ArrayList<String>();
			char[] base32 =  {
				    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				    'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
				    'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				    'y', 'z', '0', '1', '2', '3', '4', '5'
			};
			
			String Md5result = MD5Algo(inputURL);
			if(Md5result == null) {
				if(logger.isDebugEnabled()) logger.debug(" :: generated  MD5 algo is null");
				return null;
			}
			//logger.debug("MD5 result string is ===>"+Md5result);
			 int lenthofInt = Md5result.length();
			 int subHexLenInt = lenthofInt / 8;
			 int maxLength=8;
			 
			 for(int i=0; i < subHexLenInt; i++) {
				 String subHexStr = Md5result.substring(i*8, (i*8)+maxLength);

//				 logger.debug("Hex ::"+Long.parseLong(subHexStr, 16));
				 
				 long hexaValue = 0x3FFFFFFF & Long.parseLong(subHexStr, 16);
//				 logger.debug("hexaValue is::"+hexaValue);
				 
				 StringBuffer outSb = new StringBuffer();
				 
				 for (int j = 0; j < 6; j++) {
				      long val = 0x0000001F & hexaValue;
				      outSb.append(base32[(int)val]);
				      hexaValue = hexaValue >>> 5;
				  } //inner for
				 
//				 logger.debug("== "+outSb);
				 if(outSb !=  null)
				 sixDigitStrList.add(outSb.toString());
			 } // outer for
			 return sixDigitStrList;
			 
		} catch (Exception e) {
			logger.error("Exception");
			return null;
		}
		 
	} // getSixDigitURLCode
	*/
	
	
	
	//****return list(size 4) of String  each string contain 6-digit length ****//
	public static List getSixDigitURLCode(String inputURL) {
		
		try {
			List sixDigitStrList = new ArrayList();
			char[] base32 =  {
				    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				    'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
				    'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
				    'y', 'z', '0', '1', '2', '3', '4', '5'
			};
			
			String Md5result = MD5Algo(inputURL);
			if(Md5result == null) {
				logger.debug(" :: generated  MD5 algo is null");
				return null;
			}
			logger.debug("MD5 result string is ===>"+Md5result);
			 int lenthofInt = Md5result.length();
			 int subHexLenInt = lenthofInt / 8;
			 int maxLength=8;
			 
			 for(int i=0; i < subHexLenInt; i++) {
				 String subHexStr = Md5result.substring(i*8, (i*8)+maxLength);

//				 logger.debug("Hex ::"+Long.parseLong(subHexStr, 16));
				 
				 long hexaValue = 0x3FFFFFFF & Long.parseLong(subHexStr, 16);
//				 logger.debug("hexaValue is::"+hexaValue);
				 
				 StringBuffer outSb = new StringBuffer();
				 
				 for (int j = 0; j < 6; j++) {
				      long val = 0x0000001F & hexaValue;
				      outSb.append(base32[(int)val]);
				      hexaValue = hexaValue >>> 5;
				  } //inner for
				 
//				 logger.debug("== "+outSb);
				 if(outSb !=  null)
				 sixDigitStrList.add(outSb);
			 } // outer for
			 return sixDigitStrList;
			 
		} catch (Exception e) {
			logger.error("Exception ::::", e);
			return null;
		}
		 
	} // getSixDigitURLCode
	

	public static boolean validateEmail(String email) {
		// Pattern p = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
		if (email == null || email.trim().length()==0){
			return false;
		}
		email = email.trim();
		String pattern = PropertyUtil.getPropertyValue("emailPattern").trim();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	
	/**
	 * Added to update the email status of the contact W.R.T parental consent and Double Optin Confirmation
	 */
	public static String getContactEmailStatus(Contacts contact, MailingList ml, ContactParentalConsentDao contactParentalConsentDao) {
		
		
		
		ContactParentalConsent contactParentalConsent = contactParentalConsentDao.findByContactId(contact.getContactId());
		
		
		String status = "";
		
		boolean optInFlag = (contact.getEmailStatus() != null && contact.getEmailStatus().equals(Constants.CONT_STATUS_OPTIN_PENDING) ) ? true : false;
		
		boolean consentFlag = (contact.getEmailStatus() != null && contact.getEmailStatus().equals(Constants.CONT_STATUS_PARENTAL_PENDING) )? true : false;
		
		String optinMedium = contact.getOptinMedium();
		
		if(optInFlag ) {
			
			if(ml.isCheckParentalConsent() && optinMedium != null && optinMedium.startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM)) {
			
				if(contactParentalConsent != null && contactParentalConsent.getStatus().equals(Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL)) {
					
					status = Constants.CONT_STATUS_PARENTAL_PENDING;
					
					
				}
				else{
					status = Constants.CONT_STATUS_ACTIVE;
					
				}
				
				
				
			}//if parental is enabled
			else{
				
				status = Constants.CONT_STATUS_ACTIVE;
				
			}
			
		}
		if(consentFlag ) {
			
			
			
			if( optinMedium != null && 
					optinMedium.startsWith(Constants.CONTACT_OPTIN_MEDIUM_WEBFORM) && 
					contactParentalConsent != null && 
					contactParentalConsent.getStatus().equals(Constants.CONT_PARENTAL_STATUS_PENDING_APPROVAL)) {
				
				status = Constants.CONT_STATUS_PARENTAL_PENDING;
				
				
			}
			else{
				status = Constants.CONT_STATUS_ACTIVE;
				
			}
			
			
			
		}
		
		
		return status;
	}
	
	
	
	//***********Generate 32 bit cipherText(encrypted Text) of given URL using  MessageDigest5(MD5) Algo
			public  static BigInteger MD5Algorithm(String inputUrl) {
				try {
					MessageDigest md = MessageDigest.getInstance("MD5");
					 byte[] messageDigest = md.digest(inputUrl.getBytes());
					 BigInteger number = new BigInteger(1, messageDigest);

					 return number;
					 
				} catch (Exception e) {
					return null;
				}
			} // MD5Algorithm
			
			
		
			/*public static List<String> couponGenarationCode(String inputURL , int param) {
				
				try {
					List<String> couponGenList = new ArrayList<String>();
					char[] base32 =  {
						    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
						    'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
						    'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
						    'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 
						    'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 
						    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 
						    'W', 'X', 'Y', 'Z', '0', '1', '2', '3', 
						    '4', '5', '6', '7', '8', '9', '*', '-'
					};
					
					String Md5result = MD5Algo(inputURL);
					if(Md5result == null) {
						//logger.debug(" :: generated  MD5 algo is null");
						return null;
					}
					//logger.debug("MD5 result string is ===>"+Md5result);
					 int lenthofInt = Md5result.length();
					// logger.debug("lenthofInt ===>"+lenthofInt);
					 int subHexLenInt = lenthofInt / 8;
					// logger.debug("subHexLenInt ===>"+subHexLenInt);
					 
					 int maxLength = 8;
					 
					 for(int i=0; i < subHexLenInt; i++) {
						 String subHexStr = Md5result.substring(i*8, (i*8)+maxLength);
						// logger.debug("subHexStr ::"+subHexStr);
						// logger.debug("Hex ::"+Long.parseLong(subHexStr, 16));
						 
						 long hexaValue = 0x3FFFFFFF & Long.parseLong(subHexStr, 16);
						// logger.debug("hexaValue is::"+hexaValue);
						 
						 StringBuffer outSb = new StringBuffer();
						 
						 for (int j = 0; j < param; j++) {
						      long val = 0x0000003F & hexaValue;
						      outSb.append(base32[(int)val]);
						      hexaValue = hexaValue >>> param-1;
						  } //inner for
						 
						// logger.debug("== "+outSb);
						 if(outSb !=  null)
							 couponGenList.add(outSb.toString());
					 } // outer for
					 return couponGenList;
					 
				} catch (Exception e) {
					logger.error("Exception");
					return null;
				}
				 
			} // couponGenarationCode
*/			
			
			public static List<String> couponGenarationCode(String inputURL , int param) {
				
				try {
					List<String> couponGenList = new ArrayList<String>();
					
					char[] base32 =  {
						    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
						    'I', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 
						    'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 
						    'Z', '1', '2', '3', '4', '5', '6', '7'
					};
					
					String Md5result = MD5Algo(inputURL);
					if(Md5result == null) {
						//logger.info(" :: generated  MD5 algo is null");
						return null;
					}
					//logger.info("MD5 result string is ===>"+Md5result);
					 int lenthofInt = Md5result.length();
					// logger.info("lenthofInt ===>"+lenthofInt);
					 int subHexLenInt = lenthofInt / 8;
					// logger.info("subHexLenInt ===>"+subHexLenInt);
					 
					 int maxLength = 8;
					 
					 for(int i=0; i < subHexLenInt; i++) {
						 String subHexStr = Md5result.substring(i*8, (i*8)+maxLength);
					//	 logger.info("subHexStr ::"+subHexStr);
					//	 logger.info("Hex ::"+Long.parseLong(subHexStr, 16));
						 
						 long hexaValue = 0x3FFFFFFF & Long.parseLong(subHexStr, 16);
					//	 logger.info("hexaValue is::"+hexaValue);
						 
						 StringBuffer outSb = new StringBuffer();
						 
						 for (int j = 0; j < param; j++) {
						      long val = 0x0000001F & hexaValue;
						    
						      outSb.append(base32[(int)val]);
						      hexaValue = hexaValue >>> param-1;
						  } //inner for
						 
					//	 logger.info("== "+outSb);
						 if(outSb !=  null)
							 couponGenList.add(outSb.toString());
					 } // outer for
					 return couponGenList;
					 
				} catch (Exception e) {
					logger.error("Exception ::" , e);
					return null;
				}
				 
			} // couponGenarationCode
			
			public static String[] findOrgByUsedKeywords(String textContent, OrgSMSkeywordsDao orgSmSkeywordsDao) {
				
				boolean keyFoundFlag = true;
				String[] replyStrArr = new String[2];
				
				String keyword = null;
				
				if(textContent.trim().length() > 0) {
					
					if(textContent.trim().indexOf(" ") == -1 ) {
						
						keyword = textContent.trim();
						
						
					}else{
						
						keyword = textContent.substring(0, textContent.trim().indexOf(" "));
						
					}
					
					
				}//if
				
				replyStrArr[0] = keyword;
				Long orgId = orgSmSkeywordsDao.findorgByKeyword(keyword) ;
				
				
				
				if(orgId != null ) replyStrArr[1] = orgId+"";
				return replyStrArr;
				
				
				
				
				
			}
	
			
			public static Object[] findSettingsByKeywords(String textContent, SMSSettingsDao smsSettingsDao) {
				
				Object[] obj = new Object[2];
				
				String keyword = null;
				
				if(textContent.trim().length() > 0) {
					
					if(textContent.trim().indexOf(" ") == -1 ) {
						
						keyword = textContent.trim();
						
						
					}else{
						
						keyword = textContent.substring(0, textContent.trim().indexOf(" "));
						
					}
					
					
				}//if
				
				SMSSettings smsSettings = smsSettingsDao.findByKeyword(keyword);
				
				obj[0] = smsSettings;
				obj[1] = keyword;
				return obj;
				
			}
			
			
			public static void updateCouponCodeCounts(ApplicationContext context, Set<String> totalPhSet) {
				
				
				CouponsDao couponsDao = (CouponsDao)context.getBean("couponsDao");
				CouponsDaoForDML couponsDaoForDML = (CouponsDaoForDML)context.getBean("couponsDaoForDML");
				CouponCodesDao couponCodesDao = (CouponCodesDao)context.getBean("couponCodesDao");
				if(totalPhSet != null && totalPhSet.size() > 0) {
				
					boolean isCoupFound = false;
					
					for (String eachPh : totalPhSet) {
						
						if(!eachPh.startsWith("CC_")) continue;
						
						if(!isCoupFound) {//before updating issued and availble counts flush the couponvector
							
							CouponProvider couponProvider = CouponProvider.getCouponProviderInstance(context, null);
							try {
								couponProvider.flushCouponCodesToDB(true);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								logger.error("Exeception ", e);
							}
							isCoupFound= true;
						}
						
						
						Long couponId = null; 
						String[] strArr = eachPh.split("_");
						
						if(logger.isDebugEnabled()) logger.debug("Filling Coupons with Id = "+strArr[1]);
						try {
							
							couponId = Long.parseLong(strArr[1]);
							
						} catch (NumberFormatException e) {
							
							couponId = null;
						
						}
						
						if(couponId == null) {
							
							//TODO need to delete it from phset or???????????????????????
							continue;
							
						}
						long issuedCount = couponCodesDao.findIssuedCoupCodeByCoup(couponId);
						
						//int updateCount = couponsDao.updateIssuedCountByCouponId(couponId, issuedCount);
						int updateCount = couponsDaoForDML.updateIssuedCountByCouponId(couponId, issuedCount);
						
						if(logger.isInfoEnabled()) logger.info("coupon - "+couponId.longValue()+" has updated with issued count : "+issuedCount);
						
						
						
					}
					
					
				}
				
				
				/*********************************************/
				
			}
			
			public static Map<String, String> genFieldContMap = new HashMap<String, String>();
			public static Map<String, String> genFieldSalesMap = new HashMap<String, String>();
			public static Map<String, String> genFieldSKUMap = new HashMap<String, String>();
			public static Map<String, String> genFieldHomesPassedMap = new HashMap<String, String>();
			public static Map<String, String> zipValidateMap = new HashMap<String, String>();
			
			static{
				genFieldContMap.put("Email", "email_id");
				genFieldContMap.put("First Name", "first_name");
				genFieldContMap.put("Last Name", "last_name");
				genFieldContMap.put("Street", "address_one");
				genFieldContMap.put("Address Two", "address_two");
				genFieldContMap.put("City", "city");
				genFieldContMap.put("State", "state");
				genFieldContMap.put("Country", "country");
				
				genFieldContMap.put("ZIP", "zip");
				genFieldContMap.put("Mobile", "mobile_phone");
				genFieldContMap.put("Customer ID", "external_id" );
				genFieldContMap.put("Addressunit ID", "hp_id" );
				genFieldContMap.put("Gender", "gender");
				genFieldContMap.put("BirthDay", "birth_day");
				genFieldContMap.put("Anniversary", "anniversary");
				genFieldContMap.put("Home Store", "home_store");
						
				genFieldSalesMap.put("Customer ID", "customer_id");
				genFieldSalesMap.put("Receipt Number", "reciept_number");
				genFieldSalesMap.put("Sale Date", "sales_date");
				genFieldSalesMap.put("Quantity", "quantity");
				genFieldSalesMap.put("Sale Price", "sales_price");
				genFieldSalesMap.put("Tax", "tax");
				genFieldSalesMap.put("Promo Code", "promo_code");
				genFieldSalesMap.put("Store Number", "store_number");
				genFieldSalesMap.put("SKU", "sku");
				genFieldSalesMap.put("Tender Type", "tender_type");
				genFieldSalesMap.put("External ID", "external_id");
				genFieldSalesMap.put("Item Sid", "item_sid");
				genFieldSalesMap.put("Doc Sid", "doc_sid");
				
				genFieldSKUMap.put("Store Number", "store_number");
				genFieldSKUMap.put("SKU", "sku");
				genFieldSKUMap.put("Description", "description");
				genFieldSKUMap.put("List Price", "list_price");
				genFieldSKUMap.put("Item Category", "item_category");
				genFieldSKUMap.put("Item Sid", "item_sid");
				
				genFieldHomesPassedMap.put("Addressunit Id" , "addressUnitId");
				genFieldHomesPassedMap.put("Country" , "country");
				genFieldHomesPassedMap.put("State" , "state");
				genFieldHomesPassedMap.put("District" , "district");
				genFieldHomesPassedMap.put("City" , "city");
				genFieldHomesPassedMap.put("ZIP" , "zip");
				genFieldHomesPassedMap.put("Area" , "area");
				genFieldHomesPassedMap.put("Street" , "street");
				genFieldHomesPassedMap.put("Address One" , "addressOne");
				genFieldHomesPassedMap.put("Address Two" , "addressTwo");
				
				zipValidateMap.put(Constants.SMS_COUNTRY_US,"^[0-9]{5}(?:-[0-9]{4})?$");
				zipValidateMap.put(Constants.SMS_COUNTRY_INDIA, "\\d{6}");
				zipValidateMap.put(Constants.SMS_COUNTRY_PAKISTAN, "\\d{5}");
			//	zipValidateMap.put("UAE", "");
				zipValidateMap.put(Constants.SMS_COUNTRY_CANADA, "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$");
				zipValidateMap.put(Constants.SMS_COUNTRY_KUWAIT, "\\d{5}");
				
				zipValidateMap.put(Constants.SMS_COUNTRY_OMAN,"\\d{3}");
				zipValidateMap.put(Constants.SMS_COUNTRY_PANAMA,"\\d{4}");
				zipValidateMap.put(Constants.SMS_COUNTRY_SA,"\\d{4}");
				zipValidateMap.put(Constants.SMS_COUNTRY_MYANMAR, "\\d{5}");
				zipValidateMap.put(Constants.SMS_COUNTRY_PHILIPPINES,"\\d{4}");

				
			}
			

//			private static final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");  
			//private static Matcher matcher = null;
			//public static final Pattern phonePattern = Pattern.compile("\\b(1?|91?|92?971?)\\s*\\(?([0-9]{3})\\)?[-. *]*([0-9]{3})[-. ]*([0-9]{4})\\b");  
			
			public static String phoneParse(String csvLine, UserOrganization userOrganization) {
				try {
				if(userOrganization == null ){
				logger.error("User does not belong to any organization.", "color:red;","top");
					return null ;

				}
				if(!userOrganization.isRequireMobileValidation()) return csvLine;
				UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				Long userId=usersDao.getOwnerofOrg(userOrganization.getUserOrgId());
				Users user=usersDao.findByUserId(userId);
				String userCountryCarrier=user.getCountryCarrier().toString();
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

		public static boolean isrunning;
		
		public static void processExternalSMTPEvents(ApplicationContext context ) {
			try {
				logger.debug("===intiating ExternalSMTPEventsProcessingThread===");
				isrunning = true;
				Thread th  = new ExternalSMTPEventsProcessingThread();
				th.start();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Error in processing the files===");
				isrunning =false;
			}
			
			logger.debug("===intiated ExternalSMTPEventsProcessingThread==="+isrunning);
		}//processSMTPEvents()
		
		
	    
	    
	    
	    
	    
	   /* public static int updateCampaignReport(CampaignSent campaignSent, String type,
	    		CampaignSentDao campaignSentDao, CampaignReportDao campaignReportDao) {
	    	
			try {
				Long  crId = campaignSentDao.getCrIdBySentId(campaignSent.getSentId());
				logger.debug("Updating Campaign report , CrId Is : " + crId);		
				int resp = campaignReportDao.updateCampaignReport(crId, campaignSent.getSentId(), type);
				logger.debug("Saved to Campaign Report : "+ resp);
				return resp;
			} catch (Exception e) {
				logger.error("Exception : Problem while updating the "+ type +" in CampaignReport \n", e);
				return 0;
			}
	    	
	    }*/
	    
	    
	    
	   /* public void clickUpdate(HttpServletRequest request, HttpServletResponse response, long sentId) {
	    	
	    	 try {
	    	
	    	*//** checking for opens, if zero it update*//* 
	    	if(campaignSent != null && campaignSent.getOpens() == 0) {
	    		updateOpens(request, campaignSent);
	    		updateReport(campaignSent, "opens");
	    	} //if(campaignSent != null && campaignSent.getOpens() == 0)
	    	
			Clicks clicks = new Clicks(campaignSent, urlStr, new Date());
			clicksDao.saveOrUpdate(clicks);
			clicksDao.clear(clicks);
			
			updateReport(campaignSent, Constants.CS_TYPE_CLICKS);
			campaignSentDao.clear(campaignSent);
	    }

	    catch (Exception e) {
	    	logger.error("Exception : Problem while updating the Clicks \n", e);
		}
	    	
	    	
	    }*/
	    /**
		 * Searches for the EmailClient
		 * @param request
		 * @param campaignSent
		 * @return if found returns EmailClinet otherwise returns null
		 */
		public static EmailClient emailClientCheck(String userAgent,CampaignSent campaignSent, EmailClientDao emailClientDao){
			EmailClient client = null;
			try {
				if(logger.isDebugEnabled()) logger.debug("User-Agent : " + userAgent );
				
				//TODO: As Email Client list is static for all the request 
				//Need to declare emailClientList as instance Variable
				List<EmailClient> emailClientList  = emailClientDao.findAll();
				
				if(userAgent!=null) {
					String userAgentSearchStr = null;
					
					for (EmailClient emailClient : emailClientList) {
						userAgentSearchStr = emailClient.getUserAgent();
						if(userAgentSearchStr!=null) {
							if(userAgent.contains(userAgentSearchStr)){
								if(logger.isDebugEnabled()) logger.debug("Email client : " + client);
								return emailClient;
							}
						}
					} // for
				} // if

				String toEmailId = campaignSent.getEmailId();
				
				if(toEmailId!=null) {
					String domain = toEmailId.substring(toEmailId.indexOf('@')+1, toEmailId.indexOf('.',toEmailId.indexOf('@')));
					if(logger.isDebugEnabled()) logger.debug("Domain of sent mail : " + domain);
					
					for (EmailClient emailClient : emailClientList) {
						if(emailClient.getEmailClient().toLowerCase().contains(domain.toLowerCase())) {
							return emailClient;
						}
						else if(emailClient.getEmailClient().equals("Others")) {
							client = emailClient;
						}
					} // for
				} // if toEmailId
				emailClientList = null;
			} catch (Exception e) {
				logger.error("Exception : ", (Throwable)e);
			}		
			return client;
		} // emailClientCheck(HttpServletRequest request,CampaignSent campaignSent)
		
	    public static long getMlsBit(Set<MailingList> mlSet) {
		long retVal=0l;
		try {
			Long userId=null;
			
			Iterator<MailingList> mlIt = mlSet.iterator();
			MailingList mailingList = null;
			
			while(mlIt.hasNext()) {
			
				mailingList = mlIt.next();
				
				if(userId==null) {
					userId = mailingList.getUsers().getUserId();
				}
				else if(userId.longValue() != mailingList.getUsers().getUserId().longValue()) {
					return -1; // Different user ids
				}
				
				retVal = retVal | mailingList.getMlBit();
			} // while
			
		} catch (Exception e) {
			logger.error("Exception");
			return 0;
		}
		return retVal;
	}

	    public static void setContactFieldsOnDeletion(Contacts contact){
	  	  
	 	   if(contact.getMlBits().longValue() == 0l){
	 	  //contact.setUsers(null);
	 	  //contact.setCreatedDate(null);
	 	  contact.setPurged(false);
	 	  contact.setEmailStatus(Constants.CONT_STATUS_PURGE_PENDING);
	 	  contact.setLastStatusChange(null);
	 	  contact.setLastMailDate(null);
	 	  contact.setOptin((byte)0);
	 	  contact.setSubscriptionType(null);
	 	  contact.setOptinMedium(null);
	 	  //contact.setHomeStore(null);
	 	  //contact.setLoyaltyCustomer((byte)0);
	 	  //TODO what to do with mobile optin
	 	  //contact.setLastSMSDate(null);
	 	  //contact.setMobileOptin(false);
	 	  }
	 	   
	   } //setContactFieldsOnDeletion
	    
	    public static Map<String, OrganizationStores > retOrgStoreMap(Long orgId, OrganizationStoresDao organizationStoresDao) {
	    	
	    	List<OrganizationStores> listOfStores = organizationStoresDao.findByOrganization(orgId);
			if(listOfStores == null || listOfStores.size() == 0) return null;
			Map<String, OrganizationStores> OrgStoreMap = new HashMap<String, OrganizationStores>();
			for (OrganizationStores organizationStores : listOfStores) {
				
				OrgStoreMap.put(organizationStores.getHomeStoreId(), organizationStores);
				
			}//for
	    	
	    	return OrgStoreMap;
	    	
	    }
	    public static Map<String, String > retOrgStoreAddressMap(Long orgId, OrganizationStoresDao organizationStoresDao) {
	    	
	    	List<OrganizationStores> listOfStores = organizationStoresDao.findByOrganization(orgId);
			if(listOfStores == null || listOfStores.size() == 0) return null;
			Map<String, String> OrgStoreAddressMap = new HashMap<String, String>();
			for (OrganizationStores organizationStores : listOfStores) {
				
				
				String storeAddress = Constants.STRING_NILL;
				String strAddr[] = organizationStores.getAddressStr().split(Constants.ADDR_COL_DELIMETER);
				int count = 0;
				for(String str : strAddr){
					count++;
					
					if(count == 7 && storeAddress.length()>0 && str.trim().length()>0){
						storeAddress = storeAddress+" | Phone: "+str;
					}
					else if(storeAddress.length()==0 && str.trim().length()>0){
						storeAddress = storeAddress+str;
					}
					else if(storeAddress.length()>0 && str.trim().length()>0){
						storeAddress = storeAddress+", "+str;
					}
				}
				
				OrgStoreAddressMap.put(organizationStores.getHomeStoreId(), storeAddress);
				
			}//for
	    	
	    	return OrgStoreAddressMap;
	    	
	    }
	    
	    // added for digital receipt extraction
	    
		public  static TreeMap<String, List<String>> getPriorityMap(long userId,String mappingType, POSMappingDao posMappingDao) {
			  
			  try {
//				  logger.info("UserId is ::"+userId+" ::MappingType is ::"+mappingType);
				  TreeMap<String, List<String>> prioMap = new TreeMap<String, List<String>>();
				  
				 /* if(posMappingDao == null) {
					  
					  posMappingDao= (POSMappingDao)SpringUtil.getBean("posMappingDao");
				  }*/
						  
				  List<POSMapping> posmapList = posMappingDao.getPriorityMapByUserId(userId , "'"+mappingType+"'");
				  
				  if(posmapList == null || posmapList.size()==0) return null;
				  
				  for (POSMapping posMapObj : posmapList) {
					  	
					  String dataTypeStr = posMapObj.getDataType().toLowerCase();
					  
					  if(dataTypeStr.startsWith("date")) {
							dataTypeStr = "string"; 
					  }
					  
					
					  String priorStr = posMapObj.getCustomFieldName()+"|"+dataTypeStr;
					  
					  
					  List<String> valList = prioMap.get(""+posMapObj.getUniquePriority());
					  if(valList==null){
						  valList = new ArrayList<String>();
					  }
					  
					 if(!valList.contains(priorStr)) {
								valList.add(priorStr);
					}
					  
					 prioMap.put(""+posMapObj.getUniquePriority(), valList);
					 
					 
				  }
				  
				  return prioMap;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::::", e);
				return null;
			}
			  
		  } //getPriorityMap
		  
		  
		 public static Contacts mergeContacts(Contacts inputContact, Contacts dbContact) {

		      try {
				if(dbContact == null){
				      return inputContact;
				  }


				  if(!(inputContact.getEmailId()==null) && !(inputContact.getEmailId().trim().isEmpty())){
				      dbContact.setEmailId(inputContact.getEmailId());
				  }
				  if(!(inputContact.getFirstName()== null) &&!(inputContact.getFirstName().trim().isEmpty())){
				      dbContact.setFirstName(inputContact.getFirstName());
				  }
				  if(!(inputContact.getLastName()==null) &&!(inputContact.getLastName().trim().isEmpty())){
				      dbContact.setLastName(inputContact.getLastName());
				  }
				  if(!(inputContact.getAddressOne()==null) &&!(inputContact.getAddressOne().trim().isEmpty())){
				      dbContact.setAddressOne(inputContact.getAddressOne());
				  }
				  if(!(inputContact.getAddressTwo()==null) &&!(inputContact.getAddressTwo().trim().isEmpty())){
				      dbContact.setAddressTwo(inputContact.getAddressTwo());
				  }
				  if(!(inputContact.getCity()==null) &&!(inputContact.getCity().trim().isEmpty())){
				      dbContact.setCity(inputContact.getCity());
				  }
				  if(!(inputContact.getState()==null) &&!(inputContact.getState().trim().isEmpty())){
				      dbContact.setState(inputContact.getState());
				  }
				  if(!(inputContact.getCountry()==null) &&!(inputContact.getCountry().isEmpty())){
				      dbContact.setCountry(inputContact.getCountry());
				  }
				  if(!(inputContact.getUdf1()==null) &&!(inputContact.getUdf1().trim().isEmpty())){
				      dbContact.setUdf1(inputContact.getUdf1());
				  }
				  if(!(inputContact.getUdf2()==null) &&!(inputContact.getUdf2().trim().isEmpty())){
				      dbContact.setUdf2(inputContact.getUdf2());
				  }
				  if(!(inputContact.getUdf3()==null) &&!(inputContact.getUdf3().trim().isEmpty())){
				      dbContact.setUdf3(inputContact.getUdf3());
				  }
				  if(!(inputContact.getUdf4()==null) &&!(inputContact.getUdf4().trim().isEmpty())){
				      dbContact.setUdf4(inputContact.getUdf4());
				  }
				  if(!(inputContact.getUdf5()==null) &&!(inputContact.getUdf5().trim().isEmpty())){
				      dbContact.setUdf5(inputContact.getUdf5());
				  }
				  if(!(inputContact.getUdf6()==null) &&!(inputContact.getUdf6().trim().isEmpty())){
				      dbContact.setUdf6(inputContact.getUdf6());
				  }
				  if(!(inputContact.getUdf7()==null) &&!(inputContact.getUdf7().trim().isEmpty())){
				      dbContact.setUdf7(inputContact.getUdf7());
				  }
				  if(!(inputContact.getUdf8()==null) &&!(inputContact.getUdf8().trim().isEmpty())){
				      dbContact.setUdf8(inputContact.getUdf8());
				  }
				  if(!(inputContact.getUdf9()==null) &&!(inputContact.getUdf9().trim().isEmpty())){
				      dbContact.setUdf9(inputContact.getUdf9());
				  }
				  if(!(inputContact.getUdf10()==null) &&!(inputContact.getUdf10().trim().isEmpty())){
				      dbContact.setUdf10(inputContact.getUdf10());
				  }
				  if(!(inputContact.getUdf11()==null) &&!(inputContact.getUdf11().trim().isEmpty())){
				      dbContact.setUdf11(inputContact.getUdf11());
				  }
				  if(!(inputContact.getUdf12()==null) &&!(inputContact.getUdf12().trim().isEmpty())){
				      dbContact.setUdf12(inputContact.getUdf12());
				  }
				  if(!(inputContact.getUdf13()==null) &&!(inputContact.getUdf13().trim().isEmpty())){
				      dbContact.setUdf13(inputContact.getUdf13());
				  }
				  if(!(inputContact.getUdf14()==null) &&!(inputContact.getUdf14().trim().isEmpty())){
				      dbContact.setUdf14(inputContact.getUdf14());
				  }
				  if(!(inputContact.getUdf15()==null) &&!(inputContact.getUdf15().trim().isEmpty())){
				      dbContact.setUdf15(inputContact.getUdf15());
				  }
				  if(!(inputContact.getGender()==null) &&!(inputContact.getGender().trim().isEmpty())){
				      dbContact.setGender(inputContact.getGender());
				  }
				  if(!(inputContact.getBirthDay()==null)){
				      dbContact.setBirthDay(inputContact.getBirthDay());
				  }
				  if(!(inputContact.getAnniversary()==null)){
				      dbContact.setAnniversary(inputContact.getAnniversary());
				  }
				  if(!(inputContact.getZip()==null) &&!(inputContact.getZip().trim().isEmpty())){
				      dbContact.setZip(inputContact.getZip());
				  }
				  if(!(inputContact.getMobilePhone()==null) &&!(inputContact.getMobilePhone().trim().isEmpty())){
				      dbContact.setMobilePhone(inputContact.getMobilePhone());
				  }
				  if(!(inputContact.getExternalId()==null) &&!(inputContact.getExternalId().trim().isEmpty())){
				      dbContact.setExternalId(inputContact.getExternalId());
				  }
				  if(!(inputContact.getHomeStore()==null) &&!(inputContact.getHomeStore().trim().isEmpty())){
				      dbContact.setHomeStore(inputContact.getHomeStore());
				  }
				  if(!(inputContact.getSubsidiaryNumber()==null) &&!(inputContact.getSubsidiaryNumber().trim().isEmpty())){
				      dbContact.setSubsidiaryNumber(inputContact.getSubsidiaryNumber());
				  }
				  if(!(inputContact.getHomePhone()==null) &&!(inputContact.getHomePhone().trim().isEmpty())){
				      dbContact.setHomePhone(inputContact.getHomePhone());
				  }


				  return dbContact;
			} catch (Exception e) {
				logger.error("Exception ::::", e);
				return dbContact;
			}
		  }

		 public static SkuFile mergeSkuFile(SkuFile inputSkuFile, SkuFile dbSkuFile) {

		     
			 if(dbSkuFile == null){
			      return inputSkuFile;
			  }

			  if(!(inputSkuFile.getListPrice()==null)){
			      dbSkuFile.setListPrice(inputSkuFile.getListPrice());
			  }
			  if(!(inputSkuFile.getDescription()==null) && !(inputSkuFile.getDescription().trim().isEmpty())){
			      dbSkuFile.setDescription(inputSkuFile.getDescription());
			  }
			  if(!(inputSkuFile.getSku()==null) && !(inputSkuFile.getSku().trim().isEmpty())){
			      dbSkuFile.setSku(inputSkuFile.getSku());
			  }
			  if(!(inputSkuFile.getItemCategory()==null) && !(inputSkuFile.getItemCategory().trim().isEmpty())){
			      dbSkuFile.setItemCategory(inputSkuFile.getItemCategory());
			  }
			  if(!(inputSkuFile.getItemSid()==null) && !(inputSkuFile.getItemSid().trim().isEmpty())){
			      dbSkuFile.setItemSid(inputSkuFile.getItemSid());
			  }
			  if(!(inputSkuFile.getStoreNumber()==null) && !(inputSkuFile.getStoreNumber().trim().isEmpty())){
			      dbSkuFile.setStoreNumber(inputSkuFile.getStoreNumber());
			  }
			  if(!(inputSkuFile.getSubsidiaryNumber()==null) && !(inputSkuFile.getSubsidiaryNumber().trim().isEmpty())){
			      dbSkuFile.setSubsidiaryNumber(inputSkuFile.getSubsidiaryNumber());
			  }
			  if(!(inputSkuFile.getDescription()==null) && !(inputSkuFile.getDescription().trim().isEmpty())){
			      dbSkuFile.setDescription(inputSkuFile.getDescription());
			  }
			  if(!(inputSkuFile.getVendorCode()==null) && !(inputSkuFile.getVendorCode().trim().isEmpty())){
			      dbSkuFile.setVendorCode(inputSkuFile.getVendorCode());
			  }
			  if(!(inputSkuFile.getDepartmentCode()==null) && !(inputSkuFile.getDepartmentCode().trim().isEmpty())){
			      dbSkuFile.setDepartmentCode(inputSkuFile.getDepartmentCode());
			  }
			  if(!(inputSkuFile.getClassCode()==null) && !(inputSkuFile.getClassCode().trim().isEmpty())){
			      dbSkuFile.setClassCode(inputSkuFile.getClassCode());
			  }
			  if(!(inputSkuFile.getSubClassCode()==null) && !(inputSkuFile.getSubClassCode().trim().isEmpty())){
			      dbSkuFile.setSubClassCode(inputSkuFile.getSubClassCode());
			  }
			  if(!(inputSkuFile.getDCS()==null) && !(inputSkuFile.getDCS().trim().isEmpty())){
			      dbSkuFile.setDCS(inputSkuFile.getDCS());
			  }
			  if(!(inputSkuFile.getUdf1()==null) &&!(inputSkuFile.getUdf1().trim().isEmpty())){
				  dbSkuFile.setUdf1(inputSkuFile.getUdf1());
			  }
			  if(!(inputSkuFile.getUdf2()==null) &&!(inputSkuFile.getUdf2().trim().isEmpty())){
				  dbSkuFile.setUdf2(inputSkuFile.getUdf2());
			  }
			  if(!(inputSkuFile.getUdf3()==null) &&!(inputSkuFile.getUdf3().trim().isEmpty())){
				  dbSkuFile.setUdf3(inputSkuFile.getUdf3());
			  }
			  if(!(inputSkuFile.getUdf4()==null) &&!(inputSkuFile.getUdf4().trim().isEmpty())){
				  dbSkuFile.setUdf4(inputSkuFile.getUdf4());
			  }
			  if(!(inputSkuFile.getUdf5()==null) &&!(inputSkuFile.getUdf5().trim().isEmpty())){
				  dbSkuFile.setUdf5(inputSkuFile.getUdf5());
			  }
			  if(!(inputSkuFile.getUdf6()==null) &&!(inputSkuFile.getUdf6().trim().isEmpty())){
				  dbSkuFile.setUdf6(inputSkuFile.getUdf6());
			  }
			  if(!(inputSkuFile.getUdf7()==null) &&!(inputSkuFile.getUdf7().trim().isEmpty())){
				  dbSkuFile.setUdf7(inputSkuFile.getUdf7());
			  }
			  if(!(inputSkuFile.getUdf8()==null) &&!(inputSkuFile.getUdf8().trim().isEmpty())){
				  dbSkuFile.setUdf8(inputSkuFile.getUdf8());
			  }
			  if(!(inputSkuFile.getUdf9()==null) &&!(inputSkuFile.getUdf9().trim().isEmpty())){
				  dbSkuFile.setUdf9(inputSkuFile.getUdf9());
			  }
			  if(!(inputSkuFile.getUdf10()==null) &&!(inputSkuFile.getUdf10().trim().isEmpty())){
				  dbSkuFile.setUdf10(inputSkuFile.getUdf10());
			  }
			  if(!(inputSkuFile.getUdf11()==null) &&!(inputSkuFile.getUdf11().trim().isEmpty())){
				  dbSkuFile.setUdf11(inputSkuFile.getUdf11());
			  }
			  if(!(inputSkuFile.getUdf12()==null) &&!(inputSkuFile.getUdf12().trim().isEmpty())){
				  dbSkuFile.setUdf12(inputSkuFile.getUdf12());
			  }
			  if(!(inputSkuFile.getUdf13()==null) &&!(inputSkuFile.getUdf13().trim().isEmpty())){
				  dbSkuFile.setUdf13(inputSkuFile.getUdf13());
			  }
			  if(!(inputSkuFile.getUdf14()==null) &&!(inputSkuFile.getUdf14().trim().isEmpty())){
				  dbSkuFile.setUdf14(inputSkuFile.getUdf14());
			  }
			  if(!(inputSkuFile.getUdf15()==null) &&!(inputSkuFile.getUdf15().trim().isEmpty())){
				  dbSkuFile.setUdf15(inputSkuFile.getUdf15());
			  }
	     
			  return dbSkuFile;
	 }
	 
		 public static RetailProSalesCSV mergeSalesFile(RetailProSalesCSV inputSalesFile, RetailProSalesCSV dbSalesFile) {

				if (dbSalesFile == null) {	return inputSalesFile;	}

				if (!(inputSalesFile.getDocSid() == null)) {
					dbSalesFile.setDocSid(inputSalesFile.getDocSid());
				}
				if (!(inputSalesFile.getQuantity() == null)) {
					dbSalesFile.setQuantity(inputSalesFile.getQuantity());
				}
				if (!(inputSalesFile.getSalesDate() == null)) {
					dbSalesFile.setSalesDate(inputSalesFile.getSalesDate());
				}
				if (!(inputSalesFile.getSalesPrice() == null)) {
					dbSalesFile.setSalesPrice(inputSalesFile.getSalesPrice());
				}
				if (!(inputSalesFile.getTenderType() == null) && !(inputSalesFile.getTenderType().trim().isEmpty())) {
					dbSalesFile.setTenderType(inputSalesFile.getTenderType());
				}
				if (!(inputSalesFile.getSku() == null) && !(inputSalesFile.getSku().trim().isEmpty())) {
					dbSalesFile.setSku(inputSalesFile.getSku());
				}
				if (!(inputSalesFile.getCustomerId() == null) && !(inputSalesFile.getCustomerId().trim().isEmpty())) {
					dbSalesFile.setCustomerId(inputSalesFile.getCustomerId());
				}
				if (!(inputSalesFile.getPromoCode() == null) && !(inputSalesFile.getPromoCode().trim().isEmpty())) {
					dbSalesFile.setPromoCode(inputSalesFile.getPromoCode());
				}
				if (!(inputSalesFile.getTax() == null)) {
					dbSalesFile.setTax(inputSalesFile.getTax());
				}
				if (!(inputSalesFile.getRecieptNumber() == null)) {
					dbSalesFile.setRecieptNumber(inputSalesFile.getRecieptNumber());
				}
				if (!(inputSalesFile.getItemSid() == null) 	&& !(inputSalesFile.getItemSid().trim().isEmpty())) {
					dbSalesFile.setItemSid(inputSalesFile.getItemSid());
				}
				if (!(inputSalesFile.getStoreNumber() == null) 	&& !(inputSalesFile.getStoreNumber().trim().isEmpty())) {
					dbSalesFile.setStoreNumber(inputSalesFile.getStoreNumber());
				}

				return dbSalesFile;
			}
		 
		  
	 public static boolean validateDate(String data, String dateFormat){
			boolean isValid = false;
			try{
				
				//String usdateFormat = PropertyUtil.getPropertyValue("customFiledDateFormat"); //mm/dd/yyyy
				if(data != null && dateFormat != null) {
					
					DateFormat format = new SimpleDateFormat(dateFormat);
				//format.setLenient(false);
					Date date = format.parse(data);
					isValid = true;
				}
			}catch(java.lang.NumberFormatException nfe){
				isValid = false;
			}catch (java.text.ParseException pe){
				isValid = false;
			}catch (IllegalArgumentException e) {
		    	isValid = false;
		    	logger.error("Exception ::::", e);
		    }catch(Exception e){
				isValid = false;
			}
			return isValid;
		} // validateDate  
    
	    
	 public static String encryptPassword(String userName,String password) {

			Md5PasswordEncoder md5 = new Md5PasswordEncoder();
	    	String encryptedPwd = md5.encodePassword(password,userName);
	    	return encryptedPwd;
		}    
	    
	    
	 public static Set<String> findCoupPlaceholders(String content) {
			
		String cfpattern = "\\[([^\\[]*?)\\]";
		
		Pattern r = Pattern.compile(cfpattern,Pattern.CASE_INSENSITIVE);
		Matcher m = r.matcher(content);

		String ph = null;
		Set<String> totalPhSet = new HashSet<String>();

		try {
			while(m.find()) {
	
				ph = m.group(1); //.toUpperCase()
				if(logger.isInfoEnabled()) logger.info("Ph holder :" + ph);
	
				
				 if(ph.startsWith("CC_")) {
					 totalPhSet.add(ph);
				 }
			}
				
		}catch (Exception e) {
			// TODO: handle exception
			return totalPhSet;
		}
		
		return totalPhSet;
	}
	    
	 
	 public static File unzip(String zipFileNamePath, String destDir) {
	        File dir = new File(destDir);
	        // create output directory if it doesn't exist
	        if(!dir.exists()) dir.mkdirs();
	        FileInputStream fis;
	        //buffer for read and write data to file
	        byte[] buffer = new byte[1024];
	        File newFile = null;
	        try {
	            fis = new FileInputStream(zipFileNamePath);
	            ZipInputStream zis = new ZipInputStream(fis);
	            ZipEntry ze = zis.getNextEntry();
	            while(ze != null){
	                String fileName = ze.getName();
	                newFile = new File(destDir + File.separator + fileName);
	                logger.info("Unzipping to "+newFile.getAbsolutePath());
	                //create directories for sub directories in zip
	                new File(newFile.getParent()).mkdirs();
	                FileOutputStream fos = new FileOutputStream(newFile);
	                int len;
	                while ((len = zis.read(buffer)) > 0) {
	                fos.write(buffer, 0, len);
	                }
	                fos.close();
	                //close this ZipEntry
	                zis.closeEntry();
	                ze = zis.getNextEntry();
	            }
	            //close last ZipEntry
	            zis.closeEntry();
	            zis.close();
	            fis.close();
	        } catch (IOException e) {
	            logger.error("Exception :::",e);
	            return null;
	        }
	         return newFile;
	    }
	 
	 
	 public static boolean writeMessage(String errorMsg,  String reason, String xmlfileName, String extention,String status){
			logger.info("Started writing failed message of file :"+xmlfileName);		
			String outboxoptIntelPath = PropertyUtil.getPropertyValue("outboxoptintel");
			FileWriter writer;
			try {
				String fileName = xmlfileName.substring(0, xmlfileName.lastIndexOf(extention))+"txt";
				File file = new File(outboxoptIntelPath+"/"+fileName);
				writer = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(writer);
				StringBuffer str = new StringBuffer();
				str.append("Status : "+status);
				str.append("\r\n");
				str.append("Reason: ");
				str.append(reason);
				if(errorMsg != null || errorMsg.trim().length() > 0 ){
					
					str.append("\r\n");
					str.append("Error Message: ");
					str.append(errorMsg);
				}
				bw.write(str.toString());
				
				bw.flush();
				bw.close();
			} catch (IOException e) {
				logger.error("Exception in while writing failed message.", e);
				return false;
			}
			logger.info("Completed writing failed message of file :"+xmlfileName);
			return true;
		}
	 
	 public static boolean validateZipCode(String zip, String countryType){
			
			/*if(zip == null && zip.length() == 0) {
				
				return false;
			}*/
			
		//	if(zipValidateMap.containsKey(countryType)){
				
			logger.info("country type is =="+countryType);
			String zipRegexValue = zipValidateMap.get(countryType);
			String pattern = zipRegexValue;
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(zip);
			return m.matches();
			
		//	}else return true;
		}
	 
	 public static String validateNumberValue(String num){
			String value = num;
			value = value.replaceAll("[^0-9]", "");
			value = value.replaceFirst("^0*", "");
			
			if(value.length() > 0)
			return value;
			else return null;
		}
		
		public static String validateDoubleValue(String num){

			String str = num;
			str = str.replaceFirst("^0*", "");
			String value = str.toString().replaceAll(",", "").trim();
			String pattern = "\\d*\\.?\\d*";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(value);
			boolean matches = m.matches();
			if(matches){
				return value;
			}else {
				return null;
			}

		}
		
		public static void sendCampaignFailureAlertMailToSupport(Users user, String smsCampaignName,String emailCampaignName,String notificationCampaignName,
				Calendar scheduledDate,String errorStatus,String configuredCount,String notSubmittedCount,String subjectLine) throws BaseServiceException{
		try {
			// TODO Auto-generated method stub
			
			/*Date date = scheduledDate.getTime();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
			String strDate = dateFormat.format(date);*/ 
			
			EmailQueueDao emailQueueDao = null;
			EmailQueueDaoForDML emailQueueDaoForDML = null;
			try {
				emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
				emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
			} catch (Exception e) {
				
				logger.error(e);
				throw new BaseServiceException("No dao(s) found in the context");
				
			}
			
			
			String supportMail = PropertyUtil.getPropertyValueFromDB(Constants.Mail_To_Support);
			String subject = PropertyUtil.getPropertyValueFromDB(OCConstants.CAMPAIGN_FAILED_TO_SEND_SUBJECT);
			String message ="";
			
			String userName= Utility.getOnlyUserName(user.getUserName());
			subject=subject.replace(Constants.ALERT_MESSAGE_PH_USERNAME, userName);
			if(smsCampaignName!=""){
				message =PropertyUtil.getPropertyValueFromDB(OCConstants.SMS_CAMPAIGN_FAILED_TO_SEND_CONTENT);
				message=message.replace(OCConstants.CAMPAIGN_NAME, smsCampaignName);
			}
			if(emailCampaignName!=""){
				message=PropertyUtil.getPropertyValueFromDB(OCConstants.EMAIL_CAMPAIGN_FAILED_TO_SEND_CONTENT);
				message=message.replace(OCConstants.CAMPAIGN_NAME, emailCampaignName);
				message=message.replace(OCConstants.SUBJECT_LINE,subjectLine);
			}
			if(notificationCampaignName!="") {
				message=PropertyUtil.getPropertyValueFromDB(OCConstants.NOTIFICATION_CAMPAIGN_FAILED_TO_SEND_CONTENT);
				message=message.replace(OCConstants.CAMPAIGN_NAME, notificationCampaignName);
				message=message.replace(OCConstants.SUBJECT_LINE,subjectLine);
			}
			
			
			if(errorStatus!=""){
			message=message.replace(OCConstants.ERROR_STATUS, errorStatus);
			}
			
			message=message.replace(OCConstants.SCHEDULED_DATE, MyCalendar.calendarToString(scheduledDate, MyCalendar.FORMAT_DATETIME_STYEAR));
			
			if(notSubmittedCount!=""){
			message=message.replace(OCConstants.CONFIGURED_COUNT,configuredCount);
			message=message.replace(OCConstants.NOT_SUBMITTED_COUNT,notSubmittedCount);
			}
			message=message.replace(OCConstants.USER_FNAME,user.getFirstName());
			message=message.replace(Constants.ALERT_MESSAGE_PH_USERNAME, userName);
			message = message.replace(OCConstants.USER_ORGID, Utility.getOnlyOrgId(user.getUserName()));
			
			
			EmailQueue emailQueueSupport = new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,
					Constants.EQ_STATUS_ACTIVE, supportMail, new Date());
			
			emailQueueDaoForDML.saveOrUpdate(emailQueueSupport);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error while sending a mail to support", e);
			throw new BaseServiceException("Error while sending a mail to support");
		}
		
		}
		
		public static void sendNotSubmittedAlertMailToSupport(Users user, String smsCampaignName,
				Calendar scheduledDate,String errorStatus,String configuredCount,String notSubmittedCount,String subjectLine) throws BaseServiceException{
		try {
			// TODO Auto-generated method stub
			
			/*Date date = scheduledDate.getTime();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
			String strDate = dateFormat.format(date);*/ 
			
			EmailQueueDao emailQueueDao = null;
			EmailQueueDaoForDML emailQueueDaoForDML = null;
			try {
				emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
				emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
			} catch (Exception e) {
				
				logger.error(e);
				throw new BaseServiceException("No dao(s) found in the context");
				
			}
			
			
			String supportMail = PropertyUtil.getPropertyValueFromDB(Constants.Mail_To_Support);
			String subject = PropertyUtil.getPropertyValueFromDB(OCConstants.CAMPAIGN_FAILED_TO_SEND_SUBJECT);
			String message ="";
			
			String userName= Utility.getOnlyUserName(user.getUserName());
			subject=subject.replace(Constants.ALERT_MESSAGE_PH_USERNAME, userName);
			if(smsCampaignName!=""){
				message =PropertyUtil.getPropertyValueFromDB(OCConstants.SMS_CAMPAIGN_NOT_SUBMITTED_CONTENT);
				message=message.replace(OCConstants.CAMPAIGN_NAME, smsCampaignName);
			}
			
			if(errorStatus!=""){
			message=message.replace(OCConstants.ERROR_STATUS, errorStatus);
			}
			else message=message.replace(OCConstants.ERROR_STATUS, "Not Submitted");
			
			message=message.replace(OCConstants.SCHEDULED_DATE, MyCalendar.calendarToString(scheduledDate, MyCalendar.FORMAT_DATETIME_STYEAR));
			
			if(notSubmittedCount!=""){
			message=message.replace(OCConstants.CONFIGURED_COUNT,configuredCount);
			message=message.replace(OCConstants.NOT_SUBMITTED_COUNT,notSubmittedCount);
			}
			message=message.replace(OCConstants.USER_FNAME,user.getFirstName());
			message=message.replace(Constants.ALERT_MESSAGE_PH_USERNAME, userName);
			message = message.replace(OCConstants.USER_ORGID, Utility.getOnlyOrgId(user.getUserName()));
			
			
			EmailQueue emailQueueSupport = new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,
					Constants.EQ_STATUS_ACTIVE, supportMail, new Date());
			
			emailQueueDaoForDML.saveOrUpdate(emailQueueSupport);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error while sending a mail to client & support", e);
			throw new BaseServiceException("Error while sending a mail to client & support");
		}
		
		}

        public static void sendCampaignEmailNegativeCreditAlertMailToSupport(Users user, String smsCampaignName,
        		String campaignName, Calendar scheduledDate, String errorStatus, String configuredCount,String notSubmittedCount,String subjectLine) throws BaseServiceException{
            try {
                EmailQueueDaoForDML emailQueueDaoForDML = null;
                try {
                    emailQueueDaoForDML = (EmailQueueDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
                } catch (Exception e) {
                    logger.error(e);
                    throw new BaseServiceException("No dao(s) found in the context");
                }
                    String supportMail = PropertyUtil.getPropertyValueFromDB(Constants.Mail_To_Support);
                    String subject = PropertyUtil.getPropertyValueFromDB(OCConstants.CAMPAIGN_WITH_NEGATIVE_EMAIL_CREDIT);
                    String message = "";
        
                    String userName = Utility.getOnlyUserName(user.getUserName());
                    subject = subject.replace(Constants.ALERT_MESSAGE_PH_USERNAME, userName);
        
                    message =PropertyUtil.getPropertyValueFromDB(OCConstants.CAMPAIGN_WITH_NEGATIVE_EMAIL_CREDIT_BODY);
                    
                    if (errorStatus != "") {
                        message = message.replace(OCConstants.ERROR_STATUS, errorStatus);
                    }
        
                    message = message.replace(Constants.ALERT_MESSAGE_PH_USERNAME, userName);
                    message = message.replace(OCConstants.USER_ORGID, Utility.getOnlyOrgId(user.getUserName()));
                    int emailBalance = (user.getEmailCount() - user.getUsedEmailCount());
                    message = message.replace("<EMAILBALANCE>", ""+emailBalance+"");
        
                    EmailQueue emailQueueSupport = new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,
                        Constants.EQ_STATUS_ACTIVE, supportMail, new Date());
        
                    emailQueueDaoForDML.saveOrUpdate(emailQueueSupport);
            } catch (Exception e) {
            	logger.error("Negative Email Credit Exception", e);
                throw new BaseServiceException("Error while sending a mail to support");
            }
            
        }
        
        public static void pingSubscriberService(String targetServiceUrl) {
    		
    		try {
    				URL url = new URL(targetServiceUrl);
    			
    		        URLConnection subscriberConnection = url.openConnection();
    		        DataInputStream dis = new DataInputStream(subscriberConnection.getInputStream());
    	            String inputLine;

    	            while ((inputLine = dis.readLine()) != null) {
    	                logger.info("Received message : "+ inputLine);
    	            }
    	            dis.close();
    		        
    		    } catch (MalformedURLException me) {
    		        logger.info("MalformedURLException: " + me);
    		        logger.error("Exception ::" , me);;
    		    } catch (Exception e) {
    		        logger.info("IOException: " + e);
    		        logger.error("Exception ::" , e);
    		    }
    		
    		
    	}
        public static void sendDRExtractionFailureMail(String docsid,Users user,String fileType) {
    		
        	try{
    			String supportMail = PropertyUtil.getPropertyValueFromDB(OCConstants.Mail_To_Support);
    			String subject = PropertyUtil.getPropertyValueFromDB(OCConstants.DRToLtyFailureSubject);
    			subject=subject.replace(OCConstants.USERNAME, user.getUserName());
    			String message = PropertyUtil.getPropertyValueFromDB(OCConstants.DRToLtyFailureMessage);
    			
    			message = message.replace(OCConstants.DOCSID,docsid);
    			message = message.replace(OCConstants.USER_ORGID,Utility.getOnlyOrgId(user.getUserName()));
    			message = message.replace(OCConstants.USERNAME,user.getUserName());
    			
    			EmailQueueDaoForDML emailQueueDaoForDML = null;
    			try {
    				emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
    			} catch (Exception e) {
    				
    				logger.error(e);
    				throw new BaseServiceException("No dao(s) found in the context");
    				
    			}
    			
    			EmailQueue emailQueueSupport = new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,
    					Constants.EQ_STATUS_ACTIVE, supportMail, new Date());
    			
    			emailQueueDaoForDML.saveOrUpdate(emailQueueSupport);
    		}
    		catch(Exception e){
    			logger.error("** Exception : ", e);
    		}
    		
    	}
        public static void sendCampaignDraftAlertMailToSupport(StringBuffer mailContentSb) throws BaseServiceException{
		try {
			// TODO Auto-generated method stub
			
			EmailQueueDao emailQueueDao = null;
			EmailQueueDaoForDML emailQueueDaoForDML = null;
			try {
				emailQueueDao = (EmailQueueDao)ServiceLocator.getInstance().getDAOByName("emailQueueDao");
				emailQueueDaoForDML = (EmailQueueDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName("emailQueueDaoForDML");
			} catch (Exception e) {
				
				logger.error(e);
				throw new BaseServiceException("No dao(s) found in the context");
				
			}
			
			
			String supportMail = PropertyUtil.getPropertyValueFromDB(Constants.Mail_To_Support);
			String subject = PropertyUtil.getPropertyValueFromDB(OCConstants.CAMPAIGN_DRAFT_SCH_ACTIVE_ALERT_SUBJECT);
			String message =PropertyUtil.getPropertyValueFromDB(OCConstants.CAMPAIGN_DRAFT_SCH_ACTIVE_CONTENT);
			message = message.replace("[mailContentSb]",mailContentSb);
			EmailQueue emailQueueSupport = new EmailQueue(subject, message, Constants.EQ_TYPE_SUPPORT_ALERT,
					Constants.EQ_STATUS_ACTIVE, supportMail, new Date());
			
			emailQueueDaoForDML.saveOrUpdate(emailQueueSupport);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error while sending a mail to support", e);
			throw new BaseServiceException("Error while sending a mail to support");
		}
		
		}
}
