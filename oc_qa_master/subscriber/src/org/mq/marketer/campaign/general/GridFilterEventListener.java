package org.mq.marketer.campaign.general;

import java.util.Collection;
import java.util.List;

import org.mq.marketer.campaign.custom.MyDatebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class GridFilterEventListener implements EventListener {
	
	private Grid filterGrid;
	
	private GridFilterEventListener(Grid filterGrid) {
		this.filterGrid=filterGrid;
	}

	public static void gridFilterSetup(Grid filterGrid) {
		//TODO need to check if same kind of object is already added as event listener
		filterGridSetup(filterGrid);
	}
	
	private static void filterGridSetup(Grid filterGrid) {
		GridFilterEventListener myGridFilter = new GridFilterEventListener(filterGrid);

		Collection<Component> hedderList = filterGrid.getHeads();
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

					if(eachComp instanceof Listbox) {
						((Listbox)eachComp).addEventListener("onSelect", myGridFilter);
					}
					else if(eachComp instanceof Textbox) {
						((Textbox)eachComp).addEventListener("onChanging", myGridFilter);
					}
					else if(eachComp instanceof Toolbarbutton) {
						((Toolbarbutton)eachComp).addEventListener("onClick", myGridFilter);
					}
					else if(eachComp instanceof MyDatebox) {
						((MyDatebox)eachComp).addEventListener("onChange", myGridFilter);
					}
				} // for
			} // inner for
		} // outer for
	} // filterLBSetup

	@Override
	public void onEvent(Event event) throws Exception {

		Object target =  event.getTarget();
		if(target instanceof Textbox) {
			Textbox tmp = (Textbox)target;
			tmp.setValue(((InputEvent)event).getValue());
			Utility.filterGridByRows(filterGrid, false);
			
		}
		else if(target instanceof Listbox) {
			Utility.filterGridByRows(filterGrid, false);
		}
		else if(target instanceof MyDatebox) {
			Utility.filterGridByRows(filterGrid, false);
		}
		else if(target instanceof Toolbarbutton) {
			Utility.filterGridByRows(filterGrid, true);
		}
	}
	
} // class


