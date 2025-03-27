#!/bin/bash

echo "모든 서비스를 중지하고 제거합니다..."

# Docker Compose로 실행 중인 서비스 중지 및 제거
docker-compose down

# 데이터 볼륨 삭제
docker volume rm mtsSBJ35_oracle_data
docker volume rm mtsSBJ35_elasticsearch_data
docker volume rm mtsSBJ35_prometheus_data
docker volume rm mtsSBJ35_grafana_data

echo "모든 서비스가 제거되었습니다." 