package com.org.worker.util;

import com.org.worker.aspect.SimpleEncryptJavaAop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Crypto {

    public static String encrypt(String value) {
        return SimpleEncryptJavaAop.encrypt(value);
    }

    public static String decrypt(String value) {
        return SimpleEncryptJavaAop.decrypt(value);
    }

}
