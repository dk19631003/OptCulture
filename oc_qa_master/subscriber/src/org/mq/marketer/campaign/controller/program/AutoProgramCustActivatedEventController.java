package org.mq.marketer.campaign.controller.program;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Checkbox;

public class AutoProgramCustActivatedEventController extends GenericForwardComposer{
	
	private Checkbox runForCbId;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		session.setAttribute("MY_COMPOSER", this);
		
		
		
	}
	
	public Long isEnabled() {
		if(runForCbId.isChecked())
			return 1l;
		else 
			return 0l;
	
	}

	
	public void setEnable(Long isEnable) {
		if(isEnable != null) {
			if(isEnable == 1l){
				runForCbId.setChecked(true);
			}else {
				runForCbId.setChecked(false);
			}
		}else {
			runForCbId.setChecked(false);
		}
		
	}//setEnable
}//class
