package com.wirebarley.remittance.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 * 각 에러 코드는 HTTP 상태 코드, 에러 코드, 메시지를 포함합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C004", "유효하지 않은 타입의 값입니다."),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C005", "접근이 거부되었습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C006", "요청한 리소스를 찾을 수 없습니다."),
    
    // 계좌 관련 에러
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "A001", "계좌를 찾을 수 없습니다."),
    DUPLICATE_ACCOUNT_NUMBER(HttpStatus.CONFLICT, "A002", "이미 존재하는 계좌번호입니다."),
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "A003", "잔액이 부족합니다."),
    INACTIVE_ACCOUNT(HttpStatus.BAD_REQUEST, "A004", "비활성화된 계좌입니다."),
    
    // 트랜잭션 관련 에러
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "트랜잭션을 찾을 수 없습니다."),
    INVALID_TRANSACTION_STATUS(HttpStatus.BAD_REQUEST, "T002", "유효하지 않은 트랜잭션 상태입니다."),
    TRANSFER_FAILED(HttpStatus.BAD_REQUEST, "T003", "이체에 실패했습니다."),
    SAME_ACCOUNT_TRANSFER(HttpStatus.BAD_REQUEST, "T004", "동일한 계좌 간 이체는 불가능합니다."),
    
    // 분산 락 관련 에러
    LOCK_ACQUISITION_FAILURE(HttpStatus.SERVICE_UNAVAILABLE, "L001", "락 획득에 실패했습니다. 잠시 후 다시 시도해주세요.");
    
    private final HttpStatus status;
    private final String code;
    private final String message;
} 