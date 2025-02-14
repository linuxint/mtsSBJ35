# Spring Application Lifecycle Events

Spring Boot에서 애플리케이션 생명주기 이벤트와 이를 처리하는 각 이벤트 리스너를 설명합니다.  
아래의 기능명을 클릭하면 관련 소스 파일을 열 수 있습니다.

## Event Listeners and Roles
1. **[ApplicationStartingEventListener](./ApplicationStartingEventListener.java)**
    - 애플리케이션 부팅이 시작될 때 실행되며 초기 상태 정보를 로깅합니다.

2. **[ApplicationEnvironmentPreparedEventListener](./ApplicationEnvironmentPreparedEventListener.java)**
    - 애플리케이션 환경(Environment) 초기화 완료 후, 하지만 ApplicationContext가 생성되기 전 실행됩니다.

3. **[ApplicationContextInitializedEventListener](./ApplicationContextInitializedEventListener.java)**
    - 애플리케이션 컨텍스트 초기화 단계에 도달했을 때 실행되며 컨텍스트 관련 초기 작업을 처리합니다.

4. **[ApplicationPreparedEventListener](./ApplicationPreparedEventListener.java)**
    - 애플리케이션 컨텍스트가 초기화 완료된 상태에서, 활성화되기 전 단계에서 실행됩니다.

5. **[ApplicationContextRefreshedEventListener](./ApplicationContextRefreshedEventListener.java)**
    - 애플리케이션 컨텍스트가 새로 고침되었을 때 실행되며 컨텍스트 준비 상태를 확인합니다.

6. **[ApplicationReadyEventListener](./ApplicationReadyEventListener.java)**
    - 애플리케이션이 완전히 시작되고 요청을 처리할 준비가 되었을 때 실행되며 마지막 단계로 실행됩니다.

7. **[ApplicationFailedEventListener](./ApplicationFailedEventListener.java)**
    - 애플리케이션 초기화 도중 에러가 발생했을 때 실행되며 예외 정보를 기록합니다.

8. **[ApplicationContextClosedEventListener](./ApplicationContextClosedEventListener.java)**
    - 애플리케이션이 종료될 때 실행되며 컨텍스트 종료 및 클린업 작업을 처리합니다.

---

## Events Execution Order
1. **ApplicationStartingEvent**
2. **ApplicationEnvironmentPreparedEvent**
3. **ApplicationContextInitializedEvent**
4. **ApplicationPreparedEvent**
5. **ContextRefreshedEvent**
6. **ApplicationReadyEvent**
7. *(에러 발생 시)* **ApplicationFailedEvent**
8. *(애플리케이션 종료 시)* **ContextClosedEvent**