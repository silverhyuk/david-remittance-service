package com.wirebarley.remittance.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES256 암호화/복호화 유틸리티
 */
@Slf4j
@Component
public class AES256Util {

    private final SecretKey key;
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16 * 8; // 128 bits

    public AES256Util(@Value("${security.encryption.key}") String configKey) {
        try {
            // 키가 제공되지 않은 경우 예외 발생 (기본값 사용 방지)
            if (configKey.isEmpty()) {
                throw new IllegalArgumentException("암호화 키가 설정되지 않았습니다. security.encryption.key 속성을 설정하세요.");
            }

            byte[] keyBytes = new byte[32]; // 256 bits
            byte[] b = configKey.getBytes(StandardCharsets.UTF_8);
            int len = Math.min(b.length, keyBytes.length);
            System.arraycopy(b, 0, keyBytes, 0, len);
            this.key = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            log.error("AES256Util 초기화 실패", e);
            throw new RuntimeException("암호화 유틸리티 초기화에 실패했습니다.", e);
        }
    }

    /**
     * AES-GCM 암호화
     * @param text 암호화할 문자열
     * @return Base64로 인코딩된 IV + 암호문 + 인증 태그
     */
    public String encrypt(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

            byte[] cipherText = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));

            // IV와 암호문 결합
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            byte[] encrypted = byteBuffer.array();

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("암호화 실패", e);
            throw new RuntimeException("암호화에 실패했습니다.", e);
        }
    }

    /**
     * AES-GCM 복호화
     * @param encryptedText Base64로 인코딩된 IV + 암호문 + 인증 태그
     * @return 복호화된 원본 문자열
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);

            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("복호화 실패", e);
            throw new RuntimeException("복호화에 실패했습니다.", e);
        }
    }
}