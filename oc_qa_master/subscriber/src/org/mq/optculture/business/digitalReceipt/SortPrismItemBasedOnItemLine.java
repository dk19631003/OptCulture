package org.mq.optculture.business.digitalReceipt;

import java.util.Comparator;

import org.json.simple.JSONObject;
import org.mq.optculture.model.DR.prism.PrismDRItem;

public class SortPrismItemBasedOnItemLine implements Comparator<PrismDRItem> {
    /*
    * (non-Javadoc)
    * 
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    * lhs- 1st message in the form of json object. rhs- 2nd message in the form
    * of json object.
    */
    @Override
    public int compare(PrismDRItem lhs, PrismDRItem rhs) {
        try {
            return Integer.parseInt(lhs.getItem_pos()) > Integer.parseInt(rhs.getItem_pos()) ? 1 : 
            	(Integer.parseInt(lhs.getItem_pos()) < Integer.parseInt(rhs.getItem_pos()) ? -1 : 0);
        } catch (Exception e) {
            
        }
        return 0;

    }

}
