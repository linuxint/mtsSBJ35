# SBOM 자동 생성 가이드

## 소개
이 프로젝트는 이미 CycloneDX SBOM(Software Bill of Materials) 자동 생성 기능이 설정되어 있습니다. 
build.gradle 파일에 'org.cyclonedx.bom' 플러그인이 설정되어 있어 간단한 명령어로 SBOM을 생성할 수 있습니다.

## SBOM 생성 방법
1. 터미널에서 프로젝트 루트 디렉토리로 이동합니다.
2. 다음 명령어를 실행합니다:
   ```bash
   ./gradlew cyclonedxBom
   ```
3. 생성된 SBOM 파일은 기본적으로 `build/reports/bom.json` 경로에 저장됩니다.

## 주의사항
- SBOM 생성은 프로젝트의 모든 의존성을 분석하므로 처음 실행 시 시간이 걸릴 수 있습니다.
- 생성된 SBOM 파일은 JSON 형식으로 제공되며, 프로젝트의 모든 의존성 정보를 포함합니다.
- 프로젝트의 의존성이 변경될 때마다 SBOM을 새로 생성하는 것이 좋습니다.

## 자동화 방법
빌드 프로세스의 일부로 SBOM 생성을 자동화하려면 build.gradle에 다음과 같은 설정을 추가할 수 있습니다:
```groovy
build.dependsOn cyclonedxBom
```
이렇게 하면 `./gradlew build` 명령 실행 시 자동으로 SBOM이 생성됩니다.