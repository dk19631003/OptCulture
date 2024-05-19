


package org.mq.captiway.scheduler.dao;


import org.hibernate.*;
import org.mq.captiway.scheduler.beans.Opens;
import java.util.*;

@SuppressWarnings({ "unchecked", "serial","unused"})
public class OpensDao extends AbstractSpringDao {
    public OpensDao() {}
    private SessionFactory sessionFactory;

    public Opens find(Long id) {
        return (Opens) super.find(Opens.class, id);
    }

   /* public void saveOrUpdate(Opens opens) {
        super.saveOrUpdate(opens);
    }

    public void delete(Opens opens) {
        super.delete(opens);
    }*/

    public List findAll() {
        return super.findAll(Opens.class);
    }
   /* public void saveByCollection(Collection<Opens> opensList) {
		super.saveOrUpdateAll(opensList);
	}*/
    
    
    
    
    
    
}
