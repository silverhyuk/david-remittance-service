package com.wirebarley.remittance.infrastructure.common.aop;

import com.wirebarley.remittance.common.aop.DistributedLock;
import com.wirebarley.remittance.infrastructure.common.lock.LockAcquisitionException;
import com.wirebarley.remittance.infrastructure.common.lock.RedissonLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.StringJoiner;

/**
 * 분산락 AOP 구현
 * DistributedLock 어노테이션이 붙은 메서드에 분산락 적용
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) // 트랜잭션보다 먼저 실행되도록 순서 지정
public class DistributedLockAspect {
    private final RedissonLockService lockService;
    private final ExpressionParser parser = new SpelExpressionParser();
    
    /**
     * DistributedLock 어노테이션이 붙은 메서드 실행 시 분산락 적용
     */
    @Around("@annotation(com.wirebarley.remittance.common.aop.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        
        String lockKey = generateLockKey(distributedLock, joinPoint, method);
        log.debug("분산락 시도: {}, 메서드: {}", lockKey, method.getName());
        
        RLock lock = null;
        try {
            lock = lockService.tryLock(
                    lockKey, 
                    distributedLock.waitTime(), 
                    distributedLock.leaseTime(), 
                    distributedLock.timeUnit()
            );
            
            log.debug("메서드 실행: {}, 락 키: {}", method.getName(), lockKey);
            return joinPoint.proceed();
        } catch (LockAcquisitionException e) {
            log.error("분산락 획득 실패: {}, 메서드: {}", lockKey, method.getName(), e);
            throw e;
        } catch (Throwable e) {
            log.error("메서드 실행 중 예외 발생: {}, 메서드: {}", lockKey, method.getName(), e);
            throw e;
        } finally {
            if (lock != null) {
                try {
                    lockService.unlock(lock, lockKey);
                    log.debug("분산락 해제: {}, 메서드: {}", lockKey, method.getName());
                } catch (Exception e) {
                    log.error("분산락 해제 중 예외 발생: {}, 메서드: {}", lockKey, method.getName(), e);
                }
            }
        }
    }
    
    /**
     * 락 키 생성
     */
    private String generateLockKey(DistributedLock distributedLock, ProceedingJoinPoint joinPoint, Method method) {
        String key = distributedLock.key();
        if (key.isEmpty()) {
            key = method.getName();
        }
        
        // 파라미터 이름으로 동적 키 생성
        String[] paramNames = distributedLock.paramNames();
        if (paramNames.length > 0) {
            StringJoiner joiner = new StringJoiner(":");
            joiner.add(key);
            
            String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            Object[] args = joinPoint.getArgs();
            StandardEvaluationContext context = new StandardEvaluationContext();
            
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
            
            for (String paramName : paramNames) {
                try {
                    String value = parser.parseExpression("#" + paramName).getValue(context, String.class);
                    joiner.add(value != null ? value : "null");
                } catch (Exception e) {
                    log.warn("락 키 생성 중 파라미터 값 추출 실패: {}", paramName, e);
                    joiner.add("unknown");
                }
            }
            
            return joiner.toString();
        }
        
        return key;
    }
} 