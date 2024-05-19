package org.mq.optculture.sales.json;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class FCDR {

private List<DRFC> clsDRFC;
	
	public List<DRFC> getClsDRFC() {
		return clsDRFC;
	}
	@XmlElement(name = "clsDRFC")
	public void setClsDRFC(List<DRFC> clsDRFC) {
		this.clsDRFC = clsDRFC;
	}
	
}
