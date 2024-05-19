/**
 * 
 */
package org.mq.loyality.exception;

import org.hibernate.HibernateException;

/**
 * @author manjunath.nunna
 *
 */
public class BaseDAOException  extends HibernateException{
	
	public BaseDAOException(String message) {
        super(message);
    }

    public BaseDAOException(String message, Throwable throwable) {
        super(message, throwable);
    }


}
