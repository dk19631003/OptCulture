package org.mq.marketer.campaign.general;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.components.MyListComparator;
import org.mq.marketer.campaign.custom.MyDatebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.SortEvent;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Menuseparator;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.event.PagingEvent;


public class LBFilterEventListener implements EventListener {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public static final String FROM_DB="FROM_DB";
	public static final String HANDLE_ON_PAGING="HANDLE_ON_PAGING";
	public static final String COLINDEX="COLINDEX";
	public static final String FILTER_MENUPOPUP="FILTER_MENUPOPUP";
	public static final String FILTER_BUTTON="FILTER_BUTTON";
	public static final String FILTER_ENUM="FILTER_ENUM";
	public static final String FOOTER="footer";

	
	private Listbox filterLB;
	private Listfoot footerLB;
	private Paging paging;
	private Paging paging1;
	private String query;
	private String countQuery;
	private String qryPrefix;
	private Menupopup mp;
	
	private String sortQry;
	private Grid filterGd;
	
	
	private LBFilterEventListener(Grid filterGd, Listbox filterLB, Paging paging, String query, String countQuery, String qryPrefix) {
		this.filterGd = filterGd;
		this.filterLB = filterLB;
		this.paging = paging;
		this.query = query;
		this.countQuery = countQuery;
		this.qryPrefix = qryPrefix;
	}
	
	private LBFilterEventListener(Grid filterGd, Listbox filterLB, Paging paging, Paging paging1,String query, String countQuery, String qryPrefix) {
		this.filterGd = filterGd;
		this.filterLB = filterLB;
		this.paging = paging;
		this.paging1 = paging1;
		this.query = query;
		this.countQuery = countQuery;
		this.qryPrefix = qryPrefix;
	}

	private LBFilterEventListener(Grid filterGd, Listbox filterLB, Paging paging, String query, String countQuery, String qryPrefix, Listfoot footerLB) {
		this.filterGd = filterGd;
		this.filterLB = filterLB;
		this.paging = paging;
		this.query = query;
		this.countQuery = countQuery;
		this.qryPrefix = qryPrefix;
		this.footerLB = footerLB;
		
	}
	
	private LBFilterEventListener(Grid filterGd, Listbox filterLB, Paging paging, Paging paging1,String query, String countQuery, String qryPrefix, Listfoot footerLB) {
		this.filterGd = filterGd;
		this.filterLB = filterLB;
		this.paging = paging;
		this.paging1 = paging1;
		this.query = query;
		this.countQuery = countQuery;
		this.qryPrefix = qryPrefix;
		this.footerLB = footerLB;
		
	}
	
	public static LBFilterEventListener lbFilterSetup(Listbox filterLB) {
		return lbFilterSetup(filterLB, null, null, null, null, null);
	}


	public static LBFilterEventListener lbFilterSetup(Listbox filterLB,  Paging paging, 
			String query,String countQuery, String qryPrefix, Map<Integer, Field> objMap) {
		
		//TODO need to check if same kind of object is already added as event listener
		return filterLBSetup(null, filterLB, paging, query, countQuery, qryPrefix, objMap);
	}
	
	public static LBFilterEventListener lbFilterSetup1(Listbox filterLB,  Paging paging,  Paging paging1,
			String query,String countQuery, String qryPrefix, Map<Integer, Field> objMap) {
		
		//TODO need to check if same kind of object is already added as event listener
		return filterLBSetup1(null, filterLB, paging, paging1,query, countQuery, qryPrefix, objMap);
	}
	
	
	 public static LBFilterEventListener grFilterSetup(Grid filterG,  Paging paging, 
				String query,String countQuery, String qryPrefix, Map<Integer, Field> objMap) {
			
			//TODO need to check if same kind of object is already added as event listener
			return filterLBSetup(filterG, null,paging, query, countQuery, qryPrefix, objMap);
		}
	
	public Listbox getFilterLB() {
		return filterLB;
	}

	public void setFilterLB(Listbox filterLB) {
		this.filterLB = filterLB;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}
	
	public Paging getPaging1() {
		return paging1;
	}

	public void setPaging1(Paging paging1) {
		this.paging1 = paging1;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getCountQuery() {
		return countQuery;
	}

	public void setCountQuery(String countQuery) {
		this.countQuery = countQuery;
	}

	public String getQryPrefix() {
		return qryPrefix;
	}

	public void setQryPrefix(String qryPrefix) {
		this.qryPrefix = qryPrefix;
	}

	public Menupopup getMp() {
		return mp;
	}

	public void setMp(Menupopup mp) {
		this.mp = mp;
	}

	public String getSortQry() {
		return sortQry;
	}

	public void setSortQry(String sortQry) {
		this.sortQry = sortQry;
	}

	
	public Grid getFilterGd() {
		return filterGd;
	}

	public void setFilterGd(Grid filterGd) {
		this.filterGd = filterGd;
	}

	private void createAndAssignMenupopup() {
		
		try {
			Listhead lh = filterLB.getListhead();
			if(mp==null && lh.getMenupopup()!=null) {
				
				mp = (Menupopup)lh.getFellowIfAny(lh.getMenupopup());
				//logger.info("Get Menupopup -------- = "+mp);
			}
			
			if(mp==null) return;
			
			List<Component> lhList = lh.getChildren();
			lh.getMenupopup();
			Listheader tempLheader=null;
			Menuitem tempMi=null;
			
			Components.removeAllChildren(mp);
			 
			for (int i = 0; i < lhList.size(); i++) {
				tempLheader = (Listheader)lhList.get(i);
				tempMi = new Menuitem(tempLheader.getLabel());
				tempMi.setAutocheck(true);
				tempMi.setCheckmark(true);
				tempMi.setChecked(tempLheader.isVisible());
				tempMi.setAttribute(COLINDEX, new Integer(i));
				tempMi.setParent(mp);
				tempMi.addEventListener("onClick", this);
			} // for
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		
	}
	
	
	private static LBFilterEventListener filterLBSetup(Grid filterGD,Listbox filterLB, Paging paging,  
			String query,String countQuery, String qryPrefix, Map<Integer, Field> objMap) {
		
		//LBFilterEventListener myLbFilter = new LBFilterEventListener(filterGD, filterLB, paging, query, countQuery, qryPrefix);
		LBFilterEventListener myLbFilter;
		if(filterLB != null && filterLB.getAttribute(FOOTER) != null){
			Listfoot footerLBId = (Listfoot) filterLB.getAttribute(FOOTER);
			myLbFilter = new LBFilterEventListener(filterGD, filterLB, paging, query, countQuery, qryPrefix, footerLBId);
		}else{
			myLbFilter = new LBFilterEventListener(filterGD, filterLB, paging, query, countQuery, qryPrefix);
		}
		
		if(paging!=null) {
			String handlePaging = (String)paging.getAttribute(HANDLE_ON_PAGING);

			if(handlePaging==null || handlePaging.equalsIgnoreCase("true")) {
				paging.addEventListener("onPaging", myLbFilter);
			}
		}
		
		// Check if ojbMap is available
		if(objMap==null) {
			objMap = new HashMap<Integer, Field>(); // Dummy object
		}
		
		logger.info("ObjMap="+objMap);
		if(filterGD != null){
			Columns lh = filterGD.getColumns();
			List<Component> lhList = lh.getChildren();
		for (int i=0; i < lhList.size(); i++ ) {
			
			Column eachColumn = (Column)lhList.get(i);
			if(!(eachColumn instanceof Column)) continue;
			Field tempField = objMap.get(i+1);
			if(tempField != null) {
				
				eachColumn.setAttribute("colName", tempField);
				try{
				eachColumn.setSort("auto");
				}catch(Exception e){
					logger.error("Exception ",e);
				}
				//eachColumn.setStyle("curson:pointer");
				eachColumn.addEventListener("onSort", myLbFilter);
				//eachColumn.addEventListener("onClick", myLbFilter);
			}
			
		}
		return myLbFilter;
		}
		
		// Check for fetch data by Query
				if(query!=null && query.trim().length() > 0) {
					
					filterLB.setAttribute(FROM_DB, "true");
				}
		
		Listhead lh = filterLB.getListhead();
		List<Component> lhList = lh.getChildren();
		
		for (int i=0; i < lhList.size(); i++ ) {

			Listheader eachHeader = (Listheader)lhList.get(i);
			if(!(eachHeader instanceof Listheader) ) continue;
			
			//logger.info(">>>>>>>>>>>>>> Adding LH Listener" + objMap.containsKey(i));
			Field tempField = objMap.get(i);
			
			if(query==null || query.trim().isEmpty()) { // Client side sorting
				logger.info("Client side sorting");
				if(objMap.containsKey(i)) {
					eachHeader.setSortAscending(new MyListComparator(true, tempField, i));
					eachHeader.setSortDescending(new MyListComparator(false, tempField, i));
				}
				continue;
			}
			else if(tempField != null) {
				
				eachHeader.setAttribute("colName", tempField);
				eachHeader.setSort("auto");
				eachHeader.addEventListener("onSort", myLbFilter);
			} 
		} // for

		
		// Check for List header MenuPopup options
		myLbFilter.createAndAssignMenupopup();
		
		
		Collection<Component> hedderList = filterLB.getHeads();
		for (Component eachHeader : hedderList) {
			if(!(eachHeader instanceof Auxhead)) continue;
			
			List<Component> childList = eachHeader.getChildren();
			for (int i=0; i<childList.size(); i++ ) {
				Component eachAuxheader = childList.get(i);
				
				if(!(eachAuxheader instanceof Auxheader)) continue;
				
				List<Component> auxChildList = eachAuxheader.getChildren();
				if(auxChildList.isEmpty()) continue;
				
				for (int j=0; j<auxChildList.size(); j++ ) {
					Component eachComp = auxChildList.get(j);
					
					if(eachComp instanceof Toolbarbutton) {
						Field tempField = objMap.get(i);
						if(tempField != null) eachComp.setAttribute("colName", tempField);
						
						myLbFilter.prepareFilterMenupopup((Toolbarbutton)eachComp);
						((Toolbarbutton)eachComp).addEventListener("onClick", myLbFilter);
					}
					else if(eachComp instanceof Listbox) {
						((Listbox)eachComp).addEventListener("onSelect", myLbFilter);
					}
					else if((eachComp instanceof Textbox)) {
						//((Textbox)eachComp).addEventListener("onChanging", myLbFilter);
				        ((Textbox)eachComp).addEventListener("onOK", myLbFilter);
				    //    ((Textbox)eachComp).addEventListener("onChange", myLbFilter);
					}
					else if((eachComp instanceof Intbox)) {
						((Intbox)eachComp).addEventListener("onChanging", myLbFilter);
					}
					else if((eachComp instanceof Doublebox)) {
						((Doublebox)eachComp).addEventListener("onChanging", myLbFilter);
					}
					else if(eachComp instanceof MyDatebox) {
						((MyDatebox)eachComp).addEventListener("onChange", myLbFilter);
					}
				} // for
			} // inner for
		} // outer for
		
		
		return myLbFilter;
	} // filterLBSetup

	
	private void prepareFilterMenupopup(Toolbarbutton tButton) {
		try {
			String ttStr = tButton.getTooltiptext();
			if(ttStr==null || ttStr.trim().isEmpty()) return;
			ttStr = ttStr.toUpperCase().trim();
			
			Menupopup tbMpopup = (Menupopup)tButton.getFellowIfAny(tButton.getContext());
			
			if(tbMpopup!=null) {
				tButton.setAttribute(FILTER_MENUPOPUP, tbMpopup);
				tButton.setContext((String)null);
			}
			
			if(!ttStr.startsWith("FILTERS|")) return;
			
			String strArr[] = ttStr.split("\\|");
			if(strArr.length<=1) return;
			
			tButton.setTooltiptext(null);

			
			SearchFilterEnum tempFEnum;
			//logger.info("tempMp="+tbMpopup);
			Menuitem tempMi=null;
			
			// Prepare menu items
			for (int i = 1; i < strArr.length; i++) {
				
				if(strArr[i].equals("-")) {
					if(tbMpopup!=null) {
						tbMpopup.appendChild(new Menuseparator());
					}
					continue;
				} // if
				
				try {
					tempFEnum = SearchFilterEnum.valueOf(strArr[i].trim());
				}
				catch(Exception e) {
					logger.info("NEED to put FilterEnum for String : "+strArr[i].trim());
					continue;
				}
				
				if(tButton.getTooltiptext()==null) {
					tButton.setTooltiptext(tempFEnum.getTooltip());
					tButton.setImage(tempFEnum.getBtnImage());
					tButton.setAttribute(FILTER_ENUM, tempFEnum);
					if(tempFEnum==SearchFilterEnum.CL) return;
				}
				
				if(tbMpopup != null) {
					tempMi = new Menuitem(tempFEnum.getTooltip());
					tempMi.setImage(tempFEnum.getMiImage());
					tempMi.addEventListener("onClick", this);
					tempMi.setAttribute(FILTER_BUTTON, tButton);
					tempMi.setAttribute(FILTER_ENUM, tempFEnum);
					tempMi.setParent(tbMpopup);
				}
			} // for 
			
			if(tbMpopup != null) {
				tButton.setAttribute(FILTER_MENUPOPUP, tbMpopup);
			}
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
	} // 
	
	
	@Override
	public void onEvent(Event event) throws Exception {

		Component target =  event.getTarget();
		boolean clearFilter=false;
		String lbAttribute =  filterLB != null ? (String)filterLB.getAttribute(FROM_DB):"true";
		
		if(target instanceof Listheader) {
			
			Listheader lh = (Listheader)target;
			SortEvent se = (SortEvent)event;
			Field sortField = (Field)lh.getAttribute("colName");
			
			if(sortField!=null && lbAttribute!=null && lbAttribute.equalsIgnoreCase("true")) {
				String qryPrefix=this.getQryPrefix();
				
				if(qryPrefix==null || qryPrefix.trim().isEmpty()) qryPrefix="";
				else qryPrefix = qryPrefix+".";
				
				sortQry =" ORDER BY "+qryPrefix + sortField.getName() + " " + (se.isAscending() ? " ASC " : " DESC ");
			}
			
		}else if(target instanceof Column){
			
			Column column = (Column)target;
			SortEvent se = (SortEvent)event;
			Field sortField = (Field)column.getAttribute("colName");
			
			if(sortField!=null) {
				String qryPrefix=this.getQryPrefix();
				
				if(qryPrefix==null || qryPrefix.trim().isEmpty()) qryPrefix="";
				else qryPrefix = qryPrefix+".";
				
				sortQry =" ORDER BY "+qryPrefix + sortField.getName() + " " + (se.isAscending() ? " ASC " : " DESC ");
			}
			
		}
		else if(target instanceof Menuitem) {
			Menuitem tempMi =(Menuitem)target;
			
			if(tempMi.hasAttribute(FILTER_BUTTON)) { // Filter menu item
				
				Toolbarbutton tempTb = (Toolbarbutton)tempMi.getAttribute(FILTER_BUTTON);
				SearchFilterEnum tempFenum  = (SearchFilterEnum)tempMi.getAttribute(FILTER_ENUM);
				
				tempTb.setAttribute(FILTER_ENUM, tempFenum);
				tempTb.setImage(tempFenum.getBtnImage());
				tempTb.setTooltiptext(tempFenum.getTooltip());
			}
			else { // List Header menuitem
				Integer tempInt = (Integer)tempMi.getAttribute(COLINDEX);
				
				// Verify if there is only one column to make visible false
				if(tempMi.isChecked()==false) {
					Listhead listHead = filterLB.getListhead();
					Collection<Component> headderList = listHead.getChildren();
					
					int visCount=0;
					for (Component eachHeader : headderList) {
						if(eachHeader.isVisible()) visCount++;
					}
					if(visCount<=1) { 
						tempMi.setChecked(true);
						return;
					}
				} // if
				
				// make Visible List header based on the Menu item check
				Collection<Component> headList = filterLB.getHeads();
				
				for (Component eachHead : headList) {
					List<Component> headerList = eachHead.getChildren();
					if(headerList.size() <= tempInt.intValue()) continue;
					
					headerList.get(tempInt.intValue()).setVisible(tempMi.isChecked());
				} // outer for
				
				return;
			}
		} // if
		
		else if(target instanceof Paging) {
			PagingEvent pe = (PagingEvent) event;
			if(filterLB == null) Utility.refreshGridModel(this, pe.getActivePage(), false);
			else {
				if(this.paging1 != null) {
					if("myPaging1Id".equals(this.paging1.getId())) {
						Utility.refreshModel1(this, pe.getActivePage(), true);//filterLB, paging, query, countQuery, qryPrefix, sortQry, pe.getActivePage(), false);
					}else {
						Utility.refreshModel(this, pe.getActivePage(), false);//filterLB, paging, query, countQuery, qryPrefix, sortQry, pe.getActivePage(), false);
					}
				}else {
					Utility.refreshModel(this, pe.getActivePage(), false);//filterLB, paging, query, countQuery, qryPrefix, sortQry, pe.getActivePage(), false);
				}
			}
			return;
		}
		else if(target instanceof Textbox) {
			Textbox tmp = (Textbox)target;
			//tmp.setValue(((InputEvent)event).getValue());
			   if((event.getName()).equals("onOK")&&(event.getName()).equals("onChange"))
			    tmp.setValue(((InputEvent)event).getValue());
			//Utility.filterListboxByListitems(filterLB, false);
		}
		else if(target instanceof Intbox) {
			Intbox tmp = (Intbox)target;
			Object obj = ((InputEvent)event).getValue();
			if(!obj.toString().trim().isEmpty()) {
				try {
					tmp.setValue( Integer.parseInt(obj.toString()) );
				} catch (Exception e) {
//					logger.error("Exception ::" , e);
					tmp.setValue(null);
				}
			}
			else {
				tmp.setValue(null);
			}
			//Utility.filterListboxByListitems(filterLB, false);
		}
		else if(target instanceof Doublebox) {
			Doublebox tmp = (Doublebox)target;
			Object obj = ((InputEvent)event).getValue();
			if(!obj.toString().trim().isEmpty()) {
				tmp.setValue( Double.parseDouble(obj.toString()) );
			}
			else {
				tmp.setValue(null);
			}
			//Utility.filterListboxByListitems(filterLB, false);
		}
		else if(target instanceof Listbox) {
			//Utility.filterListboxByListitems(filterLB, false);
			Listbox targetLB = ((Listbox)target);
			String ttStr = targetLB.getSelectedItem().getTooltiptext();
			if(ttStr!=null && !ttStr.trim().isEmpty()) targetLB.setTooltiptext(ttStr);
		}
		else if(target instanceof MyDatebox) {
			//Utility.filterListboxByListitems(filterLB, false);
		}
		else if(target instanceof Toolbarbutton) {
			Toolbarbutton tb = (Toolbarbutton)target;

			if(tb.hasAttribute(FILTER_MENUPOPUP)) {
				((Menupopup)tb.getAttribute(FILTER_MENUPOPUP)).open(tb, "after_start");
				return;
			}
			clearFilter=true;
			Utility.filterListboxByListitems(filterLB, clearFilter);
		}
		
		// ************************* Filter Data ******************** 
		if(target instanceof Column){
			Utility.refreshGridModel(this, 0, true);
		}else if(lbAttribute!=null && lbAttribute.equalsIgnoreCase("true")) {
			logger.info("Fetch from DB..");
			
			Utility.refreshModel(this, 0, true);//filterLB, paging, query, countQuery, qryPrefix, sortQry, 0, true);
		}
		else {
			if(!clearFilter) Utility.filterListboxByListitems(filterLB, clearFilter);
		}
		
		if(filterLB != null && filterLB.getAttribute(FOOTER) != null){
			Listfoot footerId = (Listfoot) filterLB.getAttribute(FOOTER);
			List<Component> footerList = footerId.getChildren();
			List<Listitem> listItems =  filterLB.getItems();
			
			int row = 0;
			int col = 0;
			int vis = 0;
			for (Object item : filterLB.getItems()) {
				if(!((Listitem)item).isVisible()){
					vis++;
					continue;
				}
			}
			int[][] res = new int[listItems.size()-vis][footerList.size()-2];
			
			for (Object item : filterLB.getItems()) {
				if(!((Listitem)item).isVisible()){
					continue;
				}
				col = 0;
				for (Object cell : ((Listitem) item).getChildren()) {
				
					if(((Listcell)cell).getListheader()!=null){
						if(((Listcell)cell).getListheader().isVisible() && ((Listcell)cell).getColumnIndex()>1){
							//i += ((Listcell) cell).getLabel() + ";";
							res[row][col] = Integer.parseInt(((Listcell) cell).getLabel());
							logger.info("res["+row+"]["+col+"] :::"+res[row][col]);
							col++;
						}
					}
				}
				//sb.append(i + "\r\n");
				row++;
			}
			int count = 2;
			for(int k=0;k<(footerList.size()-2);k++){
				int sum = 0;
				for(int l=0;l<(listItems.size()-vis); l++){
					logger.info("sum of res["+l+"]["+k+"] :::"+res[l][k]);
					sum = sum + res[l][k];
				}
				logger.info("footerList :::"+footerList);
				((Label)footerList.get(count).getChildren().get(0)).setValue(""+sum);
				count++;
				
				logger.info("sum value :::"+sum);
			}
		
		}
	}
	
	private static LBFilterEventListener filterLBSetup1(Grid filterGD,Listbox filterLB, Paging paging,Paging paging1,  
			String query,String countQuery, String qryPrefix, Map<Integer, Field> objMap) {
		
		//LBFilterEventListener myLbFilter = new LBFilterEventListener(filterGD, filterLB, paging, query, countQuery, qryPrefix);
		LBFilterEventListener myLbFilter;
		if(filterLB != null && filterLB.getAttribute(FOOTER) != null){
			Listfoot footerLBId = (Listfoot) filterLB.getAttribute(FOOTER);
			myLbFilter = new LBFilterEventListener(filterGD, filterLB, paging, paging1,query, countQuery, qryPrefix, footerLBId);
		}else{
			myLbFilter = new LBFilterEventListener(filterGD, filterLB, paging, paging1,query, countQuery, qryPrefix);
		}
		
		if(paging!=null) {
			String handlePaging = (String)paging.getAttribute(HANDLE_ON_PAGING);

			if(handlePaging==null || handlePaging.equalsIgnoreCase("true")) {
				paging.addEventListener("onPaging", myLbFilter);
				paging1.addEventListener("onPaging", myLbFilter);
			}
		}
		
		// Check if ojbMap is available
		if(objMap==null) {
			objMap = new HashMap<Integer, Field>(); // Dummy object
		}
		
		logger.info("ObjMap="+objMap);
		if(filterGD != null){
			Columns lh = filterGD.getColumns();
			List<Component> lhList = lh.getChildren();
		for (int i=0; i < lhList.size(); i++ ) {
			
			Column eachColumn = (Column)lhList.get(i);
			if(!(eachColumn instanceof Column)) continue;
			Field tempField = objMap.get(i+1);
			if(tempField != null) {
				
				eachColumn.setAttribute("colName", tempField);
				try{
				eachColumn.setSort("auto");
				}catch(Exception e){
					logger.error("Exception ",e);
				}
				//eachColumn.setStyle("curson:pointer");
				eachColumn.addEventListener("onSort", myLbFilter);
				//eachColumn.addEventListener("onClick", myLbFilter);
			}
			
		}
		return myLbFilter;
		}
		
		// Check for fetch data by Query
				if(query!=null && query.trim().length() > 0) {
					
					filterLB.setAttribute(FROM_DB, "true");
				}
		
		Listhead lh = filterLB.getListhead();
		List<Component> lhList = lh.getChildren();
		
		for (int i=0; i < lhList.size(); i++ ) {

			Listheader eachHeader = (Listheader)lhList.get(i);
			if(!(eachHeader instanceof Listheader) ) continue;
			
			//logger.info(">>>>>>>>>>>>>> Adding LH Listener" + objMap.containsKey(i));
			Field tempField = objMap.get(i);
			
			if(query==null || query.trim().isEmpty()) { // Client side sorting
				logger.info("Client side sorting");
				if(objMap.containsKey(i)) {
					eachHeader.setSortAscending(new MyListComparator(true, tempField, i));
					eachHeader.setSortDescending(new MyListComparator(false, tempField, i));
				}
				continue;
			}
			else if(tempField != null) {
				
				eachHeader.setAttribute("colName", tempField);
				eachHeader.setSort("auto");
				eachHeader.addEventListener("onSort", myLbFilter);
			} 
		} // for

		
		// Check for List header MenuPopup options
		myLbFilter.createAndAssignMenupopup();
		
		
		Collection<Component> hedderList = filterLB.getHeads();
		for (Component eachHeader : hedderList) {
			if(!(eachHeader instanceof Auxhead)) continue;
			
			List<Component> childList = eachHeader.getChildren();
			for (int i=0; i<childList.size(); i++ ) {
				Component eachAuxheader = childList.get(i);
				
				if(!(eachAuxheader instanceof Auxheader)) continue;
				
				List<Component> auxChildList = eachAuxheader.getChildren();
				if(auxChildList.isEmpty()) continue;
				
				for (int j=0; j<auxChildList.size(); j++ ) {
					Component eachComp = auxChildList.get(j);
					
					if(eachComp instanceof Toolbarbutton) {
						Field tempField = objMap.get(i);
						if(tempField != null) eachComp.setAttribute("colName", tempField);
						
						myLbFilter.prepareFilterMenupopup((Toolbarbutton)eachComp);
						((Toolbarbutton)eachComp).addEventListener("onClick", myLbFilter);
					}
					else if(eachComp instanceof Listbox) {
						((Listbox)eachComp).addEventListener("onSelect", myLbFilter);
					}
					else if((eachComp instanceof Textbox)) {
						//((Textbox)eachComp).addEventListener("onChanging", myLbFilter);
				        ((Textbox)eachComp).addEventListener("onOK", myLbFilter);
				    //    ((Textbox)eachComp).addEventListener("onChange", myLbFilter);
					}
					else if((eachComp instanceof Intbox)) {
						((Intbox)eachComp).addEventListener("onChanging", myLbFilter);
					}
					else if((eachComp instanceof Doublebox)) {
						((Doublebox)eachComp).addEventListener("onChanging", myLbFilter);
					}
					else if(eachComp instanceof MyDatebox) {
						((MyDatebox)eachComp).addEventListener("onChange", myLbFilter);
					}
				} // for
			} // inner for
		} // outer for
		
		
		return myLbFilter;
	} // filterLBSetup
	
	

} // class


