package org.mq.marketer.campaign.components;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.ReflectionTools;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.A;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

public class MyListComparator implements Comparator<Object>, Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);	 
    
	private boolean asc = true;
	private Field field = null;
	private Method m_getter = null;
	private Integer colIndex = null;
   
   public MyListComparator(boolean asc, Field field, Integer colIndex ) {
       this.asc = asc;
       this.field = field;
       this.colIndex = colIndex;
   }


	@Override
	public int compare(Object o1, Object o2) {
	   try {
		   if(field==null && colIndex==null) return 0;
		   
		   if(!(o1 instanceof Listitem)) return 0;
		   Listitem li1 = (Listitem)o1;
		   Listitem li2 = (Listitem)o2;
		   
		   if(field==null) {
			  
			  Listcell tempLc1 = (Listcell)li1.getChildren().get(colIndex.intValue());
			  Listcell tempLc2 = (Listcell)li2.getChildren().get(colIndex.intValue());
			  
				String cellVal1="", cellVal2="";
				
				if(tempLc1.getChildren().isEmpty()) {
					cellVal1 = tempLc1.getLabel().trim();
					cellVal2 = tempLc2.getLabel().trim();
				}
				else {
					Component cellComp = tempLc1.getFirstChild();
					if(cellComp instanceof Label) {
						cellVal1 = ((Label)cellComp).getValue();
						cellVal2 = ((Label)cellComp).getValue();
					}
					else if(cellComp instanceof A) {
						cellVal1 = ((A)cellComp).getLabel();
						cellVal2 = ((A)cellComp).getLabel();
					}
					else if(cellComp instanceof Textbox) {
						cellVal1 = ((Textbox)cellComp).getValue();
						cellVal2 = ((Textbox)cellComp).getValue();
					}
				}
				logger.info("Cell Vals : "+cellVal1+" , "+cellVal2);
				return cellVal1.compareTo(cellVal2) * (asc ? 1 : -1);
		   } // if
		   
		   
		   Object entity1 = li1.getValue();
		   Object entity2 = li2.getValue();
		   
		   if(entity1==null || entity2==null) return 0;
		   
		   Class type = field.getType();
		   
		   logger.info("entity1="+entity1+"  entity2="+entity2);
		   
		   if (m_getter == null) {
			   m_getter = ReflectionTools.getGetterMethod(entity1, field.getName());
		   }
		   
			Object value1 = m_getter.invoke(entity1);
			Object value2 = m_getter.invoke(entity2);

			logger.info("Values ========="+value1+" :: "+value1);
			if(value1==null && value2==null) return 0;
			else if(value1==null) return 1 * (asc ? 1 : -1);
			else if(value2==null) return -1 * (asc ? 1 : -1);
			
		   if(type.isAssignableFrom(String.class)) {
			   logger.info("String ========="+value1+" :: "+value2);
			   return value1.toString().toLowerCase().compareTo(value2.toString().toLowerCase())  * (asc ? 1 : -1);
		   }
		   else if(type.isAssignableFrom(Long.class) || 
				   type.isAssignableFrom(Integer.class) || 
				   type.isAssignableFrom(Float.class) ||
				   type.isAssignableFrom(Double.class)) {
			   logger.info("Long ========="+value1+" :: "+value2);
			   if(Double.parseDouble(value1.toString().trim()) > Double.parseDouble(value2.toString().trim())) {
				   return 1 * (asc ? 1 : -1);
			   }
			   else {
				   return -1 * (asc ? 1 : -1);
			   }
		   } 
		   else if(type.isAssignableFrom(Calendar.class)) {
			   logger.info("Calendar ========="+value1+" :: "+value2);
			   return ((Calendar)value1).compareTo((Calendar)value2) * (asc ? 1 : -1);
		   } 
		
		} catch (Exception e) {
			logger.error("Exception  ::", e);
		}
	   return 0;

   }

}
