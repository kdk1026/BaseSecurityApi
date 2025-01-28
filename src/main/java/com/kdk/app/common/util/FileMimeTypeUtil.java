package com.kdk.app.common.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 10. 20. 김대광	최초작성
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

	public static FileMimeTypeUtil getInstance() {
		return LazyHolder.INSTANCE;
	}

	private static class LazyHolder {
		private static final FileMimeTypeUtil INSTANCE = new FileMimeTypeUtil();
	}

	public String getFileMimeTypeTika(InputStream is) {
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
