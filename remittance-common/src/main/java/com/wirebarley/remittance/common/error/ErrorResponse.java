package com.wirebarley.remittance.common.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * API 에러 응답 모델
 * API 호출 시 발생하는 에러에 대한 상세 정보를 포함합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private String code;
    private String status;
    private String message;
    private List<FieldError> errors;
    private String path;
    
    @Builder
    public ErrorResponse(String code, String status, String message, List<FieldError> errors, String path) {
        this.timestamp = LocalDateTime.now();
        this.code = code;
        this.status = status;
        this.message = message;
        this.errors = errors != null ? errors : new ArrayList<>();
        this.path = path;
    }
    
    /**
     * 에러 코드로부터 에러 응답 생성
     * @param errorCode 에러 코드
     * @param path 요청 경로
     * @return 에러 응답
     */
    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus().toString())
                .message(errorCode.getMessage())
                .path(path)
                .build();
    }
    
    /**
     * 에러 코드와 상세 메시지로부터 에러 응답 생성
     * @param errorCode 에러 코드
     * @param message 상세 메시지
     * @param path 요청 경로
     * @return 에러 응답
     */
    public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus().toString())
                .message(message)
                .path(path)
                .build();
    }
    
    /**
     * 유효성 검사 실패 시 에러 응답 생성
     * @param errorCode 에러 코드
     * @param bindingResult 유효성 검사 결과
     * @param path 요청 경로
     * @return 에러 응답
     */
    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult, String path) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .status(errorCode.getStatus().toString())
                .message(errorCode.getMessage())
                .errors(FieldError.ofBindingResult(bindingResult))
                .path(path)
                .build();
    }
    
    /**
     * 필드 에러
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;
        
        @Builder
        public FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }
        
        /**
         * BindingResult로부터 필드 에러 목록 생성
         * @param bindingResult 유효성 검사 결과
         * @return 필드 에러 목록
         */
        public static List<FieldError> ofBindingResult(BindingResult bindingResult) {
            List<FieldError> errors = new ArrayList<>();
            
            bindingResult.getFieldErrors().forEach(error -> {
                errors.add(FieldError.builder()
                        .field(error.getField())
                        .value(error.getRejectedValue() != null ? error.getRejectedValue().toString() : "")
                        .reason(error.getDefaultMessage())
                        .build());
            });
            
            return errors;
        }
    }
} 