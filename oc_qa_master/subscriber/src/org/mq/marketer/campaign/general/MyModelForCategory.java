package org.mq.marketer.campaign.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.AbstractListModel;

public class MyModelForCategory extends AbstractListModel  {
	
	private Integer totalSize=null;
	private  Users users = GetUser.getUserObj();
	private SkuFileDao skuFileDao;
	private int chunkSize;
	private String column;
	//private String displayColumn=null;
	private String qryStr=null;
	private Map<Integer, List<Object[]>> chunkCache = new HashMap<>();
	private Map<Integer, List<Object[]>> chunkCacheForFilter = new HashMap<>();

	long orgOwnerUserId=0;
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	public MyModelForCategory(int size,String cloumn,long ownerId){
		this.chunkSize=size;
		this.column=cloumn;
		this.orgOwnerUserId=ownerId;
		 skuFileDao = (SkuFileDao)SpringUtil.getBean("skuFileDao");
	}
	public MyModelForCategory(int size,String column,long ownerId,String qryStr){
		this.chunkSize=size;
		this.column=column;
		this.orgOwnerUserId=ownerId;
		this.qryStr=qryStr;
		 skuFileDao = (SkuFileDao)SpringUtil.getBean("skuFileDao");
	}
	/*@Override
	public Object getElementAt(int index) {
		logger.info("Index  ::"+index);
		int chunkIndex = index / chunkSize;
		List<Object[]> chunkElements = chunkCache.get(chunkIndex);
		if(chunkElements == null) {
			chunkElements = loadChunkFromDB(chunkIndex * chunkSize, chunkSize,column);
			chunkCache.put(chunkIndex, chunkElements);
		}
		logger.info("chunkElements==="+chunkElements.size());
		logger.info("chunkCache==="+chunkCache);
		logger.info("chunkIndex=="+chunkIndex+"===index==="+index);
		logger.info("index % chunkSize==="+index % chunkSize);
		if((chunkElements.size()-1)!=index % chunkSize){
			logger.info("inside if ....");
			return chunkElements.get(index % chunkSize);
		}
		return null;
	}*/
	@Override
	public Object getElementAt(int index) {
		try{
		if(qryStr!=null){
			logger.info("Index  ::"+index);
			int chunkIndex = index / chunkSize;
			List<Object[]> chunkElements = chunkCacheForFilter.get(chunkIndex);
			
			if(chunkElements == null) {
				chunkElements = loadChunkFromDB(chunkIndex * chunkSize, chunkSize, column, qryStr);
				if(index % chunkSize >= chunkElements.size()) return null;
				chunkCacheForFilter.put(chunkIndex, chunkElements);
			}
			logger.info("chunkElements==="+chunkElements.size());
			logger.info("chunkCacheForFilter==="+chunkCacheForFilter);
			logger.info("chunkIndex=="+chunkIndex+"===index==="+index);
			logger.info("index % chunkSize==="+index % chunkSize);
			/*if((chunkElements.size()-1)<=index % chunkSize){
				logger.info("inside if ....");
				return null;
			}*/
			if(index % chunkSize >= chunkElements.size()) return null;

			return chunkElements.get(index % chunkSize);
			
		}
		logger.info("Index  ::"+index);
		int chunkIndex = index / chunkSize;
		List<Object[]> chunkElements = chunkCache.get(chunkIndex);
		if(chunkElements == null) {
			chunkElements = loadChunkFromDB(chunkIndex * chunkSize, chunkSize,column);
			chunkCache.put(chunkIndex, chunkElements);
		}
		/*logger.info("chunkElements==="+chunkElements.size());
		logger.info("chunkCache==="+chunkCache);
		logger.info("chunkIndex=="+chunkIndex+"===index==="+index);
		logger.info("index % chunkSize==="+index % chunkSize);*/
		return chunkElements.get(index % chunkSize);
		}catch(Exception e){
			logger.error("Exception :: ", e);
			return null;
		}
	}

	@Override
	public int getSize() {
		logger.info("*******In getSize method*******");
		if(qryStr!=null){
			logger.info("*****Inside get size and qryStr not null****");
			return loadTotalSize(column,qryStr);
		}
		logger.info("*******Inside get size and qryStr null******");

		return loadTotalSize(column);
	}
	private Integer loadTotalSize(String column){
		logger.info("*******In getloadSize method*******");
		Long size  =  (Long) skuFileDao.executeQuery("Select count(distinct "+column+") From SkuFile WHERE userId = "+orgOwnerUserId+"").get(0);
		logger.info("count   ++"+size);
		if(totalSize==null)
			totalSize=size.intValue();
		return totalSize;
	}
	private Integer loadTotalSize(String displayColumn,String qryStr){
		logger.info("*******In getloadSize method*******");
		Long size  =  (Long) skuFileDao.executeQuery("Select count(distinct "+displayColumn+") From SkuFile WHERE userId = "+orgOwnerUserId+"" + qryStr).get(0);
		logger.info("count in display  ++"+size);
		if(totalSize==null)
			totalSize=size.intValue();
		return totalSize;
	}
	protected List<Object[]> loadChunkFromDB(int startIndex, int size,String column) {
		List<Object[]> results = null;
	//	String tempQry = " SELECT DISTINCT "+column+" FROM SkuFile  WHERE userId = "+users.getUserId().longValue()+" and "+column+" is not null order by 1";
		String tempQry = " SELECT DISTINCT "+column+",count(*) FROM SkuFile  WHERE userId = "+orgOwnerUserId+" and "+column+" is not null group by "+column+" order by 1";
		logger.info("Data query ::"+tempQry);
		results = skuFileDao.executeQuery(tempQry,startIndex,size);
		logger.info("Result  "+results);
		return results;
		// return list;
	}
	protected List<Object[]> loadChunkFromDB(int startIndex, int size,String displayColumn,String qryStr) {
		List<Object[]> results = null;
	//	String tempQry = " SELECT DISTINCT "+column+" FROM SkuFile  WHERE userId = "+users.getUserId().longValue()+" and "+column+" is not null order by 1";
		//String tempQry = " SELECT DISTINCT "+column+",count(*) FROM SkuFile  WHERE userId = "+orgOwnerUserId+" and "+column+" is not null group by "+column+" order by 1";
		String tempQry = " SELECT DISTINCT "+displayColumn+",count(*) FROM SkuFile  WHERE userId = "+orgOwnerUserId+" and "+displayColumn+" is not null";
		if(qryStr!=null){
			tempQry+=qryStr;
		}
		
			tempQry+=" group by "+displayColumn+" order by 1";
		
		
		logger.info("Data query ::"+tempQry);
		results = skuFileDao.executeQuery(tempQry,startIndex,size);
		logger.info("Result  "+results);
		return results;
		// return list;
	}
	public void clearCaches() {
		logger.info("**********Clearing Cache..*******");
		totalSize = null;
		chunkCache.clear();
		chunkCacheForFilter.clear();
	}
	

}
