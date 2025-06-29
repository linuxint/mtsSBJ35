#!/bin/bash

echo "Docker 서비스 상태를 확인합니다..."

# Docker 데몬 상태 확인
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 데몬이 실행 중이지 않습니다."
    exit 1
fi
echo "✅ Docker 데몬이 실행 중입니다."

# Docker Compose 확인
if ! docker-compose version > /dev/null 2>&1; then
    echo "❌ Docker Compose가 설치되어 있지 않습니다."
    exit 1
fi
echo "✅ Docker Compose가 설치되어 있습니다."

# 필요한 이미지 확인
echo "필요한 Docker 이미지를 확인합니다..."
required_images=(
    "container-registry.oracle.com/database/enterprise:latest"
    "docker.elastic.co/elasticsearch/elasticsearch:8.12.2"
    "prom/prometheus:latest"
    "grafana/grafana:latest"
)

for image in "${required_images[@]}"; do
    if docker image inspect "$image" > /dev/null 2>&1; then
        echo "✅ $image 이미지가 존재합니다."
    else
        echo "❌ $image 이미지가 없습니다. 이미지를 가져옵니다..."
        docker pull "$image"
        if [ $? -eq 0 ]; then
            echo "✅ $image 이미지를 성공적으로 가져왔습니다."
        else
            echo "❌ $image 이미지를 가져오는데 실패했습니다."
            exit 1
        fi
    fi
done

# Docker 서비스 상태 확인
echo "Docker 서비스 상태를 확인합니다..."
services=("oracle" "elasticsearch" "prometheus" "grafana")
for service in "${services[@]}"; do
    if docker ps | grep -q "$service"; then
        echo "✅ $service 서비스가 실행 중입니다."
    else
        echo "❌ $service 서비스가 실행 중이지 않습니다."
    fi
done

echo "확인이 완료되었습니다." 