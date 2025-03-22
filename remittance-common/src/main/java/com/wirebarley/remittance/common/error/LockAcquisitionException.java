package com.wirebarley.remittance.common.error;

/**
 * 분산 락 획득 실패 예외
 */
public class LockAcquisitionException extends BusinessException {
    
    public LockAcquisitionException() {
        super(ErrorCode.LOCK_ACQUISITION_FAILURE);
    }
    
    public LockAcquisitionException(String message) {
        super(ErrorCode.LOCK_ACQUISITION_FAILURE, message);
    }
    
    public LockAcquisitionException(String message, Throwable cause) {
        super(ErrorCode.LOCK_ACQUISITION_FAILURE, message, cause);
    }
} 