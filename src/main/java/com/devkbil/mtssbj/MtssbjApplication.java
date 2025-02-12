package com.devkbil.mtssbj;

import com.devkbil.mtssbj.common.Listener.*;
import com.devkbil.mtssbj.common.events.CustomApplicationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "${app.cors.allowed-origins}")   //'Access-Control-Allow-Origin' header 추가
// Spring Data JPA Repository 스캔 범위 지정
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

        // 커스텀 ApplicationListeners 추가
        application.addListeners(
                new ApplicationStartingEventListener(),
                new ApplicationReadyEventListener(),
                new ApplicationPreparedEventListener(),
                new ApplicationFailedEventListener(),
                new ApplicationEnvironmentPreparedEventListener(),
                new ApplicationContextInitializedEventListener(),
                new ApplicationContextClosedEventListener(),
                new ApplicationContextRefreshedEventListener()
        );

        // custom banner of java
        /*
        application.setBanner(new Banner() {
            @Override
            public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {

                out.println("888b     d888 88888888888 .d8888b.        d888        .d8888b.)");
                out.println("8888b   d8888     888    d88P  Y88b      d8888       d88P  Y88b)");
                out.println("88888b.d88888     888    Y88b.             888       888    888)");
                out.println("888Y88888P888     888     \"Y888b.          888       888    888)");
                out.println("888 Y888P 888     888        \"Y88b.        888       888    888)");
                out.println("888  Y8P  888     888          \"888        888       888    888)");
                out.println("888   \"   888     888    Y88b  d88P        888   d8b Y88b  d88P)");
                out.println("888       888     888     \"Y8888P\"       8888888 Y8P  \"Y8888P\")");

            }
        });
        */

        // 애플리케이션 실행
        application.run(args);
//        int exitCode = SpringApplication.exit(application.run(args));
//        System.exit(exitCode);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Application started with CommandLineRunner");

        // 사용자 정의 이벤트를 발행
        CustomApplicationEvent customEvent = new CustomApplicationEvent(this, "Hello from CustomApplicationEvent");
        applicationEventPublisher.publishEvent(customEvent);
        log.info("Custom application event published");
    }

    @Bean
    public CommandLineRunner additionalCommandLineRunner() {
        return args -> {
            log.info("CommandLineRunner executed as a bean definition  with " + args.length + "arguments");
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