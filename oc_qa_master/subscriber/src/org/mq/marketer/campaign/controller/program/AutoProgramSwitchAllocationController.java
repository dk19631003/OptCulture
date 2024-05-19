package org.mq.marketer.campaign.controller.program;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.ProgramUtility;
import org.springframework.format.number.PercentFormatter;
import org.zkforge.canvas.Canvas;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

public class AutoProgramSwitchAllocationController extends GenericForwardComposer{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private Radiogroup percCalCriteriaRgId,dateConsRgId;
	private Div dateConsDivId;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		session.setAttribute("MY_COMPOSER", this);
	}

	public void onCheck$percCalCriteriaRgId() {
		//logger.info("kskjkfjdkflk");
		Radio radio = percCalCriteriaRgId.getSelectedItem();
		if(radio.getId().equalsIgnoreCase("percCalForEachRunRbId")) {
			
			dateConsDivId.setVisible(false);
			
			
		}else if (radio.getId().equalsIgnoreCase("percCalForOverAllRbId")) {
			dateConsDivId.setVisible(true);
		}
		
		
		
		
		
	}//onClick$percCalCriteriaRgId()
	
	
	
	
	public void onChange$percCb1Id() {
		try {
			int val = Integer.parseInt(percCb1Id.getValue());
			if(val >= 100) {
				val=100;
				percCb1Id.setValue(""+val);
				if(percCb2Id.isVisible()) {
					percCb2Id.setValue(""+0);
					
				}//if
				
				
			}
			
			if(percCb2Id.isVisible()) {
				
				percCb2Id.setValue(""+(100-Integer.parseInt(percCb1Id.getValue())));
				
			}//if
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
	}

	public void onChange$percCb2Id() {
		try {
			int val = Integer.parseInt(percCb2Id.getValue());
			
			if(val >= 100) {
				val = 100;
				percCb2Id.setValue(""+val);
				if(percCb1Id.isVisible()) {
					percCb1Id.setValue(""+0);
				}
				
			}
			if(percCb1Id.isVisible()) {
				
				percCb1Id.setValue(""+(100-val));
				
			}//if
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		
	}
	
	
	private Listbox title1LbId,title2LbId;
	private Hbox nextLine2HbId;
	public void getNextComponents(List<Label> winTitleList,boolean visibility) {
		try {
			
			if(visibility) {
				Components.removeAllChildren(title1LbId);
				Components.removeAllChildren(title2LbId);
				
				nextLine2HbId.setVisible(true);
				
				Listitem li = null;
				for (Label title : winTitleList) {
					
					
					li = new Listitem();
					/*if(title1LbId.getItemCount() > 0) {
						for(int i=0; i<title1LbId.getItemCount(); i++) {
							
							if(title.getValue().equalsIgnoreCase(title1LbId.getItemAtIndex(i).getLabel())) {
								
								li.setLabel(title.getValue()+i);
								
							}//if
							else {
								li.setLabel(title.getValue());
							}
						}//for
					}else{
						li.setLabel(title.getValue());
					}*/
					li.setLabel(title.getValue());
					li.setValue(title);
					li.setParent(title1LbId);
					
					li = new Listitem();
					/*
					if(title2LbId.getItemCount()>0) {
						for(int i=0; i<title2LbId.getItemCount(); i++) {
							
							if(title.getValue().equalsIgnoreCase(title2LbId.getItemAtIndex(i).getLabel())) {
								
								li.setLabel(title.getValue()+i);
								
							}//if
							else {
								li.setLabel(title.getValue());
							}
						}//for
					}else {
						li.setLabel(title.getValue());
						
					}*/
					li.setLabel(title.getValue());
					li.setValue(title);
					li.setParent(title2LbId);
					
					
				}//for
				
				title1LbId.setSelectedIndex(0);
				title2LbId.setSelectedIndex(0);
				/*if(!((Label)win.getFellowIfAny("nextCompLblId")).getValue().contains(",")) {
					if( Messagebox.show("Component should have two output lines.","Captiway",
							 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION) == Messagebox.OK ){
						return;
					}
				}
				else {
				}*/
			}//if
			else {
				
				Components.removeAllChildren(title1LbId);
				
				Listitem li = null;
				for (Label title : winTitleList) {
					
					
					li = new Listitem();
					li.setLabel(title.getValue());
					li.setValue(title);
					li.setParent(title1LbId);
				}//for
				title1LbId.setSelectedIndex(0);
				nextLine2HbId.setVisible(false);
				
			}//else
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
	}//getNextComponents
	
	public void setProperties(String tempStr, Label msgLbl1, Label msgLbl2, Canvas centerDiv, boolean visibility){
		
		String moveTo = "";
		String[] tempArr = null;
		String title = "";
		if(tempStr != null) {
			if(tempStr.contains(Constants.DELIMETER_DOUBLECOLON)) {
				percCalCriteriaRgId.setSelectedIndex(1);
				dateConsDivId.setVisible(true);
				String dateToCons = tempStr.split(Constants.DELIMETER_DOUBLECOLON)[1];
				if(dateToCons.equalsIgnoreCase(Constants.AUTO_PROGRAM_EMAILREP_FROM_CREATED)) {
					dateConsRgId.setSelectedIndex(1);
					
				}else if(dateToCons.equalsIgnoreCase(Constants.AUTO_PROGRAM_EMAILREP_FROM_MODIFIED)) {
					dateConsRgId.setSelectedIndex(0);
					
					
				}
				
				
				
			}else {
				
				percCalCriteriaRgId.setSelectedIndex(0);
				dateConsDivId.setVisible(false);
				
			}//else
		
		}//if
		
		
		
		moveTo = (String)msgLbl1.getAttribute("moveTo");
		//logger.info("yes move to is====>"+moveTo);
		if(moveTo != null) {
			//has the value : "true||ACTIVITY_SEND_EMAIL-11w0" 
			tempArr = moveTo.split("\\|\\|");
			
			
			String mode =  tempArr[0];
			
			//logger.info("mode 1st is============>"+mode);
			percCb1Id.setValue(mode);
			for(int i=0; i<percCb1Id.getItemCount(); i++) {
				
				if(percCb1Id.getItemAtIndex(i).getLabel().trim().equalsIgnoreCase(mode))
					percCb1Id.setSelectedIndex(i);
			}
			
			String nextWinId = tempArr[1];
			Window nextWin = ProgramUtility.getWindowFromCenterDiv(nextWinId, centerDiv);
			
			//logger.info("the window object got from center div for 1st time======>"+nextWin);
			if(nextWin != null) {
			
				/*if(nextWinId.startsWith("ACTIVITY_")) {	
					title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
				}
				else if(nextWinId.startsWith("EVENT_")) {
					
					title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
				}*/
				title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
				for(int i=0; i<title1LbId.getItemCount(); i++) {
					
					if(title1LbId.getItemAtIndex(i).getLabel().trim().equalsIgnoreCase(title))
						title1LbId.setSelectedIndex(i);
					
				}//for
				
				
			}//if
			
			msgLine1TbId.setValue(msgLbl1.getValue());
			
		}//if
		
		
		if(visibility == true ) {
			moveTo = (String)msgLbl2.getAttribute("moveTo");
			percCb1Id.setDisabled(false);
		
		
			if(moveTo != null) {
				
	
				//has the value : "true||ACTIVITY_SEND_EMAIL-11w0" 
				tempArr = moveTo.split("\\|\\|");
				
				
				String mode =  tempArr[0];
				//logger.info("mode 2nd is============>"+mode);
				percCb2Id.setValue(mode);
				for(int i=0; i<percCb2Id.getItemCount(); i++) {
					
					if(percCb2Id.getItemAtIndex(i).getLabel().trim().equalsIgnoreCase(mode))
						percCb2Id.setSelectedIndex(i);
				}
				
				String nextWinId = tempArr[1];
				Window nextWin = ProgramUtility.getWindowFromCenterDiv(nextWinId, centerDiv);
				
				//logger.info("the window got from center div is====>"+nextWin);
				if(nextWin != null) {
					/*if(nextWinId.startsWith("ACTIVITY_")) {	
						title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
					}
					else if(nextWinId.startsWith("EVENT_")) {
						
						title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
					}*/
					title = ((Label)nextWin.getFellowIfAny("titleLblId")).getValue();
					for(int i=0; i<title2LbId.getItemCount(); i++) {
						
						if(title2LbId.getItemAtIndex(i).getLabel().trim().equalsIgnoreCase(title))
							title2LbId.setSelectedIndex(i);
						
					}
					
					msgLine2TbId.setValue(msgLbl2.getValue());
				
				}//if
				
			}//if
		
		}
		if(visibility == false) {
			
			
			percCb1Id.setValue("100");
			percCb1Id.setDisabled(true);
			
		}
		
		
		
	}//setProperties()
	
	private Combobox percCb1Id,percCb2Id;
	private Textbox msgLine1TbId,msgLine2TbId;
	public String validateAndGetTempStr(Label msgLine1, Label msgLine2) {
		
		String tempStr = null;
		if(dateConsDivId.isVisible()) {
			
			if(dateConsRgId.getSelectedItem().getId().equalsIgnoreCase("fromModifiedRbId")) {
				//need to consider the contacts from the program modified date 
				tempStr = Constants.AUTO_PROGRAM_SWITCHALLOC_CRITERIA_OVERALL+
							Constants.DELIMETER_DOUBLECOLON+Constants.AUTO_PROGRAM_EMAILREP_FROM_MODIFIED;
				
				
				
				
			}else if(dateConsRgId.getSelectedItem().getId().equalsIgnoreCase("fromCreatedRbId")) {
				//need to consider the contacts from program created date
				
				
				tempStr = Constants.AUTO_PROGRAM_SWITCHALLOC_CRITERIA_OVERALL+
							Constants.DELIMETER_DOUBLECOLON+Constants.AUTO_PROGRAM_EMAILREP_FROM_CREATED;
	
	
				
				
			}//else if
			
		}//if
		else {
			
			
			tempStr = Constants.AUTO_PROGRAM_SWITCHALLOC_CRITERIA_EACHRUN;
			
			
			
		}
		
		
		
		
		if(nextLine2HbId.isVisible()){
			String mode1 = percCb1Id.getValue();
			String mode2 = percCb2Id.getValue();
			
			if( Integer.parseInt(mode1)+Integer.parseInt(mode2) > 100 || Integer.parseInt(mode1)+Integer.parseInt(mode2) < 100) {
				
				MessageUtil.setMessage("Please ensure that total percentage is 100.", "color:red");
				
				return null;
				
			}
			
			String title1 = title1LbId.getSelectedItem().getLabel();
			String title2 = title2LbId.getSelectedItem().getLabel();
			
			if( title1.equalsIgnoreCase(title2) || title2.equalsIgnoreCase(title1) ) {
				MessageUtil.setMessage("Please ensure that both GoTo titles are not same.", "color:red");
				
				return null;
			}
			
			
			msgLine1.setValue(msgLine1TbId.getValue());
			msgLine1.setAttribute("moveTo", mode1+"||"+((Label)title1LbId.getSelectedItem().getValue()).getAttribute("mode"));
			//logger.info("dfsdfdsfadfsdfdsfsdaf"+mode1LbId.getSelectedItem().getLabel()+"||"+((Label)title1LbId.getSelectedItem().getValue()).getAttribute("mode"));
			
			msgLine2.setValue(msgLine2TbId.getValue());
			msgLine2.setAttribute("moveTo", mode2+"||"+((Label)title2LbId.getSelectedItem().getValue()).getAttribute("mode"));
			//logger.info("dfsdfdsfadfsdfdsfsdaf"+mode2LbId.getSelectedItem().getLabel()+"||"+((Label)title2LbId.getSelectedItem().getValue()).getAttribute("mode"));
		}
		else {
			
			String mode1 =  percCb1Id.getValue();
			
			String title1 = title1LbId.getSelectedItem().getLabel();
			msgLine1.setValue(msgLine1TbId.getValue());
			msgLine1.setAttribute("moveTo", mode1+"||"+((Label)title1LbId.getSelectedItem().getValue()).getAttribute("mode"));
		}
		
		return tempStr;
		
		
		
	}
	
	
}
