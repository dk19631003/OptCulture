package org.mq.optculture.model.opySync;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.UpdateOptSyncData;
import org.mq.marketer.campaign.beans.UserOrganization;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDao;
import org.mq.marketer.campaign.dao.UpdateOptSyncDataDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;

/**
 * This class helps to perform database operation's for CreateOptSyncController as well as ManageOptSyncController, hence avoid's direct access of database logic 
 * from controller class.
 * @author vinod.bokare
 */
public class OptSyncService {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private UpdateOptSyncDataDao updateOptSyncDataDao;
	private UpdateOptSyncDataDaoForDML updateOptSyncDataDaoForDML;
	private UsersDao usersDao;
	private UsersDaoForDML usersDaoForDML;

	/**
	 * This method save new optSyncData or Updates the existing data. 
	 * @param updateOptSyncData
	 */
	public void saveOrUpdate(UpdateOptSyncData updateOptSyncData){
		logger.debug(">>>>>>>>>>>>> entered in saveOrUpdate");
		try {
			updateOptSyncDataDao = (UpdateOptSyncDataDao)ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			updateOptSyncDataDaoForDML = (UpdateOptSyncDataDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO_FOR_DML);
			//updateOptSyncDataDao.saveOrUpdate(updateOptSyncData);
			updateOptSyncDataDaoForDML.saveOrUpdate(updateOptSyncData);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed saveOrUpdate ");
	}//saveOrUpdate

	/**
	 * This method delete's optSyncPlugin. 
	 * @param updateOptSyncData
	 */
	public void deleteOptSyncPlugin(UpdateOptSyncData updateOptSyncData){
		logger.debug(">>>>>>>>>>>>> entered in deleteOptSyncPlugin");
		try {
			updateOptSyncDataDao = (UpdateOptSyncDataDao)ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			updateOptSyncDataDaoForDML = (UpdateOptSyncDataDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO_FOR_DML);
			//updateOptSyncDataDao.delete(updateOptSyncData);
			updateOptSyncDataDaoForDML.delete(updateOptSyncData);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed deleteOptSyncPlugin ");
	}//deleteOptSyncPlugin

	/**
	 * This method find OptSyncPlugin from generated plugIn, and if found then loop again generate plugIn
	 * @param generateOptSyncId
	 * @return
	 */
	public List<UpdateOptSyncData> getAllOptSyncPluginByPluginId(long generateOptSyncId) {
		logger.debug(">>>>>>>>>>>>> entered in getAllOptSyncPluginByPluginId");
		List<UpdateOptSyncData>  updateOptSyncDataList = null;
		try {
			updateOptSyncDataDao = (UpdateOptSyncDataDao)ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			updateOptSyncDataList =  updateOptSyncDataDao.findAllByOptSynId(generateOptSyncId);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed getAllOptSyncPluginByPluginId ");
		return updateOptSyncDataList;
	}//getAllOptSyncPluginByPluginId

	/**
	 * This method updates the optSyncPluginStatus.
	 * @param pluginStatus
	 * @param optSyncId
	 * @return
	 */
	public int updatePluginStatus(String pluginStatus, Long optSyncId) {
		logger.debug(">>>>>>>>>>>>> entered in updatePluginStatus");
		int rowsEffected =0;
		try {
			updateOptSyncDataDao = (UpdateOptSyncDataDao)ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			updateOptSyncDataDaoForDML = (UpdateOptSyncDataDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO_FOR_DML);
			//rowsEffected =  updateOptSyncDataDao.updatePluginStatus(pluginStatus, optSyncId);
			rowsEffected =  updateOptSyncDataDaoForDML.updatePluginStatus(pluginStatus, optSyncId);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed updatePluginStatus ");
		return rowsEffected;
	}//updatePluginStatus

	/**
	 * This method get's UserOrganizationList based on optSyncKey
	 * @param optSyncAuth
	 * @return userOrganizations
	 */
	public List<UserOrganization> getAllUserOrganizationByOptSyncKey(String optSyncAuthKey) {
		logger.debug(">>>>>>>>>>>>> entered in getAllUserOrganizationByOptSyncKey");
		List<UserOrganization> userOrganizations = null;
		try {
			updateOptSyncDataDao = (UpdateOptSyncDataDao)ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			userOrganizations = updateOptSyncDataDao.findAllByOptSyncName(optSyncAuthKey);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed getAllUserOrganizationByOptSyncKey ");
		return userOrganizations;
	}//getAllUserOrganizationByOptSyncKey

	/**
	 * This method find's all the organization details.
	 * @return
	 */
	public List<UserOrganization> findAllOrganizations() {
		logger.debug(">>>>>>>>>>>>> entered in findAllOrganizations");
		List<UserOrganization> userOrganizations = null;
		try {
			usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			userOrganizations = usersDao.findAllOrganizations();
		} catch (Exception e) {
			userOrganizations = null;
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed findAllOrganizations ");
		return userOrganizations;
	}//findAllOrganizations

	/**
	 * This method get's all the users based on userOrgaization.
	 * @param userOrgId
	 * @return users
	 */
	public List<Users> getAllUsersByOrganizationId(Long userOrgId) {
		logger.debug(">>>>>>>>>>>>> entered in getAllUsersByOrganizationId");
		List<Users> users = null;
		try {
			usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			users = usersDao.getPrimaryUsersByOrg(userOrgId);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed getAllUsersByOrganizationId ");
		return users;
	}//getAllUsersByOrganizationId

	/**
	 * This method updates the optSynckey based on organizationId.
	 * @param organizationId
	 * @param optSyncAuthkey
	 */
	public void updateOptSynKeyByOrgId(long organizationId, String optSyncAuthkey) {
		logger.debug(">>>>>>>>>>>>> entered in updateOptSynKeyByOrgId");
		try {
			usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			usersDaoForDML = (UsersDaoForDML)ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.USERS_DAOForDML);
			//usersDao.updateOptSynKey(organizationId, optSyncAuthkey);
			usersDaoForDML.updateOptSynKey(organizationId, optSyncAuthkey);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed updateOptSynKeyByOrgId ");
	}//updateOptSynKey

	/**
	 * This method find's UserOrganization based on OrgId.
	 * @param orgId
	 * @return UserOrganization
	 */
	public UserOrganization findByOrgId(Long orgId) {
		logger.debug(">>>>>>>>>>>>> entered in findByOrgId");
		UserOrganization userOrganization =null;
		try {
			usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			userOrganization = usersDao.findByOrgId(orgId);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed findByOrgId ");
		return userOrganization;
	}//findByOrgId

	/**
	 * This method find's User based on userId.
	 * @param userId
	 * @return User
	 */
	public Users findByUserId(Long userId) {
		logger.debug(">>>>>>>>>>>>> entered in findByUserId");
		Users users = null;
		try {
			usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			users = usersDao.findByUserId(userId);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed findByUserId ");
		return users;
	}//findByUserId

	/**
	 * This method find's userOrganization by optSyncAuthKey.
	 * @param generateString
	 * @return userOrganization
	 */
	public UserOrganization getUserOrgByOptSyncAuthKey(String generatedOptSyncKey) {
		logger.debug(">>>>>>>>>>>>> entered in findUserOrgByOptSyncAuthKey");
		UserOrganization userOrganization =  null;
		try {
			usersDao = (UsersDao)ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
			userOrganization = usersDao.findUserOrgByOptSyncAuthKey(generatedOptSyncKey);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<<<<<<<<<< completed findUserOrgByOptSyncAuthKey ");
		return userOrganization;
	}

	/*public List<UserOrganization> findAllOrgByOptSyncAuthKey(String optSyncAuth) {
		// TODO Auto-generated method stub
		return null;
	}*/
	/**
	 * This method update's Alert Sending's Status.
	 * @param onAlertsBy
	 * @param optSyncId
	 * @return noOfRowsUpdated
	 */
	public int updateAlertSendingsStatus(String onAlertsBy, Long optSyncId) {
		logger.debug(">>>>>>> Started  updateAlertSendingsStatus :: ");
		UpdateOptSyncDataDao updateOptSyncDataDao = null;
		int noOfRowsUpdated = 0;
		try {
			updateOptSyncDataDao =  (UpdateOptSyncDataDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			updateOptSyncDataDaoForDML =  (UpdateOptSyncDataDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO_FOR_DML);
			//noOfRowsUpdated = updateOptSyncDataDao.updateAlertSendingStatus(onAlertsBy,optSyncId);
			noOfRowsUpdated = updateOptSyncDataDaoForDML.updateAlertSendingStatus(onAlertsBy,optSyncId);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<< Completed updateAlertSendingsStatus .");
		return noOfRowsUpdated;
	}//updateAlertSendingsStatus

	public UserOrganization findUserOrgByPluginId(Long optSyncId) {
		logger.debug(">>>>>>> Started  findUserOrgByPluginId :: ");
		UpdateOptSyncDataDao updateOptSyncDataDao = null;
		UsersDao usersDao =  null;
		UserOrganization userOrganization = null;
		UpdateOptSyncData updateOptSyncData = null;
		try {
			updateOptSyncDataDao =  (UpdateOptSyncDataDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			updateOptSyncData = updateOptSyncDataDao.findOptSyncByPluginId(optSyncId);
			if(updateOptSyncData != null){
				usersDao =(UsersDao) ServiceLocator.getInstance().getDAOByName(OCConstants.USERS_DAO);
				userOrganization = usersDao.findByOrgId(updateOptSyncData.getOrgId());
				logger.debug("<<<<< Completed findUserOrgByPluginId .");
				return userOrganization;
			}
			else{
				logger.debug("<<<<< Completed findUserOrgByPluginId .");
				return userOrganization;
			}
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
			logger.debug("<<<<< Completed findUserOrgByPluginId .");
			return userOrganization;
		}
		
	}//findUserOrgByPluginId
	/**
	 * This method Checks UpdateOptSyncData based on userId
	 * @param userId
	 * @return updateOptSyncDataList
	 */
	public List<UpdateOptSyncData> findOptSyncByUserId(Long userId) {
		logger.debug(">>>>>>> Started  findOptSyncByUserId :: ");
		UpdateOptSyncDataDao updateOptSyncDataDao = null;
		List <UpdateOptSyncData> updateOptSyncDataList = null;
		try {
			updateOptSyncDataDao =  (UpdateOptSyncDataDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			updateOptSyncDataList = updateOptSyncDataDao.findOptSyncByUserId(userId);
			
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		logger.debug("<<<<< Completed findOptSyncByUserId .");
		return updateOptSyncDataList;
	}//findOptSyncByUserId

	/**
	 * This method update's OptSync Monitoring
	 * @param userId
	 * @param optSyncEnableMointoring
	 * @return rowsEffected
	 */
	public int updateOptSyncMonitoring(Long userId, String optSyncEnableMointoring) {
		int rowsEffected = 0;
		UpdateOptSyncDataDao updateOptSyncDataDao = null;
		UpdateOptSyncDataDaoForDML updateOptSyncDataDaoForDML = null;
		try {
			updateOptSyncDataDao =  (UpdateOptSyncDataDao) ServiceLocator.getInstance().getDAOByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO);
			updateOptSyncDataDaoForDML =  (UpdateOptSyncDataDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.UPDATE_OPTSYNC_DATA_DAO_FOR_DML);
			//rowsEffected = updateOptSyncDataDao.updateOptSyncMonitoring(userId,optSyncEnableMointoring);
			rowsEffected = updateOptSyncDataDaoForDML.updateOptSyncMonitoring(userId,optSyncEnableMointoring);
		} catch (Exception e) {
			logger.error("Exception in processing :: ",e);
		}
		return rowsEffected;
	}

}//OptSyncService
