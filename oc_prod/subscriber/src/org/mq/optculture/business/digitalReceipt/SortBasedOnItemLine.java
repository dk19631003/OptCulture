package org.mq.optculture.business.digitalReceipt;

import java.util.Comparator;

import org.json.simple.JSONObject;

public class SortBasedOnItemLine implements Comparator<JSONObject> {
    /*
    * (non-Javadoc)
    * 
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    * lhs- 1st message in the form of json object. rhs- 2nd message in the form
    * of json object.
    */
    @Override
    public int compare(JSONObject lhs, JSONObject rhs) {
        try { 
            return Integer.parseInt(lhs.get("ItemLine").toString()) > Integer.parseInt(rhs.get("ItemLine").toString()) ? 1 : 
            	(Integer.parseInt(lhs.get("ItemLine").toString()) < Integer.parseInt(rhs.get("ItemLine").toString()) ? -1 : 0);
        } catch (Exception e) { 
            
        }
        return 0;

    }

}
