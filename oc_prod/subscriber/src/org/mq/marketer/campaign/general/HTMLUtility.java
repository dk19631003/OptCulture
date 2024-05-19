package org.mq.marketer.campaign.general;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HTMLUtility {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	/**
	 * Gets text from the given HTML String.
	 * @param tempFile - HTML as a String
	 * @return Returns the grabbed text from the Given HTML String.
	 */
	public static String getTextFromHtml(String htmlStr) {
		String returnStr=null;
		try {
			
			//***** Convert HTML as a single line StringBuffer **********************			
			String lineArr[] = htmlStr.split("\n");
			StringBuffer sb = new StringBuffer();
			
			for (String tempStr : lineArr) {
				if(tempStr.trim().length()>0) {
					sb.append(tempStr.trim());
				}
			} // for
			
			Pattern r ;
			Matcher m ;
			
			//***** Remove Head **********************
			StringBuffer outsb = new StringBuffer();
			String formatpat="<\\s*?head.*?>.*?<\\s*?/\\s*?head\\s*?>";
			
			r = Pattern.compile(formatpat, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			
			while (m.find()) {
				m.appendReplacement(outsb, "");
			} //while
			m.appendTail(outsb);

			//***** Remove Script tag **********************
			
			String jsPtrn1 = "<\\s*?(script)\\s\\s*?[^><]*?/\\s*?>";
			
			sb=outsb;
			outsb=new StringBuffer();
			
			r = Pattern.compile(jsPtrn1, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			
			while (m.find()) {
				m.appendReplacement(outsb, "");
			} //while
			m.appendTail(outsb);


			String jsPtrn2 = "<\\s*?(script|style).*?>.*?<\\s*?/\\s*?\\1\\s*?>";
			
			sb=outsb;
			outsb=new StringBuffer();
			
			r = Pattern.compile(jsPtrn2, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			
			while (m.find()) {
				m.appendReplacement(outsb, "");
			} //while
			m.appendTail(outsb);
			
			//***** Replace Anchor Tags **********************
			sb=outsb;
			outsb=new StringBuffer();
			String anchorptrn = "<a\\s.*?href\\s*?=\\s*?\"(.*?)\".*?>(.*?)<\\s*?/\\s*?a\\s*?>";

			r = Pattern.compile(anchorptrn, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			
			while (m.find()) {
				if(m.group().contains("#")) continue;
				m.appendReplacement(outsb, m.group(2)+"("+m.group(1)+")");
			} //while
			m.appendTail(outsb);

			//***** Remove All HTML tags **********************
			sb=outsb;
			outsb=new StringBuffer();
			
			String htmltagptrn="<.*?>";
			r = Pattern.compile(htmltagptrn, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			
			while (m.find()) {
				// String url = m.group();
				m.appendReplacement(outsb, "\r\n");
			} //while
			m.appendTail(outsb);

			returnStr = StringEscapeUtils.unescapeHtml(removeBlankLines(outsb));
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		
		return returnStr;
	} // getTextFromHtml 
	
	public static String getBodyContentOnly(String htmlStr, String siteUrlStr) {
		String returnStr=null;
		try {
			logger.info("HTML content : " + htmlStr);
			
			//***** Convert HTML as a single line StringBuffer **********************			
			String lineArr[] = htmlStr.split("\n");
			StringBuffer sb=new StringBuffer();
			
			for (String tempStr : lineArr) {
				if(tempStr.trim().length()>0) {
					sb.append(tempStr.trim());
				} //if
			} // for
			
			Pattern r ;
			Matcher m ;
			
			
			//***** Get Head Content **********************
			String headPrtn="<\\s*?head.*?>(.*?)<\\s*?/\\s*?head\\s*?>";
			String headContent = null;
			r = Pattern.compile(headPrtn, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			
			while (m.find()) {
				headContent = m.group(1);
			} //while

			//***** Get BODY Content **********************
			String bodyPtrn = "<\\s*?body.*?>(.*?)<\\s*?/\\s*?body\\s*?>";
			String bodyContent = null;
			r = Pattern.compile(bodyPtrn, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			
			while (m.find()) {
				bodyContent = m.group(1);
			} //while
			
			if(bodyContent == null) {
				return null;
			} //if
			
			logger.info(">>>>>>>>>>>> Body Content : " + bodyContent);
			
			
			//***** Remove Script tag **********************
			
			String jsPtrn1 = "<\\s*?(script)\\s\\s*?[^><]*?/\\s*?>";
			StringBuffer outsb =new StringBuffer();
			
			r = Pattern.compile(jsPtrn1, Pattern.CASE_INSENSITIVE);
			m = r.matcher(bodyContent);
			
			while (m.find()) {
				m.appendReplacement(outsb, "");
			} //while
			m.appendTail(outsb);


			//***** Remove Script tag **********************
			String jsPtrn2 = "<\\s*?(script).*?>.*?<\\s*?/\\s*?\\1\\s*?>";
			sb=outsb;
			outsb=new StringBuffer();
			
			r = Pattern.compile(jsPtrn2, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			
			while (m.find()) {
				m.appendReplacement(outsb, "");
			} //while
			m.appendTail(outsb);
			
			
			//***** Handling Images **********************
		
			sb= outsb;
			outsb=new StringBuffer();
			
			String imgPtrn = PropertyUtil.getPropertyValue("HiddenImgPattern");
			r = Pattern.compile(imgPtrn, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			
			String imglinkStr = ""; 
			String imgUrl = "";
			while (m.find()) {
				imglinkStr = m.group();
				logger.info("******* Image found : " + imglinkStr + " groupCount : " + m.groupCount());
				imgUrl = getURLAbsolutePath(siteUrlStr, m.group(2));
				
				imglinkStr = imglinkStr.replace(m.group(2), imgUrl);
				
				logger.info("old img Url :" + m.group(2));
				logger.info("New img Url :" + imgUrl);
				m.appendReplacement(outsb, imglinkStr);
			} //while
			m.appendTail(outsb);
			logger.info("###################### After image tag Replace : " + outsb.toString());
	
			
			//***** Handling Anchor Tags **********************
			
			sb= outsb;
			outsb=new StringBuffer();
			
			String linkPtrn = PropertyUtil.getPropertyValue("LinkPattern");
			r = Pattern.compile(linkPtrn, Pattern.CASE_INSENSITIVE);
			m = r.matcher(sb);
			
			String alinkStr = ""; 
			String linkUrl = "";
			while (m.find()) {
				alinkStr = m.group();
				logger.info("******* Image found : " + alinkStr + " groupCount : " + m.groupCount());
				linkUrl = getURLAbsolutePath(siteUrlStr, m.group(2));
				
				alinkStr = alinkStr.replace(m.group(2), linkUrl);
				
				logger.info("old img Url :" + m.group(1));
				logger.info("New img Url :" + linkUrl);
				m.appendReplacement(outsb, alinkStr);
			} //while
			m.appendTail(outsb);
			logger.info("###################### After Anchor tag Replace : " + outsb.toString());
	
			
			
			
			
			
			if(headContent == null) {
				return removeBlankLines(outsb);
			} //if
			
			
			//***** Handling Style tag **********************
			String stylePtrn = "<\\s*?(style).*?>(.*?)<\\s*?/\\s*?\\1\\s*?>";
			
			r = Pattern.compile(stylePtrn, Pattern.CASE_INSENSITIVE);
			m = r.matcher(headContent);
			StringBuffer styleTagsb = new StringBuffer("<style>");
			while (m.find()) {
				logger.info("Style Found :" + m.group());
				styleTagsb.append(m.group(2));
			} //while
			styleTagsb.append("</style>");
			outsb.insert(0, styleTagsb);
			logger.info("###################### After style Replace : " + outsb.toString());
			
			
			//***** Handling css links **********************
			
			String linkCssPtrn = "(<\\s*?link.*?href\\s*=\\s*[\"\'](.*?)[\"\'].*?)>";
			r = Pattern.compile(linkCssPtrn, Pattern.CASE_INSENSITIVE);
			m = r.matcher(headContent);
			
			String linkStr = ""; 
			String cssUrl = "";
			while (m.find()) {
				linkStr = m.group(1);
				cssUrl = m.group(2);
				logger.info("******* Link found : " + linkStr + " groupCount : " + m.groupCount());
				
				cssUrl = getURLAbsolutePath(siteUrlStr, m.group(2));
				
				linkStr = linkStr.replace(m.group(2), cssUrl);
				
				logger.info("old Link Url :" + m.group(2));
				logger.info("New Link Url :" + cssUrl);
				
				if(linkStr.contains("css")) {
					if(linkStr.endsWith("/"))
						linkStr += ">";
					else
						linkStr += "/>";
					logger.info("***************************** CSS Link found : " + linkStr);
					outsb.insert(0, linkStr);
				} //if
			} //while
			
			logger.info("###################### After link Replace : " + outsb.toString());

			returnStr = removeBlankLines(outsb);
			
		}catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return returnStr;
	}
	
	 private static String getURLAbsolutePath(String url, String sufixStr) {
			
			if(url==null || sufixStr==null) return null;
			url=url.trim();
			sufixStr=sufixStr.trim();
			
			if(sufixStr.toLowerCase().startsWith("http://") || 
			   sufixStr.toLowerCase().startsWith("https://")) {
				
				return sufixStr;
			} // if
			
			if(url.endsWith("/")) url = url.substring(0,url.length()-1);
			
			if(sufixStr.endsWith("/")) sufixStr = sufixStr.substring(0,sufixStr.length()-1); // Can be removed
			
			String retStr=null;
			
			int domainStInd = url.indexOf("//")+2;
			int firstSlashInd = url.indexOf("/", domainStInd);
			int lastSlashInd = url.lastIndexOf("/");

			if(lastSlashInd <= domainStInd) {
				lastSlashInd = -1;
			}
			
			logger.info("domainStInd="+domainStInd+"  SlashInd="+firstSlashInd +"  LastSlInd="+lastSlashInd);

			
			if(sufixStr.startsWith("/")) {
				retStr = (firstSlashInd != -1) ? (url.substring(0, firstSlashInd) + sufixStr) : (url + sufixStr);
			}
			else {
				retStr = (lastSlashInd != -1) ?(url.substring(0, lastSlashInd) + "/" + sufixStr) : (url + "/" + sufixStr); 
			}
			
			return retStr; 
		} // getURLAbsolutePath
	
	private static String removeBlankLines(StringBuffer outsb) {
		//***** Remove Multiple blank lines **********************
		String[] linesArr= outsb.toString().split("\n");
		StringBuffer sb=new StringBuffer();
		
		for (String str : linesArr) {
			if(str.trim().length()>0)
				sb.append(str+"\r\n");
		}// for
		
		return sb.toString().trim();
	}
	
	
}
