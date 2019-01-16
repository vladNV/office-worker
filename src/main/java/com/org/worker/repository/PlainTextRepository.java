package com.org.worker.repository;

public interface PlainTextRepository {

    String authenticate(String login, String password);

    boolean authIgnoreEncrypt(String login, String encryptedPassword);

}
