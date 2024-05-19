package org.mq.captiway.scheduler.beans;

public class DomainStatus{
private Long domainId;
private String domain;
private String status;
public DomainStatus(){}
public DomainStatus(String domain, String status) {
	this.domain = domain;
	this.status = status;
}
public Long getDomainId() {
	return domainId;
}
public void setDomainId(Long domainId) {
	this.domainId = domainId;
}
public String getDomain() {
	return domain;
}
public String getStatus() {
	return status;
}
public void setDomain(String domain) {
	this.domain = domain;
}
public void setStatus(String status) {
	this.status = status;
}
@Override
public boolean equals(Object obj) {
	DomainStatus current =  (DomainStatus)obj;
	return this.domain.toString().equalsIgnoreCase(current.domain);
}

}
