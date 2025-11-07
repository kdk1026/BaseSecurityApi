package com.kdk.app.common.encryption.controller;

import java.util.Base64;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdk.app.common.component.SpringBootProperty;
import com.kdk.app.common.encryption.vo.AesKeyResVo;
import com.kdk.app.common.encryption.vo.AesKeyVo;
import com.kdk.app.common.vo.ResponseCodeEnum;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * <pre>
 * -----------------------------------
 * 개정이력
 * -----------------------------------
 * 2025. 11. 7. 김대광	최초작성
 * </pre>
 *
 * <pre>
 * secretKey = primaryData.substring(18, 50);
 * iv = secondaryData.substring(34, 50);
 * </pre>
 *
 * <pre>
 * SSL MITM 프록시 공격에 의한 개인정보 방어 전략
 *  - MITM 이란 클라이언트와 서버 사이에서 스니핑 하는 행위
 *  - 암호화 되어 전송 되어도 프록시 서버에서 인증서 변조를 통하여 기밀정보를 스니핑 당하는 위험에 노출됨
 * </pre>
 *
 * <pre>
 * [이중 암호화에 대한 AI 의견]
 *  - 이미 SSL/TLS가 적용된 환경에서 AES를 추가적으로 적용하는 것은 매우 훌륭한 심층 방어 전략입니다.
 *  - 특히 개인정보와 같이 민감한 데이터를 다루는 서비스라면 더욱 권장할 만합니다.
 *  - 다만, 이로 인해 발생하는 성능 오버헤드와 특히 암호화 키 관리의 복잡성을 충분히 이해하고, 이를 안전하고 효율적으로 처리할 수 있는 방안을 마련하는 것이 중요합니다.
 * </pre>
 *
 * @author 김대광
 */
@Tag(name = "AES 이중 암호화", description = "AES 이중 암호화 API")
@RestController
@RequestMapping("/capsule-config")
public class DoubleEncryptionController {

	private final SpringBootProperty springBootProperty;

	public DoubleEncryptionController(SpringBootProperty springBootProperty) {
		this.springBootProperty = springBootProperty;
	}

	@Operation(summary = "암호화 키")
	@GetMapping()
	public ResponseEntity<AesKeyResVo> getAesEncryptionConfigData() {
		AesKeyResVo resVo = new AesKeyResVo();

		String secretKey = springBootProperty.getProperty("crypto.aes.key");
		String iv = springBootProperty.getProperty("crypto.aes.iv");

		String secretKeyPrefix = RandomStringUtils.secure().nextAlphanumeric(18);
		String ivPrefix = RandomStringUtils.secure().nextAlphanumeric(34);

		String secretKeySufix = RandomStringUtils.secure().nextAlphanumeric(14);
		String ivSufix = RandomStringUtils.secure().nextAlphanumeric(14);

		StringBuilder sb = new StringBuilder();

		sb.setLength(0);
		sb.append(secretKeyPrefix).append(Base64.getEncoder().encodeToString(secretKey.getBytes())).append(secretKeySufix);
		String primaryData = sb.toString();

		sb.setLength(0);
		sb.append(ivPrefix).append(Base64.getEncoder().encodeToString(iv.getBytes())).append(ivSufix);
		String secondaryData = sb.toString();

		AesKeyVo vo = new AesKeyVo();
		vo.setPrimaryData(primaryData);
		vo.setSecondaryData(secondaryData);
		resVo.setData(vo);

		resVo.setCode(ResponseCodeEnum.SUCCESS.getCode());
		resVo.setMessage(ResponseCodeEnum.SUCCESS.getMessage());
		return ResponseEntity.status(HttpStatus.OK).body(resVo);
	}

}
