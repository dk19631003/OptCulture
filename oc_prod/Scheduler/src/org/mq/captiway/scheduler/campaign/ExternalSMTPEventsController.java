package org.mq.captiway.scheduler.campaign;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.mq.captiway.scheduler.ExternalSMTPEventsProcessor;
import org.mq.captiway.scheduler.dao.DRSentDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.PropertyUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ExternalSMTPEventsController  extends AbstractController {
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
		public ExternalSMTPEventsController() {
			
		}
	
		private ExternalSMTPEventsQueue externalSMTPEventsQueue ;
		
		
		public ExternalSMTPEventsQueue getExternalSMTPEventsQueue() {
			
			return externalSMTPEventsQueue;
		}

		public void setExternalSMTPEventsQueue(	ExternalSMTPEventsQueue externalSMTPEventsQueue) {
			
			this.externalSMTPEventsQueue = externalSMTPEventsQueue;
			
		}
		
		private ExternalSMTPEventsProcessor externalSMTPEventsProcessor;
		
		
		public ExternalSMTPEventsProcessor getExternalSMTPEventsProcessor() {
			return externalSMTPEventsProcessor;
		}

		public void setExternalSMTPEventsProcessor(
				ExternalSMTPEventsProcessor externalSMTPEventsProcessor) {
			this.externalSMTPEventsProcessor = externalSMTPEventsProcessor;
		}
		

		@Override
		protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		
		try {
			
			
			//logger.info("requesturl is ::"+request.getParameter("EmailType") );
			/*String emailType = request.getParameter("EmailType");
			if(emailType != null) {
				
				try {
					if( emailType.equals(Constants.EQ_TYPE_DIGITALRECIEPT)){
						
						String serverName = request.getParameter(Constants.URL_PARAM_SERVERNAME);
						String sentId = request.getParameter(Constants.URL_PARAM_SENTID);
						String userId = request.getParameter(Constants.URL_PARAM_USERID);
						String email = request.getParameter(Constants.URL_PARAM_EMAIL);
						String eventType = request.getParameter(Constants.URL_PARAM_EVENT);
						
						if(serverName != null && !serverName.equalsIgnoreCase(PropertyUtil.getPropertyValue("schedulerIp"))) {
							
							if(logger.isErrorEnabled()) logger.error("Exception : Differenct server, Redirecting to server :" + serverName);
							
							//messageHeader =  "{\"unique_args\": {\"userId\": \""+ user.getUserId() +"\" ,\"EmailType\" : \""+Constants.EQ_TYPE_DIGITALRECIEPT +"\",\"sentId\" : \""+drSent.getId()+"\" ,\"ServerName\": \""+ serverName +"\" }}";
							
							String redirecturl = serverName + "/Scheduler/sendGridEventHandler.mqrm";
							String postData = "?sentId="+ sentId + "&userId="+ userId+"&EmailType="+emailType
							+ "&event="+ eventType + "&ServerName="+ serverName +
							(email != null && !email.isEmpty() ? "&email="+email : "");
							//logger.debug(" ****>>> Redirecting to : "+ redirecturl+postData );
							//open a connection to the new server(need to optimize it)
							try{
								
								URL url = new URL(redirecturl+postData);
								
								HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
								//logger.debug("CON OPENED ? "+urlconnection.getURL());
								urlconnection.setRequestMethod("POST");
								urlconnection.setRequestProperty("Content-Type","text/html");
								urlconnection.setDoOutput(true);
								OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
								out.write(postData);
								out.flush();
								out.close();
								BufferedReader in = new BufferedReader(	new InputStreamReader(urlconnection.getInputStream()));
								in.close();
								urlconnection.disconnect();
					        
						    } catch (MalformedURLException me) {
						        logger.error("MalformedURLException: " , me);
						        return null;
						    } catch (Exception e) {
						    	logger.error("IOException: " , e);
						    	 return null;
						    }
						    return null;
							
						}
						
						
						
						updateDrsent(request.getParameter(Constants.URL_PARAM_EVENT),request.getParameter(Constants.URL_PARAM_SENTID));
						return null;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("got exception",e);
				}
			}*/
			
			//**********************APP INCREASING RESPONSETIME PROLEM SOLVING*********************************************
			/*String queryString = Constants.STRING_NILL;
			try {
				queryString = request.getParameter(Constants.URL_PARAM_CRID) != null ?
						(Constants.URL_PARAM_CRID)
						+Constants.URL_TOKEN_EQUALTO+
						request.getParameter(Constants.URL_PARAM_CRID)+
						Constants.URL_TOKEN_AMBERSENT  : "";
						
				queryString += request.getParameter(Constants.URL_PARAM_SENTID) != null ? 
						(Constants.URL_PARAM_SENTID)
						+Constants.URL_TOKEN_EQUALTO+
						request.getParameter(Constants.URL_PARAM_SENTID)+
						Constants.URL_TOKEN_AMBERSENT : "";
								
				queryString += request.getParameter(Constants.URL_PARAM_USERID) != null ?
						(Constants.URL_PARAM_USERID)
						+Constants.URL_TOKEN_EQUALTO+
						request.getParameter(Constants.URL_PARAM_USERID)+
						Constants.URL_TOKEN_AMBERSENT : "";
				
				queryString += request.getParameter(Constants.URL_PARAM_EMAIL) != null ?
						(Constants.URL_PARAM_EMAIL)
						+Constants.URL_TOKEN_EQUALTO+
						request.getParameter(Constants.URL_PARAM_EMAIL)+
						Constants.URL_TOKEN_AMBERSENT : "";
				
				queryString += request.getParameter(Constants.URL_PARAM_EVENT) != null ?
						(Constants.URL_PARAM_EVENT)
						+Constants.URL_TOKEN_EQUALTO+
						request.getParameter(Constants.URL_PARAM_EVENT)+
						Constants.URL_TOKEN_AMBERSENT : "";
				
				queryString += request.getParameter(Constants.URL_PARAM_SERVERNAME) != null ?
						(Constants.URL_PARAM_SERVERNAME)
						+Constants.URL_TOKEN_EQUALTO+
						request.getParameter(Constants.URL_PARAM_SERVERNAME)+
						Constants.URL_TOKEN_AMBERSENT : "";
				
				queryString += request.getParameter(Constants.URL_PARAM_STATUS) != null ?
						(Constants.URL_PARAM_STATUS)
						+Constants.URL_TOKEN_EQUALTO+
						request.getParameter(Constants.URL_PARAM_STATUS)+
						Constants.URL_TOKEN_AMBERSENT : "";
				
				queryString += request.getParameter(Constants.URL_PARAM_TYPE) != null ?
						(Constants.URL_PARAM_TYPE)
						+Constants.URL_TOKEN_EQUALTO+
						request.getParameter(Constants.URL_PARAM_TYPE)+
						Constants.URL_TOKEN_AMBERSENT : "";
				
				queryString += request.getParameter(Constants.URL_PARAM_REASON) != null ?
						(Constants.URL_PARAM_REASON)
						+Constants.URL_TOKEN_EQUALTO+
						request.getParameter(Constants.URL_PARAM_REASON)+
						Constants.URL_TOKEN_AMBERSENT : "";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("exception** at preparing query str",e);
			}*/
			
			
			JSONArray jsonRootObject;
			
			  	InputStream is = request.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				char[] chr = new char[1024];
				int bytesRead = 0;
				StringBuilder sb = new StringBuilder();
				
				while ((bytesRead = br.read(chr)) > 0) {
			         sb.append(chr, 0, bytesRead);
			    }
				//logger.info("Rest body value is "+ sb.toString());
				try {
					
					jsonRootObject = (JSONArray)JSONValue.parse(sb.toString());
					//logger.info("JSON Root obj is ::"+jsonRootObject);

				} catch(Exception e) {
					
					logger.error("Error : Invalid json Object .. Returning. ****");
					return null;
				}		
		
			//logger.info("requesturl is ::"+requestUrlStr);
				//StringBuffer jsonRootObjectStrBfr = new StringBuffer(jsonRootObject.toJSONString());
				String jsonRootObjectStr = jsonRootObject.toJSONString();
			
			externalSMTPEventsQueue.addObjToQueue(jsonRootObjectStr);
			if(externalSMTPEventsQueue.getQueueSize() >= 2000) {
				
				if(!externalSMTPEventsProcessor.isRunning()) {
					logger.info("processor is not running , try to ping it....");
					externalSMTPEventsProcessor.run();
					return null;
				}
				
				
			}
		
			
			return null;
		} catch (Exception e) {
			logger.error("exception**",e);
			return null;
		}
	}
		
	
}
