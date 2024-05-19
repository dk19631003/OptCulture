package org.mq.optculture.restservice.beefileapiservice;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.utils.OCConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


import com.google.gson.Gson;


public class SavedRowsRestService extends AbstractController{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String json = null;

		try {
			String saveRowType = request.getParameter("name");
			//String userOrg = request.getParameter("userOrg");
			logger.info("SavedRows API ");
			 json = PropertyUtil.getPropertyValueFromDB(saveRowType);
			
			logger.info("SavedRows API request getHeaderNames:"+request.getHeaderNames());
			String contextPath= request.getContextPath();
			logger.info("request.getContextPath()"+contextPath);
			
			String userName = null;
			
				logger.info("Bee File API request getHeaderNames:"+request.getHeaderNames());
				String method = request.getMethod();
				HashMap<String, String> map = new HashMap<String, String>();
				Enumeration<String> headerNames = request.getHeaderNames();
				while(headerNames.hasMoreElements()) {
					String key = (String) headerNames.nextElement();
					String value = request.getHeader(key);
					map.put(key, value);
					if(key.equalsIgnoreCase(OCConstants.BEE_UID_KEY)) userName = value;
				}
			
			
			
			//String json= "[{\"metadata\": { \"name\": \"First item\", } \"columns\": [{	\"weight\": \"12\",	\"modules\": [{	\"type\": \"title\", \"text\": \"How am I supposed to fight?\"},{ \"type\": \"paragraph\",		\"text\": \"Look, I can take you as far as Anchorhead. You can get a transport there to Mos Eisley or wherever you're going.\"	}] }] }]";			
			
			//json="[{\"content\":{\"style\":{\"background-image\":\"none\",\"background-repeat\":\"no-repeat\",\"color\":\"#000000\",\"width\":\"500px\",\"background-position\":\"top left\",\"background-color\":\"transparent\"},\"computedStyle\":{\"rowColStackOnMobile\":true}},\"container\":{\"style\":{\"background-image\":\"none\",\"background-position\":\"top left\",\"background-repeat\":\"no-repeat\",\"background-color\":\"transparent\"}},\"locked\":false,\"columns\":[{\"style\":{\"padding-bottom\":\"5px\",\"border-right\":\"0px dotted transparent\",\"border-bottom\":\"0px dotted transparent\",\"border-top\":\"0px dotted transparent\",\"padding-right\":\"0px\",\"border-left\":\"0px dotted transparent\",\"padding-top\":\"5px\",\"background-color\":\"transparent\",\"padding-left\":\"0px\"},\"modules\":[{\"type\":\"mailup-bee-newsletter-modules-text\",\"descriptor\":{\"text\":{\"html\":\"<p style=\\\\\\\"font-size: 12px; line-height: 14px;\\\\\\\">saveeeeeeeeeeeeeeeeeeeeeeeeee</p>\",\"style\":{\"color\":\"#555555\",\"line-height\":\"120%\",\"font-family\":\"inherit\"},\"computedStyle\":{\"linkColor\":\"#0068A5\"}},\"style\":{\"padding-top\":\"10px\",\"padding-right\":\"10px\",\"padding-bottom\":\"10px\",\"padding-left\":\"10px\"},\"computedStyle\":{\"hideContentOnMobile\":false}}}],\"grid-columns\":12}],\"type\":\"one-column-empty\",\"metadata\":{\"name\":\"metadata\",\"value\":\"mmmmmmmmmmmmm\"}}]";
		
			
			/*String paymentHTML = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\"><tr>	"
					+ "<td valign=\"top\">	<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"word-wrap:break-word; "
					+ "word-break:break-all; table-layout: fixed;\" width=\"100%\"><tbody><tr><td align=\"left\" valign=\"top\" "
					+ "width=\"65%\"><u>Tender / التحصيل</u></td><td align=\"right\" valign=\"top\" width=\"35%\"> </td></tr></tbody></table>"
					+ "<div id=\"##BEGIN PAYMENTS##\"></div><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" "
					+ "style=\"word-wrap:break-word; word-break:break-all; table-layout: fixed;font-family:arial; font-size:12px; "
					+ "color:#000000;\" width=\"100%\"><tbody><tr><td align=\"left\" style=\"color:#333333; padding-top:5px;\" "
					+ "valign=\"top\" width=\"65%\">#Payment.Type#<br />[#Payment.CurrencyName#]:  #Payment.Taken_DEC3# </td><td "
					+ "align=\"right\" style=\"color:#333333;\" valign=\"top\" width=\"35%\">#Payment.TypeInLangArb#</td></tr><tr"
					+ " style=\"[TENDER_ROW1_DISPLAYSTYLE]\"><td align=\"left\" style=\"color:#333333;\" valign=\"top\""
					+ " width=\"65%\">#Payment.Number#</td><td align=\"right\" style=\"color:#333333;\" valign=\"top\" "
					+ "width=\"35%\">#Payment.CardTypeInLangArb#</td></tr><tr style=\"[TENDER_ROW2_DISPLAYSTYLE]\"><td align=\"left\""
					+ " colspan=\"2\" valign=\"top\">#Payment.Auth#</td></tr><tr style=\"[TENDER_ROW3_DISPLAYSTYLE]\"><td align=\"left\" "
					+ "colspan=\"2\" valign=\"top\">#Payment.BaseTaken#</td></tr></tbody></table><div id=\"##END PAYMENTS##\"></div>"
					+ "</td></tr><tr style=\"[CHANGE_VISIBLE]\"><td align=\"center\" style=\"padding-top:5px;\"><table border=\"0\" "
					+ "cellpadding=\"0\" cellspacing=\"0\" style=\"word-wrap:break-word; word-break:break-all; table-layout: fixed;\" "
					+ "width=\"100%\"><tbody><tr style=\"[CHANGE_VISIBLE]\"><td align=\"left\" valign=\"top\"><u>Change / الباقي</u></td></tr>"
					+ "</tbody></table><div id=\"##BEGIN CHANGE PAYMENTS##\"></div><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\""
					+ " style=\"word-wrap:break-word; word-break:break-all; table-layout: fixed; font-family:arial; font-size:12px; "
					+ "color:#000000;\" width=\"100%\"><tr style=\"[CHANGE_VISIBLE]\"><td align=\"left\" style=\"color:#333333;\" valign=\"top\">"
					+ "#Change.Type#: #Change.Given_DEC3#</td><td align=\"right\" style=\"color:#333333;\" valign=\"top\">#Change.TypeInLangArb#"
					+ "</td></tr></table><div id=\"##END CHANGE PAYMENTS##\"></div></td></tr><tr><td align=\"center\""
					+ " style=\"padding-top:5px;\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"word-wrap:break-word; "
					+ "word-break:break-all; table-layout: fixed; font-family:arial; font-size:12px; color:#000000;\" width=\"100%\"><tbody>"
					+ "<tr><td align=\"left\" valign=\"top\"> </td></tr><tr><td style=\"color:#333333;[COUPON_VISIBLE]\">"
					+ "Coupon#:   #Receipt.Coupon#</td></tr><tr><td style=\"padding-bottom:5px;\">#Receipt.InvcComment1#<br />"
					+ "#Receipt.InvcComment2#</td></tr></tbody></table></td></tr></table>";
			
			
			json="[{\"content\":{\"style\":{\"background-image\":\"none\",\"background-repeat\":\"no-repeat\",\"color\":\"#000000\",\"width\":\"500px\",\"background-position\":\"top left\",\"background-color\":\"transparent\"},\"computedStyle\":{\"rowColStackOnMobile\":true}},\"container\":{\"style\":{\"background-image\":\"none\",\"background-position\":\"top left\",\"background-repeat\":\"no-repeat\",\"background-color\":\"transparent\"}},\"locked\":false,\"columns\":[{\"style\":{\"padding-bottom\":\"5px\",\"border-right\":\"0px dotted transparent\",\"border-bottom\":\"0px dotted transparent\",\"border-top\":\"0px dotted transparent\",\"padding-right\":\"0px\",\"border-left\":\"0px dotted transparent\",\"padding-top\":\"5px\",\"background-color\":\"transparent\",\"padding-left\":\"0px\"},\"modules\":[{\"type\":\"mailup-bee-newsletter-modules-text\",\"descriptor\":{\"text\":{\"html\":\""+paymentHTML+"\",\"style\":{\"color\":\"#555555\",\"line-height\":\"120%\",\"font-family\":\"inherit\"},\"computedStyle\":{\"linkColor\":\"#0068A5\"}},\"style\":{\"padding-top\":\"10px\",\"padding-right\":\"10px\",\"padding-bottom\":\"10px\",\"padding-left\":\"10px\"},\"computedStyle\":{\"hideContentOnMobile\":false}}}],\"grid-columns\":12}],\"type\":\"one-column-empty\",\"metadata\":{\"name\":\"metadata\",\"value\":\"mmmmmmmmmmmmm\"}}]";
			*/
			
			//json = "[{\"content\":{\"style\":{\"background-image\":\"none\",\"background-repeat\":\"no-repeat\",\"color\":\"#000000\",\"width\":\"500px\",\"background-position\":\"top left\",\"background-color\":\"transparent\"},\"computedStyle\":{\"rowColStackOnMobile\":true}},\"container\":{\"style\":{\"background-image\":\"none\",\"background-position\":\"top left\",\"background-repeat\":\"no-repeat\",\"background-color\":\"transparent\"}},\"locked\":false,\"columns\":[{\"style\":{\"padding-bottom\":\"5px\",\"border-right\":\"0px dotted transparent\",\"border-bottom\":\"0px dotted transparent\",\"border-top\":\"0px dotted transparent\",\"padding-right\":\"0px\",\"border-left\":\"0px dotted transparent\",\"padding-top\":\"5px\",\"background-color\":\"transparent\",\"padding-left\":\"0px\"},\"modules\":[{\"type\":\"mailup-bee-newsletter-modules-html\",\"descriptor\":{\"html\":{\"html\":\"<table cellpadding=\\\"0\\" cellspacing=\\\"0\\\" width=\\\"100%\\\">\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:11px; font-weight:bold; color:#000000;\\\" width=\\\"8%\\\">LN#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"center\\\" style=\\\"font-family:arial; font-size:10px; font-weight:bold; color:#000000;\\\" width=\\\"34%\\\">ITEM /<br />\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tرقم القطعة:</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:11px; font-weight:bold; color:#000000;\\\" width=\\\"20%\\\">PRICE /<br />\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tالسعر:</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:11px; font-weight:bold; color:#000000;\\\" width=\\\"11%\\\">QTY /<br />\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tالكمية:</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"right\\\" style=\\\"font-family:arial; font-size:10px; font-weight:bold; color:#000000;\\\" width=\\\"27%\\\">NET /<br />\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tالمجموع النهاءي:</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n<div id=\\\"##BEGIN ITEMS##\\\"></div>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" border=\\\"0\\\" width=\\\"100%\\\">\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td bgcolor=\\\"#f4f4f4\\\">\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table bgcolor=\\\"#f4f4f4\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" style=\\\"word-wrap:break-word; word-break:break-all; table-layout: fixed;\\\" width=\\\"100%\\\">\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:11px; color:#000000; table-layout: fixed;\\\" width=\\\"8%\\\">#Item.LineNumber#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:11px; color:#000000; table-layout: fixed;\\\" width=\\\"34%\\\">#Item.ALU#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:11px; color:#000000; table-layout: fixed;\\\" width=\\\"20%\\\">#Item.UnitPrice#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"center\\\" style=\\\"font-family:arial; font-size:11px; color:#000000; table-layout: fixed;\\\" width=\\\"11%\\\">#Item.Quantity#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"right\\\" style=\\\"font-family:arial; font-size:11px; color:#000000; table-layout: fixed;\\\" width=\\\"27%\\\">#Item.NetPrice#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table bgcolor=\\\"#f4f4f4\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" width=\\\"100%\\\">\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:11px; color:#000000;\\\">#Item.Description2#   #Item.Description1#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table bgcolor=\\\"#f4f4f4\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" style=\\\"word-wrap:break-word; word-break:break-all; table-layout: fixed;\\\" width=\\\"100%\\\">\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:10px; color:#000000;\\\" valign=\\\"top\\\" width=\\\"31%\\\">VAT%: #Item.TaxPrc#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"center\\\" style=\\\"font-family:arial; font-size:10px; color:#000000;\\\" valign=\\\"top\\\" width=\\\"36%\\\">Disc%: #Item.DiscountPercent#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"right\\\" style=\\\"font-family:arial; font-size:10px; color:#000000;\\\" valign=\\\"top\\\" width=\\\"33%\\\">#Item.DiscountReason#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table bgcolor=\\\"#f4f4f4\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" width=\\\"100%\\\">\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:12px; color:#000000;\\\">#Item.Desc3#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table bgcolor=\\\"#f4f4f4\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" width=\\\"100%\\\">\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:12px; color:#000000;\\\" width=\\\"50%\\\">#Item.Note1#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"right\\\" style=\\\"font-family:arial; font-size:12px; color:#000000;\\\" width=\\\"50%\\\">#Item.Note3#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div id=\\\"##BEGIN REF##\\\"></div>\n\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<table bgcolor=\\\"#f4f4f4\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" style=\\\"word-wrap:break-word; word-break:break-all; table-layout: fixed;\\\" width=\\\"100%\\\">\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<tr style=\\\"[REFSECTIONSTYLE]\\\">\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:12px; color:#000000;\\\" width=\\\"33%\\\">Ref Rcpt#:#Item.RefReceipt#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:12px; color:#000000;\\\" width=\\\"33%\\\">Store:#Item.RefStoreCode#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<td align=\\\"left\\\" style=\\\"font-family:arial; font-size:12px; color:#000000;\\\" width=\\\"33%\\\">Sub:#Item.RefSubsidiaryNumber#</td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tbody>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div id=\\\"##END REF##\\\"></div></td>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</tr>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t</table>\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t<div id=\\\"##END ITEMS##\\\"></div>\"},\"style\":{\"padding-top\":\"0px\",\"padding-right\":\"0px\",\"padding-bottom\":\"0px\",\"padding-left\":\"0px\"},\"computedStyle\":{\"hideContentOnMobile\":false}}},{\"type\":\"mailup-bee-newsletter-modules-text\",\"descriptor\":{\"text\":{\"html\":\"<p data-mce-style=\\\"font-size: 12px; line-height: 14px;\\\" style=\\\"font-size: 12px; line-height: 14px;\\\"><br data-mce-bogus=\\\"1\\\"></p>\",\"style\":{\"color\":\"#555555\",\"line-height\":\"120%\",\"font-family\":\"inherit\"},\"computedStyle\":{\"linkColor\":\"#0068A5\"}},\"style\":{\"padding-top\":\"10px\",\"padding-right\":\"10px\",\"padding-bottom\":\"10px\",\"padding-left\":\"10px\"},\"computedStyle\":{\"hideContentOnMobile\":false}}}],\"grid-columns\":12}],\"type\":\"one-column-empty\",\"metadata\":{\"name\":\"header\",\"tags\":\"product, two columns, blue\"}}]";	
			
				//json =
		//logger.info("saveRowType :"+saveRowType +" saverow_json :"+json);		
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		} finally {
			try {
				Gson gson = new Gson();
				//String json= "[{\"metadata\" : { \"name\": \"First item\", } \"columns\": [{	\"weight\": \"12\",	\"modules\": [{	\"type\": \"title\", \"text\": \"How am I supposed to fight?\"},{ \"type\": \"paragraph\",	\"text\": \"Look, I can take you as far as Anchorhead. You can get a transport there to Mos Eisley or wherever you're going.\"	}] }] }\"]";
				response.setContentType("application/json");
				PrintWriter pw = response.getWriter();
				pw.write(json);
				pw.flush();
				pw.close();
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}
		
		return null;
	}
}



