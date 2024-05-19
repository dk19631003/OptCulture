package org.mq.loyality.common.dao;

import java.util.List;

import org.hibernate.criterion.Criterion;

/**
 * The AbstractDAO Interface.
 * 
 * @author Swapna Ayyalaraju
 * @param <E>
 * @param <I>
 */
public interface AbstractDAO<E, I> {

	E findById(I id);

	void saveOrUpdate(E e);

     void delete(E e);

     List<E> findByCustomQuery(String criteria, E e);
 	
     List<E> findByCriteria(List<Criterion> criterionList);
     
   

}
