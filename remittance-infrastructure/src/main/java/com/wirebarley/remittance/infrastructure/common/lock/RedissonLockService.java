package com.wirebarley.remittance.infrastructure.common.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redisson을 이용한 분산락 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonLockService {
    private final RedissonClient redissonClient;
    
    /**
     * 락 획득 시도
     * @param key 락 키
     * @param waitTime 락 획득 대기 시간
     * @param leaseTime 락 유지 시간
     * @param timeUnit 시간 단위
     * @return 락 객체
     * @throws LockAcquisitionException 락 획득 실패 시
     */
    public RLock tryLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(key);
        
        try {
            boolean isLocked = lock.tryLock(waitTime, leaseTime, timeUnit);
            
            if (!isLocked) {
                log.error("락 획득에 실패했습니다: {}", key);
                throw new LockAcquisitionException("락 획득에 실패했습니다: " + key);
            }
            
            log.debug("락 획득 성공: {}", key);
            return lock;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("락 획득 중 인터럽트 발생: {}", key, e);
            throw new LockAcquisitionException("락 획득 중 인터럽트 발생: " + key, e);
        } catch (Exception e) {
            log.error("락 획득 중 예외 발생: {}", key, e);
            throw new LockAcquisitionException("락 획득 중 예외 발생: " + key, e);
        }
    }
    
    /**
     * 락 해제
     * @param lock 락 객체
     * @param key 락 키
     */
    public void unlock(RLock lock, String key) {
        try {
            if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.debug("락 해제 성공: {}", key);
            }
        } catch (Exception e) {
            log.error("락 해제 중 예외 발생: {}", key, e);
        }
    }
} 