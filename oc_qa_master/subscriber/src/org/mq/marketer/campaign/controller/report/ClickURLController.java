package org.mq.marketer.campaign.controller.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.dao.ClicksDao;
import org.mq.marketer.campaign.general.Constants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Column;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Rows;

@SuppressWarnings("serial")
public class ClickURLController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	Desktop desktop = null;
	ClicksDao clicksDao;
	CampaignReport campaignReport; 
	
	private Grid viewClickUrlsGridId;
	private Footer uniqueClicksLBFId, totClicksLBFId;
	private Column urlNameColId, uniqueClickColId, totalClickColId;
	
	public static String clickUrlColName = ""; 
           
	public ClickURLController() {
		
		clicksDao = (ClicksDao)SpringUtil.getBean("clicksDao");
		campaignReport = (CampaignReport)
						Sessions.getCurrent().getAttribute("campaignReport");
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
		List<Object[]> objlist = getClickReport();
		
		List<Url> urlList = new ArrayList<Url>();
		
		for(Object[] eachObj : objlist){
			
			urlList.add(new Url(eachObj[0].toString(), Integer.parseInt(eachObj[1].toString().trim()), Integer.parseInt(eachObj[2].toString().trim())));
		}
		
		ListModel urlModel = new ListModelList(urlList);
		RowRenderer rowRenderer = new MyRowRenderer();
		viewClickUrlsGridId.setModel(urlModel); 		
		viewClickUrlsGridId.setRowRenderer(rowRenderer);
		
		setFooter(uniqueClicksLBFId,totClicksLBFId);
	}
	
	
	public void onSort$urlNameColId(SortEvent event){
		
		clickUrlColName = "urlNameColId";
		
	 }
	
	public void onSort$uniqueClickColId(SortEvent event){
		
		clickUrlColName = "uniqueClickColId";
		
	 }
	public void onSort$totalClickColId(SortEvent event){
		
		clickUrlColName = "totalClickColId";
		
	 }
	
	public List<Object[]> getClickReport() {
		
		logger.info("Just Entered ...");
		try {
			
			if(campaignReport == null)
				campaignReport = (CampaignReport)desktop.getAttribute("campaignReport");
			
			long crId = campaignReport.getCrId();
			Set<String> urls = campaignReport.getUrls();
			
			logger.info("--Getting the  Url Click report for : "+urls +" of crId -"+crId);
			return clicksDao.getClickList(crId,urls,urls.size());
		} 
		catch(Exception e) {
			logger.error("** Exception : Problem while getting the Links info - "+ e + "**");
			return null;
		}
   }
	
	public void setFooter(Footer uniqueClicksLBFId,Footer totClicksLBFId) {
		
		if(campaignReport == null)
			campaignReport = (CampaignReport)desktop.getAttribute("campaignReport");
		
		long crId = campaignReport.getCrId();
		//uniqueClicksLBFId.setLabel(""+clicksDao.getTotClickCount(crId,"unique"));
		totClicksLBFId.setLabel(""+clicksDao.getTotClickCount(crId,"total"));
	}
	
	
	private class MyRowRenderer implements RowRenderer {

		Label lbl;
		@Override
		public void render(Row row, Object data, int arg2) throws Exception {
			Url item = (Url) data;
			lbl = new Label(item.getUrlname());
			lbl.setTooltiptext(item.getUrlname());
			lbl.setParent(row);
			
			new Label(item.getUnique()+"").setParent(row);
			new Label(item.getTotal()+"").setParent(row);
			
		}
		
	}
	
}

  class Url implements Comparable {
	
	private String urlname;
	private int unique;
	private int total;

	public Url(String urlname, int unique, int total) {
		super();
		this.urlname = urlname;
		this.unique = unique;
		this.total = total;
	}

	public String getUrlname() {
		return urlname;
	}
	public void setUrlname(String urlname) {
		this.urlname = urlname;
	}
	public int getUnique() {
		return unique;
	}
	public void setUnique(int unique) {
		this.unique = unique;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	
	public int compareTo(Object obj) throws ClassCastException {
		if (!(obj instanceof Url))
			throw new ClassCastException("Error msg here.");
		Url Itemobj = (Url) obj;
		
		if(ClickURLController.clickUrlColName.equals("uniqueClickColId")){
			return new Integer(this.unique).compareTo(Itemobj.getUnique());
		}
		else if(ClickURLController.clickUrlColName.equals("totalClickColId")){
			return new Integer(this.total).compareTo(Itemobj.getTotal());	
		}
		else if(ClickURLController.clickUrlColName.equals("urlNameColId")){
			return this.getUrlname().compareTo(Itemobj.getUrlname());	
		}
		else return 0;
		
	}
	
	
}






