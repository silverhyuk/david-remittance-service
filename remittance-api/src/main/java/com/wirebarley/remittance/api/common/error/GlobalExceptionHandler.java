package com.wirebarley.remittance.api.common.error;

import com.wirebarley.remittance.common.error.BusinessException;
import com.wirebarley.remittance.common.error.ErrorCode;
import com.wirebarley.remittance.common.error.ErrorResponse;
import com.wirebarley.remittance.infrastructure.common.lock.LockAcquisitionException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;

/**
 * 전역 예외 핸들러
 * 모든 컨트롤러에서 발생한 예외를 처리합니다.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("비즈니스 예외 발생: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode, e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }
    
    /**
     * 유효성 검사 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("유효성 검사 실패: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult(), request.getRequestURI());
        return new ResponseEntity<>(response, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }
    
    /**
     * 바인딩 예외 처리
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest request) {
        log.error("바인딩 예외 발생: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult(), request.getRequestURI());
        return new ResponseEntity<>(response, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }
    
    /**
     * 지원하지 않는 HTTP 메서드 요청 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("지원하지 않는 HTTP 메서드: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED, request.getRequestURI());
        return new ResponseEntity<>(response, ErrorCode.METHOD_NOT_ALLOWED.getStatus());
    }
    
    /**
     * 메서드 인자 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("메서드 인자 타입 불일치: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE, request.getRequestURI());
        return new ResponseEntity<>(response, ErrorCode.INVALID_TYPE_VALUE.getStatus());
    }
    
    /**
     * 접근 거부 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.error("접근 거부: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.HANDLE_ACCESS_DENIED, request.getRequestURI());
        return new ResponseEntity<>(response, ErrorCode.HANDLE_ACCESS_DENIED.getStatus());
    }
    
    /**
     * 분산 락 획득 실패 예외 처리
     */
    @ExceptionHandler(LockAcquisitionException.class)
    protected ResponseEntity<ErrorResponse> handleLockAcquisitionException(LockAcquisitionException e, HttpServletRequest request) {
        log.error("분산 락 획득 실패: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.LOCK_ACQUISITION_FAILURE, request.getRequestURI());
        return new ResponseEntity<>(response, ErrorCode.LOCK_ACQUISITION_FAILURE.getStatus());
    }
    
    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("예외 발생: ", e);  // 스택 트레이스 포함하여 로깅
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 