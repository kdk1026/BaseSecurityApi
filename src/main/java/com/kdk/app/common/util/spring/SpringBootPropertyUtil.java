package com.kdk.app.common.util.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.kdk.app.common.component.ApplicationContextServe;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 6. 7. kdk	최초작성
 * </pre>
 *
 *
 * @author kdk
 */
public class SpringBootPropertyUtil {

	private static final Logger logger = LoggerFactory.getLogger(SpringBootPropertyUtil.class);

	public static String getProperty(String propertyName) {
		return getProperty(propertyName, null);
	}

	public static String getProperty(String propertyName, String defaultValue) {
		if ( StringUtils.hasText(propertyName) ) {
			throw new IllegalStateException("propertyName is null");
		}

		String value = "";

		ApplicationContext applicationContext = ApplicationContextServe.getContext();

		String property = applicationContext.getEnvironment().getProperty(propertyName);
		if ( property == null ) {
			logger.warn("{} properties was not loaded.", propertyName);
			value = defaultValue;
		} else {
			value = property.trim();
		}

		return value;
	}

}
