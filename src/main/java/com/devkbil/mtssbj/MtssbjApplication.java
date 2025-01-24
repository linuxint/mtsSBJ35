package com.devkbil.mtssbj;

import com.devkbil.mtssbj.common.Listener.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "${app.cors.allowed-origins}")   //'Access-Control-Allow-Origin' header 추가
// Spring Data JPA Repository 스캔 범위 지정
@Slf4j
@EnableCaching
public class MtssbjApplication implements CommandLineRunner {

    public static void main(String[] args) {
        //SpringApplication.run(MtssbjApplication.class, args);
        SpringApplication application = new SpringApplication(MtssbjApplication.class);

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

        //application.setBannerMode(Banner.Mode.OFF); -- banner mode off
        application.setWebApplicationType(WebApplicationType.SERVLET);
        //application.setWebApplicationType(WebApplicationType.REACTIVE);

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
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("CourseTrackerApplication CommandLineRunner has executed");
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            log.info("CommandLineRunner executed as a bean definition  with " + args.length + "arguments");
            for (int nLoop = 0; nLoop < args.length; nLoop++) {
                log.info("Argument: " + args[nLoop]);
            }
        };
    }

}
