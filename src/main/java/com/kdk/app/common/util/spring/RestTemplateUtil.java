package com.kdk.app.common.util.spring;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2024. 10. 22. 김대광	최초작성
 * 2025. 05. 22. 김대광   개선
 * </pre>
 *
 * <pre>
 * Spring 전용 Http Client
 *  - Dependency
 *    > Apache HttpClient5
 *    > Jackson
 * </pre>
 *
 * isVerify 의 경우, true로 해서 오류 나는 경우 false로
 *
 * @author 김대광
 */
public class RestTemplateUtil {

	private static final Logger logger = LoggerFactory.getLogger(RestTemplateUtil.class);

	private RestTemplateUtil() {
		super();
	}

	private static class ExceptionMessage {

		private ExceptionMessage() {
		}

		public static String isNull(String paramName) {
	        return String.format("'%s' is null", paramName);
	    }

	}

	private static final int TIMEOUT_MS = 5000;

	// 일반용 (isVerify = true)
    private static final RestTemplate VERIFIED_TEMPLATE = new RestTemplate();

    // SSL 무시용 (isVerify = false)
    private static final RestTemplate UNVERIFIED_TEMPLATE = createUnverifiedTemplate();

    public static RestTemplate getRestTemplate(boolean isVerify) {
        return isVerify ? VERIFIED_TEMPLATE : UNVERIFIED_TEMPLATE;
    }

    private static RestTemplate createUnverifiedTemplate() {
    	try {
    		// 모든 인증서를 신뢰하는 SSLContext
    		SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                    .build();

    		// TlsStrategy 사용
    		TlsSocketStrategy tlsStrategy = new DefaultClientTlsStrategy(
                    sslContext,
                    NoopHostnameVerifier.INSTANCE
            );

    		// 1. ConnectionConfig 설정
    		ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setConnectTimeout(Timeout.ofMilliseconds(TIMEOUT_MS))
                    .setSocketTimeout(Timeout.ofMilliseconds(TIMEOUT_MS))
                    .build();

    		// 2. ConnectionManager에 TLS 전략과 ConnectionConfig 주입
            HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setTlsSocketStrategy(tlsStrategy)
                    .setDefaultConnectionConfig(connectionConfig)
                    .setMaxConnTotal(100)
                    .setMaxConnPerRoute(5)
                    .build();

            // 3. RequestConfig 설정 (ResponseTimeout 및 연결 요청 타임아웃)
            RequestConfig requestConfig = RequestConfig.custom()
                    .setResponseTimeout(Timeout.ofMilliseconds(TIMEOUT_MS))
                    .setConnectionRequestTimeout(Timeout.ofMilliseconds(TIMEOUT_MS))
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(requestConfig)
                    .build();

            return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));

		} catch (Exception e) {
			logger.error("RestTemplate 생성 실패", e);
			return new RestTemplate();
		}
    }


	private static class Convert {
		@SuppressWarnings("unchecked")
		private static Map<String, Object> objectToMap(Object obj) {
			Map<String, Object> map = null;

			ObjectMapper oMapper = new ObjectMapper();
			map = oMapper.convertValue(obj, Map.class);

			return map;
		}

		private static MultiValueMap<String, String> mapToHttpHeaders(Map<String, Object> headerMap, HttpHeaders headers) {
			MultiValueMap<String, String> mMap = new LinkedMultiValueMap<>();

			MediaType mediaType = headers.getContentType();
			if ( mediaType != null ) {
				mMap.add(HttpHeaders.CONTENT_TYPE, mediaType.toString());
			}

			if ( headerMap != null ) {
				Iterator<String> it = headerMap.keySet().iterator();

				while ( it.hasNext() ) {
					String sKey = it.next();
					Object value = headerMap.get(sKey);

					mMap.add(sKey, String.valueOf(value));
				}
			}

			return mMap;
		}

		private static MultiValueMap<String, Object> hashMapToMultiValueMap(Map<String, Object> map) throws IOException {
			MultiValueMap<String, Object> mMap = new LinkedMultiValueMap<>();

			Iterator<String> it = map.keySet().iterator();
			while ( it.hasNext() ) {
				String sKey = it.next();
				Object value = map.get(sKey);

				if ( value instanceof List<?> ) {
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>) value;
					mMap.put(sKey, list);

				} else if ( value instanceof File file ) {
					mMap.add(sKey, new FileSystemResource(file));

				} else if ( value instanceof MultipartFile mFile ) {
					mMap.add(sKey, new ByteArrayResource(mFile.getBytes()) {

						@Override
						public String getFilename() {
							return mFile.getOriginalFilename();
						}
					});

				} else {
					mMap.add(sKey, String.valueOf(value));
				}
			}

			return mMap;
		}
	}

	@SuppressWarnings("unchecked")
	public static ResponseEntity<Object> get(boolean isVerify, String url, MediaType mediaType
			, Map<String, Object> headerMap, Class<?> responseType, Object... uriVariables) {

		if ( ObjectUtils.isEmpty(url.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("url"));
		}

		Objects.requireNonNull(responseType, ExceptionMessage.isNull("responseType"));

		RestTemplate restTemplate = RestTemplateUtil.getRestTemplate(isVerify);
		if (restTemplate == null) {
			return null;
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		if (mediaType != null) {
			httpHeaders.setContentType(mediaType);
		}

		MultiValueMap<String, String> headers = Convert.mapToHttpHeaders(headerMap, httpHeaders);

		HttpEntity<Object> request = new HttpEntity<>(headers);

		if ( uriVariables != null ) {
			return (ResponseEntity<Object>) restTemplate.exchange(url, HttpMethod.GET, request, responseType, uriVariables);
		} else {
			return (ResponseEntity<Object>) restTemplate.exchange(url, HttpMethod.GET, request, responseType);
		}
	}

	public static ResponseEntity<Object> post(boolean isVerify, String url, MediaType mediaType
			, Map<String, Object> headerMap, Object body, Class<?> responseType, Object... uriVariables) throws IOException {

		Map<String, Object> bodyMap = Convert.objectToMap(body);
		return post(isVerify, url, mediaType, headerMap, bodyMap, responseType, uriVariables);
	}

	@SuppressWarnings("unchecked")
	public static ResponseEntity<Object> post(boolean isVerify, String url, MediaType mediaType
			, Map<String, Object> headerMap, Map<String, Object> bodyMap, Class<?> responseType, Object... uriVariables) throws IOException {

		if ( ObjectUtils.isEmpty(url.trim()) ) {
			throw new IllegalArgumentException(ExceptionMessage.isNull("url"));
		}

		Objects.requireNonNull(responseType, ExceptionMessage.isNull("responseType"));

		RestTemplate restTemplate = RestTemplateUtil.getRestTemplate(isVerify);
		if (restTemplate == null) {
			return null;
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		if (mediaType != null) {
			httpHeaders.setContentType(mediaType);
		}

		MultiValueMap<String, String> headers = Convert.mapToHttpHeaders(headerMap, httpHeaders);

		HttpEntity<Object> request = null;
		MultiValueMap<String, Object> mMap = null;

		if ( bodyMap != null ) {
			if ( mediaType == null || MediaType.APPLICATION_FORM_URLENCODED.equals(mediaType) || MediaType.MULTIPART_FORM_DATA.equals(mediaType) ) {
				mMap = Convert.hashMapToMultiValueMap(bodyMap);
				request = new HttpEntity<>(mMap, headers);

			} else {
				request = new HttpEntity<>(bodyMap, headers);
			}
		} else {
			request = new HttpEntity<>(headers);
		}

		if ( uriVariables != null ) {
			return (ResponseEntity<Object>) restTemplate.postForEntity(url, request, responseType, uriVariables);
		} else {
			return (ResponseEntity<Object>) restTemplate.postForEntity(url, request, responseType);
		}
	}

}
