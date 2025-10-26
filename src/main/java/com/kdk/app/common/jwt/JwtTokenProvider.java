package com.kdk.app.common.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.WebUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kdk.app.common.CommonConstants;
import com.kdk.app.common.component.SpringBootProperty;
import com.kdk.app.common.util.crypto.BouncyCastleAesUtil;
import com.kdk.app.common.util.crypto.EncryptResult;
import com.kdk.app.common.util.json.GsonUtil;
import com.kdk.app.login.vo.UserVo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class JwtTokenProvider {

	private final SpringBootProperty springBootProperty;

	public JwtTokenProvider(SpringBootProperty springBootProperty) {
		this.springBootProperty = springBootProperty;
	}

	private static final String JWT_SECRET_KEY = "jwt.secret.key";
	private static final String CRYPTO_AES_KEY = "crypto.aes.key";

// ------------------------------------------------------------------------
// 토큰 생성
// ------------------------------------------------------------------------
	/**
	 * Access 토큰 생성
	 * @param user
	 * @return
	 */
	public JwtTokenVo generateAccessToken(UserVo user) {
		String sExpireTime = springBootProperty.getProperty("jwt.access.expire.minute");
		String sSubject = springBootProperty.getProperty("jwt.subject");
		String sIssuer = springBootProperty.getProperty("jwt.issuer");

		String sSecretKey = springBootProperty.getProperty(JWT_SECRET_KEY);
		SecretKey key = Keys.hmacShaKeyFor(sSecretKey.getBytes());

		JwtBuilder builder = Jwts.builder();
		builder.claims()
			.id(user.getUsername())
			.issuedAt(new Date())
			.subject(sSubject)
			.issuer(sIssuer)
			.expiration(this.getExpirationTime(sExpireTime) )
			.and()
			.signWith(key, Jwts.SIG.HS256)
			.compact();

		String sUserJson = GsonUtil.ToJson.converterObjToJsonStr(user, false);

		String sKey = springBootProperty.getProperty(CRYPTO_AES_KEY);

		EncryptResult encryptResult = BouncyCastleAesUtil.encrypt(BouncyCastleAesUtil.Algorithm.AES_CBC_PKCS5PADDING, sKey, null, sUserJson);
		String sEncryptUserJson = encryptResult.getEncryptedText();

		builder.claim(CommonConstants.Jwt.USER_INFO, sEncryptUserJson);
		builder.claim(CommonConstants.Jwt.TOKEN_KIND, CommonConstants.Jwt.ACCESS_TOKEN);
		builder.claim("iv", encryptResult.getIv());

		JwtTokenVo jwtTokenVo = new JwtTokenVo();
		jwtTokenVo.setAccessToken(builder.compact());
		jwtTokenVo.setAccessTokenIv(encryptResult.getIv());
		return jwtTokenVo;
	}

	/**
	 * Refresh 토큰 생성
	 * @param user
	 * @return
	 */
	public JwtTokenVo generateRefreshToken(UserVo user) {
		String sExpireTime = springBootProperty.getProperty("jwt.refresh.expire.minute");
		String sSubject = springBootProperty.getProperty("jwt.subject");
		String sIssuer = springBootProperty.getProperty("jwt.issuer");

		String sSecretKey = springBootProperty.getProperty(JWT_SECRET_KEY);
		SecretKey key = Keys.hmacShaKeyFor(sSecretKey.getBytes());

		JwtBuilder builder = Jwts.builder();
			builder.claims()
			.id(user.getUsername())
			.issuedAt(new Date())
			.subject(sSubject)
			.issuer(sIssuer)
			.expiration(this.getExpirationTime(sExpireTime) )
			.and()
			.signWith(key, Jwts.SIG.HS256)
			.compact();

		String sUserJson = GsonUtil.ToJson.converterObjToJsonStr(user, false);

		String sKey = springBootProperty.getProperty(CRYPTO_AES_KEY);

		EncryptResult encryptResult = BouncyCastleAesUtil.encrypt(BouncyCastleAesUtil.Algorithm.AES_CBC_PKCS5PADDING, sKey, null, sUserJson);
		String sEncryptUserJson = encryptResult.getEncryptedText();

		builder.claim(CommonConstants.Jwt.USER_INFO, sEncryptUserJson);
		builder.claim(CommonConstants.Jwt.TOKEN_KIND, CommonConstants.Jwt.REFRESH_TOKEN);
		builder.claim("iv", encryptResult.getIv());

		JwtTokenVo jwtTokenVo = new JwtTokenVo();
		jwtTokenVo.setRefreshToken(builder.compact());
		jwtTokenVo.setAccessTokenIv(encryptResult.getIv());
		return jwtTokenVo;
	}

	/**
	 * 만료일 계산
	 * @param expireIn
	 * @return
	 */
	private Date getExpirationTime(String expireIn) {
		int nExpireIn = Integer.parseInt(expireIn);
		Date date = null;

		LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(nExpireIn);
		date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

		return date;
    }

// ------------------------------------------------------------------------
// 토큰 가져오기
// ------------------------------------------------------------------------
	/**
	 * 쿠키에서 JWT 토큰 추출
	 * @param request
	 * @return
	 */
	public String getTokenFromCookie(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, CommonConstants.Jwt.ACCESS_TOKEN);
		return cookie == null ? null : cookie.getValue();
	}

	/**
	 * 헤더에서 JWT 토큰 추출
	 * @param request
	 * @return
	 */
	public String getTokenFromReqHeader(HttpServletRequest request) {
		String sToken = null;

		String sJwtHeader = springBootProperty.getProperty("jwt.header");

		String sAuthorizationHeader = request.getHeader(sJwtHeader);
		String sTokenType = springBootProperty.getProperty("jwt.token.type");

		if ( sTokenType.lastIndexOf(" ") == -1 ) {
			sTokenType = sTokenType + " ";
		}

		if ( !StringUtils.isBlank(sAuthorizationHeader) && sAuthorizationHeader.startsWith(sTokenType) ) {
			sToken = sAuthorizationHeader.substring(sTokenType.length());
		}

		return sToken;
	}

	/**
	 * JWT 토큰 유효성 검증
	 * @param token
	 * @return (0: false, 1: true, 2: expired)
	 */
	public int isValidateJwtToken(String token) {
		String sSecretKey = springBootProperty.getProperty(JWT_SECRET_KEY);
		SecretKey key = Keys.hmacShaKeyFor(sSecretKey.getBytes());

		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return 1;

		} catch (SignatureException e) {
			log.error("Invalid JWT signature");

		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token");

		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
			return 2;

		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");

		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
		}

		return 0;
	}

	/**
	 * JWT 토큰에서 만료일 가져오기
	 * @param token
	 * @return
	 */
	public Date getExpirationFromJwt(String token) {
		String sSecretKey = springBootProperty.getProperty(JWT_SECRET_KEY);
		SecretKey key = Keys.hmacShaKeyFor(sSecretKey.getBytes());

		Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

		return claims.getExpiration();
	}

	/**
	 * JWT 토큰 유형 가져오기
	 * @param token
	 * @return
	 */
	public String getTokenKind(String token) {
		String sSecretKey = springBootProperty.getProperty(JWT_SECRET_KEY);
		SecretKey key = Keys.hmacShaKeyFor(sSecretKey.getBytes());

		Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

		return (String) claims.get(CommonConstants.Jwt.TOKEN_KIND);
	}

// ------------------------------------------------------------------------
// 토큰에서 로그인 정보 추출
// ------------------------------------------------------------------------
	/**
	 * JWT 토큰에서 로그인 정보 가져오기
	 * @param token
	 * @return
	 */
	public UserVo getAuthUserFromJwt(String token) {
		UserVo user = null;

		if ( !StringUtils.isEmpty(token) ) {
			String sSecretKey = springBootProperty.getProperty(JWT_SECRET_KEY);
			SecretKey key = Keys.hmacShaKeyFor(sSecretKey.getBytes());

			Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

			String sKey = springBootProperty.getProperty(CRYPTO_AES_KEY);
			String sEncryptUserJson = String.valueOf(claims.get(CommonConstants.Jwt.USER_INFO));

			String sIv = String.valueOf(claims.get("iv"));
			String sUserJson = BouncyCastleAesUtil.decrypt(BouncyCastleAesUtil.Algorithm.AES_CBC_PKCS5PADDING, sKey, sIv, true, sEncryptUserJson);

			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			user = gson.fromJson(sUserJson, UserVo.class);
		}

		return user;
	}

}