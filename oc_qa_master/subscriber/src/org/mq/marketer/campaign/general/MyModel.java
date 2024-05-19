package org.mq.marketer.campaign.general;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ext.Sortable;

public class MyModel extends AbstractListModel   {
	
	private static final long serialVersionUID = 1L;
	
	private Integer totalSize=null;
	private  Users users = GetUser.getUserObj();
	private SkuFileDao skuFileDao;
	private int chunkSize;
	public MyModel(int size){
		 skuFileDao = (SkuFileDao)SpringUtil.getBean("skuFileDao");
		 this.chunkSize=size;
	}
	private Map<Integer, List<Object[]>> chunkCache = new HashMap<>();
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	@Override
	public Object[] getElementAt(int index) {
		//logger.info("Index  ::"+index);
		int chunkIndex = index / chunkSize;
		List<Object[]> chunkElements = chunkCache.get(chunkIndex);
		if(chunkElements == null) {
			chunkElements = loadChunkFromDB(chunkIndex * chunkSize, chunkSize);
			chunkCache.put(chunkIndex, chunkElements);
		}
		return chunkElements.get(index % chunkSize);
	}
	protected List<Object[]> loadChunkFromDB(int startIndex, int size) {
		List<Object[]> results = null;
		//String query = " FROM SkuFile  WHERE userId =  "+users.getUserId().longValue()+"  And sku is not null ";
		String query = " select sku,listPrice,description, count(*) from SkuFile where userId =  "+users.getUserId().longValue()+"  And sku is not null  group by sku  ";
		results= skuFileDao.executeQuery(query,startIndex,size);
		//logger.info("Result List::: "+results);
		return results;
		// return list;
	}

	@Override
	public int getSize() {
		if(totalSize == null) {
			totalSize = loadTotalSize().intValue();
		}
		return totalSize;
	}
	
	private Long loadTotalSize(){
		return (Long) skuFileDao.executeQuery("Select count(distinct sku) From SkuFile WHERE userId = "+users.getUserId().longValue()+" And sku is not null").get(0);
	}
	
	public void clearCaches() {
		 totalSize = null;
		chunkCache.clear();
	}
	
}
