package com.optculture.app.util;
import java.util.*;

public class DateRangeCalculator {
            // Define a reference date (current date)

            public Calendar getDateByRange(String dateRange){
                Calendar calendar = Calendar.getInstance();
                        switch(dateRange){
                    case "LAST_ONE_MONTH" :calendar.add(Calendar.MONTH, -1);
                                               break;
                    case "LAST_SIX_MONTHS" :calendar.add(Calendar.MONTH, -6);
                                                break;
                    case "LAST_THREE_MONTHS":calendar.add(Calendar.MONTH, -3);;
                                                break;
                    case "TODAY"     : calendar.add(Calendar.DAY_OF_YEAR, -1);
                                                break;
                    default       :  calendar.add(Calendar.YEAR, -1);
                                                 break;
                        }
              return calendar;
        }

}
