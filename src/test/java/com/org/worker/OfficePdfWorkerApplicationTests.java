package com.org.worker;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OfficePdfWorkerApplicationTests {

	@Test
	public void test() {
		BasicPasswordEncryptor basicPasswordEncryptor = new BasicPasswordEncryptor();
		String encrypted = basicPasswordEncryptor.encryptPassword("123");
		System.out.println(encrypted);
	}

}

