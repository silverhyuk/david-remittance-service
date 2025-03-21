package com.wirebarley.remittance.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 분산락을 적용하기 위한 어노테이션
 * 메서드에 이 어노테이션을 붙이면 해당 메서드 실행 시 분산락이 적용됩니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /**
     * 락의 이름
     * 메서드 이름이 기본값으로 사용됩니다.
     */
    String key() default "";
    
    /**
     * 락을 획득하기 위해 대기할 최대 시간
     */
    long waitTime() default 5L;
    
    /**
     * 락 유지 시간
     */
    long leaseTime() default 10L;
    
    /**
     * 시간 단위
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    
    /**
     * 락 키를 생성할 때 사용할 파라미터 이름들
     */
    String[] paramNames() default {};
} 