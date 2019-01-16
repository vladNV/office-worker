package com.org.worker.repository;

import com.org.worker.exception.AuthenticationFailed;
import com.org.worker.exception.FileApiError;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class PlainTextRepositoryImpl implements PlainTextRepository {
    @Value("${secret.path}")
    private String path;

    @Override
    public String authenticate(@NotBlank final String login, @NotBlank final String password) {
        BasicPasswordEncryptor basicPasswordEncryptor = new BasicPasswordEncryptor();
        String encrypted = findUserByLogin(login);
        if (!basicPasswordEncryptor.checkPassword(password,encrypted)) {
            throw new AuthenticationFailed("Invalid password");
        }
        return encrypted;
    }

    @Override
    public boolean authIgnoreEncrypt(String login, String encryptedPassword) {
        return encryptedPassword.equals(findUserByLogin(login));
    }

    private String findUserByLogin(String login) {
        try {
            return Files.lines(Paths.get(path))
                    .filter(line -> line.startsWith(login + "_"))
                    .findFirst()
                    .orElseThrow(() -> new AuthenticationFailed("Did not find this user"))
                    .replaceFirst(login + "_", StringUtils.EMPTY);
        } catch (IOException e) {
            throw new FileApiError(e);
        }
    }
}
