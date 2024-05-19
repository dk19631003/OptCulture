package org.mq.marketer.campaign.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Entities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements; 
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PlaceHolders;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.utils.OCConstants;





public class PrepareFinalHtml {
		
		static final String HTTP_TOKEN = "http://";
		static final String LINK_PATTERN = PropertyUtil.getPropertyValue("LinkPattern");
		static final String CLICK_TRACK_URL = PropertyUtil.getPropertyValue("DRClickTrackUrl");
		static final String SUBSCRIBER_URL = PropertyUtil.getPropertyValue("subscriberUrl");
		static final String FOOTER_TEXT = PropertyUtil.getPropertyValue("footerText");
		static final String OPEN_TRACK_URL = PropertyUtil.getPropertyValue("DROpenTrackUrl");
		//static final String DR_PDF_URL=PropertyUtil.getPropertyValue("DRPdfGenerationUrl");
		//static final String DR_PDF_DIV_TEMPLATE = PropertyUtil.getPropertyValue("DRPDFdivTemplate");
		static final String DR_BARCODE_WIDTH = PropertyUtil.getPropertyValue("drBarcodeWidth");//get width from db application properties table
		static final String DR_BARCODE_HEIGHT = PropertyUtil.getPropertyValue("drBarcodeHeight");//get height from db application properties table

		static final String APP_URL = PropertyUtil.getPropertyValue("ApplicationUrl");
		static final String APP_MAIN_URL = "https://app.optculture.com/subscriber/";
		static final String APP_MAIN_URL_HTTP = "http://app.optculture.com/subscriber/";
		static final String MAILCONTENT_URL = "http://mailcontent.info/subscriber/";
		static final String MAILHANDLER_URL = "http://mailhandler01.info/subscriber/";
		private static final String ImageServer_Url = PropertyUtil.getPropertyValue("ImageServerUrl");
		private static final String DR_PDF_URL=PropertyUtil.getPropertyValue("DRPdfGenerationUrl");
		private static final String DR_PDF_DIV_TEMPLATE = PropertyUtil.getPropertyValue("DRPDFdivTemplate");
		
		
		private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	 
	
		public static String prepareStuff( String htmlStuff, String editorType, String docSid, String recptNum) {
			

			try {
				if(htmlStuff == null ) {

					logger.info(" Input content is null for test mail");
					return null;
				}

				StringBuffer contentSb = null;

				//logger.info("preparefinalhtml initial htmlcontent : " + htmlStuff);

				
				//BeeEditor
					contentSb = new StringBuffer();
					Document doc = Jsoup.parse(htmlStuff);
					//suraj-3 lines
					doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
			        doc.outputSettings().prettyPrint(true);
			        doc.outputSettings().charset("ISO-8859-1");
					
					//Elements bodyele = doc.getElementsByTag("body");
					//Element body = doc.body();
					/*Elements elements = doc.select("body").first().children();
					if(elements!=null){
					for (Element el : elements) {
						// System.out.println(el.toString());
						contentSb.append(el.toString());
					}
					logger.info("preparefinalhtml jsoup htmlcontent : " + contentSb.toString());
					}
				else contentSb = new StringBuffer(htmlStuff);*/
					String head= null;
				if(editorType != null && editorType.equalsIgnoreCase(OCConstants.DRAG_AND_DROP_EDITOR)) {
					
					Elements elements = doc.select("head");
					 head = elements.toString();
					Elements elements1 = doc.select("body");
					String body = elements1.toString();
					contentSb.append(body);
					
				}else{
					contentSb = new StringBuffer(htmlStuff);
					
				}
				


					
				StringBuffer sb = new StringBuffer();

				String replaceStr;
				int options = 0;
				options |= 128; 	//This option is for Case insensitive
				options |= 32; 		//This option is for Dot matches newline

				/**
				 *  converts links with click track 
				 */
				Pattern  r = Pattern.compile(LINK_PATTERN, options);
				Matcher  m = r.matcher(contentSb);
				String anchorUrl;
				while (m.find()) {
					anchorUrl = m.group(2).trim(); // has rec tag 
					if(anchorUrl.contains("|^REC_")) {
						
						continue;
					}
			//		logger.info("Anchor Tag : " + htmlStuff); APP-4568
					try{
						if(anchorUrl.indexOf("#") != -1 || anchorUrl.indexOf("mailto") != -1) {
							continue;
						}

						if(anchorUrl.indexOf("/susbscriber/updateDigitalReport.mqrm?requestedAction=") != -1 ) {
							if(anchorUrl.indexOf("&amp;url=") != -1) {
								anchorUrl = anchorUrl.substring(anchorUrl.indexOf("&amp;url=")+9).trim();
							}
							else {
								continue;
							}
						}

						if(!anchorUrl.startsWith(HTTP_TOKEN) && !(anchorUrl.startsWith("https://"))) {

							anchorUrl = HTTP_TOKEN + anchorUrl;
						}
						if(anchorUrl.contains("|^REC_")){
						 // because the action url is not replacing 
								replaceStr = CLICK_TRACK_URL.replace(Constants.DR_CLICK_TRACK_URL, anchorUrl);

						}else {
						anchorUrl = Utility.encodeUrl(anchorUrl);
						replaceStr = CLICK_TRACK_URL.replace(Constants.DR_CLICK_TRACK_URL, anchorUrl);
						}
						replaceStr = m.group().replace(m.group(2), replaceStr);
						m.appendReplacement(sb, replaceStr);
					}catch (Exception e) {
						logger.info("** Exception : Problem while encoding the URL " + e);
					}

				} //while
				m.appendTail(sb);
				logger.info("------Click URLs are converted for tracking  --------");

				String openTrackUrl = Constants.DR_OPEN_TRACK_URL;
				// String openTrackImg = SUBSCRIBER_URL+ "img/digitransparent.gif";
				String  footerTextStr =FOOTER_TEXT.replace(openTrackUrl, OPEN_TRACK_URL);

				//sb.append(footerTextStr.replace(footerTextStr, openTrackImg));
				sb.append(footerTextStr);
				//wrap the conent in HTML Tag

				StringBuffer htmlTotStringBuffer = new StringBuffer();
				String pdflinkUrl = DR_PDF_URL;
				 pdflinkUrl = pdflinkUrl.replace("|^", "[").replace("^|", "]");
				 String pdfUrl = DR_PDF_DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,"To Download as a PDF <a href='" + pdflinkUrl + "'> click here</a>");
				 
				//String pdflinkUrl = DR_PDF_URL;
				//String pdfUrl = DR_PDF_DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,"To Download as a PDF <a href='" + pdflinkUrl + "'> click here</a>");
				if(editorType != null && editorType.equalsIgnoreCase(OCConstants.DRAG_AND_DROP_EDITOR)) {
					
					Document doc1 = Jsoup.parse(sb.toString());
					//suraj-3 lines
					doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
			        doc.outputSettings().prettyPrint(true);
			        doc.outputSettings().charset("ISO-8859-1");
					//Element bodyelements = doc1.select("body").first();
					Element bodyelements = doc1.select("body").first().prepend(pdfUrl);;
					String bodyStr = bodyelements.toString();
					htmlTotStringBuffer = new StringBuffer(bodyStr);
					String mobileHeadStr = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional //EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> <html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">";
					head = head.replace("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">", "<meta name=\"viewport\" content=\"width=device-width\"> ")
							.replace("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">", "<meta name=\"viewport\" content=\"width=device-width\"> ")
							.replace("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">", "<meta name=\"viewport\" content=\"width=device-width\"> ")
							.replace("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">", "<meta name=\"viewport\" content=\"width=device-width\"> ");
					htmlTotStringBuffer.insert(0, head);
					htmlTotStringBuffer.insert(0, mobileHeadStr);
					htmlTotStringBuffer.append("</html>");
					
				}else{
					String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth_oc]{width:100% !important; height:auto !important;} *[class=headerblock_oc] {width:100% !important; height:auto !important;}	*[class=headerblock_oc] img{width:100% !important; height:auto !important;}	*[class=prodblock_oc]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock_oc] img{height:auto !important;}	}	</style></head><body bgcolor='#FFFFFF' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FFFFFF;-webkit-text-size-adjust:none; margin:0;'>";
					sb.insert(0, pdfUrl);
					sb.insert(0, mobileHeadStr);
					sb.append("</body></html>");
					htmlTotStringBuffer = sb;
				}
				//Commented after responsive headers


				//Added after responsive headers
				//String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth_oc]{width:100% !important; height:auto !important;} *[class=headerblock_oc] {width:100% !important; height:auto !important;}	*[class=headerblock_oc] img{width:100% !important; height:auto !important;}	*[class=prodblock_oc]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock_oc] img{height:auto !important;}	}	</style></head><body bgcolor='#FFFFFF' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FFFFFF;-webkit-text-size-adjust:none; margin:0;'>";
				//String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>Untitled Document</title><style type='text/css'>	@media screen and (max-width:480px){.fullwidth{width:100% !important; height:auto !important;}.headerblock {width:100% !important; height:auto !important;}.headerblock img{width:100% !important; height:auto !important;}.prodblock{width:100% !important; display:block !important; float:left !important;}.prodblock img{width:100% !important; height:auto !important;}}</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
				
				//To enable PDF Link in mail uncomment the following section 



				logger.info("------ Footer added------");
				String htmlContent = htmlTotStringBuffer.toString();
				//logger.info("------ final content------" + htmlContent);

				htmlContent = replaceReceiptImgSrcWithMqrm(htmlContent, docSid, recptNum);
				//htmlContent = htmlContent.replace("[", "[[").replace("|^", "[").replace("^|", "]");
				htmlContent = htmlContent.replace("|^", "[").replace("^|", "]");


				//DR BARCODE VenkataRatna Motupalli
				//String receiptNumber=null;
				return htmlContent;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ", e);
				return null;
			}
		
		}
		public static String replaceReceiptImgSrcWithMqrm(String htmlContent, String docsid, String recptnumber){
			

			try{
				String imgPattern = "<img\\s+.*?((?:id\\s*=\\s*\\\"?DRBC_\\w\\\"?)).*?>";//DRBC 
				String idPattern = "<img .*?id\\s*?=\\\"?(.*?)\\\".*?>";
				String srcPattern = "<img .*?src\\s*?=\\\"?(.*?)\\\".*?>";
				String wPattern = "<img .*?width\\s*?=\\\"?(.*?)\\\".*?>";
				String hPattern = "<img .*?height\\s*?=\\\"?(.*?)\\\".*?>";
				String stylePattern = "<img .*?style\\s*?=\\\"?(.*?)\\\".*?>";

				Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(htmlContent);
				
				
				String imgPatternDocsId = "<img\\s+.*?((?:id\\s*=\\s*\\\"?DRBC_DOCSID_\\w\\\"?)).*?>";//DRBC 
				String idPatternDocsId = "<img .*?id\\s*?=\\\"?(.*?)\\\".*?>";
				String srcPatternDocsId = "<img .*?src\\s*?=\\\"?(.*?)\\\".*?>";
				String wPatternDocsId = "<img .*?width\\s*?=\\\"?(.*?)\\\".*?>";
				String hPatternDocsId = "<img .*?height\\s*?=\\\"?(.*?)\\\".*?>";
				String stylePatternDocsId = "<img .*?style\\s*?=\\\"?(.*?)\\\".*?>";

				Pattern patternDocsId = Pattern.compile(imgPatternDocsId,Pattern.CASE_INSENSITIVE);
				Matcher matcherDocsId = patternDocsId.matcher(htmlContent);

				
				
				while(matcherDocsId.find()) {

					String imgtag = null;
					//String srcAttr = null;
					String idAttr = null;
					String wAttr = null;
					String hAttr = null;
					String styleAttr = null;

					imgtag = matcherDocsId.group();

					Pattern idp = Pattern.compile(idPatternDocsId, Pattern.CASE_INSENSITIVE);
					Matcher idm = idp.matcher(imgtag);

					while(idm.find()){
						idAttr = idm.group(1);
					}

					wAttr = DR_BARCODE_WIDTH.trim();
					hAttr = DR_BARCODE_HEIGHT.trim();

					String ccPhTokens[] = idAttr.split("_");

					//String phStr = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2];
					String COUPON_CODE_URL = "";
					String mqrmImgtag = "";

					//TODO 	******************** DRCouponCodeUrl give the dropenclicks mqrm *********************
					/*COUPON_CODE_URL = PropertyUtil.getPropertyValue("DRCouponCodeUrl").replace("|^code^|","["+receiptNumber+"]")
							.replace("|^width^|", wAttr).replace("|^height^|", hAttr)
							.replace("|^type^|", ccPhTokens[3]);*/

					COUPON_CODE_URL = PropertyUtil.getPropertyValue("DRBarCodeUrl");//.replace("|^receiptNumber^|","[|^receiptNumber^|]");
				//	COUPON_CODE_URL = PropertyUtil.getPropertyValue("DRBarDocSidCodeUrl"); //for docsid
					//.replace("|^width^|", wAttr).replace("|^height^|", hAttr)
					//.replace("|^type^|", ccPhTokens[3]);
					//COUPON_CODE_URL=COUPON_CODE_URL.replace("DRBCreceiptNumber", "DRBCdocumentNumber");
					
					COUPON_CODE_URL = COUPON_CODE_URL.replace("|^", "[").replace("^|", "]");
					mqrmImgtag = "<img id=\""+idAttr+"\" src=\""+COUPON_CODE_URL+"\" width=\""+wAttr+"\" height=\""+hAttr+"\"  alt=\"[DRBCdocumentNumber]\" />";

					logger.info("PrepareStuff DR Barcode for DOCSID: "+mqrmImgtag);
					logger.info(" docsid  "+docsid);
					//logger.info("code= phStr"+phStr+" width="+wAttr+" height="+hAttr+" type="+ccPhTokens[3]);
					//logger.info("code= receiptNumber |^receiptNumber^| width="+wAttr+" height="+hAttr+" type="+ccPhTokens[3]);


					htmlContent = htmlContent.replace(imgtag, mqrmImgtag);
					htmlContent = htmlContent.replace("[DRBCreceiptNumber]", docsid);
					//htmlContent = htmlContent.replace("[DRBCdocumentNumber]", docsid);
					//	logger.info("htmlContent with barcode = "+htmlContent);

				} 



				while(matcher.find()) {

					String imgtag = null;
					//String srcAttr = null;
					String idAttr = null;
					String wAttr = null;
					String hAttr = null;
					String styleAttr = null;

					imgtag = matcher.group();

					Pattern idp = Pattern.compile(idPattern, Pattern.CASE_INSENSITIVE);
					Matcher idm = idp.matcher(imgtag);

					while(idm.find()){
						idAttr = idm.group(1);
					}

					wAttr = DR_BARCODE_WIDTH.trim();
					hAttr = DR_BARCODE_HEIGHT.trim();

					String ccPhTokens[] = idAttr.split("_");

					//String phStr = ccPhTokens[0]+"_"+ccPhTokens[1]+"_"+ccPhTokens[2];
					String COUPON_CODE_URL = "";
					String mqrmImgtag = "";

					//TODO 	******************** DRCouponCodeUrl give the dropenclicks mqrm *********************
					/*COUPON_CODE_URL = PropertyUtil.getPropertyValue("DRCouponCodeUrl").replace("|^code^|","["+receiptNumber+"]")
							.replace("|^width^|", wAttr).replace("|^height^|", hAttr)
							.replace("|^type^|", ccPhTokens[3]);*/

					COUPON_CODE_URL = PropertyUtil.getPropertyValue("DRBarCodeUrl");//.replace("|^receiptNumber^|","[|^receiptNumber^|]");
					//.replace("|^width^|", wAttr).replace("|^height^|", hAttr)
					//.replace("|^type^|", ccPhTokens[3]);
					COUPON_CODE_URL = COUPON_CODE_URL.replace("|^", "[").replace("^|", "]");
					mqrmImgtag = "<img id=\""+idAttr+"\" src=\""+COUPON_CODE_URL+"\" width=\""+wAttr+"\" height=\""+hAttr+"\"  alt=\"[DRBCreceiptNumber]\" />";

					logger.info("PrepareStuff DR Barcode: "+mqrmImgtag);
					//logger.info("code= phStr"+phStr+" width="+wAttr+" height="+hAttr+" type="+ccPhTokens[3]);
					//logger.info("code= receiptNumber |^receiptNumber^| width="+wAttr+" height="+hAttr+" type="+ccPhTokens[3]);


					htmlContent = htmlContent.replace(imgtag, mqrmImgtag);
					htmlContent = htmlContent.replace("[DRBCreceiptNumber]", recptnumber);
					//	logger.info("htmlContent with barcode = "+htmlContent);

				} 

			}catch(Exception e){
				logger.error("Exception ::::", e);
				logger.error("** Exception in replacing coupon img tag with barcode mqrm **");
				return htmlContent;
			}
			return htmlContent;
		
		}
	public static String prepareStuff( String htmlStuff, String editorType ) {
		if(htmlStuff == null ) {
			
		logger.info(" Input content is null for test mail");
			return null;
		}
		
		StringBuffer contentSb = null;
		
		
		//BeeEditor
		contentSb = new StringBuffer();
		Document doc = Jsoup.parse(htmlStuff);
		//suraj-3 lines
		doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
        doc.outputSettings().prettyPrint(true);
        doc.outputSettings().charset("ISO-8859-1");	
		String head= null;
		if(editorType != null && editorType.equalsIgnoreCase(OCConstants.DRAG_AND_DROP_EDITOR)) {

			Elements elements = doc.select("head");
			head = elements.toString();
			Elements elements1 = doc.select("body");
			String body = elements1.toString();
			contentSb.append(body);

		}else{
			contentSb = new StringBuffer(htmlStuff);

		}
		
		//contentSb = new StringBuffer(htmlStuff);
		StringBuffer sb = new StringBuffer();
		
		String replaceStr;
		int options = 0;
		options |= 128; 	//This option is for Case insensitive
		options |= 32; 		//This option is for Dot matches newline
		

		
		
		/**
		 *  converts links with click track 
		 */
		
		Pattern  r = Pattern.compile(LINK_PATTERN, options);
		Matcher  m = r.matcher(contentSb);
	    String anchorUrl;
        while (m.find()) {
			anchorUrl = m.group(2).trim();
			logger.info("Anchor Tag : " + anchorUrl);
			try{
	            if(anchorUrl.indexOf("#") != -1 || anchorUrl.indexOf("mailto") != -1) {
	            	continue;
	            }
	            	
	            if(anchorUrl.indexOf("/susbscriber/updateDigitalReport.mqrm?requestedAction=") != -1 ) {
	            	if(anchorUrl.indexOf("&amp;url=") != -1) {
	            		anchorUrl = anchorUrl.substring(anchorUrl.indexOf("&amp;url=")+9).trim();
	            	}
	            	else {
	            		continue;
	            	}
	            }
	            
            	if(!anchorUrl.startsWith(HTTP_TOKEN) && !(anchorUrl.startsWith("https://"))) {
            		
            			anchorUrl = HTTP_TOKEN + anchorUrl;
            	}
            	
            	anchorUrl = Utility.encodeUrl(anchorUrl);
				replaceStr = CLICK_TRACK_URL.replace(Constants.DR_CLICK_TRACK_URL, anchorUrl);;
				replaceStr = m.group().replace(m.group(2), replaceStr);
                m.appendReplacement(sb, replaceStr);
			}catch (Exception e) {
				logger.info("** Exception : Problem while encoding the URL " + e);
			}
			
        } //while
	    m.appendTail(sb);
	    logger.info("------ URLs are converted --------");
	    
	    String openTrackUrl = Constants.DR_OPEN_TRACK_URL;
	   // String openTrackImg = SUBSCRIBER_URL+ "img/digitransparent.gif";
	    String  footerTextStr =FOOTER_TEXT.replace(openTrackUrl, OPEN_TRACK_URL);
		
		//sb.append(footerTextStr.replace(footerTextStr, openTrackImg));
	    sb.append(footerTextStr);
		//wrap the conent in HTML Tag
	    
	    //Commented after responsive headers
		/*sb.insert(0, "<HTML><HEAD></HEAD><BODY>");
	    //String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth]{width:100% !important; height:auto !important;} *[class=headerblock] {width:100% !important; height:auto !important;}	*[class=headerblock] img{width:100% !important; height:auto !important;}	*[class=prodblock]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock] img{width:100% !important; height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
	    //sb.insert(0, mobileHeadStr);
		sb.append("</BODY></HTML>");*/
		 String pdflinkUrl = DR_PDF_URL;
		 pdflinkUrl = pdflinkUrl.replace("|^", "[").replace("^|", "]");
		 String pdfUrl = DR_PDF_DIV_TEMPLATE.replace(PlaceHolders.DIV_CONTENT,"To Download as a PDF <a href='" + pdflinkUrl + "'> click here</a>");
		// Document doc1 = Jsoup.parse(htmlContent);
		 //Element bodyelements = doc1.select("body").first().prepend(pdfUrl);;
		
	    
	    StringBuffer htmlTotStringBuffer = new StringBuffer();
	    
	    if(editorType != null && editorType.equalsIgnoreCase(OCConstants.DRAG_AND_DROP_EDITOR)) {
			 
			Document doc1 = Jsoup.parse(sb.toString());
			//suraj-3 lines
			doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
	        doc.outputSettings().prettyPrint(true);
	        doc.outputSettings().charset("ISO-8859-1");
			//Element bodyelements = doc1.select("body").first();
			 Element bodyelements = doc1.select("body").first().prepend(pdfUrl);;

			String bodyStr = bodyelements.toString();
			
			htmlTotStringBuffer = new StringBuffer(bodyStr);
			//htmlTotStringBuffer.insert(0, pdfUrl);
			String mobileHeadStr = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional //EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> <html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">";
			htmlTotStringBuffer.insert(0, head);
			htmlTotStringBuffer.insert(0, mobileHeadStr);
			htmlTotStringBuffer.append("</html>");
			
		}else{
			
			String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth_oc]{width:100% !important; height:auto !important;} *[class=headerblock_oc] {width:100% !important; height:auto !important;}	*[class=headerblock_oc] img{width:100% !important; height:auto !important;}	*[class=prodblock_oc]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock_oc] img{height:auto !important;}	}	</style></head><body bgcolor='#FFFFFF' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FFFFFF;-webkit-text-size-adjust:none; margin:0;'>";
			sb.insert(0, pdfUrl);  		
			sb.insert(0, mobileHeadStr);
			sb.append("</body></html>");
			htmlTotStringBuffer = sb;
		}
	   
	    
	   /* //Added after responsive headers
	    String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />	<style type='text/css'>@media screen and (max-width:480px){*[class=fullwidth_oc]{width:100% !important; height:auto !important;} *[class=headerblock_oc] {width:100% !important; height:auto !important;}	*[class=headerblock_oc] img{width:100% !important; height:auto !important;}	*[class=prodblock_oc]{width:100% !important; display:block !important; float:left !important;}	*[class=prodblock_oc] img{height:auto !important;}	}	</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
		//String mobileHeadStr = "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>Untitled Document</title><style type='text/css'>	@media screen and (max-width:480px){.fullwidth{width:100% !important; height:auto !important;}.headerblock {width:100% !important; height:auto !important;}.headerblock img{width:100% !important; height:auto !important;}.prodblock{width:100% !important; display:block !important; float:left !important;}.prodblock img{width:100% !important; height:auto !important;}}</style></head><body bgcolor='#e4e4e4' style='-webkit-font-smoothing: antialiased;width:100% !important;background:#FAFAFA;-webkit-text-size-adjust:none; margin:0;'>";
		sb.insert(0, mobileHeadStr);
		sb.append("</body></html>");
		
		 logger.info("------ Footer added------");
		String htmlContent = sb.toString();*/
	    
	    logger.info("------ Footer added------");
		String htmlContent = htmlTotStringBuffer.toString();
		logger.info("------ final content------" + htmlContent);
		
		//htmlContent = htmlContent.replace("[", "[[").replace("|^", "[").replace("^|", "]");
		htmlContent = htmlContent.replace("|^", "[").replace("^|", "]");
		
		return htmlContent;
		
	}
	
	public static String replaceImgURL(String htmlContent, String userName) {
		/**
		 *  handles SystemData images in background urls
		 */
		
		String imgPattern = "<img.*?src=\"(.*?)\"[^\\>]+>";
		
		Pattern pattern = Pattern.compile(imgPattern,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(htmlContent);
		
		while(matcher.find()) {

			String imgtag = null;
			String srcAttr = null;
			
			imgtag = matcher.group();
			
			String imgtag1 =null;
			
			srcAttr = matcher.group(1);
			String UserData="UserData/" + userName + "/Gallery";
			if(srcAttr.startsWith(APP_URL)||srcAttr.startsWith(APP_MAIN_URL)||srcAttr.startsWith(APP_MAIN_URL_HTTP)
					||srcAttr.startsWith(MAILCONTENT_URL)||srcAttr.startsWith(MAILHANDLER_URL)){
				if(srcAttr.contains(UserData)){
					
					imgtag1 = matcher.group(1);
					imgtag1 = imgtag1.replace(APP_URL, ImageServer_Url).replace(APP_MAIN_URL, ImageServer_Url)
							.replace(APP_MAIN_URL_HTTP, ImageServer_Url).replace(MAILCONTENT_URL, ImageServer_Url)
							.replace(MAILHANDLER_URL, ImageServer_Url);
					logger.info("imgtag1....."+ imgtag1);
					imgtag1 = matcher.group().replace(matcher.group(1), Utility.encodeSpace(imgtag1));
					logger.info("imgtag2....."+ imgtag1);
					htmlContent = htmlContent.replace(imgtag, imgtag1);
				}
			}
		} 
		return htmlContent;
	}
	
	/*
	 * public static void main (String args[]) { String htmlcontent
	 * ="<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional //EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
	 * +
	 * "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n"
	 * + " <head> \n" +
	 * "  <!--[if gte mso 9]><xml><o:OfficeDocumentSettings><o:AllowPNG/><o:PixelsPerInch>96</o:PixelsPerInch></o:OfficeDocumentSettings></xml><![endif]--> \n"
	 * +
	 * "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"> \n"
	 * + "  <meta name=\"viewport\" content=\"width=device-width\"> \n" +
	 * "  <!--[if !mso]><!--> \n" +
	 * "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \n" +
	 * "  <!--<![endif]--> \n" + "  <title></title> \n" + "  <!--[if !mso]><!--> \n"
	 * + "  <!--<![endif]--> \n" +
	 * "  <style type=\"text/css\">		body {			margin: 0;			padding: 0;		}		table,		td,		tr {			vertical-align: top;			border-collapse: collapse;		}		* {			line-height: inherit;		}		a[x-apple-data-detectors=true] {			color: inherit !important;			text-decoration: none !important;		}	</style> \n"
	 * +
	 * "  <style type=\"text/css\" id=\"media-query\">		@media (max-width: 520px) {			.block-grid,			.col {				min-width: 320px !important;				max-width: 100% !important;				display: block !important;			}			.block-grid {				width: 100% !important;			}			.col {				width: 100% !important;			}			.col>div {				margin: 0 auto;			}			img.fullwidth,			img.fullwidthOnMobile {				max-width: 100% !important;			}			.no-stack .col {				min-width: 0 !important;				display: table-cell !important;			}			.no-stack.two-up .col {				width: 50% !important;			}			.no-stack .col.num4 {				width: 33% !important;			}			.no-stack .col.num8 {				width: 66% !important;			}			.no-stack .col.num4 {				width: 33% !important;			}			.no-stack .col.num3 {				width: 25% !important;			}			.no-stack .col.num6 {				width: 50% !important;			}			.no-stack .col.num9 {				width: 75% !important;			}			.video-block {				max-width: none !important;			}			.mobile_hide {				min-height: 0px;				max-height: 0px;				max-width: 0px;				display: none;				overflow: hidden;				font-size: 0px;			}			.desktop_hide {				display: block !important;				max-height: none !important;			}		}	</style>\n"
	 * + " </head>\n" +
	 * " <body class=\"clean-body\" style=\"margin: 0; padding: 0; -webkit-text-size-adjust: 100%; background-color: #FFFFFF;\"> \n"
	 * + "  <!--[if IE]><div class=\"ie-browser\"><![endif]--> \n" +
	 * "  <table class=\"nl-container\" style=\"table-layout: fixed; vertical-align: top; min-width: 320px; Margin: 0 auto; border-spacing: 0; border-collapse: collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; background-color: #FFFFFF; width: 100%;\" cellpadding=\"0\" cellspacing=\"0\" role=\"presentation\" width=\"100%\" bgcolor=\"#FFFFFF\" valign=\"top\"> \n"
	 * + "   <tbody> \n" +
	 * "    <tr style=\"vertical-align: top;\" valign=\"top\"> \n" +
	 * "     <td style=\"word-break: break-word; vertical-align: top;\" valign=\"top\"> \n"
	 * +
	 * "      <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"center\" style=\"background-color:#FFFFFF\"><![endif]--> \n"
	 * + "      <div style=\"background-color:transparent;\"> \n" +
	 * "       <div class=\"block-grid \" style=\"Margin: 0 auto; min-width: 320px; max-width: 500px; overflow-wrap: break-word; word-wrap: break-word; word-break: break-word; background-color: transparent;\"> \n"
	 * +
	 * "        <div style=\"border-collapse: collapse;display: table;width: 100%;background-color:transparent;\"> \n"
	 * +
	 * "         <!--[if (mso)|(IE)]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"background-color:transparent;\"><tr><td align=\"center\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"width:500px\"><tr class=\"layout-full-width\" style=\"background-color:transparent\"><![endif]--> \n"
	 * +
	 * "         <!--[if (mso)|(IE)]><td align=\"center\" width=\"500\" style=\"background-color:transparent;width:500px; border-top: 0px dotted transparent; border-left: 0px dotted transparent; border-bottom: 0px dotted transparent; border-right: 0px dotted transparent;\" valign=\"top\"><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td style=\"padding-right: 0px; padding-left: 0px; padding-top:5px; padding-bottom:5px;\"><![endif]--> \n"
	 * +
	 * "         <div class=\"col num12\" style=\"min-width: 320px; max-width: 500px; display: table-cell; vertical-align: top; width: 500px;\"> \n"
	 * + "          <div style=\"width:100% !important;\"> \n" +
	 * "           <!--[if (!mso)&(!IE)]><!--> \n" +
	 * "           <div style=\"border-top:0px dotted transparent; border-left:0px dotted transparent; border-bottom:0px dotted transparent; border-right:0px dotted transparent; padding-top:5px; padding-bottom:5px; padding-right: 0px; padding-left: 0px;\"> \n"
	 * + "            <!--<![endif]--> \n" +
	 * "            <div class=\"img-container center  autowidth \" align=\"center\"> \n"
	 * +
	 * "             <!--[if mso]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr style=\"line-height:0px\"><td style=\"\" align=\"center\"><![endif]-->\n"
	 * +
	 * "             <img class=\"center  autowidth \" align=\"center\" border=\"0\" src=\"https://app.optculture.com/subscriber/UserData/Srikanth__org__SS/Gallery/srikanth/logo.jpeg\" alt=\"Image\" title=\"Image\" style=\"text-decoration: none; -ms-interpolation-mode: bicubic; border: 0; height: auto; width: 100%; max-width: 297px; display: block;\" width=\"297\"> \n"
	 * + "             <!--[if mso]></td></tr></table><![endif]--> \n" +
	 * "            </div> \n" +
	 * "            <div class=\"img-container center  autowidth  fullwidth\" align=\"center\"> \n"
	 * +
	 * "             <!--[if mso]><table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr style=\"line-height:0px\"><td style=\"\" align=\"center\"><![endif]-->\n"
	 * +
	 * "             <img class=\"center  autowidth  fullwidth\" align=\"center\" border=\"0\" src=\"https://qcapp.optculture.com/subscriber/UserData/Srikanth__org__SS/Gallery/srikanth/image.jpeg\" alt=\"Image\" title=\"Image\" style=\"text-decoration: none; -ms-interpolation-mode: bicubic; border: 0; height: auto; width: 100%; max-width: 500px; display: block;\" width=\"500\"> \n"
	 * + "             <!--[if mso]></td></tr></table><![endif]--> \n" +
	 * "            </div> \n" + "            <!--[if (!mso)&(!IE)]><!--> \n" +
	 * "           </div> \n" + "           <!--<![endif]--> \n" +
	 * "          </div> \n" + "         </div> \n" +
	 * "         <!--[if (mso)|(IE)]></td></tr></table><![endif]--> \n" +
	 * "         <!--[if (mso)|(IE)]></td></tr></table></td></tr></table><![endif]--> \n"
	 * + "        </div> \n" + "       </div> \n" + "      </div> \n" +
	 * "      <!--[if (mso)|(IE)]></td></tr></table><![endif]--> </td> \n" +
	 * "    </tr> \n" + "   </tbody> \n" + "  </table> \n" +
	 * "  <!--[if (IE)]></div><![endif]-->\n" + " </body>\n" + "</html>"; String
	 * username ="Srikanth__org__SS"; htmlcontent=replaceImgURL(htmlcontent,
	 * username); System.out.println("htmlcontent in main....."+ htmlcontent); }
	 */
}
