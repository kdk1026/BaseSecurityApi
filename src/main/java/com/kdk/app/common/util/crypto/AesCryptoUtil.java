package com.kdk.app.common.util.crypto;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kdk.app.common.ExceptionMessage;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 1. 28. kdk	최초작성
 * </pre>
 *
 * <pre>
 * FrontEnd와 암복호화 처리용
 *
 * - 암호화 키 : 128비트(16자), 192비트(24자), 256비트(32자)
 * - iv : 16자
 * </pre>
 *
 * @author kdk
 */
public class AesCryptoUtil {

	private static final Logger logger = LoggerFactory.getLogger(AesCryptoUtil.class);

	private AesCryptoUtil() {
		super();
	}

	private static final String CHARSET = StandardCharsets.UTF_8.toString();

	public static final String AES_CBC_PKCS5PADDING ="AES/CBC/PKCS5Padding";
	public static final String AES_ECB_PKCS5PADDING ="AES/ECB/PKCS5Padding";

	public static EncryptResult encrypt(String key, String padding, String plainText) {
		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException("key must not be null");
		}

		if ( StringUtils.isBlank(padding) ) {
			throw new IllegalArgumentException("padding must not be null");
		}

		if ( StringUtils.isBlank(plainText) ) {
			throw new IllegalArgumentException("plainText must not be null");
		}

		if ( key.length() != 16 && key.length() != 24 && key.length() != 32 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("key"));
		}

		String encryptedText = "";
		String generatedIvString = null;

		try {
			SecretKey secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				SecureRandom secureRandom = new SecureRandom();
                byte[] ivBytes = new byte[16];
                secureRandom.nextBytes(ivBytes);

				// CBC의 경우, IvParameterSpec 생략 가능
				cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));

				generatedIvString = Base64.getEncoder().encodeToString(ivBytes);
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			}

			byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(CHARSET));
			encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                UnsupportedEncodingException | IllegalArgumentException e) {
        	logger.error("", e);
        }
		return new EncryptResult(encryptedText, generatedIvString);
	}

	public static String decrypt(String key, String iv, String padding, String encryptedText) {
		if ( StringUtils.isBlank(key) ) {
			throw new IllegalArgumentException("key must not be null");
		}

		if ( StringUtils.isBlank(padding) ) {
			throw new IllegalArgumentException("padding must not be null");
		}

		if ( StringUtils.isBlank(encryptedText) ) {
			throw new IllegalArgumentException("encryptedText must not be null");
		}

		if ( key.length() != 16 && key.length() != 24 && key.length() != 32 ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("key"));
		}

		String decryptedText = "";
		try {
			SecretKey secretKey = new SecretKeySpec(key.getBytes(CHARSET), "AES");

			Cipher cipher = Cipher.getInstance(padding);

			if ( padding.indexOf("CBC") > -1 ) {
				Objects.requireNonNull(iv, "iv must not be null for CBC mode");

				byte[] ivBytes = Base64.getDecoder().decode(iv);

				// CBC의 경우, IvParameterSpec 생략 가능
				cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));
			} else {
				// ECB의 경우, IvParameterSpec 사용 불가
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
			}

			byte[] textBytes = Base64.getDecoder().decode(encryptedText);
			decryptedText = new String(cipher.doFinal(textBytes), CHARSET);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException |
                UnsupportedEncodingException | IllegalArgumentException e) {
        	logger.error("", e);
        }
		return decryptedText;
	}

	public static class EncryptResult {
        private String encryptedText;
        private String iv; // Base64 인코딩된 IV 문자열

        public EncryptResult(String encryptedText, String iv) {
            this.encryptedText = encryptedText;
            this.iv = iv;
        }

        public String getEncryptedText() {
            return encryptedText;
        }

        public String getIv() {
            return iv;
        }
    }

}
