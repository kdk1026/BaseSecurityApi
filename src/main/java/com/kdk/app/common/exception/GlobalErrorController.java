package com.kdk.app.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 11. 12. 김대광	최초작성
 * </pre>
 *
 *
 * @author 김대광
 */
@ControllerAdvice
public class GlobalErrorController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
    	// XXX `홈`, `되돌아가기` 버튼 액션을 위해, frontUrl 을 넘겨준다.
        return "error/404";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException ex, Model model) {
    	// XXX `홈`, `되돌아가기` 버튼 액션을 위해, frontUrl 을 넘겨준다.
        return "error/404";
    }

}
