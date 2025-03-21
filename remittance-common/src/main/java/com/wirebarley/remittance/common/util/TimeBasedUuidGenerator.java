package com.wirebarley.remittance.common.util;


import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

import java.util.UUID;

public final class TimeBasedUuidGenerator {
    private static final TimeBasedGenerator TIME_BASED_GENERATOR = Generators.timeBasedGenerator();

    private TimeBasedUuidGenerator() {
        // 유틸리티 클래스 인스턴스화 방지
    }

    /**
     * 시간 기반 UUID(버전 1) 생성
     * @return 시간 기반 UUID
     */
    public static synchronized UUID generate() {
        return TIME_BASED_GENERATOR.generate();
    }

    /**
     * 보안 난수 기반 UUID(버전 4) 생성
     * @return 난수 기반 UUID
     */
    public static UUID generateRandom() {
        return Generators.randomBasedGenerator().generate();
    }
}