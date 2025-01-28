package com.kdk.app.common.util.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 28. kdk	최초작성
 * </pre>
 *
 * <pre>
 * - 암호화 키 : 128비트(16자), 192비트(24자), 256비트(32자)
 * - iv : 16자
 * </pre>
 *
 * @author kdk
 */
public class AesCryptoUtil {

	private AesCryptoUtil() {
		super();
	}

	private final Logger logger = LoggerFactory.getLogger(AesCryptoUtil.class);

	private static class LazyHolder {
		private static final AesCryptoUtil INSTANCE = new AesCryptoUtil();
	}

	public static AesCryptoUtil getInstance() {
		return LazyHolder.INSTANCE;
	}

	private final String CHARSET = StandardCharsets.UTF_8.toString();

	public final String AES_CBC_PKCS5PADDING ="AES/CBC/PKCS5Padding";
	public final String AES_ECB_PKCS5PADDING ="AES/ECB/PKCS5Padding";

	public String encrypt(String key, String iv, String padding, String plainText) {
		String sEncryptText = "";

		try {
			SecretKey secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				// CBC의 경우, IvParameterSpec 생략 가능
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes(CHARSET)));
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			}

			byte[] textBytes = cipher.doFinal(plainText.getBytes(CHARSET));
			sEncryptText = Base64.getEncoder().encodeToString(textBytes);

		} catch (Exception e) {
			logger.error("", e);
		}

		return sEncryptText;
	}

	public String decrypt(String key, String iv, String padding, String encryptText) {
		String sDecryptText = "";
		try {
			SecretKey secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				// CBC의 경우, IvParameterSpec 생략 가능
				cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv.getBytes(CHARSET)));
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
			}

			byte[] textBytes = Base64.getDecoder().decode(encryptText);
			sDecryptText = new String(cipher.doFinal(textBytes), CHARSET);

		} catch (Exception e) {
			logger.error("", e);
		}
		return sDecryptText;
	}

}
