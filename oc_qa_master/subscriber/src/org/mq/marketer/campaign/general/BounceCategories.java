package org.mq.marketer.campaign.general;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public interface BounceCategories {

	String NON_EXISTENT_ADDRESS = "Non-Existent address";
	String UNDELIVERABLE = "Undeliverable";
	String BLOCKED = "Blocked";
	String MAILBOX_FULL = "Mailbox full";
	String OTHERS = "Others";
	String HARD_BOUNCED = "Hard Bounced";
	String SOFT_BOUNCED = "Soft Bounce";
	String BOUNCED = "Bounce";
	String DROPPED = "Dropped";

	Map<String, String> categories = new HashMap<String, String>() { 
 
		{ 
			put("bad-configuration", UNDELIVERABLE);
			put("bad-connection", UNDELIVERABLE);
			put("message expired", UNDELIVERABLE);
			put("no-answer-from-host", UNDELIVERABLE);
			put("routing-errors", UNDELIVERABLE);
			
			put("invalid-sender", BLOCKED);
			put("policy related", BLOCKED);
			put("protocol-errors", BLOCKED);
			put("spam-related", BLOCKED); 
			put("virus-related", BLOCKED);
			
			put("bad-domain", NON_EXISTENT_ADDRESS);
			put("bad-mailbox", NON_EXISTENT_ADDRESS);
			put("inactive-mailbox", NON_EXISTENT_ADDRESS);

			put("quota-issues", MAILBOX_FULL);
			
			put("others", OTHERS);
		}
	};

}
