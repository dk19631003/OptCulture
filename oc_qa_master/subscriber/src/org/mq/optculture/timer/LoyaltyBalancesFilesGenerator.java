package org.mq.optculture.timer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SparkBaseLocationDetails;
import org.mq.marketer.campaign.beans.UpdateOptSyncData;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.ContactsLoyaltyDao;
import org.mq.marketer.campaign.dao.SparkBaseLocationDetailsDao;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.model.loyalty.LoyaltyDumpBalances;
import org.mq.optculture.model.loyalty.LoyaltyDumpContactInfo;
import org.mq.optculture.model.loyalty.LoyaltyDumpData;
import org.mq.optculture.model.loyalty.LoyaltyDumpDataRecord;
import org.mq.optculture.model.loyalty.LoyaltyDumpIndex;
import org.mq.optculture.model.loyalty.LoyaltyIndex;
import org.mq.optculture.model.loyalty.LtyBalanceType;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.mq.optculture.utils.XMLUtil;

public class LoyaltyBalancesFilesGenerator extends TimerTask{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override	
	public void run() {
		logger.info("Started creating loyalty balances dump...");
		generateXMLDump();
		logger.info("Completed creating loyalyt balances dump file...");
	}
	
	private void generateXMLDump() {
		logger.info("Generating xml dump file ...");
		String dumpFilePath = PropertyUtil.getPropertyValue("outboxloyaltydatadump");
		//	logger.info("dumpFilePathdumpFilePath::::"+dumpFilePath); 
		LoyaltyDumpIndex loyaltyindexes = null;

		List<SparkBaseLocationDetails> sblist = getActiveSBList();
		for(SparkBaseLocationDetails sbloc : sblist){

			try{

				UserOrganization userOrg = sbloc.getUserOrganization();

				if(!isOptSyncEnabled(userOrg.getUserOrgId())) continue;

				logger.info("Generating for organization...."+userOrg.getOrgExternalId());

				createOrgIdFolder(userOrg.getOrgExternalId(), dumpFilePath);

				StringBuilder orgDumpPath = new StringBuilder(dumpFilePath).append("/").append(userOrg.getOrgExternalId());
				
				File file = new File(orgDumpPath.toString());
				File[] files  = file.listFiles();
				for(File file2 : files){
					// logger.info("File2" + file2.getName());
					if(file2.getName().startsWith("LoyaltyDump")) {
						//	logger.info("inside delete");
						file2.delete();

					}}
				
				//	logger.info("orgDumpPathorgDumpPath :: "+orgDumpPath);
				String indexfileName = "LoyaltyDump_index_"+userOrg.getOrgExternalId()+".xml";

				loyaltyindexes = new LoyaltyDumpIndex(); 
				loyaltyindexes.setOrgId(userOrg.getOrgExternalId());
				loyaltyindexes.setUserName("");
				loyaltyindexes.setLocationId(sbloc.getLocationId());
				//	logger.info("indexfileNameindexfileName "+indexfileName);
				List<LoyaltyIndex> indexfiles = new ArrayList<LoyaltyIndex>();
				String dumpfileName = null;

				String userIdStr = getOrgIdStr(userOrg.getUserOrgId());
				if(userIdStr == null) continue;

				int startIndex = 0;
				int pageSize = 1000;
				int count = pageSize;
				while(count >= pageSize){

					logger.info("records start index ="+startIndex+" pageSize ="+pageSize);

					List<Object[]> records = getRecords(userIdStr, startIndex, pageSize);

					if(records != null){
						logger.info("no of records fetched..."+records.size());
					}
					else {
						logger.info("no of records fetched...null");
						break;
					}

					count = records.size();
					startIndex += pageSize;
					//create a index file name
					LoyaltyIndex indexfile = null;

					if(records.size() > 0){
						dumpfileName = "LoyaltyDump_"+records.get(0)[0].toString()+"_"+userOrg.getOrgExternalId();
						indexfile = new LoyaltyIndex();
						indexfile.setMinCardNumber(records.get(0)[0].toString());
						indexfile.setMaxCardNumber(records.get(count-1)[0].toString());
						indexfile.setFileName(dumpfileName);
						indexfiles.add(indexfile);
					}
					else break;
					//	logger.info("dumpfileNamedumpfileName :: "+dumpfileName);

					


						//writeRecordsTofile(bwdump, records);
						writeRecordsToXML(records, orgDumpPath+"/"+dumpfileName+".xml", userOrg.getOrgExternalId(), "", sbloc.getLocationId());
						

					if(indexfiles.size() >0){
						loyaltyindexes.setDumpFileName(indexfiles);
						XMLUtil.marshalAndWriteToFileAsXML(orgDumpPath+"/"+indexfileName, 
								LoyaltyDumpIndex.class, loyaltyindexes);
					}
				}
			}catch(Exception e){
				logger.error("Exception ...", e);
				continue;
			}

		}

		logger.info("Completed generating xml dump file ...");
	}
	
	
	private void writeRecordsToXML(List<Object[]> records, String filepath, String orgId, String userName, String locationId){
		logger.info("Entered..writeRecordsToXML method...");
		LoyaltyDumpData dumpData = null;
		List<LoyaltyDumpDataRecord> listOfCards = new ArrayList<LoyaltyDumpDataRecord>();
		LoyaltyDumpDataRecord loyaltyCard = null;
		LoyaltyDumpBalances balancesInfo;
		LoyaltyDumpContactInfo contactInfo;
		LtyBalanceType ptsBalance = null;
		LtyBalanceType amtBalance = null;
		
		//      0				1				2					3				4				5			6		
		//cl.card_number, cl.card_pin, cl.loyalty_balance, cl.giftcard_balance, c.external_id, c.email_id, c.mobile_phone
		for(Object[] objArr : records){
			//logger.info("card number = "+objArr[0]);
			//set balance info
			ptsBalance = new LtyBalanceType();
			ptsBalance.setValueCode("Points");
			ptsBalance.setValue(objArr[2] == null ? "" : objArr[2].toString());
			
			amtBalance = new LtyBalanceType();
			amtBalance.setValueCode("USD");
			amtBalance.setValue(objArr[3] == null ? "" : objArr[3].toString());
			
			List<LtyBalanceType> balanceTypes = new ArrayList<LtyBalanceType>();
			balanceTypes.add(ptsBalance);
			balanceTypes.add(amtBalance);
			balancesInfo = new LoyaltyDumpBalances();
			balancesInfo.setBalanceTypes(balanceTypes);
			
			//set contact info
			contactInfo = new LoyaltyDumpContactInfo();
			contactInfo.setCustomerId(objArr[4] == null ? "" : objArr[4].toString());
			contactInfo.setEmailId(objArr[5] == null ? "" : objArr[5].toString());
			contactInfo.setPhone(objArr[6] == null ? "" : objArr[6].toString());
			
			//setcardinfo
			loyaltyCard = new LoyaltyDumpDataRecord();
			loyaltyCard.setBalanceInfo(balancesInfo);
			loyaltyCard.setContactInfo(contactInfo);
			loyaltyCard.setCardNumber(objArr[0] == null ? "" : objArr[0].toString());
			loyaltyCard.setPin(objArr[1] == null ? "" : objArr[1].toString());
			listOfCards.add(loyaltyCard);
			
		}
		
		dumpData = new LoyaltyDumpData();
		dumpData.setOrgId(orgId);
		dumpData.setUserName(userName);
		dumpData.setLocationId(locationId);
		dumpData.setLoyaltyInfoRecords(listOfCards);
		
		try{
			XMLUtil.marshalAndWriteToFileAsXML(filepath, LoyaltyDumpData.class, dumpData);
		}catch(Exception e){
			logger.error("Exception ....", e);
		}
		
		logger.info("Completed..writeRecordsToXML method...");
	}
	
	
	private List<Object[]> getRecords(String userIdStr, int startIndex, int pageSize) throws Exception{
		
		List<Object[]> recordsArr = null;
		ContactsLoyaltyDao loyaltyDao = (ContactsLoyaltyDao)ServiceLocator.getInstance().getDAOByName(OCConstants.CONTACTS_LOYALTY_DAO);
		recordsArr = loyaltyDao.getLoyaltyCardsByuserIdStr(userIdStr, startIndex, pageSize);
		return recordsArr;
		
	}
	
	
	
	private String getOrgIdStr(Long orgId) throws Exception {
		UsersDao usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
		List<Users> userList = usersDao.getUsersListByOrg(orgId);
		String userIdStr = null;
		for(Users users : userList){
			if(userIdStr == null){
				userIdStr = ""+users.getUserId();
			}
			else{
				userIdStr += ","+users.getUserId();
			}
		}
		return userIdStr;
	}
	
	private void createOrgIdFolder(String orgExtId, String dumpFilePath) {
		File orgPath = new File(dumpFilePath+"/"+orgExtId);
		if(orgPath.exists()){
			for(File file : orgPath.listFiles()){
//				if(file.getName().startsWith("LoyaltyDump")) file.delete();
			}
		}
		else{
			orgPath.mkdirs();
		}
	}
	
	private List<SparkBaseLocationDetails> getActiveSBList(){
		List<SparkBaseLocationDetails> sblist = null;
		
		try{
		
		SparkBaseLocationDetailsDao sblocDao = 
				(SparkBaseLocationDetailsDao)ServiceLocator.getInstance().getDAOByName(OCConstants.SPARKBASE_LOCATIONDETAILS_DAO);
		sblist = sblocDao.findAllActiveLoc();
		}catch(Exception e){
			logger.error("Exception in get active sb list...", e);
		}
		return sblist;
	}
	
	private boolean isOptSyncEnabled(Long orgId){
		
		boolean optSyncStatus = false;
		try{
		
			UpdateOptSyncDataDao optSyncDao = (UpdateOptSyncDataDao)ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			List<UpdateOptSyncData> optSyncList = optSyncDao.findByOrgId(orgId);
			if(optSyncList != null){
				for(UpdateOptSyncData optSync : optSyncList){
					if("Y".equalsIgnoreCase(optSync.getEnabledOptSyncFlag())){
						optSyncStatus = true;
						return optSyncStatus;
					}
				}
			}
		}catch(Exception e){
			logger.error("exception ...", e);
			optSyncStatus = false;
		}
		return optSyncStatus;
	}
	
}
