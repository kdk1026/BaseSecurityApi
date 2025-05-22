package com.kdk.app.common.util.spring;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 6. 7. kdk	최초작성
 * </pre>
 *
 * @see <a href="https://offbyone.tistory.com/144">Ref</a>
 * @author kdk
 */
public class ContextUtil {

	private static ContextUtil instance;

	/**
	 * 외부에서 객체 인스턴스화 불가
	 */
	private ContextUtil() {
		super();
	}

	public static synchronized ContextUtil getInstance() {
		if (instance == null) {
			instance = new ContextUtil();
		}

		return instance;
	}

	/**
	 * 빈을 직접 얻습니다.
	 * @param beanName
	 * @return
	 */
	public Object getBean(String beanName) {
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		return (context == null) ? null : context.getBean(beanName);
	}

	/**
	 * HttpServletReqeust 객체를 직접 얻습니다.
	 * @return
	 */
	public HttpServletRequest getRequest() {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getRequest();
	}

	/**
	 * HttpServletResponse 객체를 직접 얻습니다.
	 * @return
	 */
	public HttpServletResponse getResponse() {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getResponse();
	}

	/**
	 * HttpSession 객체를 직접 얻습니다.
	 * @param create
	 * @return
	 */
	public HttpSession getSession(boolean create) {
		return getRequest().getSession(create);
	}

	/**
	 * REQUEST 영역에서 가져오기
	 * @param key
	 * @return
	 */
	public Object getAttrFromRequest(String key) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getAttribute(key, ServletRequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * REQUEST 영역에 객체 저장
	 * @param key
	 * @param obj
	 */
	public void setAttrToRequest(String key, Object obj) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		attr.setAttribute(key, obj, ServletRequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * REQUEST 영역에서 삭제
	 * @param key
	 */
	public void removeAttrFromRequest(String key) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		attr.removeAttribute(key, ServletRequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * SESSION 영역에서 가져오기
	 * @param key
	 * @return
	 */
	public Object getAttrFromSession(String key) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getAttribute(key, ServletRequestAttributes.SCOPE_SESSION);
	}

	/**
	 * SESSION 영역에 객체 저장
	 * @param key
	 * @return
	 */
	public void setAttrToSession(String key, Object obj) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		attr.setAttribute(key, obj, ServletRequestAttributes.SCOPE_SESSION);
	}

	/**
	 * SESSION 영역에서 삭제
	 * @param key
	 */
	public void removeAttrFromSession(String key) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		attr.removeAttribute(key, ServletRequestAttributes.SCOPE_SESSION);
	}
}