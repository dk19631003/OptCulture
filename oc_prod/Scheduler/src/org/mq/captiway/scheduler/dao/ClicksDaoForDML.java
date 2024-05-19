package org.mq.captiway.scheduler.dao;

import org.mq.captiway.scheduler.beans.Clicks;
import java.util.*;

@SuppressWarnings({"unchecked","serial"})
public class ClicksDaoForDML extends AbstractSpringDaoForDML {
    public ClicksDaoForDML() {}
    
 /*   public Clicks find(Long id) {
        return (Clicks) super.find(Clicks.class, id);
    }*/

    public void saveOrUpdate(Clicks clicks) {
        super.saveOrUpdate(clicks);
    }

    public void delete(Clicks clicks) {
        super.delete(clicks);
    }

  /*  public List findAll() {
        return super.findAll(Clicks.class);
    }*/
    
    public void saveByCollection(Collection<Clicks> clicksList) {
		super.saveOrUpdateAll(clicksList);
	}
    
    
    
}
