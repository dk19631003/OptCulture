package org.mq.marketer.campaign.controller;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Include;

public class IncludeController extends Include {

	@Override
	public void setSrc(String src) throws WrongValueException {
		//System.gc();
		super.setSrc(src);
	}

}
