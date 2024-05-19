package org.mq.optculture.model.updatesku;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.mq.optculture.model.HeaderInfo;
import org.mq.optculture.model.UserDetails;

public class UpdateSkuRequest {
	private HeaderInfo HEADERINFO;
	private UserDetails USERDETAILS;
	private SkuInfo SKUINFO;

	private List<SkuInfo> PRODUCTSINFO;

	public List<SkuInfo> getPRODUCTSINFO() {
		return PRODUCTSINFO;
	}

	public void setPRODUCTSINFO(List<SkuInfo> pRODUCTSINFO) {
		PRODUCTSINFO = pRODUCTSINFO;
	}

	public UpdateSkuRequest() {
	}

	public UpdateSkuRequest(HeaderInfo hEADERINFO, UserDetails uSERDETAILS, SkuInfo sKUINFO) {
		HEADERINFO = hEADERINFO;
		USERDETAILS = uSERDETAILS;
		SKUINFO = sKUINFO;
	}

	public HeaderInfo getHEADERINFO() {
		return HEADERINFO;
	}

	@XmlElement(name = "HEADERINFO")
	public void setHEADERINFO(HeaderInfo hEADERINFO) {
		HEADERINFO = hEADERINFO;
	}

	public UserDetails getUSERDETAILS() {
		return USERDETAILS;
	}

	@XmlElement(name = "USERDETAILS")
	public void setUSERDETAILS(UserDetails uSERDETAILS) {
		USERDETAILS = uSERDETAILS;
	}

	public SkuInfo getSKUINFO() {
		return SKUINFO;
	}

	@XmlElement(name = "SKUINFO")
	public void setSKUINFO(SkuInfo sKUINFO) {
		SKUINFO = sKUINFO;
	}

}
