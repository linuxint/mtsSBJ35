#!/bin/bash

# Docker가 설치되어 있는지 확인
if ! command -v docker &> /dev/null; then
    echo "Docker가 설치되어 있지 않습니다. Docker를 설치합니다..."
    
    # macOS인 경우 Homebrew를 통해 Docker 설치
    if [[ "$OSTYPE" == "darwin"* ]]; then
        if ! command -v brew &> /dev/null; then
            echo "Homebrew가 설치되어 있지 않습니다. Homebrew를 먼저 설치합니다..."
            /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
        fi
        brew install --cask docker
    else
        echo "지원하지 않는 운영체제입니다."
        exit 1
    fi
fi

# Docker Compose가 설치되어 있는지 확인
if ! command -v docker-compose &> /dev/null; then
    echo "Docker Compose가 설치되어 있지 않습니다. Docker Compose를 설치합니다..."
    
    # macOS인 경우 Homebrew를 통해 Docker Compose 설치
    if [[ "$OSTYPE" == "darwin"* ]]; then
        brew install docker-compose
    else
        echo "지원하지 않는 운영체제입니다."
        exit 1
    fi
fi

# Docker 서비스가 실행 중인지 확인
if ! docker info &> /dev/null; then
    echo "Docker 서비스가 실행되어 있지 않습니다. Docker Desktop을 실행해주세요."
    exit 1
fi

# 프로젝트 빌드
echo "프로젝트를 빌드합니다..."
./gradlew clean build -x test

# Docker 서비스 시작
echo "Docker 서비스를 시작합니다..."
docker-compose up -d

# 서비스 상태 확인
echo "서비스 상태를 확인합니다..."
docker-compose ps

echo "설치가 완료되었습니다."
echo "다음 URL에서 서비스에 접근할 수 있습니다:"
echo "Spring Boot 애플리케이션: http://localhost:9090"
echo "Oracle Database: localhost:1521"
echo "Elasticsearch: http://localhost:9200"
echo "Prometheus: http://localhost:9091"
echo "Grafana: http://localhost:3001"
echo "Grafana 기본 계정: admin / admin" 