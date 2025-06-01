
package com.kdk.hash;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.kdk.app.common.util.crypto.BouncyCastleHashingUtil;

public class TestBcrypt {

	@Test
	public void test() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		String hashedText = encoder.encode("qwer1234");
		System.out.println(hashedText);

		assertTrue( encoder.matches("qwer1234", hashedText) );

		String hashedText2 = BouncyCastleHashingUtil.Bcrypt.bcryptHash("qwer1234");
		System.out.println(hashedText2);

		assertTrue( BouncyCastleHashingUtil.Bcrypt.checkBcryptHash("qwer1234", hashedText2) );
	}

}
