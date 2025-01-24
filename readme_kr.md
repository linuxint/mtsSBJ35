## SpringBoot 3.4 + MyBatis 3 + Oracle/MariaDB 기반 웹 프로젝트 템플릿 ##
본 샘플은  SpringBoot 3.4 + MyBatis 3 + Oracle/MariaDB (Maven) 기반으로  제작한 웹 프로젝트 템플릿이다.

웹 개발시 많이 사용되는 다양한 기능들 미리 구현해 향후 소소한 제품으로 성장할 계획입니다.

좀더 자세한 설명은 [여기](http://forest71.tistory.com/78)에서 얻을 수 있다.
참조한 소스 기반으로 프레임워크가 변경되었습니다.
오라클버전은 DB 전환후 일부 기능의 오류가 있을수 있습니다.

블로그에 공유된 3개의 프로젝트를 1개로 합쳤습니다.
project9 - 3> 멀티게시판
pms9 - 2) 프로젝트관리
groupware9 - 1)전자결제

### 주요 구현 기능 ###
#### 1) 전자결제
- 일정: 월간일정
- 결제: 기안하기, 결재 받을 문서, 결재 할 문서
- 전자우편: 새메일, 받은 메일, 보낸 메일
- 게시판
#### 2) 프로젝트관리
- 과제 생성 및 개인별 할당
- 과제 상제 정보 보기(과제, 일정, 사용자 중심)
- 개인별 과제 진행 사항 입력
#### 3) 멀티 게시판, 사용자/그룹관리
- 멀티 게시판 (무한 댓글, 좋아요 등)
- 회원 기능: 모든 페이지는 회원만 접속 가능. 로그인/로그 아웃 기능 (로그 저장). 회원관리 등
- 보안 기능: 일반사용자(U)와 관리자(A)로 구분하여 일반 사용자는 관리자 페이지에 접근 불가.
- 사용자 선택: 부서, 사용자 선택 기능 (팝업)
- 날짜 선택 및 챠트 사용법 샘플
- 엑셀 다운로드(jXLS) 샘플
- 다국어 처리
- 디자인: 부트스트랩기반 반응형 웹 적용 (SB-Admin)
- 공통 에러 페이지 처리(404, 500)
- 로그 처리(logback, log4jdbc)

#### 소스제공사항
- CrossOrigin - DevkbilApplication.java, WebMvcConfig.java - remark해제
- Banner Custom - mts-banner.txt, application.properties, DevkbilApplication.java

### 주요 LIB  ###
- JQuery-2.2.3
- CKEditor 4.5.10
- SB-Admin 2, morris v0.5.0, DatePicker
- DynaTree 1.2.4
- jQuery EasyUI 1.4.3
- FullCalendar v5

### 개발 환경 ###
    Programming Language - Java 22
    IDE - intelliJ
    DB - Oracle/MariaDB
    Framework - MyBatis, SpringBoot 3.4
    Build Tool - Maven

### 설치 ###
- OracleDB에 데이터 베이스(mts)를 생성(user_database_oracle.sql) 하고 tables_oracle.sql, tableData_oracle.sql를 실행하여 테이블과 데이터를 생성한다.
- MariaDB에 데이터 베이스(mts)를 생성(user_database_myriadb.sql) 하고 tables_myriadb.sql, tableData_myriadb.sql를 실행하여 테이블과 데이터를 생성한다.
- applicationContext.xml에 적절한 접속 정보를 입력한다.
- 톰캣이나 Intellij/이클립스에서 mts를 실행
- http://localhost:9090/mts/ 로 접속
- ID/PW: admin/admin, user1/user1, user2/user2 ...
  Oracle PW소스는 변경되지 않아 PW는 아이디로 입력된다.

### colima ###
    0. colima 설치
      brew install colima, docker, qemu
    1. colima 서비스 실행
      colima start --memory 4 --arch x86_64
    2. colima 환경삭제
      colima delete
    3. colima 캐쉬삭제
      rm -r "$(brew --cache)/api"
    4. colima 프로세스 멈춤
      export HOMEBREW_NO_INSTALL_FROM_API=1
    5. 도커전환
      docker context ls
      docker context use colima
    6. colima 자동시작 설정
      echo "autoload -U compinit; compinit" >> ~/.zshrc

### oracle11g 환경설정 ###
    0. 도커 이미지 다운로드
      docker pull linuxint/oracle11g
    1. 도커 실행
      docker run --name oracle11g -d -p 1521:1521 linuxint/oracle11g

### elasticsearch 환경설정 ###
    0. 도커 이미지 다운로드
      docker pull elasticsearch:7.17.6
      docker pull elasticsearch:8.15.1
    1. 도커 실행
      docker run -p 9200:9200 -p 9300:9300 --name elasticsearch -e "discovery.type=single-node" -e "xpack.ml.enabled=false" linuxint/elasticsearch
      docker run -p 9200:9200 -p 9300:9300 --name es8 -e "discovery.type=single-node" -e "xpack.ml.enabled=false" elasticsearch:8.15.1
        > docker cp elasticsearch:/usr/share/elasticsearch/config/elasticsearch.yml ./
        > vi elasticsearch.yml  (xpack.security.enabled: false 편집)
        > docker cp elasticsearch.yml elasticsearch:/usr/share/elasticsearch/config/
    2. 사용자 비밀번호 설정
      ./docker exec -t elasticsearch /bin/bash
      ./bin/elasticsearch-setup-passwords interactive
           ~ password : manager

              Initiating the setup of passwords for reserved users elastic,apm_system,kibana,kibana_system,logstash_system,beats_system,remote_monitoring_user.
              You will be prompted to enter passwords as the process progresses.
              Please confirm that you would like to continue [y/N]y
              
              Enter password for [elastic]:
              Reenter password for [elastic]:
              Enter password for [apm_system]:
              Reenter password for [apm_system]:
              Enter password for [kibana_system]:
              Reenter password for [kibana_system]:
              Enter password for [logstash_system]:
              Reenter password for [logstash_system]:
              Enter password for [beats_system]:
              Reenter password for [beats_system]:
              Enter password for [remote_monitoring_user]:
              Reenter password for [remote_monitoring_user]:
              Changed password for user [apm_system]
              Changed password for user [kibana_system]
              Changed password for user [kibana]
              Changed password for user [logstash_system]
              Changed password for user [beats_system]
              Changed password for user [remote_monitoring_user]
              Changed password for user [elastic]

    3. docker exec -it elasticsearch /bin/bash
    4. Elasticsearch에서 기본적으로 제공하는 형태소 분석기 nori를 설치
       ./elasticsearch/bin/elasticsearch-plugin install analysis-nori
    5. 사전복사
      ./elasticsearch/stopwords.txt, synonym.txt, userdict.txt -> elasticsearch/config 
      docker cp stopwords.txt elasticsearch:/usr/share/elasticsearch/config/
      docker cp synonyms.txt elasticsearch:/usr/share/elasticsearch/config/
      docker cp userdict.txt elasticsearch:/usr/share/elasticsearch/config/
    6. index생성
      curl -XPUT http://elastic:manager@localhost:9200/mts -d @index_board.json -H "Content-Type: application/json"
    7. imagemagick , terssaract , terssaract-lang , exiftool , ffmpeg , leptonica install
      ./brew install imagemagick
      ./brew install tesseract
      ./brew install terssaract-lang
      ./brew install exiftool
      ./brew install ffmpeg
      ./brew install leptonica
    8. mac호환성추가
      config/elasticsearch.yml 에서 아래 설정을 추가해준다.
      xpack.ml.enabled: false

### james 환경설정 ###
    1. docker pull apache/james:demo-latest
    2. docker run -p "465:465" -p "993:993" --name james apache/james:demo-latest
      IMAP port : 993
      SMTP port : 465
      user : user01@james.local
      passwd : 1234

### redis 환경설정 ###
    1. docker pull redis
    2. docker run --name redis -d -p 6379:6379  redis

### 네트워크 환경설정 ###
    0. 네티워크 생성
      > docker network create somenetwork
    1. 네트워크 정보 확인
      > docker network inspect somenetwork
    2. 컨테이너에 네트워크 연결1
      > docker network connect somenetwork elasticsearch
    3. 컨테이너에 네트워크 연결2
      > docker network connect somenetwork oracle11g
    4. 네트워크 정보 확인
      > docker network inspect somenetwork
    5. 네트워크 연결 확인
      > docker exec oracle11g ping elasticsearch
      PING elasticsearch (172.19.0.2) 56(84) bytes of data.
      64 bytes from elasticsearch.somenetwork (172.19.0.2): icmp_seq=1 ttl=64 time=0.091 ms
      64 bytes from elasticsearch.somenetwork (172.19.0.2): icmp_seq=2 ttl=64 time=0.076 ms
      64 bytes from elasticsearch.somenetwork (172.19.0.2): icmp_seq=6 ttl=64 time=0.048 ms

### spring-format ###
  https://github.com/spring-io/spring-javaformat

### License ###
MIT
