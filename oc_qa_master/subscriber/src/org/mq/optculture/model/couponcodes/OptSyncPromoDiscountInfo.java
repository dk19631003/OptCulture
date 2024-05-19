package org.mq.optculture.model.couponcodes;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class OptSyncPromoDiscountInfo {

	private String VALUE;
	private String VALUECODE;
	private String MINPURCHASEVALUE;
	
	private PromoItemCodeInfo promoItemCodeInfo;
//	private List<ItemCodeInfo> ITEMCODEINFO;
	
	
	public OptSyncPromoDiscountInfo() {
	}
	
	
	
	@XmlAttribute(name="VALUE")
	public String getVALUE() {
		return VALUE;
	}
	public void setVALUE(String vALUE) {
		VALUE = vALUE;
	}
	
	@XmlElement(name="VALUECODE")
	public String getVALUECODE() {
		return VALUECODE;
	}
	public void setVALUECODE(String vALUECODE) {
		VALUECODE = vALUECODE;
	}
	
	@XmlElement(name="MINPURCHASEVALUE")
	public String getMINPURCHASEVALUE() {
		return MINPURCHASEVALUE;
	}
	public void setMINPURCHASEVALUE(String mINPURCHASEVALUE) {
		MINPURCHASEVALUE = mINPURCHASEVALUE;
	}


	@XmlElement(name="ITEMCODEINFO")
	public PromoItemCodeInfo getPromoItemCodeInfo() {
		return promoItemCodeInfo;
	}
	public void setPromoItemCodeInfo(PromoItemCodeInfo promoItemCodeInfo) {
		this.promoItemCodeInfo = promoItemCodeInfo;
	}
	
	/*@XmlElement(name="ITEMCODEINFO")
	public List<ItemCodeInfo> getITEMCODEINFO() {
		return ITEMCODEINFO;
	}
	public void setITEMCODEINFO(List<ItemCodeInfo> iTEMCODEINFO) {
		ITEMCODEINFO = iTEMCODEINFO;
	}*/

	
}

