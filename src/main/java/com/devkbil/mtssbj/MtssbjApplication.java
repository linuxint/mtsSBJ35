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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@CrossOrigin(origins = "${app.cors.allowed-origins}") // 'Access-Control-Allow-Origin' header 추가
@Slf4j
@EnableCaching
public class MtssbjApplication implements CommandLineRunner {

    private final ApplicationEventPublisher applicationEventPublisher;

    // ApplicationEventPublisher 주입받기
    public MtssbjApplication(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public static void main(String[] args) {
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

        // 애플리케이션 실행
        application.run(args);

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
}