package org.mq.optculture.restservice.digitalReceipt;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.DR.DigitalReceipt;
import org.mq.optculture.model.DR.DigitalReceiptResponse;
import org.mq.optculture.model.DR.heartland.HeartlandDRRequest;
import org.mq.optculture.model.DR.orion.OrionDRRequest;
import org.mq.optculture.model.DR.magento.MagentoBasedDRRequest;
import org.mq.optculture.model.DR.prism.PrismBasedDRRequest;
import org.mq.optculture.model.DR.shopify.ShopifyBasedDRRequest;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceDRRequest;
import org.mq.optculture.model.DR.wooCommerce.WooCommerceReturnDRRequest;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.gson.Gson;

public class ProcessDigitalReceiptsRestService extends AbstractController{

	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DigitalReceiptResponse digitalReceiptResponse = null;

		try {
			logger.debug("===entered into===");
			response.setContentType("application/json");
			String requestJson = OptCultureUtils.getParameterJsonValue(request);
			
			logger.info("JSON Request: = "+requestJson);
			Gson gson = new Gson();
			DigitalReceipt digitalReceipt = null;
			PrismBasedDRRequest prismBasedDRRequest = null;
			MagentoBasedDRRequest magentoBasedDRRequest = null;
			WooCommerceDRRequest wooCommerceDRRequest = null;
			WooCommerceReturnDRRequest wooCommerceReturnDRRequest = null;
			ShopifyBasedDRRequest shopifyBasedDRRequest = null;
			BaseResponseObject responseObject = null;
			HeartlandDRRequest heartlandDRRequest = null;
			OrionDRRequest orionDRRequest = null;
			if(requestJson.contains(OCConstants.DR_SOURCE_TYPE_OPTDR)){
				

				try{
					
					digitalReceipt = gson.fromJson(requestJson, DigitalReceipt.class);
				}catch(Exception e){
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				if(digitalReceipt == null){
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				
				if(digitalReceipt.getHead() == null || digitalReceipt.getBody() == null || digitalReceipt.getBody().getReceipt() == null ){
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				digitalReceipt.setSource(OCConstants.DR_SOURCE_TYPE_OPTDR);
				digitalReceipt.setAction(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL);
				digitalReceipt.setMode(OCConstants.DR_ONLINE_MODE);
				BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.PROCESS_DR_BUSINESS_SERVICE);
				
				responseObject = baseService.processRequest(digitalReceipt);
				digitalReceiptResponse = (DigitalReceiptResponse)responseObject;
				
				
			}else if(requestJson.contains(OCConstants.DR_SOURCE_TYPE_PRISM)){
				logger.info("Prism request");
				
				try{
					
					prismBasedDRRequest = gson.fromJson(requestJson, PrismBasedDRRequest.class);
				}catch(Exception e){
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response == "+responseJson);
					return null;
				}
				if(prismBasedDRRequest == null){
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				
				if(prismBasedDRRequest.getHead() == null || prismBasedDRRequest.getBody() == null  ){
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				prismBasedDRRequest.setAction(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL);
				prismBasedDRRequest.setSource(OCConstants.DR_SOURCE_TYPE_PRISM);
				prismBasedDRRequest.setMode(OCConstants.DR_ONLINE_MODE);
				BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.PROCESS_DR_BUSINESS_SERVICE);
				
				responseObject = baseService.processRequest(prismBasedDRRequest);
				digitalReceiptResponse = (DigitalReceiptResponse)responseObject;
				
			}else if(requestJson.contains(OCConstants.DR_SOURCE_TYPE_HEARTLAND)){
				logger.info("Heartland request");
				
				try{
					
					heartlandDRRequest = gson.fromJson(requestJson, HeartlandDRRequest.class);
				}catch(Exception e){
					logger.error("Heartland Exception",e);
					
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response == "+responseJson);
					return null;
				}
				if(heartlandDRRequest == null){
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				
				if(heartlandDRRequest.getHead() == null || heartlandDRRequest.getBody() == null  ){
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				heartlandDRRequest.setAction(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL);
				heartlandDRRequest.setSource(OCConstants.DR_SOURCE_TYPE_HEARTLAND);
				heartlandDRRequest.setMode(OCConstants.DR_ONLINE_MODE);
				BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.PROCESS_DR_BUSINESS_SERVICE);
				
				responseObject = baseService.processRequest(heartlandDRRequest);
				digitalReceiptResponse = (DigitalReceiptResponse)responseObject;
				
			
			}
			else if(requestJson.contains(OCConstants.DR_SOURCE_TYPE_Magento)){
					
					
					try{
						
						magentoBasedDRRequest = gson.fromJson(requestJson, MagentoBasedDRRequest.class);
					}catch(Exception e){
						logger.info("Exception ",e);
						String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
						PrintWriter printWriter = response.getWriter();
						printWriter.write(responseJson);
						printWriter.flush();
						printWriter.close();
						logger.info("Response = "+responseJson);
						return null;
					}
					if(magentoBasedDRRequest == null){
						String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
						PrintWriter printWriter = response.getWriter();
						printWriter.write(responseJson);
						printWriter.flush();
						printWriter.close();
						logger.info("Response = "+responseJson);
						return null;
					}
					
					if(magentoBasedDRRequest.getHead() == null || magentoBasedDRRequest.getBody() == null  ){
						String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
						PrintWriter printWriter = response.getWriter();
						printWriter.write(responseJson);
						printWriter.flush();
						printWriter.close();
						logger.info("Response = "+responseJson);
						return null;
					}
					magentoBasedDRRequest.setAction(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL);
					magentoBasedDRRequest.setSource(OCConstants.DR_SOURCE_TYPE_Magento);
					magentoBasedDRRequest.setMode(OCConstants.DR_ONLINE_MODE);
					BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.PROCESS_DR_BUSINESS_SERVICE);
					
					responseObject = baseService.processRequest(magentoBasedDRRequest);
					digitalReceiptResponse = (DigitalReceiptResponse)responseObject;
					
				}else if(requestJson.contains(OCConstants.DR_SOURCE_TYPE_WooCommerce)){
					
						if(requestJson.contains(OCConstants.DR_RECEIPT_TYPE_SALE)||(requestJson.contains(OCConstants.DR_RECEIPT_TYPE_RETURN)&&
								requestJson.contains("cancelled"))) {
							try{
							wooCommerceDRRequest = gson.fromJson(requestJson, WooCommerceDRRequest.class);
							}catch(Exception e){
								logger.info("Exception ",e);
								String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
								PrintWriter printWriter = response.getWriter();
								printWriter.write(responseJson);
								printWriter.flush();
								printWriter.close();
								logger.info("Response = "+responseJson);
								return null;
							}
							if(wooCommerceDRRequest == null){
								String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
								PrintWriter printWriter = response.getWriter();
								printWriter.write(responseJson);
								printWriter.flush();
								printWriter.close();
								logger.info("Response = "+responseJson);
								return null;
							}
							
							if(wooCommerceDRRequest.getHead() == null || wooCommerceDRRequest.getBody() == null ){
								String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
								PrintWriter printWriter = response.getWriter();
								printWriter.write(responseJson);
								printWriter.flush();
								printWriter.close();
								logger.info("Response = "+responseJson);
								return null;
							}
							BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.PROCESS_DR_BUSINESS_SERVICE);
							wooCommerceDRRequest.setAction(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL);
							wooCommerceDRRequest.setSource(OCConstants.DR_SOURCE_TYPE_WooCommerce);
							wooCommerceDRRequest.setMode(OCConstants.DR_ONLINE_MODE);
							wooCommerceDRRequest.setMsgContent("Sale");
							responseObject = baseService.processRequest(wooCommerceDRRequest);
							digitalReceiptResponse = (DigitalReceiptResponse)responseObject;
						}else {
							//refund
							try {
							wooCommerceReturnDRRequest = gson.fromJson(requestJson, WooCommerceReturnDRRequest.class);
							}catch(Exception e){
								logger.info("Exception ",e);
								String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
								PrintWriter printWriter = response.getWriter();
								printWriter.write(responseJson);
								printWriter.flush();
								printWriter.close();
								logger.info("Response = "+responseJson);
								return null;
							}
							if(wooCommerceReturnDRRequest==null) {
								String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
								PrintWriter printWriter = response.getWriter();
								printWriter.write(responseJson);
								printWriter.flush();
								printWriter.close();
								logger.info("Response = "+responseJson);
								return null;
							}
								if(wooCommerceReturnDRRequest.getHead() == null || wooCommerceReturnDRRequest.getBody() == null){
									String responseJ = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
									PrintWriter pw = response.getWriter();
									pw.write(responseJ);
									pw.flush();
									pw.close();
									logger.info("Response = "+responseJ);
									return null;
								}
								BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.PROCESS_DR_BUSINESS_SERVICE);
								wooCommerceReturnDRRequest.setAction(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL);
								wooCommerceReturnDRRequest.setSource(OCConstants.DR_SOURCE_TYPE_WooCommerce);
								wooCommerceReturnDRRequest.setMode(OCConstants.DR_ONLINE_MODE);
								wooCommerceReturnDRRequest.setMsgContent("Refund");
								responseObject = baseService.processRequest(wooCommerceReturnDRRequest);
								digitalReceiptResponse = (DigitalReceiptResponse)responseObject;
							}
					
					
					
				}else if(requestJson.contains(OCConstants.DR_SOURCE_TYPE_Shopify)){
				logger.info("shopify...");
				try{
					shopifyBasedDRRequest = gson.fromJson(requestJson, ShopifyBasedDRRequest.class);
				}catch(Exception e){
					logger.info("Exception ",e);
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				if(shopifyBasedDRRequest == null){
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				
				if(shopifyBasedDRRequest.getHead() == null || shopifyBasedDRRequest.getBody() == null  ){
					String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
					PrintWriter printWriter = response.getWriter();
					printWriter.write(responseJson);
					printWriter.flush();
					printWriter.close();
					logger.info("Response = "+responseJson);
					return null;
				}
				shopifyBasedDRRequest.setAction(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL);
				shopifyBasedDRRequest.setSource(OCConstants.DR_SOURCE_TYPE_Shopify);
				shopifyBasedDRRequest.setMode(OCConstants.DR_ONLINE_MODE);
				BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.PROCESS_DR_BUSINESS_SERVICE);
				
				responseObject = baseService.processRequest(shopifyBasedDRRequest);
				digitalReceiptResponse = (DigitalReceiptResponse)responseObject;
			
		}
		//APP-4773 Yateem
		else if(requestJson.contains(OCConstants.DR_SOURCE_TYPE_ORION)){
			logger.info("Orion request");
			
			try{
				
				orionDRRequest = gson.fromJson(requestJson, OrionDRRequest.class);
			}catch(Exception e){
				logger.error("Exception while converting json to OrionDRRequest POJO ",e);
				
				String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response == "+responseJson);
				return null;
			}
			if(orionDRRequest == null){
				String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			
			if(orionDRRequest.getHead() == null || orionDRRequest.getBody() == null  ){
				String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 101001: Invalid request.\",\"ERRORCODE\":\"101001\"}}}";
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
				logger.info("Response = "+responseJson);
				return null;
			}
			orionDRRequest.setAction(OCConstants.DIGITAL_RECEIPT_SERVICE_ACTION_SENDEMAIL);
			orionDRRequest.setSource(OCConstants.DR_SOURCE_TYPE_ORION);
			orionDRRequest.setMode(OCConstants.DR_ONLINE_MODE);
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.PROCESS_DR_BUSINESS_SERVICE);
			
			responseObject = baseService.processRequest(orionDRRequest);
			digitalReceiptResponse = (DigitalReceiptResponse)responseObject;
			
		
		}else { //added for APP-3405(Unknown Request Source Key)
			try {
				String responseJson = "{\"RESPONSEINFO\":{\"STATUS\":{\"STATUS\":\"Failure\",\"MESSAGE\":\"Error 100024: Request Source not found.\",\"ERRORCODE\":\"100024\"}}}";
				logger.info(responseJson);
				PrintWriter pw = response.getWriter();
				pw.write(responseJson);
				pw.flush();
				pw.close();
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}
						
						
			if(responseObject != null) {
				try {
					String json = gson.toJson(digitalReceiptResponse);
					PrintWriter pw = response.getWriter();
					pw.write(json);
					pw.flush();
					pw.close();
				} catch (Exception e) {
					logger.error("Exception ::" , e);
				}
				
			}
			
		} catch(Exception e) {
			logger.error("Exception ::" , e);
		} 
		return null;
	}

	
	
}
