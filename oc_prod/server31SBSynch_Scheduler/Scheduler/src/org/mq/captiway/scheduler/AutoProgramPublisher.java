package org.mq.captiway.scheduler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.AutoProgram;
import org.mq.captiway.scheduler.beans.AutoProgramComponents;
import org.mq.captiway.scheduler.beans.ComponentsAndContacts;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.beans.MailingList;
import org.mq.captiway.scheduler.beans.Messages;
import org.mq.captiway.scheduler.beans.ProgramOnlineReports;
import org.mq.captiway.scheduler.beans.SwitchCondition;
import org.mq.captiway.scheduler.beans.TempComponentsData;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.dao.AutoProgramComponentsDao;
import org.mq.captiway.scheduler.dao.AutoProgramComponentsDaoForDML;
import org.mq.captiway.scheduler.dao.CampaignReportDao;
import org.mq.captiway.scheduler.dao.CampaignSentDao;
import org.mq.captiway.scheduler.dao.CampaignsDao;
import org.mq.captiway.scheduler.dao.ComponentsAndContactsDao;
import org.mq.captiway.scheduler.dao.ComponentsAndContactsDaoForDML;
import org.mq.captiway.scheduler.dao.ContactsDao;
import org.mq.captiway.scheduler.dao.MessagesDao;
import org.mq.captiway.scheduler.dao.MessagesDaoForDML;
import org.mq.captiway.scheduler.dao.ProgramOnlineReportsDao;
import org.mq.captiway.scheduler.dao.ProgramOnlineReportsDaoForDML;
import org.mq.captiway.scheduler.dao.SMSCampaignsDao;
import org.mq.captiway.scheduler.dao.SwitchConditionDao;
import org.mq.captiway.scheduler.dao.TempActivityDataDao;
import org.mq.captiway.scheduler.dao.TempActivityDataDaoForDML;
import org.mq.captiway.scheduler.dao.TempComponentsDataDao;
import org.mq.captiway.scheduler.dao.TempComponentsDataDaoForDML;
import org.springframework.context.ApplicationContext;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.Utility;

/**
 * 
 * @author Proumya
 *
 */
public class AutoProgramPublisher extends Thread {

	private volatile boolean isRunning = false; 
	private ApplicationContext applicationContext;
	private AutoProgramComponentsDao autoProgramComponentsDao;
	private AutoProgramComponentsDaoForDML autoProgramComponentsDaoForDML;

	private ContactsDao contactsDao;
	private CampaignReportDao campaignReportDao;
	private CampaignSentDao campaignSentDao;
	private ComponentsAndContactsDao componentsAndContactsDao;
	private ComponentsAndContactsDaoForDML componentsAndContactsDaoForDML;
	private TempComponentsDataDao tempComponentsDataDao;
	private TempComponentsDataDaoForDML tempComponentsDataDaoForDML;
	private TempActivityDataDao tempActivityDataDao;
	private TempActivityDataDaoForDML tempActivityDataDaoForDML;
	private CampaignsDao campaignsDao;
	private PMTAQueue pmtaQueue;
	private SMSCampaignsDao smsCampaignsDao;
	private SwitchConditionDao switchConditionDao;
	private SMSQueue smsQueue;
	private MessagesDao messagesDao;
	private MessagesDaoForDML messagesDaoForDML;
	private ProgramOnlineReportsDao programOnlineReportsDao;
	private ProgramOnlineReportsDaoForDML programOnlineReportsDaoForDML;

	
	
	
	private ProgramQueue programQueue;
	
	private static AutoProgramPublisher autoProgramPublisher;
	
	/**
	 * 
	 * @param applicationContext
	 */
	public static void startAutoProgramPublisher(ApplicationContext applicationContext) {
		
		if(autoProgramPublisher == null || !autoProgramPublisher.isRunning() ) {
			
			autoProgramPublisher = new AutoProgramPublisher(applicationContext);
			autoProgramPublisher.start();
			
		}//if
		
		
	}
	//consists only data giving components.
	Set<String> populated = new HashSet<String>();//required when iterating the dataConsMap (to find out is cyclic dependency there or not)
	
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	
	private AutoProgramPublisher(ApplicationContext applicationContext) {
		
		
		 this.applicationContext = applicationContext;
		 this.programQueue = (ProgramQueue)applicationContext.getBean("programQueue");
		 this.autoProgramComponentsDao = (AutoProgramComponentsDao)applicationContext.getBean("autoProgramComponentsDao");
		 this.autoProgramComponentsDaoForDML = (AutoProgramComponentsDaoForDML)applicationContext.getBean("autoProgramComponentsDaoForDML");

		 this.contactsDao = (ContactsDao)applicationContext.getBean("contactsDao");
		 this.campaignReportDao = (CampaignReportDao)applicationContext.getBean("campaignReportDao");
		 this.campaignSentDao = (CampaignSentDao)applicationContext.getBean("campaignSentDao");
		 this.componentsAndContactsDao = (ComponentsAndContactsDao)applicationContext.getBean("componentsAndContactsDao");
		 this.componentsAndContactsDaoForDML = (ComponentsAndContactsDaoForDML)applicationContext.getBean("componentsAndContactsDaoForDML");
		 this.tempComponentsDataDao = (TempComponentsDataDao)applicationContext.getBean("tempComponentsDataDao");
		 this.tempComponentsDataDaoForDML = (TempComponentsDataDaoForDML)applicationContext.getBean("tempComponentsDataDaoForDML");
		 this.tempActivityDataDao = (TempActivityDataDao)applicationContext.getBean("tempActivityDataDao");
		 this.tempActivityDataDaoForDML = (TempActivityDataDaoForDML)applicationContext.getBean("tempActivityDataDaoForDML");
		 this.campaignsDao = (CampaignsDao)applicationContext.getBean("campaignsDao");
		 this.pmtaQueue = (PMTAQueue)applicationContext.getBean("pmtaQueue");
		 this.smsCampaignsDao = (SMSCampaignsDao)applicationContext.getBean("smsCampaignsDao");
		 this.smsQueue = (SMSQueue)applicationContext.getBean("smsQueue");
		 this.switchConditionDao = (SwitchConditionDao)applicationContext.getBean("switchConditionDao");
		 this.messagesDao = (MessagesDao)applicationContext.getBean("messagesDao");
		 this.messagesDaoForDML = (MessagesDaoForDML)applicationContext.getBean("messagesDaoForDML");
		 this.programOnlineReportsDao = (ProgramOnlineReportsDao)applicationContext.getBean("programOnlineReportsDao");
		 this.programOnlineReportsDaoForDML = (ProgramOnlineReportsDaoForDML)applicationContext.getBean("programOnlineReportsDaoForDML");

	}
	
	public boolean isRunning() {
		
		return isRunning;
	}
	
	private Map<String, AutoProgramComponents> progCompMap = new HashMap<String, AutoProgramComponents>();
	private String mlStr;
	
	@Override
	public void run() {
		try {
			/**
			 * need to pick the Active autoProgram objects and do further 
			 * process in-order to execute the complete Auto Program
			 **/
			
			String qry="";
			Object object = null;
			AutoProgram autoProgram = null;
			if(logger.isDebugEnabled()) logger.debug("entered in run");
			isRunning = true;
			Users user = null;
			
			List<AutoProgramComponents> componentsList = null;
			Set<MailingList> mlList = null;
			List<Contacts> contactList = null;
			TempComponentsData tempComponentsData = null;
			String contactIdStr = "";
			
			while((object = programQueue.getObjFromQueue()) != null) {
				
				progCompMap.clear();
				dataConsMap.clear();
				populated.clear();
				componentLabelMap.clear();
				
				try {
					
					if(!(object instanceof AutoProgram)) {
						if(logger.isDebugEnabled()) logger.debug("Object is not an Instance of Auto Program "+object);
						continue;
					}
						
					/**********need to get the ActiveProgram object & ActiveProgramComponents 
					  objects associated with this program and do further processing********/
					 
					
					autoProgram = (AutoProgram)object;
					user = autoProgram.getUser();
					mlList = autoProgram.getMailingLists();
					
					qry = "DELETE FROM temp_components_data where program_id="+autoProgram.getProgramId();
					
					if(logger.isDebugEnabled()) logger.debug("the query to be executed to delete the record in the temp_components_data is===>'"+qry+"'");
					
					tempComponentsDataDaoForDML.executeJdbcQuery(qry);
					
					
					//TODO need to verify whether is it necessary or not
					mlStr = "";//to avoid unwanted list ids of other program object
					for (MailingList mailingList : mlList) {
					
						if(mlStr.length() > 0) mlStr += ",";
						mlStr +=  mailingList.getListId();
						
					} // for each mailing list

					
					if(logger.isDebugEnabled()) logger.debug("----just entered in AutoProgramPulisher FIRST pass------");
					
					componentsList = autoProgramComponentsDao.getProgramComponents(autoProgram.getProgramId(), 
										autoProgram.getUser().getUserId());//userId not required.
					
					if(logger.isDebugEnabled()) logger.debug("the number of components are====>"+componentsList.size()+" and the components are===>"+componentsList);
					

					
					/***************pass:1  Find the starting point and prepare Map ***********************/

					//AutoProgramComponents firstComp=null;
					String compType = null;

					//List<AutoProgramComponents> tempList = new ArrayList<AutoProgramComponents>();
					for(AutoProgramComponents  tempComponent : componentsList) {
						
						compType = tempComponent.getCompType();
						// Store the components in the Map(progCompMap)
						if(compType.equalsIgnoreCase(ProgramEnum.SWITCH_DATA.name()) ||
							compType.equalsIgnoreCase(ProgramEnum.SWITCH_ALLOCATION.name())	) { //for switch add four entries(for 0,1,2,3)

							for(int i= 0; i<4; i++ ) {
								progCompMap.put(tempComponent.getComponentWinId()+i, tempComponent);
							}
						}
						else {
						
							progCompMap.put(tempComponent.getComponentWinId()+"0", tempComponent); //for remaining components add just "0" at the end
						}
						
						/*tempComponent.setPopulated(false);//confirm it after review..............................
						tempList.add(tempComponent);//confirm it after review..............................
*/						
					} // for 
					//autoProgramComponentsDao.saveByCollection(tempList);//confirm it after review..............................
					if(logger.isDebugEnabled()) logger.debug("the complete map is=====>"+progCompMap);
					
					/**
					 * PASS:2 Prepare the input Set(consisting data giving component's Window Ids[along with the pick]) 
					 * for data giving components except for custActivated.
					 */
					
					if(logger.isDebugEnabled()) logger.debug("----just entered in AutoProgramPulisher SECOND pass------");
					for(AutoProgramComponents tempComponent : componentsList) {
						
						if(isDataGivingComponent(tempComponent)) {
							//need to pass mlList just for the sake of CUST_ACTIVATED data population(actual data population should done in 3rd pass)
							//TODO need to populate data for CUST_ACTIVATED in 3rd pass when other starting points introduce.
							
								prepareComponentInputList(tempComponent, mlList);
						
						}//if
						
					}//for 
					
					if(logger.isDebugEnabled()) logger.debug("the data consideration map after SECOND pass is====>"+dataConsMap);
					
					/***********PASS:3 actual population of data for each entry of dataConsMap***********/
					
					//currComp = firstComp;
					
					if(logger.isDebugEnabled()) logger.debug("----just entered in AutoProgramPulisher THIRD pass------");
					
					//no need of mlList to be passed
					if(!populateComponentOutputData(mlList)) { //this is the only block which actually populate the data for each data giving component
						
						if(logger.isDebugEnabled()) logger.debug("cant be processed:found cyclic dependency . ");
						Messages messages = new Messages("Auto Responder","The program was not designed properly:found cyclic dependency",
								"Please redesigned the program", user);
						//messagesDao.saveOrUpdate(messages);
						messagesDaoForDML.saveOrUpdate(messages);
						return;
						
						
					}//if
					
					/***********************PASS: 4 Send the SMS / Email *************************************/
					
					if(logger.isDebugEnabled()) logger.debug("----just entered in AutoProgramPulisher FOURTH pass------");
					
					for(AutoProgramComponents tempComponent : componentsList) {
						
						if(!isDataGivingComponent(tempComponent)) {
							
							
							if( tempComponent.getCompType().equalsIgnoreCase(ProgramEnum.ACTIVITY_SEND_EMAIL.name()) ) { // if activity is email
								
								//get the data to perform this activity
								
								getDataToSendEmail(tempComponent);

								
							}//if
							
							else if( tempComponent.getCompType().equalsIgnoreCase(ProgramEnum.ACTIVITY_SEND_SMS.name()) ) { // if activity is sms
								
								//get the data to perform this activity
								getDataToSendSMS(tempComponent);
								
								
								
							}// else if
							
							else if( tempComponent.getCompType().equalsIgnoreCase(ProgramEnum.EVENT_END.name()) ) {
								 
								/* set the status as 'END'(describes that those particular contacts have been reached the status 'END' )
								for those contacts which are presented(if not create new entries with the status 'END') 
								in the components_contacts */
								
								getDataToReachEnd(tempComponent);
								
								
								
							}// else if
							
						}//if
						
					}//for
				} catch (Exception e) {
					logger.error("Exception ::::" , e);
				}
				
				finally{
					
					programQueue.removeObjFromQueue(object);
					isRunning = false;
					//smsQueue.removeObjFromQueue(object);
					
				}
			} //while
			
			
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		
		

	}//run
	
	/**
	 * This method does the following things.
	 * 1.check if cyclic dependency exist in the design of the program.<BR>
	 * 2.populates the data for each data giving component.<BR><BR>
	 * @param mlList(not require)
	 * @return
	 */
	public boolean populateComponentOutputData(Set<MailingList> mlList) {
		boolean isDependent = false;
		try {
			
			AutoProgramComponents currComp = null;
			Set<String> compWinIds = dataConsMap.keySet();//these are the (components window ids which are giving data)
			Set<String> dataGivingCompList = null;

			List<String> tempList = new ArrayList<String>(); //compWinIds;
			
			tempList.addAll(compWinIds);
			
			if(logger.isDebugEnabled()) logger.debug("winIds in keySet are====>"+tempList+"   "+tempList.size());
			
			while(tempList.size() > 0) {
				
				if(logger.isDebugEnabled()) logger.debug("############# Starting of While loop ######### With list size ="+tempList.size());
				int prevSize = -1;
				String tempId;
				
			outer: for (ListIterator li = tempList.listIterator(); li.hasNext();) {
				
				 tempId = (String) li.next();
				 if(logger.isDebugEnabled()) logger.debug("entered in outer for----"+tempId+" the size of tempList and the preSize are===>"+tempList.size()+","+prevSize);
				 
				 currComp = progCompMap.get(tempId);//get the actual component object from the progCompMap
				 
				 if(tempList.size() == prevSize ) {
					 if(logger.isDebugEnabled()) logger.debug("Foud and Cyclic dependency for :"+tempId);
						return isDependent;
				 } // if
			 

					
					dataGivingCompList = dataConsMap.get(tempId);//get the list of data giving components for this examining component
					if(logger.isDebugEnabled()) logger.debug("the data giving components list for this "+tempId+ "====>"+dataGivingCompList);
					
					if(dataGivingCompList == null) {
						if(logger.isDebugEnabled()) logger.debug("Error in getting the List :"+tempId);
						continue;
					}
					if(logger.isDebugEnabled()) logger.debug("the populated set consists of...."+populated+"....these components.");
					
					boolean foundFlag=false;
					boolean foundDataComp=false;
					
					for (String tempCompId : dataGivingCompList) {
						if(logger.isDebugEnabled()) logger.debug("---entered in inner for----"+tempCompId);
						/*String temp = givePreviousId(currComp, tempComp);//this method acts as the utility method which returns the window id of child comp
						
						if(logger.isDebugEnabled()) logger.debug("the returned id is====>"+temp);
						*/
						if(!isDataGivingComponent(progCompMap.get(tempCompId))) {
							continue;
						}
						else {
							foundDataComp = true;
						}
						
						//TODO Testing code 
						if(populated.contains(tempCompId)) { foundFlag=true; break; }
						
						
					} // for tempComp
					
					if(foundDataComp==true && foundFlag==false) continue outer;
					
					// data is Populated for this li
					
			 		
			 		
					//need to clarify is it better or the earlier??????????????/
					/*if(currComp.getCompType().equalsIgnoreCase(ProgramEnum.EVENT_CUST_ACTIVATED.name()))
						populateCustActivatedOutputData(currComp, mlList);
						*/
						
					 if(currComp.getCompType().equalsIgnoreCase(ProgramEnum.EVENT_ELAPSE_TIMER.name()))
						//populateElapsedTimerOutputData(currComp);
						 populateElapsedTimerOutputData(tempId);
					 
					 else if(currComp.getCompType().equals(ProgramEnum.EVENT_TARGET_TIMER.name()) )
							 populateTargetTimerOutputData(tempId);
					 
					else if(currComp.getCompType().equalsIgnoreCase(ProgramEnum.SWITCH_DATA.name()))
						populateSwitchDataCompOutputData(tempId);
					 
					else if(currComp.getCompType().equalsIgnoreCase(ProgramEnum.SWITCH_ALLOCATION.name()))
						populateSwitchAllocationComponentOutputData(tempId);
						
						
						//TODO need to consider some more components in future
					
					prevSize = tempList.size(); 
					populated.add(tempId);
					
					
					//tempList.remove(tempId);
					li.remove();
					
					if(logger.isDebugEnabled()) logger.debug("the size of tempList after deletion===? "+tempList.size()+" , PrevSize="+prevSize);
					
				
					
				} // for li
				
				//TODO need to reinitialize all the switch allocation related variables,if not clash may occur amng the same type of components
			} // while 
			
				if(logger.isDebugEnabled()) logger.debug("populated set is====> "+populated+"  size is===> "+populated.size());
			
				isDependent= true;
			
			
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		return isDependent;
		
		
		
	}//populateComponentOutputData
	
	
	
	/**
	 * This method checks whether we should consider target timer or not(i.e whether need to traverse back from it or not)
	 * based on the time configured for this component 
	 * @param sec
	 * @return
	 */
	
	public boolean isElapsed(Long sec) {
		boolean elapsed = (sec <= Calendar.getInstance().getTimeInMillis() );
		if(logger.isDebugEnabled()) logger.debug("-----just entered in isElapsed(-)-----"+elapsed);
		return elapsed;
		
		
		
	}
	
	/**
	 * this method allows us to get the mode(path in which it is receiving input and giving out put )
	 * 
	 * @param currComp
	 * @param tempComp
	 * @return
	 */
	
	public String givePreviousId(AutoProgramComponents currComp, AutoProgramComponents tempComp) {
		//need to get the outputmode of the datagiving component(tempComp) of currComp
		String preId = "";
		try {
			if(logger.isDebugEnabled()) logger.debug("-----just entered in givepreviousId(-,-)----");
			preId = currComp.getPreviousId();
			
			preId = preId.substring(preId.indexOf(tempComp.getComponentWinId()), 
					(preId.indexOf(tempComp.getComponentWinId())+tempComp.getComponentWinId().length()+1));
			
			if(logger.isDebugEnabled()) logger.debug("preId in givePreviousId is===>"+preId+"populated contaains====>"+populated.contains(preId));
			/*
			 * prevIds = prevIds.substring(prevIds.indexOf(currComp.getComponentWinId()), 
						(prevIds.indexOf(currComp.getComponentWinId()))+currComp.getComponentWinId().length()+1);
			 */
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		return preId;
		
		
	}//givePreviousId
	
	
	/**
	 * This method allows us to prepare map entry for each data giving component(except CUST_ACTIVATED),
	 * consisting the Set of data giving component's windowId(along with the pick) for this component as a value
	 * @param currComp
	 * @param mlList
	 * @return
	 */
	public void prepareComponentInputList(AutoProgramComponents currComp, Set<MailingList> mlList) {
		
		if(currComp == null) {
			if(logger.isDebugEnabled()) logger.debug("Given component is Null");
			return;
		}
		
		String compType = currComp.getCompType(); 
		
		if(compType.equalsIgnoreCase(ProgramEnum.EVENT_CUST_ACTIVATED.name())) { // if type is cust activated
			populateCustomerActivatedOutputData(currComp, mlList);
			//populateCustActivatedOutputData(currComp, mlList);//for custActivated no need to prepare the input set,directly can populate the data
			populated.add(currComp.getComponentWinId()+"0");//says data for this components has been populated
			
			
		}else if(compType.equalsIgnoreCase(ProgramEnum.EVENT_CUST_DEACTIVATED.name())) { // if type is customer deactivated
			
			
			
			
		} else if(compType.equalsIgnoreCase(ProgramEnum.EVENT_ELAPSE_TIMER.name())){ // elapsed timer type
			
			
			// need to prepare the entry in the dataConsMap for this elapse timer
			prepareElapsedTimerinputList(currComp);
			
			
		}// else if
		
		else if(compType.equals(ProgramEnum.EVENT_TARGET_TIMER.name())) {
			
			if(isElapsed(currComp.getSupportId())) {//if target timer elapsed then only try to prepare its input list
				
				// need to prepare the entry in the dataConsMap for this target timer
				prepareTargetTimerInputList(currComp);
			}//if
			
			
		}// else if
		
		else if(compType.equalsIgnoreCase(ProgramEnum.SWITCH_DATA.name()) ||
				compType.equalsIgnoreCase(ProgramEnum.SWITCH_ALLOCATION.name())) { // switch data type
			
			// need to prepare the entry in the dataConsMap for this Switch (Data / Allocation) component
			prepareSwitchDataCompinputList(currComp);
			
			
		}//if
			
	} // prepareComponentInputList
	
	/**
	 * This method prepares map entry and populate the data in temp_activity_table for this sms activity component <BR>
	 * STEP1:run the DFS algorithm to make an entry in the dataConsMap for this component(gives set of data consiring components for it).<BR>
	 * STEP2:for each data inputting component in set gather their labels and prepare current component's label.<BR>
	 * STEP3:fetch the data from temp_components_data based on the gathered input component's label(s).<BR>
	 * STEP4:populate data for this current activity component based on the data got from step-3.<BR>
	 * STEP5: to initiate the sending process of SMS,add this component into 'smsQueue' and start the SMSCampaignSubmitter.<Br>
	 * 
	 * @param currComp specifies the SMS Activity component 
	 */
	public void getDataToSendSMS(AutoProgramComponents currComp) {
		/**************STEP1:run the DFS algorithm to make an entry in the dataConsMap for this component  **************************/
		
		if(logger.isDebugEnabled()) logger.debug("---just entered to get the data for sms sending---");
		DFSForActivity(currComp);
		
		if(logger.isDebugEnabled()) logger.debug("----the map to populate data for this sms is====>"+dataConsMap);
		
		/**********STEP2:for each data inputting component in set gather their labels and prepare current component's label.****************/
		
		Set<String> tempDatagivingComponents = dataConsMap.get(currComp.getComponentWinId()+"0");//need to handle NUllPointerException
		
		if(tempDatagivingComponents == null) {
			
			if(logger.isDebugEnabled()) logger.debug("got no input components,returning.....");
			return;
			
		}
		
		String labelToBeSet = "";
		String label = "";
		String compLabel = null;
		String prevId = "";
		
		AutoProgramComponents tempComponent = null;
		for (String tempComponentId : tempDatagivingComponents) {
			
			tempComponent = progCompMap.get(tempComponentId);
			compLabel = componentLabelMap.get(tempComponentId);
			//compLabel = tempComponent.getLabel();
			//for each and every input data giving components initiate the process  
			if(compLabel != null && compLabel.trim().length() != 0) {
				
				
				/*if(tempComponent.getCompType().contains("SWITCH_")) {
					//prevId = givePreviousId(currComp, tempComponent) ;
					//if it is a switch component the label will be("timestamp:SWITCH_DATA-11w0|timestamp:SWITCH_DATA-11w1")
					String[] switchLabel = compLabel.split("\\|");
					for (int i = 0; i < switchLabel.length; i++) {
						
						if(!switchLabel[i].contains(tempComponentId)) continue;
						
							
						if(labelToBeSet.length() > 0) labelToBeSet += ",";
						labelToBeSet += "'"+switchLabel[i]+"'";
						
						if(label.length() > 0) label += ",";
						label += switchLabel[i];
						
						
					}//for
					continue;
				}//if switch
				*/
					if(labelToBeSet.length() > 0) labelToBeSet += ",";
					
					labelToBeSet += "'"+compLabel+"'";
					
					
					
					if(label.length() > 0) label += ",";
					label += compLabel;
				
			}// if
			else continue;
			
		}// for
		
		/***********STEP3:fetch the data from temp_components_data based on the gathered input component's label(s).**********************/
		
		if(labelToBeSet.length() > 0) {
		
			String subQuery = "select contact_id from temp_components_data where label in("+labelToBeSet+")";
		
			String cids = tempComponentsDataDao.getContactIdsStr(subQuery);
		
		/***********STEP4:populate data for this current activity component based on the data got from step-3.****************************/
		if(  cids.length() > 0 ) {
			
			String query = "INSERT IGNORE INTO temp_activity_data ( " +
							" program_id, component_id, label, contact_id, component_win_id) " +
							" SELECT " +
							currComp.getAutoProgram().getProgramId()+ " , " + 
							currComp.getCompId() +	" , '" +
							label+"' , " +
							" contact_id, '" +
							currComp.getComponentWinId() +"' " +
							" FROM temp_components_data where label in(" +labelToBeSet+")";
							
			
			
			if(logger.isDebugEnabled()) logger.debug("query to be executed is=====>"+query);
			tempActivityDataDaoForDML.executeJdbcQuery(query);					
			currComp.setLabel(label);
			//autoProgramComponentsDao.saveOrUpdate(currComp);
			autoProgramComponentsDaoForDML.saveOrUpdate(currComp);

			
			/**********STEP5: to initiate the sending process of SMS,add this component into 'smsQueue' 
			 					and start the SMSCampaignSubmitter***********************************/
			List<AutoProgramComponents> listToBeAdd = new ArrayList<AutoProgramComponents>();
			listToBeAdd.add(currComp);
			smsQueue.addCollection(listToBeAdd);
			SMSCampaignSubmitter.startSMSCampaignSubmitter(applicationContext);
		
		}//if
		
		else {
			
			if(logger.isDebugEnabled()) logger.debug("no contacts are found to perform SMS semding operation....");
		}
		
		}//if
		
	}// getDataToSendSMS
	
	
	
	
	/**
	 * This method allow us to prepare a set of components which gives the data to perform email sending operation
	 * and populate the data in activity_temp_table for this email sending.
	 * @param currComp
	 */
	public void getDataToSendEmail(AutoProgramComponents currComp) {
		//run the DFS algorithm to gat all the its previous data giving components
		/**************STEP1:run the DFS algorithm to make an entry in the dataConsMap for this component  **************************/
		
		if(logger.isDebugEnabled()) logger.debug("---just entered to get the data for emeil sending---");
		DFSForActivity(currComp);
		
		if(logger.isDebugEnabled()) logger.debug("----the map to populate data for this email is====>"+dataConsMap);
		
		/*************STEP2: for each data giving component initiate the sending process  **********************/
		Set<String> tempDatagivingComponents = dataConsMap.get(currComp.getComponentWinId()+"0");
		
		if(tempDatagivingComponents == null) {
			
			if(logger.isDebugEnabled()) logger.debug("got no input components,returning.....");
			return;
			
		}//if
		
		String labelToBeSet = "";
		String label = "";
		String prevId = "";
		String compLabel = null;
		AutoProgramComponents tempComponent = null;
		for (String tempComponentId : tempDatagivingComponents) {
			
			tempComponent = progCompMap.get(tempComponentId);
			compLabel = componentLabelMap.get(tempComponentId);
			//compLabel = tempComponent.getLabel();
			//for each and every input components type that is giving the data initiate the email sending process
			if(compLabel != null && compLabel.trim().length() != 0) {
				
				
				/*if(tempComponent.getCompType().contains("SWITCH_")) {
					//prevId = givePreviousId(currComp, tempComponent) ;
					//if it is a switch component the label will be("timestamp:SWITCH_DATA-11w0|timestamp:SWITCH_DATA-11w1")
					String[] switchLabel = compLabel.split("\\|");
					for (int i = 0; i < switchLabel.length; i++) {
						
						if(!switchLabel[i].contains(tempComponentId)) continue;
						
							
						if(labelToBeSet.length() > 0) labelToBeSet += ",";
						labelToBeSet += "'"+switchLabel[i]+"'";
						
						if(label.length() > 0) label += ",";
						label += switchLabel[i];
						
						
					}//for
					continue;
				}//if switch
				*/
					if(labelToBeSet.length() > 0) labelToBeSet += ",";
					labelToBeSet += "'"+compLabel+"'";
					
					if(label.length() > 0) label += ",";
					label += compLabel;
				
				
			
			}//if
			else continue;
			
		}//for
			
		if(labelToBeSet.length() > 0 ) {
			String subQuery = "select contact_id from temp_components_data where label in("+labelToBeSet+")";
			
			String cids = tempComponentsDataDao.getContactIdsStr(subQuery);
			
			if( cids.length() > 0 ) {
			
				String query = "INSERT IGNORE INTO temp_activity_data ( " +
								" program_id, component_id, label, contact_id, component_win_id) " +
								" SELECT " +
								currComp.getAutoProgram().getProgramId()+ " , " + 
								currComp.getCompId() +	" , '" +
								label+"' , " +
								" contact_id, '" +
								currComp.getComponentWinId() +"' " +
								" FROM temp_components_data where label in(" +labelToBeSet+")";
								
				if(logger.isDebugEnabled()) logger.debug("the query to be executed is====>"+query);
				
				tempActivityDataDaoForDML.executeJdbcQuery(query);	
				currComp.setLabel(label);
				//autoProgramComponentsDao.saveOrUpdate(currComp);
				autoProgramComponentsDaoForDML.saveOrUpdate(currComp);

				
				if(logger.isDebugEnabled()) logger.debug("adding the component to pmtaQueue"+pmtaQueue.getQueueSize()+" " +
							"and the Added object is====>"+currComp.getComponentWinId());
				 List<AutoProgramComponents> list = new ArrayList<AutoProgramComponents>();
					list.add(currComp);
					pmtaQueue.addCollection(list);
				//pmtaQueue.addObjToQueue(currComp);
				PmtaMailmergeSubmitter.startPmtaMailmergeSubmitter(applicationContext);
			}//if
			else {
				if(logger.isDebugEnabled()) logger.debug("no contacts are found to perform email sending operation...");
			}
		
		}//if
	} // getDataToSendEmail
	
	
	/**
	 * This method makes the contacts for this program to be reached to END status.</BR>
	 * in components_contacts for those contacts (got from various components) sets the</BR> 
	 * status as 'END'.</BR><BR>
	 * CASE1:if previous is a data giving component get the component's related contacts and update/insert(if not exists)in components_contacts</BR><BR>
	 * CASE2:if previous is an activity check for the presence of this component in the path in components_contacts,</BR><BR>
	 * if there update the status as 'END',</BR>   
	 * and else if not continue further.</BR>	 
	 * 
	 * @param currComp
	 */
	public void getDataToReachEnd(AutoProgramComponents currComp) {
		if(logger.isDebugEnabled()) logger.debug("---just entered to reach end-----");
		String previousId = currComp.getPreviousId();
		AutoProgram autoProgram = currComp.getAutoProgram();
		AutoProgramComponents tempComponent = null;
		
		
		String[] preIds = previousId.split(",");
		String query = "";
		String subQuery = "";
		
		String cids = "";
		String totCids = "";
		
		int updateCount = 0;
		List<ComponentsAndContacts> listToBeUpdated = new ArrayList<ComponentsAndContacts>();
		List<ComponentsAndContacts> tempList = new ArrayList<ComponentsAndContacts>();
		
		ComponentsAndContacts componentsAndContacts  = null;
		
		Calendar cal = Calendar.getInstance();
		Long compId = null;
		for(String preId : preIds) {
			
			if(logger.isDebugEnabled()) logger.debug("the previous component for this END is===>"+preId);
			
			if(preId.trim().length() == 0) continue;
			
			tempComponent = progCompMap.get(preId);
			compId = tempComponent.getCompId();
			
			
			/*****************CASE1:******************************/
			if(isDataGivingComponent(tempComponent)) {
				
				//get the contacts from temp_component_data table and update the status for those contacts in components_contacts
				
				//known problem is to fetch only limited number of contact ids
				
				subQuery = "SELECT contact_id FROM components_contacts where program_id=" +
							+autoProgram.getProgramId();
				
				if(! preId.contains("SWITCH_") ) {
				
					query = "select contact_id from temp_components_data where component_id="+compId+"" +
								" AND contact_id NOT IN(<CID>) AND program_id="+autoProgram.getProgramId();
				}
				else {
					
					query = "select contact_id from temp_components_data where component_id="+compId+" " +
								"AND mode_attribute='"+preId+"' AND contact_id NOT IN(<CID>) AND program_id="+autoProgram.getProgramId();
					
				}
				
				cids = tempComponentsDataDao.getContactIdsStr(subQuery);
				
				if( cids.length() > 0) {
					
					query = query.replace("<CID>", cids);
					
					cids = tempComponentsDataDao.getContactIdsStr(query);
					if(cids.length() > 0 ) {
						
						for(String cid : cids.split(",")) { //this happens by rare infact never.
							
							
							componentsAndContacts = new ComponentsAndContacts(autoProgram.getProgramId(),
														autoProgram.getUser().getUserId(), Long.parseLong(cid), 
														currComp.getCompId(), tempComponent.getStage(), 
														"", "", cal);
							
							listToBeUpdated.add(componentsAndContacts);
								
						}//for
						
						//componentsAndContactsDao.saveByCollection(listToBeUpdated);
						componentsAndContactsDaoForDML.saveByCollection(listToBeUpdated);

						
						
					}//if
					
				}//if
				
				if( !preId.contains("SWITCH_") )
					query = "SELECT contact_id FROM temp_components_data WHERE component_id="+compId+
								" AND program_id="+autoProgram.getProgramId();
				
				else 
					query = "SELECT contact_id FROM temp_components_data WHERE component_id="+compId+
								" AND mode_attribute='"+preId+"' AND program_id="+autoProgram.getProgramId();
				
				cids = tempComponentsDataDao.getContactIdsStr(query);
				
				if(cids.length() > 0 ) {
					
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date activityDate = cal.getTime();
					String formatDate = format.format(activityDate);
					
					
					query = "update components_contacts set component_win_id='"+currComp.getComponentWinId()+"',component_id="+currComp.getCompId() +
							",activity_date='"+formatDate+"',path=concat(path,'," +currComp.getComponentWinId() +"')"+
							" WHERE contact_id in("+cids+") AND program_id="+autoProgram.getProgramId() ;
					
					if(logger.isDebugEnabled()) logger.debug("the query to be executed to reach the end status is===>"+query);
					
					updateCount = componentsAndContactsDaoForDML.executeJdbcUpdateQuery(query);
					
					
					tempList = componentsAndContactsDao.getByContactIds(cids, autoProgram.getProgramId());
					
					
				}// if
				
			}//if
			
			/*********CASE2***********************************/
			else if( !isDataGivingComponent(tempComponent) ) {
				
				//update those contacts status for whom this activity has been performed
				
				
				//subQuery = "select contact_id from components_contacts where component_win_id='"+ +",";
				
				//known problem is to fetch only the limitted number of contact ids
				
				
				
				query = "SELECT contact_id FROM components_contacts WHERE FIND_IN_SET('"
						+tempComponent.getComponentWinId()+"',path) >= 1 AND program_id="+autoProgram.getProgramId()+
						" AND component_id="+tempComponent.getCompId();
				
				cids = tempComponentsDataDao.getContactIdsStr(query);
				
				if(cids.length() > 0) {
					
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date activityDate = cal.getTime();
					String formatDate = format.format(activityDate);
					
					query = "update components_contacts set component_win_id='"+currComp.getComponentWinId()+"', component_id="+currComp.getCompId() +
							",activity_date='"+formatDate+"',path=concat(path,'," +currComp.getComponentWinId() + "')"+
							" WHERE contact_id in("+cids+") AND program_id="+autoProgram.getProgramId();
				
				if(logger.isDebugEnabled()) logger.debug("the query to be executed to reach the END status is===>"+query);
				
				updateCount = componentsAndContactsDaoForDML.executeJdbcUpdateQuery(query);
				
				
				tempList = componentsAndContactsDao.getByContactIds(cids, autoProgram.getProgramId());
				
				
				
				
				}//if
				
			}// else if
	
			if(logger.isDebugEnabled()) logger.debug("the number of contacts reached the status 'END' from the component "+preId+" are.... "+updateCount);
			
			
			
			
			List<ProgramOnlineReports> prgOnlineRepToBeSaved = new ArrayList<ProgramOnlineReports>();
			if(tempList != null && tempList.size() > 0 ) {
			
				for (ComponentsAndContacts tempComponentsAndContacts : tempList) {
					
					ProgramOnlineReports proOnlineRep = new ProgramOnlineReports(tempComponentsAndContacts,
															currComp.getComponentWinId(), cal.getTime(), 
															autoProgram.getProgramId(), currComp.getCompId(), tempComponentsAndContacts.getContactId());
					
					prgOnlineRepToBeSaved.add(proOnlineRep);
					
					
				}//for
				
				//programOnlineReportsDao.saveByCollection(prgOnlineRepToBeSaved);
				programOnlineReportsDaoForDML.saveByCollection(prgOnlineRepToBeSaved);

			
			}
			//tempComponentsDataDao.getContactIdsStr(subQuery);
			tempList.clear();
		}//for
		
	}//getDataToReachEnd
	
	
	
	/**
	 * This method prepares the list of data giving components for this  switch component component( need to consider various scenarios)</BR>
	 * CASE#1:if immediate previous is a an activity 	   	  : need to push all the activities until it found its data giving component</BR>
	 * CASE#2:if immediate previous is a switch 		   	  : need to push its mode(only that pick) in which it is giving o/p for it </BR>
	 * CASE#3:if immediate previous is a CustomerActivated 	  : need to push it directly</BR>
	 * CASE#4:if immediate previous is a elapsed timer 		  : need to push it directly</BR>
	 * CASE#5:if immediate previous is a target timer  		  : need to push it directly</BR>
	 * @param currComp
	 */
	
	public void prepareSwitchDataCompinputList(AutoProgramComponents switchComponent) {
		
		
		if(logger.isDebugEnabled()) logger.debug("----just entered for populating data for Switch Data------");
		
		if(switchComponent == null) {
			
			if(logger.isErrorEnabled()) logger.error("Error, got switchComponent as null...");
			return;
			
		}
		
		/**1.need to get the two records from switch condition table of this switch component
		 * 2.create two entries in the dataConsMap for each above two records ********/
		
		
		String winId = "";
		String prevIds = "";//currComp.getNextId().split(",");
		AutoProgramComponents tempComp = null;
		
		
		List<SwitchCondition> conditionLst = switchConditionDao.findByComponentId(switchComponent.getCompId());
		
		//run the DFS algorithm as many number of output paths it has
		for (SwitchCondition switchCondition : conditionLst) {
			
			
			tempComp = progCompMap.get(switchCondition.getModeAttribute());//it gives the next component of this switch to which this pick is giving o/p.
			
			prevIds = givePreviousId(tempComp,switchComponent);//get the exact Win id(along with the pick) of switch in which mode it is giving output(gives the out put line of switch nothing but along with the pick)
			
			if(logger.isDebugEnabled()) logger.debug("the mode of switch ====>"+prevIds);
			
			/**************STEP1:run the DFS algorithm to make an entry in the dataConsMap for this component  **************************/
			DFSForSwitch(switchComponent,prevIds);//Run the DFS algorithm to prepare its input components list
		}
		
		if(logger.isDebugEnabled()) logger.debug("----the map to populate data for this Switch is====>"+dataConsMap);
		
		
	}// prepareSwitchDataCompinputList
	
	public void populateCustomerActivatedOutputData(AutoProgramComponents custActivatedComponent, Set<MailingList> mlList) {
		
		if(custActivatedComponent == null) {
			
			
			if(logger.isErrorEnabled()) logger.error("Error, got the custActivatedComponent as null...");
			return;
			
		}
		String label="";
		AutoProgram tempAutoProgram = custActivatedComponent.getAutoProgram();
		
		
		
		
		Long programId = tempAutoProgram.getProgramId();
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar createdDate = tempAutoProgram.getCreatedDate();
		Date formatDate = createdDate.getTime();
		String dateStr = format.format(formatDate);
		
		
		Calendar cal = Calendar.getInstance();
		Date activityDate = cal.getTime();
		String activitydateStr = format.format(activityDate);
		
		Long compId = custActivatedComponent.getCompId();
		String compWinId = custActivatedComponent.getComponentWinId();
		label = programId+"_"+compId+"_"+System.currentTimeMillis();
		Long supportId = custActivatedComponent.getSupportId();//support id specifies whether we need to consider the activated date as the customer created date or program created date
		String appendQry = "";
		if(supportId != null) {
			
			if(supportId == 1l) {
				appendQry += " AND DATEDIFF(created_date,'"+dateStr+"') >= 0";
			}
			else if(supportId == 0l) {
				appendQry += "";
			}
			
		}//if
		String cidStr = "";
		
		String fetchCidsQry = "SELECT contact_id FROM components_contacts WHERE program_id=" +
							  ""+programId+" AND component_id="+compId+" AND component_win_id='"+compWinId+"'";
		
		
		cidStr = tempComponentsDataDao.getContactIdsStr(fetchCidsQry);
		String appndStr = "";
		if(cidStr.length() >0 ) {
			
			appndStr ="AND contactId NOT IN("+cidStr+")";
			
			
		}//if
		
		for (MailingList mailingList : mlList) {
		
		
			String qryStr	 =	" INSERT IGNORE INTO components_contacts ( " +
								" contact_id, stage, user_id, program_id, " +
								" component_id, component_win_id, activity_date, path, email_id, mobile  ) " +
								" SELECT "+" cid, "+0+","+tempAutoProgram.getUser().getUserId()+", "+
								programId+ " , " + 
								compId +	" , '" +
								compWinId +"' , '" +activitydateStr+"', "+"'," +compWinId +"',"+
								"email_id, "+" phone "+
								" From contacts where list_id = "+mailingList.getListId()+appendQry;
			
			  componentsAndContactsDaoForDML.executeJdbcQuery(qryStr);
			  
			if(logger.isDebugEnabled()) logger.debug("************after inserting into components_contacts*********"+qryStr);
			//if(logger.isDebugEnabled()) logger.debug("Inserted  count: "+count);
		
		} // for each mailing list
		

		String qryStr	 =	" INSERT IGNORE INTO temp_components_data ( " +
							" program_id, component_id, label, contact_id, stage, component_win_id) " +
							" SELECT "+
							programId+ " , " + 
							compId +	" , '" +
							label +"' , " +
							" contact_id, " +
							custActivatedComponent.getStage() + " , '" +
							compWinId +"' " +
							" FROM components_contacts WHERE program_id="+programId+" AND component_id=" +
							""+compId+" AND component_win_id='"+compWinId+"'";
		
		  tempComponentsDataDaoForDML.executeJdbcQuery(qryStr);
		  
		if(logger.isDebugEnabled()) logger.debug("************after inserting into temp_components_data*********"+qryStr);
		
		String qryStrForChild = "FROM ComponentsAndContacts Where programId="+programId+"" +
								" AND componentId="+compId+" AND componentWinId='"+compWinId+"'"+appndStr;
		
		List<ProgramOnlineReports> prgOnlineRepToBeSaved = new ArrayList<ProgramOnlineReports>();
		List<ComponentsAndContacts> retCompAndConList = componentsAndContactsDao.getListByQry(qryStrForChild);
		if(retCompAndConList != null && retCompAndConList.size() > 0) {
			
			for (ComponentsAndContacts tempComponentsAndContacts : retCompAndConList) {
				
				ProgramOnlineReports proOnlineRep = new ProgramOnlineReports(tempComponentsAndContacts, 
						compWinId, activityDate, programId, custActivatedComponent.getCompId(), tempComponentsAndContacts.getContactId());

				prgOnlineRepToBeSaved.add(proOnlineRep);
				
			}//for
			
			//programOnlineReportsDao.saveByCollection(prgOnlineRepToBeSaved);
			programOnlineReportsDaoForDML.saveByCollection(prgOnlineRepToBeSaved);

			if(logger.isDebugEnabled()) logger.debug("************after inserting into program_online_reports********"+qryStrForChild);
		}//if
		
		componentLabelMap.put(compWinId+"0", label);
		
	}//populateCustomerActivatedOutputData
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This method populates the output data for customerActivated(CA) event type component as specified below.</BR></BR>
	 * 
	 * STEP1:get the contacts from mailing list insert into temp table.</BR>
	 * STEP2:delete the contacts from temp table which are already exists in components_contacts.this step lets</BR> 
	 * 		 the data for this CA component is only of newly added/activated contacts.</BR>
	 * STEP3:try to insert all these new contacts related data into the components_contacts table including this</BR> 
	 * 		 components in the 'path'.(this step avoids the program running without performing any activity if the </BR>
	 * 		 program design is: CA--->ET---->ET----).</BR>
	 * 
	 * @param currComp is the customerActivated type component</BR>
	 * @param mlList is set of mailing list(s) associated with this running program</BR>
	 */
	public void populateCustActivatedOutputData(AutoProgramComponents custActivatedComponent, Set<MailingList> mlList) {
		
		/*************STEP1:get the contacts from mailinglist insert into temp table*************/
		
		if(custActivatedComponent == null) {
			
			
			if(logger.isErrorEnabled()) logger.error("Error, got the custActivatedComponent as null...");
			return;
			
		}
		String label="";
		AutoProgram tempAutoProgram = custActivatedComponent.getAutoProgram();
		
		String ignoreSubQry = "";
		String ignoreSubQryInsertion = "";
		/*byte ignoreFlagVal = tempAutoProgram.getIgnoreFlag();
		
		if(ignoreFlagVal == 0){
			
			//means i shud continue for what i am doing now
			ignoreSubQry = "";
			
		}
		else if(ignoreFlagVal > 0 ) {
			
			if(ignoreFlagVal == 1){
				//means i shud ignore only email
				
				ignoreSubQry = " AND contact_id not in(SELECT cid FROM contacts WHERE )"
				
			}else if(ignoreFlagVal == 2) {
				//means i shud ignore only mobile
			}else if(ignoreFlagVal == 3) {
				//means i shud ignore both email and mobile
			}
			
			
			
		}*/
		
		Long programId = tempAutoProgram.getProgramId();
		
		Calendar createdDate = tempAutoProgram.getCreatedDate();
		Date formatDate = createdDate.getTime();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = format.format(formatDate);
		
		Long compId = custActivatedComponent.getCompId();
		String compWinId = custActivatedComponent.getComponentWinId();
		label = programId+"_"+compId+"_"+System.currentTimeMillis();
		Long supportId = custActivatedComponent.getSupportId();//support id specifies whether we need to consider the activated date as the customer created date or program created date
		String appendQry = "";
		if(supportId != null) {
			
			if(supportId == 1l) {
				appendQry += " AND DATEDIFF(created_date,'"+dateStr+"') >= 0";
			}
			else if(supportId == 0l) {
				appendQry += "";
			}
			
		}//if
		
		
		
		for (MailingList mailingList : mlList) {
		
		
			String qryStr	 =	" INSERT IGNORE INTO temp_components_data ( " +
								" program_id, component_id, label, contact_id, stage, component_win_id) " +
								" SELECT "+
								programId+ " , " + 
								compId +	" , '" +
								label +"' , " +
								" cid, " +
								custActivatedComponent.getStage() + " , '" +
								compWinId +"' " +
								" from contacts where list_id = "+mailingList.getListId()+appendQry;
			
			  tempComponentsDataDaoForDML.executeJdbcQuery(qryStr);
			  
			if(logger.isDebugEnabled()) logger.debug("************after inserting into temp_components_data*********"+qryStr);
			//if(logger.isDebugEnabled()) logger.debug("Inserted  count: "+count);
		
		} // for each mailing list
		
		/*************STEP2:delete the contacts from temptable which are already exists in components_contacts**********/
		
		//delete the existing contacts present in ComponentContacts , from the tempprogram Table.
		
		
			
			int count = componentsAndContactsDao.getCountForProgram(programId);
			
			if(count>0) {
				
				
				String qryStr =	" DELETE FROM temp_components_data WHERE program_id ="+programId+ 
								" AND contact_id IN(SELECT cc.contact_id FROM components_contacts cc WHERE cc.program_id= "+programId+ 
								" AND cc.component_win_id not in('"+compWinId+"'))";
				
				int insertCount = tempComponentsDataDaoForDML.executeJdbcUpdateQuery(qryStr);
				
			
		}//if
		
		
		if(logger.isDebugEnabled()) logger.debug("*************after deleting from temp_components_data***"+compWinId+"0"+"*******"+populated.contains(compWinId+"0"));
		
		/**************STEP3:try to insert all these new contacts related data into the components_contacts*****************/
			Calendar cal = Calendar.getInstance();
			Date activityDate = cal.getTime();
			List<ComponentsAndContacts> newCompsAndContacts = new ArrayList<ComponentsAndContacts>();
			String selectQry = "SELECT contact_id FROM components_contacts WHERE program_id="+programId+" AND component_id="+compId;
			
			String ccCids = tempComponentsDataDao.getContactIdsStr(selectQry);
			
			String qryStr;
			if(ccCids.length() > 0) {
				
				qryStr = "SELECT contact_id FROM temp_components_data WHERE component_id="+compId+" AND program_id="+programId+" AND " +
						"contact_id NOT IN("+ccCids+")";
				
				
				
			}else{
				
				qryStr = "SELECT contact_id FROM temp_components_data WHERE component_id="+compId+" AND program_id="+programId;
				
				
			}
			
			
			List<Long> contactIds = tempComponentsDataDao.getContactIdList(qryStr);
			String cidStr = tempComponentsDataDao.getContactIdsStr(qryStr);
			
			/*qryStr = "DELETE FROM components_contacts where program_id="+programId+" AND component_id="+compId+" AND contact_id in("+cidStr+")";
			*/
			if(contactIds != null && contactIds.size() > 0) {
				
				componentsAndContactsDaoForDML.executeJdbcUpdateQuery(qryStr);
				
				 for (Long contactId : contactIds) {
					
					 
					 ComponentsAndContacts compAndContact = new ComponentsAndContacts(programId, tempAutoProgram.getUser().getUserId(), contactId, 
							 									compId, 0, compWinId, ","+compWinId, cal); 
					 newCompsAndContacts.add(compAndContact);
					 
					 
				}//for
				
				//componentsAndContactsDao.saveByCollection(newCompsAndContacts);
				componentsAndContactsDaoForDML.saveByCollection(newCompsAndContacts);

				List<ProgramOnlineReports> prgOnlineRepToBeSaved = new ArrayList<ProgramOnlineReports>();
				List<ComponentsAndContacts> tempList = componentsAndContactsDao.getByContactIds(cidStr, programId);
				if(tempList != null && tempList.size() > 0) {
				
					for (ComponentsAndContacts tempComponentsAndContacts : tempList) {
						
						ProgramOnlineReports proOnlineRep = new ProgramOnlineReports(tempComponentsAndContacts, 
																compWinId, activityDate, programId, custActivatedComponent.getCompId(), tempComponentsAndContacts.getContactId());
						
						prgOnlineRepToBeSaved.add(proOnlineRep);
						
						
					}//for
					//programOnlineReportsDao.saveByCollection(prgOnlineRepToBeSaved);
					programOnlineReportsDaoForDML.saveByCollection(prgOnlineRepToBeSaved);

					
				}//if
				
			}//if
		
		componentLabelMap.put(compWinId+"0", label);
		
		if(logger.isDebugEnabled()) logger.debug("*************after deleting from temp_components_data***"+compWinId+"0"+"*******"+populated.contains(compWinId+"0"));
		
	}//populateCustActivatedOutputData
	
	
	
	
	/**
	 * This method populates the data for the target timer
	 * @param targetTimerWinId
	 */
	public void populateTargetTimerOutputData(String targetTimerWinId) {
		
		Set<String> tempDatagivingComponents = dataConsMap.get(targetTimerWinId);//need to handle NullPointerException--->require?
		
		if(tempDatagivingComponents == null) {
			
			if(logger.isDebugEnabled()) logger.debug("no input Comnponents found,returning----");
			return;
			
		}
		
		AutoProgramComponents targetTimerComponent = progCompMap.get(targetTimerWinId);
		AutoProgram autoProgram = targetTimerComponent.getAutoProgram();
		
		
		String label = autoProgram.getProgramId()+"_"+targetTimerComponent.getCompId()+"_"+System.currentTimeMillis();//label for this component
		
		String query = "";
		String subQuery = "";//used to avoid the empty data problems if it returns no data
		String cid = "";//holds the subquery returned result
		String compType = null;
		int count = 0;
		AutoProgramComponents tempComponent = null;
		
		//String compWinId = "";// just to make logger friendly(it holds the prevoius component's window id along with the pick)
		for (String tempComponentId : tempDatagivingComponents) {
			
			tempComponent = progCompMap.get(tempComponentId);
			compType = tempComponent.getCompType();
			/** * CASE#3:if immediate previous is a CustomerActivated 	  : need to fetch the data from the mailing list itself**/
			
			

			/**************** CASE:1&2&4 ********************************/
			
			
			 if(compType.equals(ProgramEnum.ACTIVITY_SEND_EMAIL.name()) ||
					compType.equals(ProgramEnum.ACTIVITY_SEND_SMS.name()) ||
					compType.equals(ProgramEnum.EVENT_CUST_ACTIVATED.name()) ||
					compType.equals(ProgramEnum.EVENT_ELAPSE_TIMER.name()) ||
					compType.equals(ProgramEnum.EVENT_TARGET_TIMER.name())) { // if previous is Activity
				
				//compWinId = tempComponent.getComponentWinId()+"0";
				 
					
				query = " INSERT IGNORE INTO temp_components_data ( " +
						" program_id, component_id, label, contact_id, stage, component_win_id) " +
						" SELECT "+
						autoProgram.getProgramId()+ " , " + 
						targetTimerComponent.getCompId() +	" , '" +
						label +"' , " +
						" contact_id, " +
						targetTimerComponent.getStage() + " , '" +
						targetTimerComponent.getComponentWinId() +"' " +
						" from  components_contacts where component_id="+tempComponent.getCompId()+" AND program_id="+autoProgram.getProgramId();
				
				
			}// else if 
			
			/**********************CASE:3************************************/
			
			
			else if(compType.equals(ProgramEnum.SWITCH_DATA.name()) || 
					compType.equals(ProgramEnum.SWITCH_ALLOCATION.name())) { // if previous is switch type
				
				
				/*subQuery = "select contact_id from components_contacts WHERE component_id="+tempComponent.getCompId()+
						   " AND component_win_id ='"+tempComponentId+"' AND Program_id="+autoProgram.getProgramId();
	
	
					
				query = " INSERT IGNORE INTO temp_components_data ( " +
						" program_id, component_id, label, contact_id, stage, component_win_id) " +
						" SELECT "+
						autoProgram.getProgramId()+ " , " + 
						targetTimerComponent.getCompId() +	" , '" +
						label +"' , " +
						" contact_id, " +
						targetTimerComponent.getStage() + " , '" +
						targetTimerComponent.getComponentWinId() +"' " +
						" from components_contacts where contact_id in(<CID>) AND program_id="+autoProgram.getProgramId();
						
				*/
				
				query = " INSERT IGNORE INTO temp_components_data ( " +
						" program_id, component_id, label, contact_id, stage, component_win_id) " +
						" SELECT "+
						autoProgram.getProgramId()+ " , " + 
						targetTimerComponent.getCompId() +	" , '" +
						label +"' , " +
						" contact_id, " +
						targetTimerComponent.getStage() + " , '" +
						targetTimerComponent.getComponentWinId() +"' " +
						" FROM components_contacts WHERE program_id="+autoProgram.getProgramId()+
						" AND component_win_id ='"+tempComponentId+"' AND component_id="+tempComponent.getCompId();
				
				
			}// else if
			/************************CASE#4:******************************************/
			
			
			 
			 /***now execute the query which is prepared based on the input component(execute the subquery if it exists)*********/
			 
			/*if(subQuery.trim().length() > 0) {
				
			cid = tempComponentsDataDao.getContactIdsStr(subQuery);
			
			if(cid.length()>0) { // to avoid empty data problems
				
				query = query.replace("<CID>", cid);
				if(logger.isDebugEnabled()) logger.debug("query to be executed for this "+tempComponentId +" is... "+query);
				
				tempComponentsDataDao.executeJdbcQuery(query);
			}//if
			else {
				
				if(logger.isDebugEnabled()) logger.debug("no data found for this component...."+tempComponentId);
			}
			}else {*/
				if(logger.isDebugEnabled()) logger.debug("the query to be executed is===>"+query);
				tempComponentsDataDaoForDML.executeJdbcQuery(query);
				
				
			//}
			
		}//for
		
		/***update the components_contacts table(for path,timestamp,activitydate)***************/
		/***make an entry in program_online_reports table for each record of components_contacts which are updated******************************/
		
		
		Calendar cal = Calendar.getInstance();
		
		
		String qry = "SELECT contact_id FROM temp_components_data WHERE component_id="
								 +targetTimerComponent.getCompId()+" AND program_id="+autoProgram.getProgramId();
		
		String cidStr = tempComponentsDataDao.getContactIdsStr(qry);//get the contact_id from temp_coponents_data.
		
		if(cidStr.trim().length() > 0) { //if atleast 1 contact exists update data for this contact_id in components_contacts.
		
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date activityDate = cal.getTime();
			String formatDate = format.format(activityDate);
			
			String updateQuery = "update components_contacts set component_win_id='"+targetTimerComponent.getComponentWinId()+"',component_id="+targetTimerComponent.getCompId() +
								 ",activity_date='"+formatDate+"' ,path=concat(path,'," +targetTimerComponent.getComponentWinId() +"')"+
								 " WHERE contact_id in("+cidStr+") AND program_id="+autoProgram.getProgramId() ;
					
			if(logger.isDebugEnabled()) logger.debug("the query to be executed to reach the end status is===>"+updateQuery);
	
			int updateCount = componentsAndContactsDaoForDML.executeJdbcUpdateQuery(updateQuery);
			
			if(logger.isDebugEnabled()) logger.debug("the number of contacts updated are===>"+updateCount);
			
			List<ProgramOnlineReports> prgOnlineRepToBeSaved = new ArrayList<ProgramOnlineReports>();
			
			//get those componentsAndContacts(CC) objects which are updated earlier.
			List<ComponentsAndContacts> tempList = componentsAndContactsDao.getByContactIds(cidStr, autoProgram.getProgramId());
			
			
			
			
			if(tempList != null && tempList.size() > 0 ) {
				
				for (ComponentsAndContacts tempComponentsAndContacts : tempList) { //for each CC object create new entry in program_online_reports table.
					
					ProgramOnlineReports proOnlineRep = new ProgramOnlineReports(tempComponentsAndContacts, targetTimerComponent.getComponentWinId(),
															activityDate, autoProgram.getProgramId(), targetTimerComponent.getCompId(), tempComponentsAndContacts.getContactId());
					
					prgOnlineRepToBeSaved.add(proOnlineRep);
					
					
				}//for
				
				//programOnlineReportsDao.saveByCollection(prgOnlineRepToBeSaved);
				programOnlineReportsDaoForDML.saveByCollection(prgOnlineRepToBeSaved);

				
			}
			

		}//if
		
		componentLabelMap.put(targetTimerWinId, label);
		
		
	}//populateTargetTimerOutputData
	
	/**
	 * This method populates the data for the elapsed timer component need to consider various cases.</BR>
	 * CASE#1:if immediate previous is a CustomerActivated 	  : need to consider data from the components_contacts.</BR>
	 * CASE#2:if immediate previous is a an activity 	   	  : need to consider data from the components_contacts.</BR>
	 * CASE#3:if immediate previous is a switch 		   	  : need to consider data from Components_contacts for this window_id(it means a particular pick)</BR>
	 * CASE#4:if immediate previous is a elapsed timer itself : need to consider data from components_contacts.</BR></BR>
	 * 
	 * @param currCompId specify the Elapsed timer window id (along with its pick).</BR>
	 */
	
	public void populateElapsedTimerOutputData(String elapsedTimerWinId) {
		
		try {
			if(logger.isDebugEnabled()) logger.debug("----just entered for Elapsed timer "+elapsedTimerWinId+" output data-----");
			
			Set<String> tempDatagivingComponents = dataConsMap.get(elapsedTimerWinId);//need to handle NullPointerException--->require?
			
			if(tempDatagivingComponents == null) {
				
				if(logger.isDebugEnabled()) logger.debug("no input Comnponents found,returning----");
				return;
				
			}
			
			
			AutoProgramComponents currComp = progCompMap.get(elapsedTimerWinId);
			AutoProgram autoProgram = currComp.getAutoProgram();
			
			
			String label = autoProgram.getProgramId()+"_"+currComp.getCompId()+"_"+System.currentTimeMillis();//label for this component
			
			
			
			
			long offset = currComp.getSupportId().longValue();//convert days into time
			String timeDiff = "";
			if(offset >= 1440) {
				
				offset = offset/1440;
				timeDiff = " AND DATEDIFF(now(),activity_date) >= "+offset;
				
				
			}else if(offset >= 0 && offset < 1440) {
				offset = offset/60;
				timeDiff = " AND now() > activity_date AND HOUR( TIMEDIFF(now(),activity_date) ) >="+offset;
				
			}
			
			
			
			
			String query = "";
			String subQuery = "";//used to avoid the empty data problems if it returns no data
			String cid = "";//holds the subquery returned result
			String compType = null;
			int count = 0;
			AutoProgramComponents tempComponent = null;
			
			//String compWinId = "";// just to make logger friendly(it holds the prevoius component's window id along with the pick)
			for (String tempComponentId : tempDatagivingComponents) {
				
				tempComponent = progCompMap.get(tempComponentId);
				
				compType = tempComponent.getCompType();
				
				/**************** CASE:1&2&4 ********************************/
				
				
				 if(compType.equals(ProgramEnum.ACTIVITY_SEND_EMAIL.name()) ||
						compType.equals(ProgramEnum.ACTIVITY_SEND_SMS.name()) ||
						compType.equals(ProgramEnum.EVENT_CUST_ACTIVATED.name()) ||
						compType.equals(ProgramEnum.EVENT_ELAPSE_TIMER.name()) ||
						compType.equals(ProgramEnum.EVENT_TARGET_TIMER.name())) { // if previous is Activity
					
					//compWinId = tempComponent.getComponentWinId()+"0";
					 
						
					query = " INSERT IGNORE INTO temp_components_data ( " +
							" program_id, component_id, label, contact_id, stage, component_win_id) " +
							" SELECT "+
							autoProgram.getProgramId()+ " , " + 
							currComp.getCompId() +	" , '" +
							label +"' , " +
							" contact_id, " +
							currComp.getStage() + " , '" +
							currComp.getComponentWinId() +"' " +
							" from  components_contacts where component_id="+tempComponent.getCompId()+" AND program_id="+autoProgram.getProgramId()+
							" "+timeDiff;
					
					
				}// else if 
				
				/**********************CASE:3************************************/
				
				
				else if(compType.equals(ProgramEnum.SWITCH_DATA.name()) || 
						compType.equals(ProgramEnum.SWITCH_ALLOCATION.name())) { // if previous is switch type
					
					//no need of subquery we can directly fetch the related data from components and contacts
					/*subQuery = "select contact_id from components_contacts WHERE component_id="+tempComponent.getCompId()+
							   " AND component_win_id ='"+tempComponentId+"' AND Program_id="+autoProgram.getProgramId();
		
		
						
					query = " INSERT IGNORE INTO temp_components_data ( " +
							" program_id, component_id, label, contact_id, stage, component_win_id) " +
							" SELECT "+
							currComp.getAutoProgram().getProgramId()+ " , " + 
							currComp.getCompId() +	" , '" +
							label +"' , " +
							" contact_id, " +
							currComp.getStage() + " , '" +
							currComp.getComponentWinId() +"' " +
							" from components_contacts where contact_id in(<CID>) AND program_id="+autoProgram.getProgramId()+
							" "+timeDiff;*/
					
					query = " INSERT IGNORE INTO temp_components_data ( " +
							" program_id, component_id, label, contact_id, stage, component_win_id) " +
							" SELECT "+
							currComp.getAutoProgram().getProgramId()+ " , " + 
							currComp.getCompId() +	" , '" +
							label +"' , " +
							" contact_id, " +
							currComp.getStage() + " , '" +
							currComp.getComponentWinId() +"' " +
							" FROM components_contacts WHERE program_id="+autoProgram.getProgramId()+"" +
							" AND component_win_id ='"+tempComponentId+"' AND component_id="+tempComponent.getCompId();
					//tempComponentsDataDao.executeJdbcQuery(query);
					
					//if(logger.isDebugEnabled()) logger.debug("inserted count"+count);
					
					
					
				}// else if
			
				 
				 /***now execute the query which is prepared based on the input component(execute the subquery if it exists)*********/
				 
				/*if(subQuery.trim().length() > 0) {
					
				cid = tempComponentsDataDao.getContactIdsStr(subQuery);
				
				if(cid.length()>0) { // to avoid empty data problems
					
					query = query.replace("<CID>", cid);
					logger.debug("query to be executed for this "+tempComponentId +" is... "+query);
					
					tempComponentsDataDao.executeJdbcQuery(query);
				}//if
				else {
					
					logger.debug("no data found for this component...."+tempComponentId);
				}
				}else {*/
					if(logger.isDebugEnabled()) logger.debug("the query to be executed is===>"+query);
					tempComponentsDataDaoForDML.executeJdbcQuery(query);
					
					
				//}
				
			}//for
			
			/***update the components_contacts table(for path,timestamp,activitydate)***************/
			/***make an entry in program_online_reports table for each record of components_contacts which are updated******************************/
			
			
			Calendar cal = Calendar.getInstance();
			
			
			String qry = "SELECT contact_id FROM temp_components_data WHERE component_id="
						 +currComp.getCompId()+" AND program_id="+autoProgram.getProgramId();
			
			String cidStr = tempComponentsDataDao.getContactIdsStr(qry);//get the contact_id from temp_coponents_data.
			
			if(cidStr.trim().length() > 0) { //if atleast 1 contact exists update data for this contact_id in components_contacts.
			
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date activityDate = cal.getTime();
				String formatDate = format.format(activityDate);
				
				String updateQuery = "update components_contacts set component_win_id='"+currComp.getComponentWinId()+"',component_id="+currComp.getCompId() +
									 ",activity_date='"+formatDate+"' ,path=concat(path,'," +currComp.getComponentWinId() +"')"+
									 " WHERE contact_id in("+cidStr+") AND program_id="+autoProgram.getProgramId() ;
						
				if(logger.isDebugEnabled()) logger.debug("the query to be executed to reach the end status is===>"+updateQuery);
		
				int updateCount = componentsAndContactsDaoForDML.executeJdbcUpdateQuery(updateQuery);
				
				if(logger.isDebugEnabled()) logger.debug("the number of contacts updated are===>"+updateCount);
				
				List<ProgramOnlineReports> prgOnlineRepToBeSaved = new ArrayList<ProgramOnlineReports>();
				
				//get those componentsAndContacts(CC) objects which are updated earlier.
				List<ComponentsAndContacts> tempList = componentsAndContactsDao.getByContactIds(cidStr, autoProgram.getProgramId());
				
				
				
				
				if(tempList != null && tempList.size() > 0 ) {
					
					for (ComponentsAndContacts tempComponentsAndContacts : tempList) { //for each CC object create new entry in program_online_reports table.
						
						ProgramOnlineReports proOnlineRep = new ProgramOnlineReports(tempComponentsAndContacts, 
															currComp.getComponentWinId(), activityDate, autoProgram.getProgramId(),
															currComp.getCompId(), tempComponentsAndContacts.getContactId());
						
						prgOnlineRepToBeSaved.add(proOnlineRep);
						
						
					}//for
					
					//programOnlineReportsDao.saveByCollection(prgOnlineRepToBeSaved);
					programOnlineReportsDaoForDML.saveByCollection(prgOnlineRepToBeSaved);

					
				}//if
				

			}//if
			
			componentLabelMap.put(elapsedTimerWinId, label);
			
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
	
		
	}//populateElapsedTimerOutputData
	
	
	public String prepareCampWinIdsForSQL(String campWinIds) {
		
		String retStr = "";
		String[] strToken = null; 
		
		if(campWinIds.contains(",")) {
			
			strToken = campWinIds.split(",");
			for (String token : strToken) {
				
				if(retStr.length()>0) retStr += ",";
				retStr += "'"+token+"'";
				
				
			}//for
			
		}//if	
		else {
			
			retStr = "'"+campWinIds+"'";
		}
		
		return retStr;
		
		
	}//prepareCampWinIdsForSQL
	
	boolean isOtherPick = false;
	private boolean isFirstCall = true;
	private Map<String,String> componentLabelMap = new HashMap<String, String>();
	private String compWinId = "";
	public void populateSwitchAllocationComponentOutputData(String componentPortId) {
		
		if(logger.isDebugEnabled()) logger.debug("----just entered for switch Allocation output data-----"+componentPortId);
		
		Set<String> tempDatagivingComponents = dataConsMap.get(componentPortId);
		if(tempDatagivingComponents == null) {
			
			if(logger.isDebugEnabled()) logger.debug("no input component found,returning-----");
			return;
		}//if
		
		//try to get the switch allocation component from map
		AutoProgramComponents currComp = progCompMap.get(componentPortId);
		AutoProgram autoProgram = currComp.getAutoProgram();
		String currCompWinId = currComp.getComponentWinId();
		
		
		
		Calendar consDate = null;
		String compType = null;
		AutoProgramComponents tempComponent = null;
		String query = null;
		AutoProgramComponents nextComp = null;
		String preIdfromNextComp = "";
		
			
		for (String tempCompId : tempDatagivingComponents) {
			
			tempComponent = progCompMap.get(tempCompId);
			compType = tempComponent.getCompType();
			
			if(compType.equals(ProgramEnum.EVENT_CUST_ACTIVATED.name()) ||
				compType.equals(ProgramEnum.ACTIVITY_SEND_EMAIL.name()) ||
				compType.equals(ProgramEnum.ACTIVITY_SEND_SMS.name()) 	||
				compType.equals(ProgramEnum.EVENT_ELAPSE_TIMER.name()) 	||
				compType.equals(ProgramEnum.EVENT_TARGET_TIMER.name()) ) {
			
				//those many number of contacts i need to get from the components_contacts
				
				
	
				query = " INSERT IGNORE INTO temp_components_data ( " +
						" program_id, component_id, label, contact_id, stage, component_win_id) " +
						" SELECT "+
						autoProgram.getProgramId()+ " , " + 
						currComp.getCompId() +	" , '" +
						"" +"' , " +
						" contact_id, " +
						currComp.getStage() + " , '" +
						currCompWinId +"'"+ 
						" from components_contacts WHERE component_id="+tempComponent.getCompId()+
						" AND Program_id="+autoProgram.getProgramId();
				
			}//if
			else if(compType.equalsIgnoreCase(ProgramEnum.SWITCH_DATA.name()) ||
					compType.equalsIgnoreCase(ProgramEnum.SWITCH_ALLOCATION.name())) {
				//need to get the data from components_contacts but consider the mode_attribute too..
				
				
				query = " INSERT IGNORE INTO temp_components_data ( " +
						" program_id, component_id, label, contact_id, stage, component_win_id) " +
						" SELECT "+
						autoProgram.getProgramId()+ " , " + 
						currComp.getCompId() +	" , '" +
						"" +"' , " +
						" contact_id, " +
						currComp.getStage() + " , '" +
						currCompWinId +"'"+
						" from components_contacts WHERE component_win_id='"+tempCompId+"' AND component_id="+tempComponent.getCompId()+
						" AND Program_id="+autoProgram.getProgramId();
				
			}//else if
			if(logger.isDebugEnabled()) logger.debug("the query to populate data is for "+componentPortId+" is======>"+query);
			tempComponentsDataDaoForDML.executeJdbcQuery(query);//by this data will be populated into temp table

		}//for
			
			
		
		List<SwitchCondition> conditionLst = switchConditionDao.findByComponentId(currComp.getCompId());
		for (SwitchCondition switchCondition : conditionLst) {
			
			String tempStr = switchCondition.getCondition();
			if(tempStr.contains(Constants.DELIMETER_DOUBLECOLON)) {
				
				//this means consider contacts flow as 'overall' and get the date criteria
				
				String dateCriteria = tempStr.split(Constants.DELIMETER_DOUBLECOLON)[1];
				if(dateCriteria.equalsIgnoreCase(Constants.AUTO_PROGRAM_EMAILREP_FROM_CREATED)) {
					if(logger.isDebugEnabled()) logger.debug("for======>"+Constants.AUTO_PROGRAM_EMAILREP_FROM_CREATED);
					consDate = autoProgram.getCreatedDate();
					
					
				}else if(dateCriteria.equalsIgnoreCase(Constants.AUTO_PROGRAM_EMAILREP_FROM_MODIFIED)) {
					if(logger.isDebugEnabled()) logger.debug("for=======>"+Constants.AUTO_PROGRAM_EMAILREP_FROM_MODIFIED);
					consDate = autoProgram.getModifiedDate();
					
				}// else if
				
			}//if
			
			String nextCompId = switchCondition.getModeAttribute();
			nextComp = progCompMap.get(nextCompId);
			
			
			preIdfromNextComp = nextComp.getPreviousId();//like 'SWITCH_DATA-11w1,EVENT_ELAPSE_TIMER-11w0' from this we need to get 'SWITCH_DATA-11w1'
			//logger.debug("=================preIdfromNextComp before======="+preIdfromNextComp);
			//get the mode of switch in which mode it is giving output
			preIdfromNextComp = preIdfromNextComp.substring(preIdfromNextComp.indexOf(currCompWinId), 
					(preIdfromNextComp.indexOf(currCompWinId))+currCompWinId.length()+1);
			
			long numOfContacts = tempComponentsDataDao.getCountToCalPercentage(currComp); 
			//total = numOfContacts;
			
			if(logger.isDebugEnabled()) logger.debug("total number of contacts in temp table are======>"+numOfContacts);
			//need to consider only when the user selected the overall option
			if(preIdfromNextComp.equals(componentPortId)) {
				
				//need to prepare label and need to put in a map rather than saving in the component in DB
				String label= currComp.getAutoProgram().getProgramId()+"_"+currComp.getCompId()+"_"+System.currentTimeMillis()+"-"+componentPortId;
				
				componentLabelMap.put(componentPortId, label);
				int modePercentage = Integer.parseInt(switchCondition.getModeFlag());
				if(logger.isDebugEnabled()) logger.debug("the mode percentage is====>"+modePercentage);
				
				if(consDate != null) {
				
					if(isOtherPick && (compWinId != null && compWinId.equals(currCompWinId)) ) {//if we need to send through the other pick no need to do anything,thats y break...
						
						if(logger.isDebugEnabled()) logger.debug("isOtherPick========>"+isOtherPick);
						isOtherPick = false;
						compWinId = "";
						String	updateQry = "UPDATE temp_components_data SET mode_attribute='"+componentPortId+"',label='"
											+label+"' where component_id="+currComp.getCompId()+" AND mode_attribute Is NUll";
						
						if(logger.isDebugEnabled()) logger.debug("the update Query is========>"+updateQry);
						tempComponentsDataDaoForDML.executeJdbcUpdateQuery(updateQry);
						break;
					}//if
					
					
					
					numOfContacts += programOnlineReportsDao.getCountToCalPercentage(currComp, consDate);
					
					if(logger.isDebugEnabled()) logger.debug("1111111after getting from child table the total number of contcats are=====>"+numOfContacts);
					//this i need to consider only for one pick
					
					long numOfContToBeSent = Utility.calCountOfContacts(numOfContacts,(modePercentage));
					
					if(logger.isDebugEnabled()) logger.debug("2222222after getting from utility total number of contcats are=====>"+numOfContToBeSent);
					
					long numOfContactsForthisPick = programOnlineReportsDao.getCountToCalPercentage(currComp,consDate,componentPortId);
					
					if(logger.isDebugEnabled()) logger.debug("3333333after getting from child table total number of contcats are=====>"+numOfContactsForthisPick);
					
					long resultCount = numOfContToBeSent-numOfContactsForthisPick;
					
					if(logger.isDebugEnabled()) logger.debug("444444 after subtracting total number of contcats are=====>"+resultCount);
					
					if(resultCount <= 0) {
						//TODO need to send all these contacts along with the other pick
						
						//sendFromOtherPick = true;
						isOtherPick = true;
						compWinId = currCompWinId;
						return;
						
						
					}//if
					//TODO need to calculate as per the over all option
					//get the contact count individually for each pick(like if there are 100 previous contacts then devide the count as per the each pick )
					
					//logger.debug("is sendFromOtherPick=======>"+sendFromOtherPick);
					
					if(!isOtherPick && (compWinId.trim().equals(""))) {
						
						String	updateQry = "UPDATE temp_components_data SET mode_attribute='"+componentPortId+"',label='"
											+label+"' WHERE component_id="+currComp.getCompId()+" AND mode_attribute Is NUll LIMIT "+resultCount;
						
						if(logger.isDebugEnabled()) logger.debug("the query to be executed is=======>"+updateQry);
						
						tempComponentsDataDaoForDML.executeJdbcUpdateQuery(updateQry);
					}//if
				
				}//if
				else {
					
					if(logger.isDebugEnabled()) logger.debug("=============is each Run================");
					long numOfContToBeSent = Utility.calCountOfContacts(numOfContacts,(modePercentage));
					
					String	updateQry = "UPDATE temp_components_data SET mode_attribute='"+componentPortId+"',label='"
										+label+"' WHERE component_id="+currComp.getCompId()+" AND mode_attribute Is NUll LIMIT "+numOfContToBeSent;
					
					tempComponentsDataDaoForDML.executeJdbcUpdateQuery(updateQry);
					
				}//else 
				
			}//if
			
		}//for
		
		
		//*****************************************************************************
		Calendar cal = Calendar.getInstance();
		
		String qry = " SELECT contact_id FROM temp_components_data WHERE component_id="
		 			 +currComp.getCompId()+" AND program_id="+autoProgram.getProgramId()+" AND mode_attribute='"+componentPortId+"'";

		String cidStr = tempComponentsDataDao.getContactIdsStr(qry);//get the contact_id from temp_coponents_data.
		
		if(cidStr.trim().length() > 0) {//if atleast 1 contact exists update data for this contact_id in components_contacts.
		
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date activityDate = cal.getTime();
			String formatDate = format.format(activityDate);
			
			String updateQuery = "update components_contacts set component_win_id='"+componentPortId+"',component_id="+currComp.getCompId() +
								 ",activity_date='"+formatDate+"' ,path=concat(path,'," +componentPortId +"')"+
								 " WHERE contact_id in("+cidStr+") AND program_id="+autoProgram.getProgramId() ;
				
			if(logger.isDebugEnabled()) logger.debug("the query to be executed to upate is===>"+updateQuery);
			
			int updateCount = componentsAndContactsDaoForDML.executeJdbcUpdateQuery(updateQuery);
			
			if(logger.isDebugEnabled()) logger.debug("the number of contacts updated are===>"+updateCount);
			
			List<ProgramOnlineReports> prgOnlineRepToBeSaved = new ArrayList<ProgramOnlineReports>();
			
			//get those componentsAndContacts(CC) objects which are updated earlier.
			List<ComponentsAndContacts> tempList = componentsAndContactsDao.getByContactIds(cidStr, autoProgram.getProgramId());
			
			if(tempList != null && tempList.size() > 0) {
				
				for (ComponentsAndContacts tempComponentsAndContacts : tempList) { //for each CC object create new entry in program_online_reports table.
					
					ProgramOnlineReports proOnlineRep = new ProgramOnlineReports(tempComponentsAndContacts, componentPortId, 
														activityDate, autoProgram.getProgramId(), currComp.getCompId(),
														tempComponentsAndContacts.getContactId());
					
					prgOnlineRepToBeSaved.add(proOnlineRep);
					
					
				}//for
			
				//programOnlineReportsDao.saveByCollection(prgOnlineRepToBeSaved);
				programOnlineReportsDaoForDML.saveByCollection(prgOnlineRepToBeSaved);

		
			}//if
			
		}//if
		
		//******************************************************************************
		
		
	}//populateSwitchAllocationComponentOutputData() 
	
	
	
	//private long min = 0;
	//private int modePercentage = 0;//conform only after review
	//private long contCount = 0l;
	//boolean sendFromOtherPick = false;
	//String otherPickWinId = null;
	//make use only either one among these two?????????????
	//long total = 0;
	//boolean isOneTime = false;
	/**
	 * mode consists the percentage and need to calculate every time the flow of contacts
	 * @param componentPortId
	 */
	/*public void populateSwitchAllocationCompOutputData(String componentPortId) {
		
		try {
			//TODO need to 
			logger.debug("----just entered for switch Allocation output data-----"+componentPortId);
			
			//try to get the switch allocation component from map
			AutoProgramComponents currComp = progCompMap.get(componentPortId);
			AutoProgram autoProgram = currComp.getAutoProgram();
			
			String query = null;
			String preIdfromNextComp = "";
			AutoProgramComponents tempComponent = null;
			AutoProgramComponents nextComp = null;
			//String compType = null;
			Calendar consDate = null;
			String label = "";
			
			String currCompWinId = currComp.getComponentWinId();
			//***********************************
			//it will run two times for me,?????????????????????//
			String compType = null;
			Set<String> tempDatagivingComponents = dataConsMap.get(componentPortId);
			
			if(tempDatagivingComponents == null) {
				
				logger.debug("no input component found,returning-----");
				return;
			}
			//i need to set the composite primary key,here it leads a bug if some other allocation component exist in the program not working??????????/
			
				for (String tempCompId : tempDatagivingComponents) {
					
					tempComponent = progCompMap.get(tempCompId);
					compType = tempComponent.getCompType();
					
					if(compType.equalsIgnoreCase(ProgramEnum.EVENT_CUST_ACTIVATED.name()) ||
							compType.equalsIgnoreCase(ProgramEnum.ACTIVITY_SEND_EMAIL.name()) ||
							compType.equalsIgnoreCase(ProgramEnum.ACTIVITY_SEND_SMS.name()) ||
							compType.equalsIgnoreCase(ProgramEnum.EVENT_ELAPSE_TIMER.name()) ) {
						
						//those many number of contacts i need to get from the components_contacts
						
						
			
						query = " INSERT IGNORE INTO temp_components_data ( " +
								" program_id, component_id, label, contact_id, stage, component_win_id) " +
								" SELECT "+
								autoProgram.getProgramId()+ " , " + 
								currComp.getCompId() +	" , '" +
								"" +"' , " +
								" contact_id, " +
								currComp.getStage() + " , '" +
								currCompWinId +"'"+ 
								" from components_contacts WHERE component_id="+tempComponent.getCompId()+
								" AND Program_id="+autoProgram.getProgramId();
			
						
						
						
					}//if
					else if(compType.equalsIgnoreCase(ProgramEnum.SWITCH_DATA.name()) ||
							compType.equalsIgnoreCase(ProgramEnum.SWITCH_ALLOCATION.name())) {
						//need to get the data from components_contacts but consider the mode_attribute too..
						
						
						query = " INSERT IGNORE INTO temp_components_data ( " +
								" program_id, component_id, label, contact_id, stage, component_win_id) " +
								" SELECT "+
								autoProgram.getProgramId()+ " , " + 
								currComp.getCompId() +	" , '" +
								"" +"' , " +
								" contact_id, " +
								currComp.getStage() + " , '" +
								currCompWinId +"'"+
								" from components_contacts WHERE component_win_id='"+tempCompId+"' AND component_id="+tempComponent.getCompId()+
								" AND Program_id="+autoProgram.getProgramId();
						
					}//else if
					logger.debug("the query to populate data is for "+componentPortId+" is======>"+query);
					tempComponentsDataDao.executeJdbcQuery(query);//by this data will be populated into temp table
					
				}//for
				
				
			//**************************************
			//to get latest value of minimum primary key
			
			
			//get the related switch condition objects...
			
			List<SwitchCondition> conditionLst = switchConditionDao.findByComponentId(currComp.getCompId());
			//here i should handle,as this is the thing causing all the related bugs......
			for (SwitchCondition switchCondition : conditionLst) {
				
				
					//************************************************
					String tempStr = switchCondition.getCondition();
					if(tempStr.contains(Constants.DELIMETER_DOUBLECOLON)) {
						
						//this means consider contacts flow as 'overall' and get the date criteria
						
						String dateCriteria = tempStr.split(Constants.DELIMETER_DOUBLECOLON)[1];
						if(dateCriteria.equalsIgnoreCase(Constants.AUTO_PROGRAM_EMAILREP_FROM_CREATED)) {
							logger.debug("for======>"+Constants.AUTO_PROGRAM_EMAILREP_FROM_CREATED);
							consDate = autoProgram.getCreatedDate();
							
							
						}else if(dateCriteria.equalsIgnoreCase(Constants.AUTO_PROGRAM_EMAILREP_FROM_MODIFIED)) {
							logger.debug("for=======>"+Constants.AUTO_PROGRAM_EMAILREP_FROM_MODIFIED);
							consDate = autoProgram.getModifiedDate();
							
						}// else if
						
					}//if
				//****************************************************************
					
					
				//get all the input components which are giving data for it..
				String nextCompId = switchCondition.getModeAttribute();
				nextComp = progCompMap.get(nextCompId);
				
				
				preIdfromNextComp = nextComp.getPreviousId();//like 'SWITCH_DATA-11w1,EVENT_ELAPSE_TIMER-11w0' from this we need to get 'SWITCH_DATA-11w1'
				//logger.debug("=================preIdfromNextComp before======="+preIdfromNextComp);
				//get the mode of switch in which mode it is giving output
				preIdfromNextComp = preIdfromNextComp.substring(preIdfromNextComp.indexOf(currCompWinId), 
						(preIdfromNextComp.indexOf(currCompWinId))+currCompWinId.length()+1);
				
				long numOfContacts = tempComponentsDataDao.getCountToCalPercentage(currComp); 
				//total = numOfContacts;
				
				logger.debug("total number of contacts in temp table are======>"+numOfContacts);
				//need to consider only when the user selected the overall option
				
				
				if(preIdfromNextComp.equals(componentPortId)) {//here i need some other var so that i can get exact one appropriate switch obj
					
					logger.debug("preIdfromNextComp========tempId"+preIdfromNextComp+"====="+componentPortId);
					
					label1 = currComp.getAutoProgram().getProgramId()+"_"+currComp.getCompId()+"_"+System.currentTimeMillis()+"-"+componentPortId;
					logger.debug("before in if mode for percentage is=======>"+modePercentage+" is Otherpick===========>"+isOtherPick);
					if(modePercentage == 0) {
						
						modePercentage = Integer.parseInt(switchCondition.getModeFlag());
						
					}
					else if(modePercentage > 0) {
						
						
						modePercentage = 100-modePercentage;//no need to keep % actually need to maintain the count of contacts
					}
						
						//int modePercentage = Integer.parseInt(switchCondition.getModeFlag());
						
					logger.debug("mode for percentage is=======>"+modePercentage);
					
					if(consDate != null) {
						
						if(isOtherPick) {//if we need to send through the other pick no need to do anything,thats y break...
							
							logger.debug("isOtherPick========>"+isOtherPick);
							isOtherPick = false;
							String	updateQry = "UPDATE temp_components_data SET mode_attribute='"+componentPortId+"',label='"
												+label1+"' where component_id="+currComp.getCompId()+" AND mode_attribute Is NUll";
							
							logger.debug("the update Query is========>"+updateQry);
							tempComponentsDataDao.executeJdbcUpdateQuery(updateQry);
							break;
						}//if
						
						
						
						numOfContacts += programOnlineReportsDao.getCountToCalPercentage(currComp, consDate);
						
						logger.debug("1111111after getting from child table the total number of contcats are=====>"+numOfContacts);
						//this i need to consider only for one pick
						
						long numOfContToBeSent = Utility.calCountOfContacts(numOfContacts,(modePercentage));
						
						logger.debug("2222222after getting from utility total number of contcats are=====>"+numOfContToBeSent);
						
						long numOfContactsForthisPick = programOnlineReportsDao.getCountToCalPercentage(currComp,consDate,componentPortId);
						
						logger.debug("3333333after getting from child table total number of contcats are=====>"+numOfContactsForthisPick);
						
						long resultCount = numOfContToBeSent-numOfContactsForthisPick;
						
						logger.debug("444444 after subtracting total number of contcats are=====>"+resultCount);
						
						if(resultCount <= 0) {
							//TODO need to send all these contacts along with the other pick
							
							//sendFromOtherPick = true;
							isOtherPick = true;
							return;
							
							
						}//if
						//TODO need to calculate as per the over all option
						//get the contact count individually for each pick(like if there are 100 previous contacts then devide the count as per the each pick )
						
						//logger.debug("is sendFromOtherPick=======>"+sendFromOtherPick);
						
						if(!isOtherPick) {
							
							String	updateQry = "UPDATE temp_components_data SET mode_attribute='"+componentPortId+"',label='"
												+label1+"' WHERE component_id="+currComp.getCompId()+" AND mode_attribute Is NUll LIMIT "+resultCount;
							
							logger.debug("the query to be executed is=======>"+updateQry);
							
							tempComponentsDataDao.executeJdbcUpdateQuery(updateQry);
						}//if
						
					}//if
					else {
						
						logger.debug("=============is each Run================");
						long numOfContToBeSent = Utility.calCountOfContacts(numOfContacts,(modePercentage));
						
						String	updateQry = "UPDATE temp_components_data SET mode_attribute='"+componentPortId+"',label='"
											+label1+"' WHERE component_id="+currComp.getCompId()+" AND mode_attribute Is NUll LIMIT "+numOfContToBeSent;
						
						tempComponentsDataDao.executeJdbcUpdateQuery(updateQry);
						
						
						
					}//else 
					
						
					}else {
						
						//this is not required because any way the switch condition object will have the percentage in modeattribute 
						
					}
					
					
					//need to get the data from components_contacts table and populte the data for this component accordingly. 
					//now calculate the percentage of caontacts to be sent
					//long numOfContToBeSent = Utility.calCountOfContacts(numOfContacts,(modePercentage));
					
					//update data in the temp_components_data for this mode
					
					
					
					break;
					
				} //if(extreme outer)
				else {
					
					logger.debug("preIdfromNextComp========tempId"+preIdfromNextComp+"====="+componentPortId);
					label2 = currComp.getAutoProgram().getProgramId()+"_"+currComp.getCompId()+"_"+System.currentTimeMillis()+"-"+componentPortId;
					logger.debug("before in else mode for percentage is=======>"+modePercentage+" is Otherpick===========>"+isOtherPick);
					if(modePercentage == 0) {
						
						modePercentage = Integer.parseInt(switchCondition.getModeFlag());
						
					}
					else if(modePercentage > 0) {
						
						modePercentage = 100-modePercentage;
					}
					//need to get the data from components_contacts table and populate the data for this component accordingly. 
					
						
					//int modePercentage = Integer.parseInt(switchCondition.getModeFlag());
					logger.debug("mode for percentage is=======>"+modePercentage);
					if(consDate != null) {
						
						if(isOtherPick) {
							logger.debug("isOtherPick========>"+isOtherPick);	
							isOtherPick = false;
							
							String	updateQry = "UPDATE temp_components_data SET mode_attribute='"+componentPortId+"',label='"
												+label2+"' WHERE component_id="+currComp.getCompId()+" AND mode_attribute Is NUll";
							
							logger.debug("the update Query is========>"+updateQry);
							
							tempComponentsDataDao.executeJdbcUpdateQuery(updateQry);
							
							break;
						}//if
						
						
						
						numOfContacts += programOnlineReportsDao.getCountToCalPercentage(currComp, consDate);
						logger.debug("1111111after getting from child table the total number of contcats are=====>"+numOfContacts);
						
						//this i need to consider only for one pick
						long numOfContToBeSent = Utility.calCountOfContacts(numOfContacts,(modePercentage));
						logger.debug("2222222after getting from utility total number of contcats are=====>"+numOfContToBeSent);
						
						
						
						long numOfContactsForthisPick = programOnlineReportsDao.getCountToCalPercentage(currComp,consDate,componentPortId);
						logger.debug("3333333after getting from child table total number of contcats are=====>"+numOfContactsForthisPick);
						
						
						
						
						long resultCount = numOfContToBeSent-numOfContactsForthisPick;//sir asked me to handle it in different way(like z=x-y)
						logger.debug("444444 after subtracting total number of contcats are=====>"+resultCount);
						
						if(resultCount <= 0) {
							//TODO need to send all these contacts along with the other pick
							
							//sendFromOtherPick = true;
							isOtherPick = true;//sir asked me to handle it in different way(like z=x-y)
							return;
						
							
						}//if
						//TODO need to calculate as per the over all option
						//get the contact count individually for each pick(like if there are 100 previous contacts then divide the count as per the each pick )
						
						//logger.debug("is sendFromOtherPick=======>"+sendFromOtherPick);
						if(!isOtherPick) {
							
							String	updateQry = "UPDATE temp_components_data SET mode_attribute='"+componentPortId+"',label='"
												+label2+"' WHERE component_id="+currComp.getCompId()+"" +
												" AND mode_attribute Is NUll LIMIT "+resultCount;
							
							
							tempComponentsDataDao.executeJdbcUpdateQuery(updateQry);
						}//if
						
					}//if
					else {
						
						logger.debug("=============is over all================");
						long numOfContToBeSent = Utility.calCountOfContacts(numOfContacts,(modePercentage));
						
						String	updateQry = "UPDATE temp_components_data SET mode_attribute='"+componentPortId+"',label='"
											+label2+"' WHERE component_id="+currComp.getCompId()+"" +
											" AND mode_attribute Is NUll LIMIT "+numOfContToBeSent;
						
						tempComponentsDataDao.executeJdbcUpdateQuery(updateQry);
						
						
						
					}//else
					
					break;
					
					
				}//else
				
				
			}//for
			label = label1+"|"+label2;//to remain data until two modes of switch completes,i kept them as instance vars.where i need to reassign these to---->""
			currComp.setLabel(label);
			autoProgramComponentsDao.saveOrUpdate(currComp);
			
			*//***update the components_contacts table(for path,timestamp,activitydate)***************//*
			*//***make an entry in program_online_reports table for each record of components_contacts which are updated******************************//*
			label = label1+"|"+label2;//to remain data until two modes of switch completes,i kept them as instance vars.where i need to reassign these to---->""
			
			logger.debug("labe1 is=======>"+label1+" label2 is========>"+label2+" currCompWinId======>"+
					currCompWinId+" condilist size is=====>"+conditionLst.size());
			
			currComp.setLabel(label);
			if( ((conditionLst.size() == 2) && (label1.contains(currCompWinId) && label2.contains(currCompWinId))) || 
					((conditionLst.size() < 2) && (label1.contains(currCompWinId) || label2.contains(currCompWinId)))) {
				logger.debug("yes became true it has to b populated");
				currComp.setPopulated(true);
			}
			
			autoProgramComponentsDao.saveOrUpdate(currComp);
			
			
			Calendar cal = Calendar.getInstance();
			
			String qry = " SELECT contact_id FROM temp_components_data WHERE component_id="
			 			 +currComp.getCompId()+" AND program_id="+autoProgram.getProgramId()+" AND mode_attribute='"+componentPortId+"'";

			String cidStr = tempComponentsDataDao.getContactIdsStr(qry);//get the contact_id from temp_coponents_data.
			
			if(cidStr.trim().length() > 0) {//if atleast 1 contact exists update data for this contact_id in components_contacts.
			
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date activityDate = cal.getTime();
				String formatDate = format.format(activityDate);
				
				String updateQuery = "update components_contacts set component_win_id='"+componentPortId+"',component_id="+currComp.getCompId() +
									 ",activity_date='"+formatDate+"' ,path=concat(path,'," +componentPortId +"')"+
									 " WHERE contact_id in("+cidStr+") AND program_id="+autoProgram.getProgramId() ;
					
				logger.debug("the query to be executed to upate is===>"+updateQuery);
				
				int updateCount = componentsAndContactsDao.executeJdbcUpdateQuery(updateQuery);
				
				logger.debug("the number of contacts updated are===>"+updateCount);
				
				List<ProgramOnlineReports> prgOnlineRepToBeSaved = new ArrayList<ProgramOnlineReports>();
				
				//get those componentsAndContacts(CC) objects which are updated earlier.
				List<ComponentsAndContacts> tempList = componentsAndContactsDao.getByContactIds(cidStr, autoProgram.getProgramId());
				
				if(tempList != null && tempList.size() > 0) {
					
					for (ComponentsAndContacts tempComponentsAndContacts : tempList) { //for each CC object create new entry in program_online_reports table.
						
						ProgramOnlineReports proOnlineRep = new ProgramOnlineReports(tempComponentsAndContacts, componentPortId, 
															activityDate, autoProgram.getProgramId(), currComp.getCompId(),
															tempComponentsAndContacts.getContactId());
						
						prgOnlineRepToBeSaved.add(proOnlineRep);
						
						
					}//for
				
					programOnlineReportsDao.saveByCollection(prgOnlineRepToBeSaved);
			
				}//if
				
			}//if
			
			
		} catch (Exception e) {
			// TODO: handle exception
			
			logger.error("** Exception while populating Switch Allocation out put data",e);
			
		}
		
		
		
		
	}//populateSwitchAllocationCompOutputData
	*/
	
	
	
	
	//String switchTrueMode;//actually it is not required
	
	String label1="";//these are for switch
	String label2="";
	//String label3=null;
	//String label4=null;
	
	/**
	 * This method prepares the out put data for the switch component based on the below given scenarios </BR></BR>
	 * 
	 * 
	 * 
	 * CASE#1:if immediate previous is a CustomerActivated 	  : need to consider the data in components_contacts.</BR>
	 * CASE#2:if immediate previous is a elapsed timer itself : need to consider the data  in components_contacts.</BR>
	 * CASE#3:if immediate previous is a switch 		   	  : need to consider data in components_contacts for this window_id(it means a particular pick).</BR>
	 * CASE#4:if immediate previous is a an activity 	   	  : need consider data in components_contacts.</BR> </BR>
	 * 
	 *
	 * 
	 * After considering the data from different tables based on the previous component type,apply the true/false condition on those</BR> 
	 * contacts present in contacts table.</BR></BR>
	 * 
	 * @param tempId specifies the window id along with its pick(output path in which it is populating data).</BR>
	 */
	public void populateSwitchDataCompOutputData(String tempId) {
		
		try {
			
			if(logger.isDebugEnabled()) logger.debug("----just entered for switch output data-----"+tempId);
			AutoProgramComponents currComp = progCompMap.get(tempId);
			AutoProgram autoProgram = currComp.getAutoProgram();
			
			String winId=""; // which will be its next componentsId
			String preIdfromNextComp = ""; // which holds the previous ids of its next component
			//String condQuery = ""; // which holds the condition query
			String modeFlag = "";
			String query = ""; // which holds the actual query to be executed
			String label=""; // which holds the label of this component
			AutoProgramComponents tempComp = null;
			//String prevId = "";
			
			String dataCondQuery = null;
			String condQueryWoGrpBy = "";
			
			String openCrIdStr = "";
			String clickCrIdStr = "";
			
			String openActivityCampWinIds = null;
			String clickActivityCampWinIds = null;
			
			String actCond = "";
			
			
			//int insertedCount = 0;
			
			
			
			label = autoProgram.getProgramId()+"_"+currComp.getCompId()+"_"+System.currentTimeMillis();
			
			List<SwitchCondition> conditionLst = switchConditionDao.findByComponentId(currComp.getCompId());//get the two records in switch_condition
			
			/********************for each condition of this switch component get the windowIds
			  	(appear in its next component's previousId) along with pick***************************************/
			
			AutoProgramComponents tempComponent = null;
			for (SwitchCondition switchCondition : conditionLst) {
				
				//String activityCondQuery = "SELECT cid FROM contacts WHERE cid in(";//" AND cid in(";
				tempComp = progCompMap.get(switchCondition.getModeAttribute());
				preIdfromNextComp = tempComp.getPreviousId();//like 'SWITCH_DATA-11w1,EVENT_ELAPSE_TIMER-11w0' from this we need to get 'SWITCH_DATA-11w1'

				//get the mode of switch in which mode it is giving output
				preIdfromNextComp = preIdfromNextComp.substring(preIdfromNextComp.indexOf(currComp.getComponentWinId()), 
						(preIdfromNextComp.indexOf(currComp.getComponentWinId()))+currComp.getComponentWinId().length()+1);
				
				
				/********************Check whether Data filter is applied or not********************************************************/
				dataCondQuery = switchCondition.getConditionQuery();//this is the query related to the conditions applied for this switch
				if(dataCondQuery != null) {
					
					condQueryWoGrpBy = dataCondQuery.substring(dataCondQuery.indexOf("AND"), dataCondQuery.lastIndexOf("GROUP "));
					
				}
				if(logger.isDebugEnabled()) logger.debug("condQueryWoGrpBy is========>"+condQueryWoGrpBy);  
				/********************Check Whether Activity Filter is applied or not*******************************************************/
				//need to get Opens and Clicks crids seperately 
				
				String activityCondQuery = prepareActivityCondQry(switchCondition, autoProgram);
				
				if(activityCondQuery == null) {
					
					if(logger.isDebugEnabled()) logger.debug(" Found no data for the applied activity filter. Returning from popultating data for this switch"+tempId);
					
					return;
					
				}
				
				modeFlag = switchCondition.getModeFlag();
				
				if(logger.isDebugEnabled()) logger.debug("dataConsMap is==========>"+dataConsMap+" And the activityCondQuery is=====>"+activityCondQuery);
				Set<String> tempDatagivingComponents = dataConsMap.get(tempId);//need to handle NullPointerException....require?
				
				if(tempDatagivingComponents == null) {
					
					if(logger.isDebugEnabled()) logger.debug("no input component found,returning-----");
					return;
				}
				
				String subQuery = "";//used to avoid the empty data problems if it returns no data
				String cid = "";//holds the subquery returned result
				String compType = null;
				if(logger.isDebugEnabled()) logger.debug("datagiving components list for this swith mode"+tempId+" is ...."+tempDatagivingComponents);
				
				/* this comparision is required to avoid unneccesary entries to be made in temp_components table
				 * it ensures that examining switch(along with its output path) and the mode(true/false) should sync
				 */
				
				if( modeFlag.equalsIgnoreCase("true") && preIdfromNextComp.equalsIgnoreCase(tempId)) { 
					
					if(logger.isDebugEnabled()) logger.debug("----just entered for true-----");
					
					label1 = currComp.getAutoProgram().getProgramId()+"_"+currComp.getCompId()+"_"+System.currentTimeMillis()+"-"+tempId;
					
					//switchTrueMode = tempId;
					for (String tempComponentId : tempDatagivingComponents) {
						
						
						tempComponent = progCompMap.get(tempComponentId);
						compType = tempComponent.getCompType();
						
						
						/***************************CASE#3:**********************************/
						
						if (compType.equals(ProgramEnum.SWITCH_DATA.name()) ||
								compType.equals(ProgramEnum.SWITCH_ALLOCATION.name())) { // if it a switch data/allocation component
							
							//here it is necessary to know the output path(pick)for only the switch comp(only for this component 
							//the compnent_win_id value will be stored as SWITCH_DATA-11w1)
							
							subQuery = "select contact_id from components_contacts WHERE component_id="+tempComponent.getCompId()+
										" AND component_win_id = '"+tempComponentId+"' AND Program_id="+autoProgram.getProgramId();
							
							query = " INSERT IGNORE INTO temp_components_data ( " +
									" program_id, component_id, label, contact_id, stage, component_win_id, mode_attribute) " +
									" SELECT "+
									autoProgram.getProgramId()+ " , " + 
									currComp.getCompId() +	" , '" +
									label1 +"' , " +
									" cid, " +
									currComp.getStage() + " , '" +
									currComp.getComponentWinId() +"' , '" +tempId+"' "+
									" from contacts where cid in(<CID>) AND list_id in("+mlStr+") "+
									condQueryWoGrpBy +activityCondQuery+" GROUP BY email_id ";
							
							
							
						}//else if
						
						/*******************************CASE:1&2&4*******************************/ 
						
						else if(compType.equals(ProgramEnum.ACTIVITY_SEND_EMAIL.name()) || 
								compType.equals(ProgramEnum.ACTIVITY_SEND_SMS.name()) ||
								compType.equals(ProgramEnum.EVENT_CUST_ACTIVATED.name()) ||
								compType.equals(ProgramEnum.EVENT_ELAPSE_TIMER.name()) ||
								compType.equals(ProgramEnum.EVENT_TARGET_TIMER.name())) { // if it is a activity component
							
							subQuery = "select contact_id from components_contacts WHERE component_id="+tempComponent.getCompId()+
										" AND Program_id="+autoProgram.getProgramId();
							
							query = " INSERT IGNORE INTO temp_components_data ( " +
									" program_id, component_id, label, contact_id, stage, component_win_id, mode_attribute) " +
									" SELECT "+
									autoProgram.getProgramId()+ " , " + 
									currComp.getCompId() +	" , '" +
									label1 +"' , " +
									" cid, " +
									currComp.getStage() + " , '" +
									currComp.getComponentWinId() +"' , '" +tempId+"' "+
									" from contacts where cid in(<CID>) AND list_id in("+mlStr+")"+
									condQueryWoGrpBy +activityCondQuery+" GROUP BY email_id ";
					
							
						}//else if
						 
						//execute query that is prepared above.
						cid = tempComponentsDataDao.getContactIdsStr(subQuery);
						
						if(cid.length()>0) { // to avoid empty data problems
							
							query = query.replace("<CID>", cid);
							if(logger.isDebugEnabled()) logger.debug("query to be executed for this "+tempComponentId +" is... "+query);
							
							tempComponentsDataDaoForDML.executeJdbcQuery(query);
						}//if
						else {
							//yet to be deleted
							if(logger.isDebugEnabled()) logger.debug("no data found for this component...."+tempComponentId);
						}//else
					}// for
					componentLabelMap.put(tempId, label1);
					break;//is required to avoid further unnecessary execution 
				}// if
				//need to verify is it really a right way or not?????????????
				else if( modeFlag.equalsIgnoreCase("false") && preIdfromNextComp.equalsIgnoreCase(tempId)) { //for the false condition
					
					if(logger.isDebugEnabled()) logger.debug("---just entered for false----");
					label2 = currComp.getAutoProgram().getProgramId()+"_"+currComp.getCompId()+"_"+System.currentTimeMillis()+"-"+tempId;
					String tempQuery = "";
					
					//query = org.mq.captiway.scheduler.utility.QueryGenerator.generateListSegmentQuery(supportData.replace("<mlIdsToBeReplaced>", mlIdStr), true);
					for (String tempComponentId : tempDatagivingComponents) {
						
						tempComponent = progCompMap.get(tempComponentId);
						compType = tempComponent.getCompType();
						
							
						/***************************CASE#3:**********************************/
						
						if (compType.equals(ProgramEnum.SWITCH_DATA.name()) ||
								compType.equals(ProgramEnum.SWITCH_ALLOCATION.name())) { // if it is a switch data component
							
							
							tempQuery = "select contact_id from components_contacts where component_id=" +
										""+tempComponent.getCompId()+" AND component_win_id='"+tempComponentId+"' AND program_id=" +autoProgram.getProgramId();
							
							subQuery = "select contact_id from components_contacts " +
							
										" where component_id="+tempComponent.getCompId()+" AND component_win_id='"+tempComponentId+"'" +
										" AND program_id="+autoProgram.getProgramId()+"" +
										" AND contact_id not in(select cid from contacts where cid in" +
										"(<TEMPCIDS>) AND list_id in("+mlStr+") "+
										condQueryWoGrpBy +activityCondQuery+" GROUP BY email_id " + ")";
								
							query = " INSERT IGNORE INTO temp_components_data ( " +
									" program_id, component_id, label, contact_id, stage, component_win_id, mode_attribute) " +
									" SELECT "+
									autoProgram.getProgramId()+ " , " + 
									currComp.getCompId() +	" , '" +
									label2 +"' , " +
									" cid, " +
									currComp.getStage() + " , '" +
									currComp.getComponentWinId() +"' , '" +tempId+"' "+
									" from contacts where cid in(<CID>)";
					 
							
						} // else if switch 
						
						/*******************************CASE#4*******************************/  
						
						else if(compType.equals(ProgramEnum.ACTIVITY_SEND_EMAIL.name()) || 
								compType.equals(ProgramEnum.ACTIVITY_SEND_SMS.name()) ||
								compType.equals(ProgramEnum.EVENT_CUST_ACTIVATED.name()) ||
								compType.equals(ProgramEnum.EVENT_ELAPSE_TIMER.name())) {
							
							tempQuery = "select contact_id from components_contacts where component_id=" +
										""+tempComponent.getCompId()+" AND program_id=" +autoProgram.getProgramId();
							
							subQuery = "select contact_id from components_contacts " +
							
										" where component_id="+tempComponent.getCompId()+" AND program_id="+autoProgram.getProgramId()+"" +
										" AND contact_id not in(select cid from contacts where cid in" +
										"(<TEMPCIDS>) AND list_id in("+mlStr+") "+
										condQueryWoGrpBy +activityCondQuery+" GROUP BY email_id " + ")";
							
							query = " INSERT IGNORE INTO temp_components_data ( " +
									" program_id, component_id, label, contact_id, stage, component_win_id, mode_attribute) " +
									" SELECT "+
									autoProgram.getProgramId()+ " , " + 
									currComp.getCompId() +	" , '" +
									label2 +"' , " +
									" cid, " +
									currComp.getStage() + " , '" +
									currComp.getComponentWinId() +"' , '" +tempId+"' "+
									" from contacts where cid in(<CID>)";
							
							
						}// else if
						
						//execute query that is prepared
						cid = tempComponentsDataDao.getContactIdsStr(tempQuery);
						
						if(logger.isDebugEnabled()) logger.debug(" tempQuery for this "+ tempComponent.getComponentWinId()+" is===>" +
						""+tempQuery+" the contacts aftr executing this query is===>"+cid);
						
						if(cid.length() > 0 ) {
							
							subQuery = subQuery.replace("<TEMPCIDS>", cid);
							
							cid = tempComponentsDataDao.getContactIdsStr(subQuery);
							if(logger.isDebugEnabled()) logger.debug(" subQuery for this "+ tempComponent.getComponentWinId()+" is====>"+
									subQuery+ "  the contacts after executing this qry is====>"+cid);
							if(cid.length()>0){ // to avoid empty data problems
								
								query = query.replace("<CID>", cid);
								if(logger.isDebugEnabled()) logger.debug("query to be executed for this "+tempComponentId +" is... "+query);
								
								tempComponentsDataDaoForDML.executeJdbcQuery(query);
							}//if
							else {
								
								if(logger.isDebugEnabled()) logger.debug("no data found for this component...."+tempComponentId);
							}//else 
						
						}// if
						
						else {
							
							if(logger.isDebugEnabled()) logger.debug("got no contacts for the tempquery....."+tempQuery+" for the component...."+tempComponentId);
						}
						
					}// for
					componentLabelMap.put(tempId,label2);
					break;
					
				}//else 
				
			}// for
			
			
			/***update the components_contacts table(for path,timestamp,activitydate)***************/
			/***make an entry in program_online_reports table for each record of components_contacts which are updated******************************/
			
			Calendar cal = Calendar.getInstance();
			String qry = " SELECT contact_id FROM temp_components_data WHERE component_id="
			 			 +currComp.getCompId()+" AND program_id="+autoProgram.getProgramId()+" AND mode_attribute='"+tempId+"'";

			String cidStr = tempComponentsDataDao.getContactIdsStr(qry);//get the contact_id from temp_coponents_data.
			
			if(cidStr.trim().length() > 0) {//if atleast 1 contact exists update data for this contact_id in components_contacts.
			
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date activityDate = cal.getTime();
				String formatDate = format.format(activityDate);
				
				String updateQuery = "update components_contacts set component_win_id='"+tempId+"',component_id="+currComp.getCompId() +
									 ",activity_date='"+formatDate+"' ,path=concat(path,'," +tempId +"')"+
									 " WHERE contact_id in("+cidStr+") AND program_id="+autoProgram.getProgramId() ;
					
				if(logger.isDebugEnabled()) logger.debug("the query to be executed to reach the end status is===>"+updateQuery);
				
				int updateCount = componentsAndContactsDaoForDML.executeJdbcUpdateQuery(updateQuery);
				
				if(logger.isDebugEnabled()) logger.debug("the number of contacts updated are===>"+updateCount);
				
				List<ProgramOnlineReports> prgOnlineRepToBeSaved = new ArrayList<ProgramOnlineReports>();
				
				//get those componentsAndContacts(CC) objects which are updated earlier.
				List<ComponentsAndContacts> tempList = componentsAndContactsDao.getByContactIds(cidStr, autoProgram.getProgramId());
				
				if(tempList != null && tempList.size() > 0) {
					for (ComponentsAndContacts tempComponentsAndContacts : tempList) { //for each CC object create new entry in program_online_reports table.
						
						ProgramOnlineReports proOnlineRep = new ProgramOnlineReports(tempComponentsAndContacts, tempId, 
								activityDate, autoProgram.getProgramId(), currComp.getCompId(), tempComponentsAndContacts.getContactId());
						
						prgOnlineRepToBeSaved.add(proOnlineRep);
						
						
					}//for
				
					//programOnlineReportsDao.saveByCollection(prgOnlineRepToBeSaved);
					programOnlineReportsDaoForDML.saveByCollection(prgOnlineRepToBeSaved);

					
				}//if
			}//if
			
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		
	}//populateSwitchDataCompOutputData
	
	/** this methos never be called
	 * this method allows us to prepare map entry(consisting list of data giving components as a value) for custactivated 
	 * @param currComp
	 */
	/*public void prepareCustActivatedInputList(AutoProgramComponents currComp) {
		
		if(currComp.getPreviousId().length()>0) {
			
			if(logger.isDebugEnabled()) logger.debug("as this is a customer activated component it should not suppose to have any previous component");
			return;
		}
		List<AutoProgramComponents> tempList = dataConsMap.get(currComp.getComponentWinId()+"0");
		if(tempList == null) tempList = new ArrayList<AutoProgramComponents>();
		
		dataConsMap.put(currComp.getComponentWinId()+"0", tempList);
		
		
	}*/

	/**
	 * This method prepares the Set of datagiving components for this  target timer component( need to consider various scenarios)</BR>
	 * CASE#1:if immediate previous is a an activity 	   	  : need to push all the activities until it found its datagiving component</BR>
	 * CASE#2:if immediate previous is a switch 		   	  : need to push its mode(only that pick) in which it is giving o/p for it </BR>
	 * CASE#3:if immediate previous is a CustomerActivated 	  : need to push it directly</BR>
	 * CASE#4:if immediate previous is a elapsed timer 		  : need to push it directly</BR>
	 * CASE#5:if immediate previous is a Target timer		  : need to push it directly</BR>
	 * @param targetTimerComponent
	 */
	public void prepareTargetTimerInputList(AutoProgramComponents targetTimerComponent) {

		
		/**************STEP1:run the DFS algorithm to make an entry in the dataConsMap for this component  **************************/
			
		if(logger.isDebugEnabled()) logger.debug("----just entered for populating data for elapsed timer------");
		
		if(targetTimerComponent == null) {
			
			if(logger.isErrorEnabled()) logger.error("Error, got targetTimerComponent as null....");
			return;
			
			
		}
		
		//find recursively until it get its data giving component
		DFS(targetTimerComponent);//Run the DFS algorithm to prepare its input components list
		
		if(logger.isDebugEnabled()) logger.debug("----the map to populate data for this elapse timer is====>"+dataConsMap);
		
		
		
	}//prepareTargetTimerInputList
	
	
	
	
	/**
	 * this method prepares the Set of datagiving components for this  elapsed timer component( need to consider various scenarios)</BR>
	 * CASE#1:if immediate previous is a an activity 	   	  : need to push all the activities until it found its datagiving component</BR>
	 * CASE#2:if immediate previous is a switch 		   	  : need to push its mode(only that pick) in which it is giving o/p for it </BR>
	 * CASE#3:if immediate previous is a CustomerActivated 	  : need to push it directly</BR>
	 * CASE#4:if immediate previous is a elapsed timer itself : need to push it directly</BR>
	 * @param elapsedTimerComponent
	 */
	
	
	public void prepareElapsedTimerinputList(AutoProgramComponents elapsedTimerComponent) {
		
		/**************STEP1:run the DFS algorithm to make an entry in the dataConsMap for this component  **************************/
			
		if(logger.isDebugEnabled()) logger.debug("----just entered for populating data for elapsed timer------");
		
		//find recursively until it get its data giving component
		if(elapsedTimerComponent == null) {
			
			if(logger.isErrorEnabled()) logger.error("Error, got elapsedTimerComponent as null...");
			return;
			
		}//if
		DFS(elapsedTimerComponent);//run the DFS algorithem for Elapsed Timer component  to prepare its input component list
		
		if(logger.isDebugEnabled()) logger.debug("----the map to populate data for this elapse timer is====>"+dataConsMap);
		
		
		
	}// prepareElapsedTimerinputList
	
	
	
	private Set<String> visitedCompSet = new HashSet<String>();
	
	private Map<String, Set<String>> dataConsMap = new HashMap<String, Set<String>>();
	/**
	 * This method runs the DFS algorithm to prepare the input components set for the component that has been passed.</BR>
	 * Help for PrepareComponentInputList(-).</BR>
	 * Called in prepareEllapseTimerInputList(),prepareTargetTimerInputList().</BR> 
	 * @param currComp
	 */
	public void DFS(AutoProgramComponents currComp) {
		
		
		try {
			if(logger.isDebugEnabled()) logger.debug("-----just entered in DFS-----");
			visitedCompSet.clear();
			Stack<AutoProgramComponents> s=new Stack<AutoProgramComponents>();
			
			s.push(currComp);
			
			// currComp.setVisited(true);
			
			visitedCompSet.add(currComp.getComponentWinId());
			//printNode(currComp.getCompId(),  currComp);
			
			boolean reachedRootNode = false;//if reaches root node
			
			while(!s.isEmpty()) {
				
				AutoProgramComponents tempComp = (AutoProgramComponents)s.peek();
					
				reachedRootNode = (tempComp==currComp);
				
				AutoProgramComponents childComp = null;
				
				String tempPrevId =  getUnvisitedChildComp(tempComp);
				//logger.debug("the returned tempid is====>"+tempPrevId);
				//tempPrevId = tempPrevId.substring(0, tempPrevId.length()-1);//replace(tempPrevId.valueOf(tempPrevId.charAt(tempPrevId.length()-1)), "");
				if(tempPrevId != null) childComp = progCompMap.get(tempPrevId);
				//logger.debug(tempPrevId+"0000000000000000000000"+childComp);
				
				if(childComp != null) {
					
					visitedCompSet.add(tempPrevId);
					
					boolean isDataGivingComponent = isDataGivingComponent(childComp);
					
					
					//if the immediate previus component is a datagiving component it will make an entry in the set,
					//and if not also until datagiving component finds it will keep on make entries in the set(in this case reachrootNode fails)
					if((reachedRootNode || isDataGivingComponent(childComp)==false))
						printNode(currComp.getComponentWinId()+"0", tempPrevId);
					
					
					
					/*if( reachedRootNode || isDataGivingComponent(childComp)==false ) 
						
						printNodeForSwitch(modeToMove, tempPrevId);*/
					
					/*if(reachedRootNode && isDataGivingComponent )
						printNode(currComp.getComponentWinId()+"0", tempPrevId);
					
					
					//check weather we reached root node, data giving component, target timer
					if((reachedRootNode && isDataGivingComponent == false) ||
						(!reachedRootNode && isDataGivingComponent == false ) ) {
					
						//printNode(currComp.getComponentWinId()+"0", childComp);
						printNode(currComp.getComponentWinId()+"0", tempPrevId);
						
					}*/
					
					
					// Need to consider dataGiving component type
					/*if()
						printNode(currComp.getComponentWinId()+"0", tempPrevId);
					
					if()
						printNode(currComp.getComponentWinId()+"0", tempPrevId);*/
					/*if(isDataGivingComponent(childComp)==false  ) {
						if(isTargetTimer(childComp) && !isElapsed(childComp.getSupportId())) continue;//if target timer and is not elapsed continue traversing
						
						s.push(childComp);//if the child is an activity and need to traverse until we find the data giving component
					}*/
					
					if(isDataGivingComponent == false ) {
						
						//if((isTargetTimer(childComp) && isElapsed(childComp.getSupportId())) || !isTargetTimer(childComp))//if target timer and is not elapsed continue traversing
						s.push(childComp);//if the child is an activity and need to traverse until we find the data giving component
					}/*else if(isDataGivingComponent(childComp)== true) {
						
						printNode(currComp.getComponentWinId()+"0", tempPrevId);
					}*/
					
					
				}
				else {
					s.pop();
				}
				
			} // while
			
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
	} // DFS
	
	public boolean isTargetTimer(AutoProgramComponents currComp) {
		
		
		boolean isTimer = currComp.getCompType().equalsIgnoreCase(ProgramEnum.EVENT_TARGET_TIMER.name());
		
		return isTimer;
		
	}
	
	public void DFSForActivity(AutoProgramComponents currComp) {
		
		try {
			visitedCompSet.clear();
			Stack<AutoProgramComponents> s=new Stack<AutoProgramComponents>();
			
			s.push(currComp);
			visitedCompSet.add(currComp.getComponentWinId());
			
			boolean reachedRootNode = false;
			
			while(!s.isEmpty()) {
				
				AutoProgramComponents tempComp = (AutoProgramComponents)s.peek();
					
				reachedRootNode = (tempComp==currComp);
				
				AutoProgramComponents childComp = null;
				
				String tempPrevId =  getUnvisitedChildComp(tempComp);
				//tempPrevId = tempPrevId.substring(0, tempPrevId.length()-1);//replace(tempPrevId.valueOf(tempPrevId.charAt(tempPrevId.length()-1)), "");
				if(tempPrevId != null) childComp = progCompMap.get(tempPrevId);
				
				
				if(childComp != null) {
					
					visitedCompSet.add(tempPrevId);
					
					boolean isDataGivingComponent = isDataGivingComponent(childComp);
					
					
					//here only we need to consider the datagiving components for all the paths of back tracking
					//if we find any data giving component make an entry in input set
					if( (reachedRootNode && isDataGivingComponent==true ) )
					
						printNode(currComp.getComponentWinId()+"0", tempPrevId);
					
					if(!reachedRootNode && isDataGivingComponent==true)
						printNode(currComp.getComponentWinId()+"0", tempPrevId);
					// Need to consider dataGiving component type
					
					
					
					if(isDataGivingComponent(childComp)==false ) {
						
						//if(isTargetTimer(childComp) && !isElapsed(childComp.getSupportId())) continue;
						s.push(childComp);

					}
					
				}
				else {
					s.pop();
				}
				
			} // while
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		
	}//DFSForActivity
	
	
	
	public void DFSForSwitch(AutoProgramComponents currComp, String modeToMove) { 
		
		try{
			
			if(logger.isDebugEnabled()) logger.debug("-----just entered in DFSForSwitch() for mode-----"+modeToMove);
			if(logger.isDebugEnabled()) logger.debug("data consideration map before "+modeToMove+" is "+dataConsMap);
			visitedCompSet.clear();
			Stack<AutoProgramComponents> s=new Stack<AutoProgramComponents>();
			
			s.push(currComp);
			visitedCompSet.add(currComp.getComponentWinId());//here it is a bug but nothing will happen wrong,since this method calls only to examine this particular component
			
			boolean reachedRootNode = false;
			
			while(!s.isEmpty()) {
				
				AutoProgramComponents tempComp = (AutoProgramComponents)s.peek();
					
				reachedRootNode = (tempComp==currComp);
				
				AutoProgramComponents childComp = null;
				
				String tempPrevId =  getUnvisitedChildComp(tempComp);
				if(logger.isDebugEnabled()) logger.debug("the previous component is====>"+tempPrevId);
				if(tempPrevId != null) childComp = progCompMap.get(tempPrevId);
				
				
				if(childComp != null) {
					
					visitedCompSet.add(tempPrevId);
					
					//check for reach rootnode,datagiving component,target timer
					//reachedNode represents currComp,
					
					
					//if the immediate previus component is a datagiving component it will make an entry in the set,
					//and if not also until datagiving component finds it will keep on make entries in the set(in this case reachrootNode fails)

					if( reachedRootNode || isDataGivingComponent(childComp)==false ) 
					
						printNodeForSwitch(modeToMove, tempPrevId);
					
					/*if( (reachedRootNode && (isDataGivingComponent(childComp)==false) ) ||
							(!reachedRootNode &&(isDataGivingComponent(childComp)==false) ) || 
							(!reachedRootNode && isDataGivingComponent(childComp)==true ) ||
							(reachedRootNode && isDataGivingComponent(childComp)) ) 
						printNodeForSwitch(modeToMove, tempPrevId);
					*/
					
					
/*					if( (reachedRootNode || isDataGivingComponent(childComp)==false ) && !isTargetTimer(childComp) ) 
						
						printNodeForSwitch(modeToMove, tempPrevId);
					*/
					
					if(isDataGivingComponent(childComp)==false ) {
						
						//if((isTargetTimer(childComp) && isElapsed(childComp.getSupportId())) || !isTargetTimer(childComp))//if target timer and is not elapsed continue traversing
						s.push(childComp);//if the child is an activity and need to traverse until we find the data giving component
					}/*else if(isDataGivingComponent(childComp)== true) {
						printNodeForSwitch(modeToMove, tempPrevId);
						
					}*/
					
				}
				else {
					s.pop();
				}
				
			} // while
			
			
			if(logger.isDebugEnabled()) logger.debug("data consideration map after "+modeToMove+" is "+dataConsMap);
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		
		
	}//DFSForSwitch
	
	
	/**
	 * 
	 * @param tempComp
	 * @return
	 */
	private String getUnvisitedChildComp( AutoProgramComponents tempComp) {
		
		try {
			
			String prevIds = tempComp.getPreviousId();
			
			//logger.debug("the unvisited child node for this"+tempComp.getComponentWinId()+ " is=====>"+prevIds);
			
			if(prevIds==null || prevIds.trim().length()==0) return null;
			
			String prevCompIds[] = prevIds.split(",");
			
			for (String prevId : prevCompIds) {
				if(prevId.trim().length() !=0 ) { // if after comma of last id we may have empty string
					//prevId = prevId.substring(0, prevId.length()-1);
					if(!visitedCompSet.contains(prevId)) {
						return prevId;
					}
				}//if
			} // for
			
		} catch (Exception e) {
			logger.error("Exception ::::" , e);
		}
		return null;
		
	} // getUnvisitedChildComp
	
	
	
	/**
	 * 
	 * @param tempComp
	 */
	
	private void printNode(String currCompWinId, String tempCompId) {
		
		if(logger.isDebugEnabled()) logger.debug("the data giving component for "+currCompWinId +" is===>"+tempCompId);
		
		Set<String> tempList = dataConsMap.get(currCompWinId);
		
		if(tempList==null) tempList=new HashSet<String>();
		
		tempList.add(tempCompId);
		
		dataConsMap.put(currCompWinId, tempList);
		
	} // printNode
	
	/*private void printNode(String currCompWinId, AutoProgramComponents tempComp) {
		
		logger.debug(tempComp.getComponentWinId()+ "   ");
		
		List<AutoProgramComponents> tempList = dataConsMap.get(currCompWinId);
		
		if(tempList==null) tempList=new ArrayList<AutoProgramComponents>();
		
		tempList.add(tempComp);
		
		dataConsMap.put(currCompWinId, tempList);
		
	} // printNode
*/	
	
	
	
	
	
	/**
	 * for switch component we should allow two types of modes for that we should add two entries in the dataConsMap for the 
	 * switch component
	 * @param currCompWinId
	 * @param tempComp
	 */
	private void printNodeForSwitch(String currCompWinId, String tempCompId) {
		
		if(logger.isDebugEnabled()) logger.debug("the data giving component for "+currCompWinId+" is===>"+tempCompId);
		
		Set<String> tempList = dataConsMap.get(currCompWinId);
		
		if(tempList==null) tempList=new HashSet<String>();
		
		tempList.add(tempCompId);
		
		dataConsMap.put(currCompWinId, tempList);
		
		
	}
	
	/*private void printNodeForSwitch(String currCompWinId, AutoProgramComponents tempComp) {
		
		logger.debug(tempComp.getComponentWinId()+ "   ");
		
		List<AutoProgramComponents> tempList = dataConsMap.get(currCompWinId);
		
		if(tempList==null) tempList=new ArrayList<AutoProgramComponents>();
		
		tempList.add(tempComp);
		
		dataConsMap.put(currCompWinId, tempList);
		
		
	}*/
	
	
	/**
	 * 
	 * @param tempComp
	 * @return
	 */
	private boolean isDataGivingComponent(AutoProgramComponents tempComp) {
		
		String compType = tempComp.getCompType();
		
		boolean foundData = compType.equals(ProgramEnum.EVENT_CUST_ACTIVATED.name()) ||  
		 compType.equals(ProgramEnum.EVENT_CUST_DEACTIVATED.name()) ||
		 compType.equals(ProgramEnum.EVENT_CUSTOM_EVENT.name()) ||
		 compType.equals(ProgramEnum.EVENT_ELAPSE_TIMER.name()) ||
		 compType.equals(ProgramEnum.EVENT_TARGET_TIMER.name()) ||
		 compType.equals(ProgramEnum.EVENT_SCHEDULED_FILTER.name()) ||
		 
		 compType.equals(ProgramEnum.SWITCH_ALLOCATION.name()) ||
		 compType.equals(ProgramEnum.SWITCH_DATA.name()) ;

		return foundData;
		
	} // isDataGivingComponent
	
	
	/**
	 * This method prepares the query for the Activity filter applied on a particuler switch component
	 * @param switchCondition
	 * @param program
	 * @return
	 */
	public String prepareActivityCondQry(SwitchCondition switchCondition, AutoProgram program) {
		
		String openCampWinIds = switchCondition.getOpenCampWinIds();
		String clickCampWinIds = switchCondition.getClickCampWinIds();
		
		String openCrIdStr = "";
		String clickCrIdStr = "";
		
		Calendar repConsDate = null;
		
		
		String actCond = null;
		String activityCondQuery = "SELECT cid FROM contacts WHERE cid in(";
		
		boolean open = (openCampWinIds != null && openCampWinIds.trim().length() > 0);
		boolean click = (clickCampWinIds != null && clickCampWinIds.trim().length() > 0);
		
		if( open || click ) {
			
			actCond = switchCondition.getCondition();
			
			
			String match = actCond.substring(actCond.indexOf(Constants.ADDR_COL_DELIMETER)+3,actCond.indexOf(":",actCond.indexOf(Constants.ADDR_COL_DELIMETER)));
			
			//actCond = actCond.substring(actCond.indexOf(Constants.ADDR_COL_DELIMETER+1), actCond.lastIndexOf(Constants.DELIMETER_COLON));
			
			match = (match.equalsIgnoreCase("all")) ? "AND" : "OR";
			if(logger.isDebugEnabled()) logger.debug("Condition applied for activity filter is====>"+match);
			
			//added to avoid the unexpected reports fetching for a particuler email component
			//**************************************************************************
			String fromDate = actCond.substring(actCond.indexOf("<",actCond.indexOf(Constants.ADDR_COL_DELIMETER)),
								actCond.indexOf(">",actCond.indexOf(Constants.ADDR_COL_DELIMETER))+1);
			
			
			if(fromDate.contains(Constants.AUTO_PROGRAM_EMAILREP_FROM_CREATED)) {
				
				repConsDate = program.getCreatedDate();
			}else if(fromDate.contains(Constants.AUTO_PROGRAM_EMAILREP_FROM_MODIFIED)) {
				
				repConsDate = program.getModifiedDate();
			}
			
			
			//**************************************************************************
			
			if(open) {
				
				openCampWinIds = prepareCampWinIdsForSQL(openCampWinIds);
				if(logger.isDebugEnabled()) logger.debug("openActivityCampWinIds======>"+openCampWinIds);
				openCrIdStr = campaignReportDao.getCridsForSwitchActivityFilter(program.getProgramName(),openCampWinIds, repConsDate);
				if(logger.isDebugEnabled()) logger.debug("openCrIdStr======>"+openCrIdStr);
				
				if(openCrIdStr.length() > 0) {
					//prepare activity query 
					activityCondQuery += "SELECT contact_id FROM campaign_sent WHERE cr_id in("+openCrIdStr+") AND opens > 0)";
					
				}//if
				
			}//if open
			if(click) {
				clickCampWinIds = prepareCampWinIdsForSQL(clickCampWinIds);
				
				if(logger.isDebugEnabled()) logger.debug("clickActivityCampWinIds======>"+clickCampWinIds);
				clickCrIdStr = campaignReportDao.getCridsForSwitchActivityFilter(program.getProgramName(), clickCampWinIds, repConsDate);
				
				if(logger.isDebugEnabled()) logger.debug("clickCrIdStr======>"+clickCrIdStr);
				
				if(openCrIdStr.length() > 0 && clickCrIdStr.length() > 0) {
					
					activityCondQuery += match+" cid in(";
					
				}
				
				
				if(clickCrIdStr.length() > 0) {
					
					//prepare activity query for 
					activityCondQuery += "SELECT contact_id FROM campaign_sent WHERE cr_id in("+clickCrIdStr+") AND clicks > 0)";
					
				}//if
				
			}//if click
			
			
			if( (openCrIdStr != null && openCrIdStr.length() > 0)  || (clickCrIdStr != null && clickCrIdStr.length() > 0)  ){
				
				String cids = campaignSentDao.getContactIdsForProgram(activityCondQuery); 
				
				
				if(cids.length() == 0) {
					
					
					if(logger.isDebugEnabled()) logger.debug("cant proceed found no data for this switch component after applying the filter(s) ");
					Messages messages = new Messages("Auto Responder","No contact has opened/clicked","The program can't move futher as the condition filter returned 0 contacts.",
							program.getUser());
					//messagesDao.saveOrUpdate(messages);
					messagesDaoForDML.saveOrUpdate(messages);
					return null;
					
				}
				activityCondQuery = " AND cid in("+cids+")";
				
			}else if( open && ( openCrIdStr != null && openCrIdStr.length() == 0 ) ||
					click && ( clickCrIdStr != null && clickCrIdStr.length() == 0 )){
				
				
				if(logger.isDebugEnabled()) logger.debug("cant proceed found no data for this switch component after applying the filter(s) ");
				Messages messages = new Messages("Auto Responder","No contact has opened/clicked","The program can't move futher as the condition filter returned 0 contacts.",
						program.getUser());
				//messagesDao.saveOrUpdate(messages);
				messagesDaoForDML.saveOrUpdate(messages);
				return null;
			}
		}//if
		
		else if(!open && !click) {
			
			activityCondQuery = "";
		}
		
		
		return activityCondQuery;
	}
	
	
	public static void main(String args[]) {
		
		
		int perc = 20;
		int num = 4;
		
		long count = Math.round((perc/100f)*num);
		
		if(logger.isDebugEnabled()) logger.debug("My count is========>"+count);
		
		
	}
	
}//class