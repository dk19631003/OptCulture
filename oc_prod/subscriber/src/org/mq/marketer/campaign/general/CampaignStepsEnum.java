package org.mq.marketer.campaign.general;

public enum CampaignStepsEnum {

	CampSett(1),
	CampMlist(2),
	CampLayout(3),
	
	blockEditor(4),
	plainTextEditor(4),
	beeEditor(4),
	plainHtmlEditor(4),
	
	CampTextMsg(5),
	CampFinal(6),
	complete(6);
	
	int pos;
	
	CampaignStepsEnum(int temppos) {
		pos=temppos;
	}
	
	public int getPos() {
		return pos;
	}
	
} // CampaignStepsEnum
