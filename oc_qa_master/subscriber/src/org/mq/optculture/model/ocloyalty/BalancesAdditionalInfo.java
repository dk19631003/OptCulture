package org.mq.optculture.model.ocloyalty;

import java.util.List;

public class BalancesAdditionalInfo {

	private Debit debit;
	private List<Credit> credit;
	
	public BalancesAdditionalInfo() {
	}

	public Debit getDebit() {
		return debit;
	}

	public void setDebit(Debit debit) {
		this.debit = debit;
	}

	public List<Credit> getCredit() {
		return credit;
	}

	public void setCredit(List<Credit> credit) {
		this.credit = credit;
	}
	
	
}
