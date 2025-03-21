# 송금 서비스 프로젝트

## 프로젝트 개요
이 프로젝트는 계좌 등록/조회, 입금, 출금 및 이체 기능을 제공하는 송금 서비스 API를 구현한 것입니다. 헥사고날 아키텍처, 멀티모듈, CQRS 패턴을 적용하여 개발되었으며, MariaDB를 데이터베이스로 사용합니다.

## 기술 스택
- Java 17
- Spring Boot
- Spring Data JPA
- MariaDB
- Docker & Docker Compose
- JUnit 5

## 아키텍처
이 프로젝트는 헥사고날 아키텍처를 기반으로 설계되었으며, 다음과 같은 모듈로 구성되어 있습니다:

1. **remittance-domain**: 핵심 도메인 모델 및 비즈니스 로직
2. **remittance-application**: 애플리케이션 서비스 및 CQRS 구현
3. **remittance-infrastructure**: 데이터베이스 및 외부 시스템 연동
4. **remittance-api**: 외부 인터페이스 (REST API 컨트롤러)
5. **remittance-common**: 공통 유틸리티 및 상수

### 헥사고날 아키텍처
헥사고날 아키텍처는 도메인 중심 설계를 강조하며, 비즈니스 로직을 외부 의존성으로부터 분리합니다. 이 프로젝트에서는 다음과 같은 구성요소로 구현되었습니다:

- **도메인 모델**: Account, Transaction 등의 핵심 엔티티
- **포트**: 도메인과 외부 세계 간의 인터페이스 (AccountRepository, TransactionRepository)
- **어댑터**: 포트 구현체 (AccountRepositoryAdapter, TransactionRepositoryAdapter)

### CQRS 패턴
CQRS(Command Query Responsibility Segregation) 패턴은 명령(Command)과 조회(Query)를 분리하는 아키텍처 패턴입니다. 이 프로젝트에서는 다음과 같이 구현되었습니다:

- **Command 모델**: CreateAccountCommand, DepositCommand, WithdrawCommand, TransferCommand
- **Query 모델**: AccountDto, TransactionDto
- **Command 서비스**: 계좌 생성, 입금, 출금, 이체 등의 상태 변경 작업
- **Query 서비스**: 계좌 조회, 트랜잭션 조회 등의 읽기 작업

## API 엔드포인트

### 계좌 API
- `POST /api/accountReads`: 계좌 등록
- `GET /api/accountReads/{accountId}`: 계좌 조회
- `GET /api/accountReads/number/{accountNumber}`: 계좌번호로 계좌 조회
- `GET /api/accountReads`: 모든 계좌 조회
- `POST /api/accountReads/{accountId}/deposit`: 입금
- `POST /api/accountReads/{accountId}/withdraw`: 출금

### 트랜잭션 API
- `POST /api/transactionReads/transfer/{sourceAccountId}`: 이체
- `GET /api/transactionReads/{transactionId}`: 트랜잭션 조회
- `GET /api/transactionReads/source-accountRead/{accountId}`: 출금 계좌 기준 트랜잭션 조회
- `GET /api/transactionReads/target-accountRead/{accountId}`: 입금 계좌 기준 트랜잭션 조회

## 데이터베이스 스키마

### 계좌 테이블 (accountRead)
- id: UUID (PK)
- account_number: VARCHAR(20) (UNIQUE)
- account_name: VARCHAR(100)
- balance: DECIMAL(19,4)
- status: VARCHAR(20)
- created_at: TIMESTAMP
- updated_at: TIMESTAMP

### 트랜잭션 테이블 (transactionRead)
- id: UUID (PK)
- source_account_id: UUID (FK)
- target_account_id: UUID (FK)
- amount: DECIMAL(19,4)
- type: VARCHAR(20)
- status: VARCHAR(20)
- description: VARCHAR(255)
- created_at: TIMESTAMP
- updated_at: TIMESTAMP

## 테스트
이 프로젝트는 다음과 같은 테스트를 포함하고 있습니다:

- **단위 테스트**: 도메인 모델, 애플리케이션 서비스, API 컨트롤러에 대한 테스트
- **통합 테스트**: 계좌 생성 및 조회, 입금, 이체 등의 핵심 기능에 대한 통합 테스트

## 실행 방법

### 요구사항
- Docker 및 Docker Compose
- Java 17 이상

### 빌드 및 실행
1. 프로젝트 클론:
   ```
   git clone <repository-url>
   cd remittance-service
   ```

2. 배포 스크립트 실행:
   ```
   ./deploy.sh
   ```

3. 서비스 접속:
   - API 서버: http://localhost:8080
   - API 문서: http://localhost:8080/swagger-ui.html

### 수동 실행
1. 프로젝트 빌드:
   ```
   ./gradlew clean build
   ```

2. Docker Compose 실행:
   ```
   docker-compose up -d
   ```

## 프로젝트 구조
```
remittance-service/
├── remittance-api/
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/wirebarley/remittance/api/
│       │   │       ├── accountRead/
│       │   │       │   ├── controller/
│       │   │       │   └── dto/
│       │   │       └── transactionRead/
│       │   │           ├── controller/
│       │   │           └── dto/
│       │   └── resources/
│       └── test/
├── remittance-application/
│   └── src/
│       ├── main/
│       │   └── java/
│       │       └── com/wirebarley/remittance/application/
│       │           ├── accountRead/
│       │           │   ├── command/
│       │           │   ├── query/
│       │           │   └── service/
│       │           └── transactionRead/
│       │               ├── command/
│       │               ├── query/
│       │               └── service/
│       └── test/
├── remittance-domain/
│   └── src/
│       ├── main/
│       │   └── java/
│       │       └── com/wirebarley/remittance/domain/
│       │           ├── accountRead/
│       │           │   └── port/
│       │           └── transactionRead/
│       │               └── port/
│       └── test/
├── remittance-infrastructure/
│   └── src/
│       ├── main/
│       │   └── java/
│       │       └── com/wirebarley/remittance/infrastructure/
│       │           ├── accountRead/
│       │           │   ├── adapter/
│       │           │   ├── entity/
│       │           │   └── repository/
│       │           └── transactionRead/
│       │               ├── adapter/
│       │               ├── entity/
│       │               └── repository/
│       └── test/
├── remittance-common/
│   └── src/
│       └── main/
│           └── java/
│               └── com/wirebarley/remittance/common/
├── build.gradle
├── settings.gradle
├── Dockerfile
├── docker-compose.yml
├── deploy.sh
└── init-scripts/
    └── 01-init-schema.sql
```
