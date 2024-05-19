package com.optculture.launchpad.submitter;


import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.optculture.launchpad.repositories.*;
import com.optculture.shared.entities.promotion.Coupons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.optculture.launchpad.configs.MessagingConfigs;
import com.optculture.launchpad.configs.OCConstants;
import com.optculture.launchpad.services.SegmentService;
import com.optculture.shared.entities.communication.ChannelAccount;
import com.optculture.shared.entities.communication.ChannelSetting;
import com.optculture.shared.entities.communication.Communication;
import com.optculture.shared.entities.communication.CommunicationReport;
import com.optculture.shared.entities.communication.CustomCommunication;
import com.optculture.shared.entities.communication.Schedule;
import com.optculture.shared.entities.communication.UserChannelSetting;
import com.optculture.shared.entities.contact.SegmentRule;
import com.optculture.shared.entities.org.User;
import com.optculture.shared.entities.system.Messages;



//purpose :  have to set segments and respective campaign together and push to queue
@Component
public class Submitter {

	@Autowired
	SegmentRuleRepository segmentRepo;

	@Autowired
	SegmentService segmentService;


	@Autowired
	private RabbitTemplate template;


	Communication communication;


	Schedule communicationSchedule;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private CommunicationRepository communicationRepository;


	@Autowired
	private CommunicationReportRepository communicationReportRepository;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	MessageRepository messageRepo;

	@Autowired
	CouponsRepository couponsRepository;
	@Autowired
	UserChannelSettingRepository userChannelRepo;

	@Autowired
	ChannelAccountRepository channelAccountRepo;

	@Autowired
	ChannelSettingRepository channelSettingRepo;


	Logger logger = LoggerFactory.getLogger(Submitter.class);

	private User user ;


	/*
	 * Purpose : start the collecting all the contactIds from the segments mentioned in the communication
	 * Param : communication object
	 * return: returns nothing
	 */
	@Transactional
	public void startProcessingCommunication(Communication commObj,Schedule scheduleObj) {

		communication = commObj;
		communicationSchedule = scheduleObj;
		String fromSourceType = communication.getChannelType()+ OCConstants.EMAIL_SOURCE_TYPE;
		user = userRepo.findByuserId(communication.getUserId());
		sendCommunication(fromSourceType,scheduleObj);

		logger.info(">>>>>>>>>>>ending of Processing>>>>>>>>>>>>>");
	}
	/*
	 * Purpose : take the rule of a single segment and gives list of contacts in that segment
	 * Params: SegmentRule Id long value
	 * returns : list of contacts.
	 */
	private List<Long> getSegmentedContacts(Communication commObj, List<Long> listOfSegmentIds) throws Exception{
		List<Long> listOfContacts;
		// String contactType;// = OCConstants.STRING_NILL;

//		SegmentRule ruleObj = segmentRepo.findBySegRuleId(segmentId);
		List<SegmentRule> listOfSegments = segmentRepo.findBySegRuleIdIn(listOfSegmentIds);
		List<String> listOfSegRules = listOfSegments.stream().map(SegmentRule::getSegRule).toList();

		// if(commObj.getChannelType().equalsIgnoreCase(OCConstants.EMAIL_COMMUNICATION)) {
		// 	contactType = OCConstants.ACTIVE_EMAIL_CONTACTS;
		// }else {
		// 	contactType = OCConstants.ACTIVE_MOBILE_CONTACTS;
		// }
		try {
			listOfContacts = segmentService.getSegmentContacts(listOfSegRules, commObj.getChannelType());
		}catch(Exception e) {
			logger.error("Error occured while connecting with click house DB.", e);
			return null;
		}

		return listOfContacts;
	}//getSegmentedContacts

	CommunicationReport communicationReport;
	boolean proceed = true ;

	private void sendCommunication(String fromSource, Schedule scheduleObj) {

		try {

			if (communication == null || communicationSchedule == null) {
				return;
			}

			logger.info("Started sending for Communication : "+communication.getName());
			Long userId = communication.getUserId();
			User currentUser = userRepo.findByuserId(userId);

			//validate based on the channel type or set properties based on the properties.

			proceed = validateChannelSettings();

			if(fromSource.contains("Email")) {
				proceed = validateEmailCommunication();	
			}
			else if(fromSource.contains("SMS")){
				proceed = validateSMSCommunication();	
			}
			else {
				proceed = validateWACommunication();
			}
			if(!proceed) {
				logger.debug("unable to process communication and message is logged in Messages.");
				return;
			}



			Long suppressedCount = 0L;
			Long configuredCount = 0L;

			int preferenceCount = 0;

			if(proceed) {
				String segmentIds = communication.getSegmentId(); // Segment:3542,####,####
				segmentIds = segmentIds.replace("Segment:","");
				List<Long> resultList = new ArrayList<Long>();
				List<Long> segContacts = new ArrayList<Long>();
//				CustomCommunication customCommunication = null;

				String[] segIds = segmentIds.split(",");
				Set<Long> resultSet = new HashSet<Long>();
				if(segIds.length > 0) {

					List<Long> listOfSegIds = Arrays.stream(segIds).map(Long::parseLong).toList();
					segContacts = getSegmentedContacts(communication, listOfSegIds);

					if(segContacts == null) {
						logger.info("We got exception while connecting with click house DB., so returning null.");
						return; // due to some exception.
					}

					if(segContacts !=null && !segContacts.isEmpty()) {
						//resultList.addAll(segContacts);
						resultSet.addAll(segContacts);
					}

				/*
				 * if(resultList.isEmpty()) { //TODO send alert mail to support for no contacts
				 * in segments. return; }
				 */

			if (resultSet.isEmpty()) {

				logger.info("no active contact in list {} "+ resultSet);

				try {
					

					communicationReport = new CommunicationReport(currentUser.getUserId(), communication.getName(),communication.getMessageContent(),
							LocalDateTime.now(), 0L, 0, 0, 0,0, 0, OCConstants.CR_STATUS_SENDING, fromSource);

					logger.info(">>>>>>> campaign submission is not futher proceeded as found 0 active contact, campaign id : {} " +communication.getCommunicationId());

			
					communicationReport.setStatus(OCConstants.CR_STATUS_SENT);
				 	communicationReport =	communicationReportRepository.save(communicationReport);

					communicationSchedule.setStatus((byte) 1);
					communicationSchedule.setCrId(communicationReport.getCrId());
					scheduleRepository.save(communicationSchedule);

					communicationRepository.updateCommunicationStatus(communication.getCommunicationId());

				} catch (Exception e) {
					logger.info("Exception >>>>>>  {} ", e);
				}

				return;
			}
			// for ensuring coupons are active in coupons campaign.
				Pattern p = Pattern.compile("\\$\\{.*?\\}", Pattern.CASE_INSENSITIVE);

				Matcher match = p.matcher(communication.getMessageContent());
				String tag = new String();
				Set<Long> couponIdSet= new HashSet<>();
				while (match.find()) {

					tag = match.group(0); // .toUpperCase()
					logger.debug("Group 0 : {}", match.group(0));
					tag = tag.replace("${", "").replace("}", "");
					if(tag.startsWith("coupon")) {
						String [] arr=tag.split("_");
						try {
							logger.info("coupon Id :"+arr[1]);
							couponIdSet.add(Long.parseLong(arr[1]));
						}
						catch (Exception e){
							logger.info("Exception while extracting coupon id ",e);
						}
					}
				}
				if(!couponIdSet.isEmpty()){
					List<Coupons> couponsList= couponsRepository.findByCouponIdIn(couponIdSet.stream().collect(Collectors.toList()));
					for(Coupons coupon:couponsList){
						LocalDateTime expiryDate=coupon.getCouponExpiryDate();
						if(expiryDate !=null && expiryDate.isBefore(LocalDateTime.now())){
							logger.info("Found coupon Expired ! ,Stopping the Campaign : "+scheduleObj.getCsId());
							communicationSchedule.setStatus((byte)9); // update failed status
							scheduleRepository.save(communicationSchedule);
							return;
						}
					}
				}

				if (resultSet.size() >= 0) {

					logger.info("num of active contact "+resultSet.size()+" in schedule : "+scheduleObj.getCsId());

					try {

						communicationReport = new CommunicationReport(communication.getUserId(), communication.getName(),communication.getMessageContent(),
								LocalDateTime.now(), 0L, 0, 0, 0,0, 0, OCConstants.CR_STATUS_SENDING, fromSource);


						logger.info(">>>>>>>  campaign submission found with active contact, campaign id : {} ", communication.getCommunicationId());
						communicationSchedule.setStatus((byte)2); // indicates schedule in progress
//						scheduleRepository.save();

						communicationReport.setStatus(OCConstants.CR_STATUS_SENT);
						communicationReport =	communicationReportRepository.save(communicationReport);

						communicationSchedule.setStatus((byte) 1);
						communicationSchedule.setCrId(communicationReport.getCrId());
						scheduleRepository.save(communicationSchedule);

						communicationRepository.updateCommunicationStatus(communication.getCommunicationId());
						communicationReport = communicationReportRepository.save(communicationReport);
						
						logger.info("communication is saved {}",communicationReport.getCrId());


					} catch (Exception e) {
						logger.info("Exception >>>>>> {} ", e);
					}

				}

				//get last sentId;
				//	Long currentSentId = communicationSentRepository.getLastSentId();
				configuredCount = (long) resultSet.size();
					logger.info("scheduleId :"+ scheduleObj.getCsId() +" publishing started time "+LocalDateTime.now());
				resultSet.parallelStream().forEach(contactId -> {

					try {
						logger.info("communication is in try contact {}",communicationReport.getCrId());

						  //contact = contactRepo.findByContactId(contactId);
					} catch (Exception e) {
						logger.error("Exception while fetching contact : "+contactId+" : ",e);
//						continue;
					}
					// ccreate sent object
					CustomCommunication customCommunication = null;
					if(communicationReport != null ) {
						
						logger.info("Sent communication Report ");

						customCommunication = new CustomCommunication(communication.getCommunicationId(),contactId,communicationReport.getCrId(),null);

						template.convertAndSend(MessagingConfigs.EXCHANGE, 
								MessagingConfigs.ROUTING_KEY, 
								customCommunication);

					}

					}); // for loop
					logger.info("scheduleId :"+ scheduleObj.getCsId() +" publishing completed time"+LocalDateTime.now());
					communicationReport.setStatus(OCConstants.CR_STATUS_SENT);
//					communicationReport.setSent(.countByCommunicationReportId(communicationReport.getCrId())); //submiited to msgBird
					communicationReport.setConfigured(configuredCount); //in wa_tempContact
					communicationReport.setSuppressedCount(suppressedCount);
					if(preferenceCount > 0) {

						communicationReport.setPreferenceCount(preferenceCount);
						communicationReport.setBounces(communicationReport.getBounces()+1);
					}
					communicationReportRepository.save(communicationReport);
					communicationSchedule.setStatus((byte) 1);
					try{
					communicationSchedule.setCrId(communicationReport.getCrId());
					scheduleRepository.save(communicationSchedule);
					communicationRepository.updateCommunicationStatus(communication.getCommunicationId());

					} catch(Exception e) {

						logger.info("** Error occured while submitting campaign", e);
					}

			} // if proceed.
				else {
					logger.info("No segment is selected to this communication : "+communication.getName());
				}
		} // try 
		}
		catch (Exception e) {
			logger.info("Exception : ", e);
		}	
		
		logger.info(">>>>>>> Completed Submitter :: sendCommunication <<<<<<<  for "+communication.getName());

	}//sendCommunication



	/*
	 * purpose : To validate and check some points before start sending.
	 * Param : no params
	 * return : boolean value whether to continue sending or break flow. 
	 */
	private boolean validateEmailCommunication() {
		//TODO
		/*
		 * 1. Need to validate based Draft status if the campaign is completed or not
		 * 2. Need to check whether the campaignschedule is in draft !=2(draft status)
		 */
		return true; 
	}
	/*
	 * purpose : To validate and check some points before start sending.
	 * Param : no params
	 * return : boolean value whether to continue sending or break flow. 
	 */
	
	private boolean validateSMSCommunication() {		
		//TODO -Many validation about country type, sms type gateway.
		if(!user.getEnableSms()) {
			Messages messages = new Messages(
					"SMS Campaign",
					"SMS camapign -" + communication.getName()
					+ " can not be reached",
					"SMS package is not enabled for your user account. Please contact Admin to enable SMS feature."
							+ "SMS campaign could not be sent.",
							LocalDateTime.now(), "Inbox", false, "Info",
							user);

			messageRepo.save(messages);
			return false;
		}
		return true;
	}//validateSMSCommunication

	private boolean validateChannelSettings() {

		UserChannelSetting userChannelSettingObj=null;
		if(communication.getSenderId()!=null && !communication.getSenderId().isEmpty()) {
		userChannelSettingObj = userChannelRepo.findByUserIdAndChannelTypeAndSenderId(communication.getUserId(), communication.getChannelType(),communication.getSenderId());
		}else {
		userChannelSettingObj = userChannelRepo.findByUserIdAndChannelType(communication.getUserId(), communication.getChannelType());
		}
		logger.info("userChannelSettingObj value in submitter is {},userChannelSettingObj");
		if (userChannelSettingObj == null) {

			Messages messages = new Messages(
					"Campaign",
					"can not be reached",
					"No UserChannelSetting setup found for this in your user account. Please contact Admin to enable UserChannelSetting feature."
							+ "campaign could not be sent.",
							LocalDateTime.now(), "Inbox", false, "Info",
							user);

			messageRepo.save(messages);

			return false;

		}//validateChannelSettings

		//Gateway info
		Optional<ChannelAccount> channelAccountObj = channelAccountRepo.findById(userChannelSettingObj.getChannelAccount().getId());

		if (channelAccountObj.isEmpty()) {

			Messages messages = new Messages(
					"Campaign",
					"Campaign can not be reached",
					"No ChannelAccount setup found for this in your user account. Please contact Admin to enable ChannelAccount feature."
							+ "campaign could not be sent.",
							LocalDateTime.now(), "Inbox", false, "Info",
							user);

			messageRepo.save(messages);

			return false;

		}


		Optional<ChannelSetting> channelSettingObj = channelSettingRepo.findById(channelAccountObj.get().getChannelSettings().getId());


		if (channelSettingObj.isEmpty()) {

			Messages messages = new Messages(
					"Campaign",
					"Campaign can not be reached",
					"No ChannelSetting setup found for this in your user account. Please contact Admin to enable ChannelSetting feature."
							+ "campaign could not be sent.",
							LocalDateTime.now(), "Inbox", false, "Info",
							user);

			messageRepo.save(messages);

			return false;

		}


		return true; 
	}
	/*
	 * purpose : To validate and check some points before start sending.
	 * Param : no params
	 * return : boolean value whether to continue sending or break flow. 
	 */
	private boolean validateWACommunication() {
		//TODO


		return true;

	}
}
