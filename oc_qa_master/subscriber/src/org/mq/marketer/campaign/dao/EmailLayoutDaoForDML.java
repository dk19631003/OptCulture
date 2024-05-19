package org.mq.marketer.campaign.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.EmailLayout;
import org.mq.marketer.campaign.general.Constants;

@SuppressWarnings({ "unchecked", "serial","unused","deprecation" })
public class EmailLayoutDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
    public EmailLayoutDaoForDML() {}


   /* public EmailLayout find(Long id) {
        return (EmailLayout) super.find(EmailLayout.class, id);
    }*/

    public void saveOrUpdate(EmailLayout emailLayout) {
        super.saveOrUpdate(emailLayout);
    }

    public void delete(EmailLayout emailLayout) {
        super.delete(emailLayout);
    }

/*    public List findAll() {
        return super.findAll(EmailLayout.class);
    }

    public List findByLayoutName(String name) {
        // return super.findAll (Email.class);
        List list = getHibernateTemplate().find(
                "from EmailLayout where layoutName = '" + name + "' ");

        if (list.size() != 1) {
            logger.error("Problem: Size of List is not 1 ; should ideally be 1");
        }
        return list; 
    }
    
	public EmailLayout findByLayoutName(String name, String userName) {
        List list = getHibernateTemplate().find("from EmailLayout where (layoutName = '" + name + "') and (users. userName = '" + userName + "')");
		EmailLayout el = null;
        if (list.size() > 0) {
            el = (EmailLayout)list.get(0);
        }
        return el; 
    }

	*//**
		Very IMP:Please review the method ..
	**//*
	public List findInfo(String userName){
		List list = new ArrayList();
		Set s = new HashSet();
		int count = 0;
		try{
			Date d = new Date();
			d.setDate(d.getDate()-14);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List emailLayout = getHibernateTemplate().find("from EmailLayout where users.userName='" + userName + "' and createdDate > '" +format.format(d) + "' ");
			for(Object obj:emailLayout){
				Set s1 = ((EmailLayout)obj).getCampaigns();
				if(s1.size()!=0)
					count++;
				s.addAll(s1);
			}
			list.add(emailLayout.size());
			list.add(count);
			list.add(s.size());
			return list;
		}catch(Exception e){
			logger.error("** Exception "+e.getMessage()+" **");
			list.add(0);
			list.add(0);
			list.add(0);
			return list;
		}
	}

	public List findByStatus(String userName,String status){
		return getHibernateTemplate().find("from EmailLayout where users.userName='" + userName + "' and status = '" + status + "' ");
	}
	public List findEmailNamesByStatus(String userName,String status){
		return getHibernateTemplate().find("select layoutName from EmailLayout where users.userName='" + userName + "' and status = '" + status + "' ");
	}
	public int getCount(String userName){
		try{
			List list =  getHibernateTemplate().find("select count(*) from EmailLayout where users.userName='" + userName + "' ");
			if(list.size()>0){
				return ((Long)list.get(0)).intValue();
			}else return 0;
		}catch(Exception e){
			logger.error("** Exception :"+e+"**");
			return 0;
		}
	}*/
}
