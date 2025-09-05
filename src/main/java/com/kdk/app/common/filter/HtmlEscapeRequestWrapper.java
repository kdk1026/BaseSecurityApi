package com.kdk.app.common.filter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 9. 5. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
public class HtmlEscapeRequestWrapper extends HttpServletRequestWrapper {

	public HtmlEscapeRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if ( value != null ) {
			return StringEscapeUtils.escapeHtml4(value);
		}
		return null;
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if ( values != null ) {
			String[] escapedValues = new String[values.length];
			for (int i=0; i < values.length; i++) {
				escapedValues[i] = StringEscapeUtils.escapeHtml4(values[i]);
			}
			return escapedValues;
		}
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = super.getParameterMap();
		Map<String, String[]> newMap = new HashMap<>();

		for ( Map.Entry<String, String[]> entry : map.entrySet() ) {
			String key = entry.getKey();
			String[] values = entry.getValue();

			if ( values != null ) {
				String[] escapedValues = new String[values.length];
				for (int i=0; i < values.length; i++) {
					escapedValues[i] = StringEscapeUtils.escapeHtml4(values[i]);
				}
				newMap.put(key, escapedValues);
			} else {
				newMap.put(key, null);
			}
		}

		return newMap;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return super.getParameterNames();
	}

}