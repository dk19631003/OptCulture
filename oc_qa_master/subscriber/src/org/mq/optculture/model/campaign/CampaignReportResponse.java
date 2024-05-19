package org.mq.optculture.model.campaign;

import java.util.List;
import java.util.Map;

import org.mq.marketer.campaign.beans.CampaignSent;
import org.mq.marketer.campaign.beans.TemplateCategory;
import org.mq.marketer.campaign.beans.Users;
import org.mq.optculture.model.BaseResponseObject;

import com.google.zxing.common.BitMatrix;

public class CampaignReportResponse extends BaseResponseObject{
	String responseType;
	String status;
	String urlStr;
	String errorResponse;
	String htmlContent;
	Map<Object, Object> resultMap;
	Long userId;
	Long sentId;
	List<TemplateCategory> catList;
	String emailId;
	Long crId;
	short categoryWeight;
	CampaignSent campaignSent;
	Users user;
	BitMatrix bitMatrix;
	Long cId;
	String campaignId;

	public String getResponseType() {
		return responseType;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

	public String getErrorResponse() {
		return errorResponse;
	}

	public void setErrorResponse(String errorResponse) {
		this.errorResponse = errorResponse;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public Map<Object, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<Object, Object> resultMap) {
		this.resultMap = resultMap;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getSentId() {
		return sentId;
	}

	public void setSentId(Long sentId) {
		this.sentId = sentId;
	}

	public List<TemplateCategory> getCatList() {
		return catList;
	}

	public void setCatList(List<TemplateCategory> catList) {
		this.catList = catList;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public Long getCrId() {
		return crId;
	}

	public void setCrId(Long crId) {
		this.crId = crId;
	}

	public short getCategoryWeight() {
		return categoryWeight;
	}

	public void setCategoryWeight(short categoryWeight) {
		this.categoryWeight = categoryWeight;
	}

	public CampaignSent getCampaignSent() {
		return campaignSent;
	}

	public void setCampaignSent(CampaignSent campaignSent) {
		this.campaignSent = campaignSent;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public BitMatrix getBitMatrix() {
		return bitMatrix;
	}

	public void setBitMatrix(BitMatrix bitMatrix) {
		this.bitMatrix = bitMatrix;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	
}
