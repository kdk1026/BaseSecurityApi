package com.kdk.app.common.util.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.kdk.app.common.component.ApplicationContextServe;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 6. 8. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class SpringBootPropertyUtil {

	private static final Logger logger = LoggerFactory.getLogger(SpringBootPropertyUtil.class);

	public static String getProperty(String propertyName) {
		return getProperty(propertyName, null);
	}

	public static String getProperty(String propertyName, String defaultValue) {
		String value = defaultValue;

		ApplicationContext applicationContext = ApplicationContextServe.getContext();

		if ( applicationContext.getEnvironment().getProperty(propertyName) == null ) {
			logger.warn(propertyName + " properties was not loaded.");
		} else {
			value = applicationContext.getEnvironment().getProperty(propertyName).toString().trim();
		}

		return value;
	}

}

