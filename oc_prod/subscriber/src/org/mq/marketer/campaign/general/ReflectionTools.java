package org.mq.marketer.campaign.general;

import java.lang.reflect.Method;

public class ReflectionTools {

	public static Method getGetterMethod(Object bean, String propertyName) {
		Class<?> beanClass = bean.getClass();

		try {
			return beanClass.getMethod(getGetterName(propertyName));
		} catch (Throwable t) {
			// ignore
		}

		try {
			return beanClass.getMethod(getBooleanGetterName(propertyName));
		} catch (Throwable t) {
			// ignore
			throw new IllegalArgumentException("not getter method found for '" 	+ propertyName + "', bean: " + bean);
		}

	}

	public static String getGetterName(String propertyName) {
		String getterName = "get" + capitalizeFirstLetter(propertyName);
		return getterName;
	}

	public static String getBooleanGetterName(String propertyName) {
		String getterName = "is" + capitalizeFirstLetter(propertyName);
		return getterName;
	}

	private static String capitalizeFirstLetter(String string) {
		String result = string.substring(0, 1).toUpperCase() + string.substring(1, string.length());
		return result;
	}
}