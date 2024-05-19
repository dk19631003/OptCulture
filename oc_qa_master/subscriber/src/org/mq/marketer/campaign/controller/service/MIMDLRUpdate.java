package org.mq.marketer.campaign.controller.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.DRSMSSent;
import org.mq.marketer.campaign.beans.OCSMSGateway;
import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.mq.marketer.campaign.controller.service.MIMSMSGateway.MIMResponse;
import org.mq.marketer.campaign.controller.service.MIMSMSGateway.PrepareMIMDLRRequest;
import org.mq.marketer.campaign.dao.DRSMSSentDao;
import org.mq.marketer.campaign.dao.DRSMSSentDaoForDML;
import org.mq.marketer.campaign.dao.OCSMSGatewayDao;
import org.mq.marketer.campaign.dao.UserSMSGatewayDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.data.dao.ReferralProgramDao;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MIMDLRUpdate extends TimerTask implements ApplicationContextAware{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	private ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		// TODO Auto-generated method stub
		this.context = context;
	}
	
	OCSMSGatewayDao ocsmsgatewayDao;
	UserSMSGatewayDao usersmsgatewayDao;
	DRSMSSentDao drsmssentDao;
	DRSMSSentDaoForDML drSmsSentDaoForDML;
	
	public void run() {
		
		logger.info("entering into MIMDLRUpdate thread");
		try {
		ocsmsgatewayDao = (OCSMSGatewayDao) context.getBean(OCConstants.OCSMSGATEWAY_DAO);
		usersmsgatewayDao= (UserSMSGatewayDao) context.getBean(OCConstants.USERSMSGATEWAY_DAO);
		drsmssentDao= (DRSMSSentDao)context.getBean(OCConstants.DR_SMS_SENT_DAO);
		drSmsSentDaoForDML = (DRSMSSentDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.DR_SMS_SENT_DAO_ForDML);

		OCSMSGateway ocsmsgatewayobj   = ocsmsgatewayDao.findTransactionalGatewayUserIds("UAE","MyInboxMedia");
		if (ocsmsgatewayobj == null ) {
				logger.info(">>> No MIM gateway is there");
				return;
		}else {
			
			List<UserSMSGateway> usersmsgatewaylist =usersmsgatewayDao.findAllByGatewayId(ocsmsgatewayobj.getId());
			
			for (UserSMSGateway usersmsgatewayObj : usersmsgatewaylist) {
				
				List<DRSMSSent> drsmssentlist = drsmssentDao.findbyuserId(usersmsgatewayObj.getUserId());
				if(drsmssentlist.size()>0) {
					for (DRSMSSent drsmsobj:drsmssentlist) {
				
						String Dlrstatus =	UpdateMIMDLR(usersmsgatewayObj.getUserId(),ocsmsgatewayobj.getUserId(), ocsmsgatewayobj.getPwd(),drsmsobj.getMessageId());
						if(Dlrstatus.equalsIgnoreCase("Delivered")) {
							logger.info("entering Delivered condition");
							drsmsobj.setStatus("Delivered"); 
						}else {
							drsmsobj.setStatus(Dlrstatus);
						}
						drSmsSentDaoForDML.saveOrUpdate(drsmsobj);

					}
				}else {
					
					logger.info(">>> No records found to update MIMDLR");

				}
			}
		}

		
		}catch(Exception e) {
			logger.error("Exception occured ::" , e);
			
		}
		
		
	}

	private String UpdateMIMDLR(Long userId, String accountuserId, String pwd,String msgId) {
		
		try{
			
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			PrepareMIMDLRRequest pj=new PrepareMIMDLRRequest();				
			
			pj.setUserId(accountuserId);
			pj.setPwd(pwd);
			pj.setMsgId(msgId);
			
			String request = gson.toJson(pj);
			logger.info("DLRrequest for MIMSMSGateway is--->"+request);

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost("https://myinboxmedia.in/api/sms/GetDelivery");
			StringEntity input = new StringEntity(request);
			input.setContentType("application/json");
			postRequest.setEntity(input);
			
			HttpResponse response = httpClient.execute(postRequest);
				
			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));
			
			String resp="";
			String output="";
			logger.info("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
					if(output!=null)resp=resp+output;
					logger.info("output fromDLR MIM---"+resp);
				}
			
			httpClient.getConnectionManager().shutdown();
			String DLRStatus = getStatus(resp);
			return DLRStatus;
			
		} catch (MalformedURLException e) {
			
			logger.error("Exception",e);
		} catch (IOException e) {
			
			logger.error("Exception",e);
			
		}
		return null;
		
	}
	
	
	
	public class PrepareMIMDLRRequest{
		
		private String userId;
		private String pwd;
		private String msgId;
		
		public String getUserId() {
			return userId;
		}


		public void setUserId(String userId) {
			this.userId = userId;
		}


		public String getPwd() {
			return pwd;
		}


		public void setPwd(String pwd) {
			this.pwd = pwd;
		}


		public String getMsgId() {
			return msgId;
		}


		public void setMsgId(String msgId) {
			this.msgId = msgId;
		}
		
		public  PrepareMIMDLRRequest() {
		
		}
		
	}
	
	
	
	
	public String getStatus(String response) {
		logger.info(" sms response mim--"+response);
		try {
			Gson gson1 = new Gson();
			MIMResponse[] mimResponse = gson1.fromJson(response,MIMResponse[].class);
			String Response="";
		       for(MIMResponse mimResponses : mimResponse) {
		    	   Response = mimResponses.getResponse();
		       }
		       String[] parts = Response.split(",");
		       String status = parts[0].split(":")[1].trim();
			 if(status!=null) {
		     	logger.info("DLR status== "+status);
		     	return status;
		    }else {
		    	return null; 
		    }
		} catch(Exception e){
			logger.error("Exception",e);
			return null;
		}
		
	}//getStatus 
	

}
