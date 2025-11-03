package com.devkbil.mtssbj;

import com.devkbil.mtssbj.common.events.CustomApplicationEvent;
import com.devkbil.mtssbj.common.listener.ApplicationContextClosedEventListener;
import com.devkbil.mtssbj.common.listener.ApplicationContextInitializedEventListener;
import com.devkbil.mtssbj.common.listener.ApplicationContextRefreshedEventListener;
import com.devkbil.mtssbj.common.listener.ApplicationEnvironmentPreparedEventListener;
import com.devkbil.mtssbj.common.listener.ApplicationFailedEventListener;
import com.devkbil.mtssbj.common.listener.ApplicationPreparedEventListener;
import com.devkbil.mtssbj.common.listener.ApplicationReadyEventListener;
import com.devkbil.mtssbj.common.listener.ApplicationStartingEventListener;

import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.data.jdbc.autoconfigure.DataJdbcRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@CrossOrigin(origins = "${app.cors.allowed-origins}") // 'Access-Control-Allow-Origin' header 추가
@Slf4j
@EnableCaching
@EnableAutoConfiguration(exclude = {
        DataJdbcRepositoriesAutoConfiguration.class
})
public class MtssbjApplication implements CommandLineRunner {

    private final ApplicationEventPublisher applicationEventPublisher;

    // ApplicationEventPublisher 주입받기
    public MtssbjApplication(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public static void main(String[] args) {
        // Gradle 관련 파라미터 필터링
        String[] filteredArgs = filterGradleParameters(args);
        
        // SpringApplication 인스턴스 생성 및 설정
        SpringApplication application = new SpringApplication(MtssbjApplication.class);

        // BufferingApplicationStartup 설정 (2048 이벤트를 버퍼링)
        application.setApplicationStartup(new BufferingApplicationStartup(2048));

        // 애플리케이션 배너 설정
        application.setBannerMode(Banner.Mode.CONSOLE);
        // 로깅 이전에 설정을 분명히 함
        application.setLogStartupInfo(true); // 기본 동작 허용

        // WebApplicationType 설정 (Servlet 모드)
        application.setWebApplicationType(WebApplicationType.SERVLET);

        // 필수 ApplicationListeners만 추가
        application.addListeners(
            new ApplicationStartingEventListener(),
            new ApplicationReadyEventListener(),
            new ApplicationPreparedEventListener(),
            new ApplicationFailedEventListener(),
            new ApplicationEnvironmentPreparedEventListener(),
            new ApplicationContextInitializedEventListener(),
            new ApplicationContextClosedEventListener(),
            new ApplicationContextRefreshedEventListener());

        // 애플리케이션 실행 (필터링된 인자 사용)
        application.run(filteredArgs);

    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Application started with CommandLineRunner");

        // 사용자 정의 이벤트를 발행
        CustomApplicationEvent customEvent = new CustomApplicationEvent(this, "Hello from CustomApplicationEvent");
        applicationEventPublisher.publishEvent(customEvent);
        log.info("Custom application event published");

        // 명령행 인수 로깅 (필요한 경우)
        if (args.length > 0) {
            log.info("Command line arguments: {}", (Object) args);
        }
    }

    @Bean
    public CommandLineRunner additionalCommandLineRunner() {
        return args -> {
            log.info(
                "CommandLineRunner executed as a bean definition  with " + args.length + "arguments");
            for (int nLoop = 0; nLoop < args.length; nLoop++) {
                log.info("Argument: " + args[nLoop]);
            }
        };
    }

    @Bean
    public ExitCodeGenerator exitCodeGenerator() {
        return () -> 42; // 종료 코드 설정
    }
    
    /**
     * Gradle 관련 파라미터를 필터링하는 메서드
     * IDE에서 실행 시 Gradle 파라미터가 애플리케이션에 전달되는 문제를 해결
     *
     * @param args 원본 명령행 인수
     * @return Gradle 관련 파라미터가 제거된 인수 배열
     */
    private static String[] filterGradleParameters(String[] args) {
        if (args == null || args.length == 0) {
            return args;
        }
        
        // 필터링할 Gradle 관련 파라미터 목록
        java.util.Set<String> gradleParams = java.util.Set.of(
            "--settings-file",
            "--build-file",
            "--project-dir",
            "--gradle-user-home"
        );
        
        java.util.List<String> filteredArgs = new java.util.ArrayList<>();
        
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            
            // Gradle 파라미터인 경우 해당 파라미터와 그 값을 건너뜀
            if (gradleParams.contains(arg) && i + 1 < args.length) {
                i++; // 파라미터 값도 건너뜀
                log.debug("Filtering out Gradle parameter: {} {}", arg, args[i]);
            } else if (arg.startsWith("--settings-file=") || 
                       arg.startsWith("--build-file=") || 
                       arg.startsWith("--project-dir=") || 
                       arg.startsWith("--gradle-user-home=")) {
                log.debug("Filtering out Gradle parameter: {}", arg);
            } else {
                filteredArgs.add(arg);
            }
        }
        
        return filteredArgs.toArray(new String[0]);
    }
}