package com.wirebarley.remittance.infrastructure.converter;

import com.wirebarley.remittance.common.util.AES256Util;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JPA 엔티티 필드 AES256 암호화/복호화 컨버터
 */
@Converter
@Component
@RequiredArgsConstructor
@Slf4j
public class AES256Converter implements AttributeConverter<String, String> {
    
    private final AES256Util aes256Util;
    
    /**
     * 엔티티 필드 값을 데이터베이스 컬럼 값으로 변환 (암호화)
     * @param attribute 엔티티 필드 값
     * @return 암호화된 데이터베이스 컬럼 값
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        
        try {
            return aes256Util.encrypt(attribute);
        } catch (Exception e) {
            log.error("암호화 실패", e);
            throw new RuntimeException("필드 암호화 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 데이터베이스 컬럼 값을 엔티티 필드 값으로 변환 (복호화)
     * @param dbData 데이터베이스 컬럼 값
     * @return 복호화된 엔티티 필드 값
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        try {
            return aes256Util.decrypt(dbData);
        } catch (Exception e) {
            log.error("복호화 실패", e);
            throw new RuntimeException("필드 복호화 중 오류가 발생했습니다.", e);
        }
    }
} 