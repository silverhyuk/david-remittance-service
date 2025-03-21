#!/bin/bash

# 송금 서비스 배포 스크립트

echo "===== 송금 서비스 배포 시작 ====="

# 1. 프로젝트 빌드
echo "프로젝트 빌드 중..."
./gradlew clean build -x test

# 빌드 실패 시 종료
if [ $? -ne 0 ]; then
  echo "빌드 실패! 배포를 중단합니다."
  exit 1
fi
echo "빌드 완료!"

# 2. Docker 이미지 빌드 및 컨테이너 실행
echo "Docker 컨테이너 실행 중..."
docker-compose down
docker-compose build --no-cache
docker-compose up -d

# Docker 실행 실패 시 종료
if [ $? -ne 0 ]; then
  echo "Docker 컨테이너 실행 실패! 배포를 중단합니다."
  exit 1
fi

# 3. 서비스 상태 확인
echo "서비스 상태 확인 중..."
sleep 10  # 서비스가 완전히 시작될 때까지 대기

# 애플리케이션 상태 확인
APP_STATUS=$(docker-compose ps | grep app | grep Up)
if [ -z "$APP_STATUS" ]; then
  echo "애플리케이션 컨테이너가 실행 중이 아닙니다! 배포를 중단합니다."
  docker-compose logs app
  exit 1
fi

# 데이터베이스 상태 확인
DB_STATUS=$(docker-compose ps | grep db | grep Up)
if [ -z "$DB_STATUS" ]; then
  echo "데이터베이스 컨테이너가 실행 중이 아닙니다! 배포를 중단합니다."
  docker-compose logs db
  exit 1
fi

echo "===== 송금 서비스 배포 완료 ====="
echo "서비스 접속 URL: http://localhost:8080"
echo "API 문서 URL: http://localhost:8080/swagger-ui.html"
