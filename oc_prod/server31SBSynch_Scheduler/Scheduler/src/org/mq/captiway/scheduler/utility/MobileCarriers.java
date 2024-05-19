package org.mq.captiway.scheduler.utility;

import java.util.HashMap;
import java.util.Map;

public class MobileCarriers {
	
	public static Map<String, Byte> mobileCarrierMap ;
	
	static{
		
		if(mobileCarrierMap == null) mobileCarrierMap = new HashMap<String, Byte>();
		mobileCarrierMap.put(Constants.USER_SMSTOOL_CLICKATELL, new Byte((byte)1));
		mobileCarrierMap.put(Constants.USER_SMSTOOL_MVAYOO, new Byte((byte)91));
		
		
	}
	
	
}
