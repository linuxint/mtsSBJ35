package com.devkbil.mtssbj.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Dog API의 상태를 확인하는 HealthIndicator 클래스입니다.
 */
@Component
public class DogsApiHealthIndicator implements HealthIndicator {

    /**
     * 상태 확인을 진행하며, 세부 정보를 포함할지 여부를 설정합니다.
     *
     * @param includeDetails 세부 정보를 포함할지 여부
     * @return Health 상태 객체
     */
    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    /**
     * Dog API의 상태를 확인합니다.
     * API에서 200 OK 및 유효한 데이터를 반환하면 시스템 상태를 정상(UP)으로 표시합니다.
     * 실패 시 다운(DOWN) 상태와 세부 정보가 포함됩니다.
     *
     * @return Health 상태 객체
     */
    @Override
    public Health health() {
        try {
            ParameterizedTypeReference<Map<String, String>> reference = new ParameterizedTypeReference<Map<String, String>>() {
            };
            ResponseEntity<Map<String, String>> result = new RestTemplate()
                .exchange("https://dog.ceo/api/breeds/image/random", HttpMethod.GET, null, reference);

            if (result.getStatusCode().is2xxSuccessful() && result.getBody() != null) {
                return Health.up().withDetails(result.getBody()).build();
            } else {
                return Health.down().withDetail("status", result.getStatusCode()).build();
            }
        } catch (RestClientException ex) {
            return Health.down().withException(ex).build();
        }
    }
}
