package org.mq.optculture.model.mobileapp;

import java.util.List;

import org.mq.optculture.model.BaseRequestObject;
import org.mq.optculture.model.ocloyalty.AdditionalInfo;
import org.mq.optculture.model.ocloyalty.Balance;
import org.mq.optculture.model.ocloyalty.HoldBalance;
import org.mq.optculture.model.ocloyalty.MatchedCustomer;
import org.mq.optculture.model.ocloyalty.MembershipResponse;
import org.mq.optculture.model.ocloyalty.ResponseHeader;
import org.mq.optculture.model.ocloyalty.Status;

public class LoyaltyMemberLoginResponse extends BaseRequestObject{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6831961210923846896L;
		private ResponseHeader header;
		private MembershipResponse membership;
		private List<Balance> balances;
		private HoldBalance holdBalance;
		private AdditionalInfo additionalInfo;
		//private Customer customer;
		private List<MatchedCustomer> matchedCustomers;
		private Status status;
		
		public LoyaltyMemberLoginResponse(){
			//Default Constructor
		}

		public ResponseHeader getHeader() {
			return header;
		}

		public void setHeader(ResponseHeader header) {
			this.header = header;
		}

		public List<Balance> getBalances() {
			return balances;
		}

		public void setBalances(List<Balance> balances) {
			this.balances = balances;
		}

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
		}

		public List<MatchedCustomer> getMatchedCustomers() {
			return matchedCustomers;
		}

		public void setMatchedCustomers(List<MatchedCustomer> matchedCustomers) {
			this.matchedCustomers = matchedCustomers;
		}

		public MembershipResponse getMembership() {
			return membership;
		}

		public void setMembership(MembershipResponse membership) {
			this.membership = membership;
		}

		public HoldBalance getHoldBalance() {
			return holdBalance;
		}

		public void setHoldBalance(HoldBalance holdBalance) {
			this.holdBalance = holdBalance;
		}

		public AdditionalInfo getAdditionalInfo() {
			return additionalInfo;
		}

		public void setAdditionalInfo(AdditionalInfo additionalInfo) {
			this.additionalInfo = additionalInfo;
		}
		
	}

