package com.kdk.app.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 10. 20. 김대광	최초작성
 * 2025. 5. 27. 김대광	유틸은 Singleton 패턴을 사용하지 않는 것이 좋다는 의견 반영, 제미나이에 의한 일부 코드 개선
 * </pre>
 *
 *
 * @author 김대광
 */
public class FileMimeTypeUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileMimeTypeUtil.class);

	private FileMimeTypeUtil() {
		super();
	}

	public static String getFileMimeTypeTika(InputStream is) {
		Objects.requireNonNull(is, "InputStream must not be null");

		String mimeType = "";
		Tika tika = new Tika();

		try {
			mimeType = tika.detect(is);

		} catch (IOException e) {
			logger.error("", e);
		}

		return mimeType;
	}

}
