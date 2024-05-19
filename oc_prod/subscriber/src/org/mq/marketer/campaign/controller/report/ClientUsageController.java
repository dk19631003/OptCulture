package org.mq.marketer.campaign.controller.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.EmailClient;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.CampaignSentDao;
import org.mq.marketer.campaign.dao.EmailClientDao;
import org.mq.marketer.campaign.dao.OpensDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PieChartEngineFormat1;
import org.mq.marketer.campaign.general.PieChartEngineFormat2;
import org.mq.marketer.campaign.general.PieChartEngineFormat3;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Flashchart;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.PieModel;
import org.zkoss.zul.SimplePieModel;

@SuppressWarnings("serial")
public class ClientUsageController extends GenericForwardComposer {
	
	private OpensDao opensDao;
	//private Desktop desktop;
	private CampaignReport campaignReport;
	private CampaignSentDao campaignSentDao;
	private EmailClientDao emailClientDao;
	
	private Chart mychart1,mychart2,mychart3;
	
	private Listbox emailClientLbId1,emailClientLbId2,emailClientLbId3;
	
	private Combobox exportCbId;
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Long totalOpens = null;
	
	
	public ClientUsageController() {
		opensDao =(OpensDao)SpringUtil.getBean("opensDao");
		this.campaignSentDao = (CampaignSentDao)SpringUtil.getBean("campaignSentDao");
		campaignReport = (CampaignReport)Sessions.getCurrent().getAttribute("campaignReport");
		emailClientDao = (EmailClientDao)SpringUtil.getBean("emailClientDao");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		logger.debug("==== Email Client Usage Report toolbar button clicked ");
		super.doAfterCompose(comp);
	
		getEmailClientReport();

		// exportCbId.setSelectedIndex(0);
	}
	
	

	/**
	 * 
	 */
	public void getEmailClientReport() {
		try {
			logger.debug("----just entered---");

			mychart1.setEngine(new PieChartEngineFormat1());
			mychart2.setEngine(new PieChartEngineFormat2());
			mychart3.setEngine(new PieChartEngineFormat3());
			 
			if(campaignReport == null)
				campaignReport = (CampaignReport)Sessions.getCurrent().getAttribute("campaignReport");
			
			long crId = campaignReport.getCrId();
			PieModel deviceModel = new SimplePieModel();
			PieModel emailClientModel = new SimplePieModel();
			PieModel browserModel = new SimplePieModel();

			EmailClient uaType=null;
			EmailClient uaOSF=null;
			EmailClient uaUAF=null;
		
			// ********** Device Information *******************
			//app-3644
			List<Long> ecids_desk = emailClientDao.getIdsByUserClient("'Email_Client','Browser','Email Client','Other'", Constants.UA_TYPE);
			String ecidsstr="";
			if(ecids_desk.size()==0) ecidsstr = "''";
			else {
				for(Long ecid:ecids_desk) {
					ecidsstr = ecidsstr + (ecidsstr.length()>0 ? ","+ecid.toString() : ecid.toString());
				}
			}
			List<Map<String, Object>>  deskList = opensDao.getDesktopReportByCrId(crId,ecidsstr);
			
			List<Long> ecids_ph = emailClientDao.getIdsByUserClient("'Mobile Browser', 'MOBILE_BROWSER'", Constants.UA_TYPE);
			String ecidsstr_ph="";
			if(ecids_ph.size()==0) ecidsstr_ph = "''";
			else {
				for(Long ecid:ecids_ph) {
					ecidsstr_ph = ecidsstr_ph + (ecidsstr_ph.length()>0 ? ","+ecid.toString() : ecid.toString());
				}
			}

			List<Map<String, Object>> phoneList = opensDao.getPhoneReportByCrId(crId,ecidsstr_ph);
			
			if(deskList.size() == 0 && phoneList.size() == 0) {
				logger.debug("no data found for Device List: returning...");
			}
			
			else {
				int deskTotal = 0,phoneTotal = 0,devTotal = 0;
				
				for(Map<String, Object> eachMap : deskList) {
					deskTotal = deskTotal + Integer.parseInt(eachMap.get("osfcount") .toString()); 
				}
				
				for(Map<String, Object> eachMap : phoneList) {
					phoneTotal = phoneTotal + Integer.parseInt(eachMap.get("osfcount").toString()); 
				}
				
				devTotal = deskTotal + phoneTotal;
			 
				if( deskTotal != 0) {
					String percent = Utility.getPercentage(deskTotal, devTotal, 2)+" %" ;
					Listgroup lg = new Listgroup();
					lg.setLabel("Desktop   (Popularity: "+percent+", Subscribers: "+deskTotal+")");
					lg.setParent(emailClientLbId1);
					deviceModel.setValue("Desktop", deskTotal);
				
					for(Map<String, Object> eachMap : deskList) {
						Long osfId = (Long)eachMap.get("osfamily");
						uaOSF = emailClientDao.findById(osfId);
					    int value = Integer.parseInt(eachMap.get("osfcount").toString());
					   
						Listitem li = new Listitem();
						li.appendChild(new Listcell(uaOSF.getUaDispValue()));
						li.appendChild(new Listcell(Utility.getPercentage(value, devTotal, 2)+" %" ));
						li.appendChild(new Listcell(""+value));
						li.setParent(emailClientLbId1);
						
						
					}//for
					
				}//if
				
			  if( phoneTotal != 0) {
				  String percent = Utility.getPercentage(phoneTotal, devTotal, 2)+" %" ;
				  Listgroup lg = new Listgroup();
				  lg.setLabel("Phone   (Popularity: "+percent+", Subscribers: "+phoneTotal+")");
				  lg.setParent(emailClientLbId1);
				  deviceModel.setValue("Phone", phoneTotal);
                  
				  for(Map<String, Object> eachMap : phoneList) {
                	Long osfId = (Long)eachMap.get("osfamily");
   					uaOSF = emailClientDao.findById(osfId);
				    int value=Integer.parseInt(eachMap.get("osfcount").toString());
				   
					Listitem li = new Listitem();
					li.appendChild(new Listcell(uaOSF.getUaDispValue()));
					li.appendChild(new Listcell(Utility.getPercentage(value, devTotal, 2)+" %" ));
					li.appendChild(new Listcell(""+value));
					li.setParent(emailClientLbId1);
				
					
				  } // for
				  
			  	}//if
			  
				mychart1.setModel(deviceModel);
			} // else
			
			//app-3644
			List<Long> ecids_email = emailClientDao.getIdsByUserClient("'Email Client','Email_Client','Browser'", Constants.UA_TYPE);
			ecidsstr="";
			if(ecids_email.size()==0) ecidsstr = "''";
			else {
				for(Long ecid:ecids_email) {
					ecidsstr = ecidsstr + (ecidsstr.length()>0 ? ","+ecid.toString() : ecid.toString());
				}
			}
			List<Map<String, Object>> ecList =  opensDao.getEmailClientReportByCrId(crId,ecidsstr);
			
			List<Map<String, Object>> mbList =  opensDao.getMobileBrowserReportByCrId(crId,ecidsstr_ph);
			
			List<Long> ecids_br = emailClientDao.getIdsByUserClient("'Browser'", Constants.UA_TYPE);
			ecidsstr="";
			if(ecids_br.size()==0) ecidsstr = "''";
			else {
				for(Long ecid:ecids_br) {
					ecidsstr = ecidsstr + (ecidsstr.length()>0 ? ","+ecid.toString() : ecid.toString());
				}
			}			
			List<Map<String, Object>> bwList =  opensDao.getBrowserReportByCrId(crId,ecidsstr);
			
			// ********** Email Client Information *******************
			
			
			if(ecList.size() == 0 && bwList.size() == 0 && mbList.size() == 0) {
				logger.debug("no data is found : returning.....");
			}
			 
			else {
				int total=0,ecTotal=0,bwTotal=0,mbTotal=0;
			
				for(Map<String, Object> eachMap : ecList) {					
				ecTotal = ecTotal + Integer.parseInt(eachMap.get("uafcount").toString());  
				} // for
				
				for(Map<String, Object> eachMap : bwList) {
					bwTotal = bwTotal + Integer.parseInt(eachMap.get("uafcount").toString()); 
				} // for
				
				for(Map<String, Object> eachMap : mbList) {
					mbTotal = mbTotal + Integer.parseInt(eachMap.get("uafcount").toString()); 
				} // for
				
				total = ecTotal + bwTotal + mbTotal;
					
				for(Map<String, Object> eachMap:ecList) {
					Long uafId = (Long)eachMap.get("uafamily");
   					uaUAF = emailClientDao.findById(uafId);
					int value=Integer.parseInt(eachMap.get("uafcount").toString());
					
					Listitem li = new Listitem();
					li.appendChild(new Listcell(uaUAF.getUaDispValue()));
					li.appendChild(new Listcell(Utility.getPercentage(value,total, 2)+" %" ));
					li.appendChild(new Listcell(""+value));
					li.setParent(emailClientLbId2);
					
					emailClientModel.setValue(uaUAF.getUaDispValue(), value);
				}//for
					
					if(bwTotal != 0) {
						Listitem li = new Listitem();
						li.appendChild(new Listcell("Webmail"));
						li.appendChild(new Listcell(Utility.getPercentage(bwTotal, total, 2)+" %" ));
						li.appendChild(new Listcell(""+bwTotal));
						li.setParent(emailClientLbId2);
						
						emailClientModel.setValue("Webmail", bwTotal);
					}//if
					if(mbTotal != 0) {
						Listitem li1 = new Listitem();
						li1.appendChild(new Listcell("Mobile"));
						li1.appendChild(new Listcell(Utility.getPercentage(mbTotal, total, 2)+" %" ));
						li1.appendChild(new Listcell(""+mbTotal));
						li1.setParent(emailClientLbId2);
						
						emailClientModel.setValue("Mobile", mbTotal);
					}//if
					
				mychart2.setModel(emailClientModel);
		}//else
			
			//*************Browser Info*********////
			
			if(bwList.size() == 0) {
				logger.debug("no data is found for BW List: returning.....");
			}
			
			else {
				int bwTotal = 0;
				for(Map<String, Object> eachMap : bwList) {
					bwTotal = bwTotal + Integer.parseInt(eachMap.get("uafcount").toString()); 
				}
				
				for(Map<String, Object> eachMap:bwList) {
					Long uafId = (Long)eachMap.get("uafamily");
   					uaUAF = emailClientDao.findById(uafId);
					int value=Integer.parseInt(eachMap.get("uafcount").toString());
					
					Listitem li = new Listitem();
					li.appendChild(new Listcell(uaUAF.getUaDispValue()));
					li.appendChild(new Listcell(Utility.getPercentage(value, bwTotal, 2)+" %" ));
					li.appendChild(new Listcell(""+value));
					li.setParent(emailClientLbId3);
						
					browserModel.setValue(uaUAF.getUaDispValue(), value);
				} // for
				
				mychart3.setModel(browserModel);
			} // else
		}//try
		
		catch (Exception e) {
			logger.error("Exception : ",e);
		}
	}
	
   
	public void export() {
		
		logger.debug("-- just entered --"+totalOpens);
		
		if(totalOpens==null)
			return;
		
		String fileType = exportCbId.getSelectedItem().getLabel();
		StringBuffer sb = null;
		String userName = GetUser.getUserName();
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
		File downloadDir = new File(exportDir);
		
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception ::" , e);
				logger.warn(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		
		if(fileType.contains("csv")){
			try {
				String name = campaignReport.getCampaignName();
				if(name.contains("/")) {
					
					name = name.replace("/", "_") ;
					
				}
				
				String filePath = exportDir +  "Report_" + name + "_" + campaignReport.getSentDate().getTime() + "_EmailClient.csv";
				logger.debug("Download File path : " + filePath);
				File file = new File(filePath);
				BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
				int count = emailClientLbId2.getItemCount();
				logger.debug("Total count : " + count);
				bw.write("\"Email Client\",\"Popularity\",\"Subscribers\"\r\n");
				int size = 1000;
				List<Object[]> list;
				double clientPercentage;
				for (int i = 0; i < count; i+=size) {
					sb = new StringBuffer();
					list = opensDao.getClientReportByCrId(campaignReport.getCrId());
					logger.debug("Got List of size : " + list.size() + " | start index : " + i);
					if(list.size()>0){
						for (Object[] obj : list) {
							clientPercentage = ((((Long)obj[1]).doubleValue()/totalOpens) * 100);
							sb.append("\"");sb.append(obj[0]); sb.append("\",");
							sb.append("\""); sb.append(clientPercentage); sb.append("\",");
							sb.append("\""); sb.append(obj[1]); sb.append("\"\r\n");
						}
					}
					bw.write(sb.toString());
					list = null;
					//System.gc();
				}
				bw.flush();
				bw.close();
				Filedownload.save(file, "text/plain");
			} catch (Exception e) {
				logger.error("Exception : ",e);
			}
			logger.debug("-- exit --");
		}
	} // export

	public void onClick$clickedExportBtnId() {
		try {
			export();
		} catch (Exception e) {
			logger.error("Exception :: error getting from the export method **",e);
		}
	} // onClick$clickedExportBtnId
	
}
