package org.mq.marketer.campaign.beans;

import java.io.Serializable;

public class Vmta implements Serializable {
	private Long id;
	private String vmtaName;
	private String description;
	private String status; 
	private String pooledVmtas;
	private String accountName;
	private String accountAPIName;
	private String accountPwd;
	private String host;
	
	public String getAccountAPIName() {
		return accountAPIName;
	}
	public void setAccountAPIName(String accountAPIName) {
		this.accountAPIName = accountAPIName;
	}
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountPwd() {
		return accountPwd;
	}
	public void setAccountPwd(String accountPwd) {
		this.accountPwd = accountPwd;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getVmtaName() {
		return vmtaName;
	}
	public void setVmtaName(String vmtaName) {
		this.vmtaName = vmtaName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus() {
		return status;
	}
	public String getPooledVmtas() {
		return pooledVmtas;
	}
	public void setPooledVmtas(String pooledVmtas) {
		this.pooledVmtas = pooledVmtas;
	}
	
}
