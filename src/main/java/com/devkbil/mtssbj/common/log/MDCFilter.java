package com.devkbil.mtssbj.common.log;

import com.devkbil.common.util.RequestUtil;
import com.devkbil.mtssbj.member.auth.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/**
 * MDCFilter는 들어오는 HTTP 요청을 가로채고 컨텍스트 로깅을 위한
 * Mapped Diagnostic Context (MDC)를 초기화하는 서블릿 필터입니다.
 * 사용자 정보, 요청 상관 ID, 클라이언트 IP 주소와 같은 특정 컨텍스트 정보를 가져와 MDC에 설정합니다.
 * 이를 통해 요청의 추적성과 디버깅 로그 항목을 개선할 수 있습니다.
 * <p>
 * 이 클래스는 `OncePerRequestFilter`를 확장하여 단일 요청 내에서 한 번만 실행되게 합니다.
 * <p>
 * 필터링 과정에서:
 * - 인증된 사용자 정보를 얻어 MDC의 USER_NAME 키에 설정합니다.
 * - 트랜잭션 ID(TRX_ID)를 생성하여 MDC에 추가하고 로그에서 사용 가능하게 합니다.
 * - 요청 헤더에서 상관 ID(CORRELATION_ID)를 가져와 MDC에 설정합니다.
 * - 사용자의 IP 주소를 가져와 MDC의 USER_IP 키에 저장합니다.
 * - 요청 처리가 완료된 후 MDC 컨텍스트를 정리하여 스레드 간 컨텍스트 누출을 방지합니다.
 * <p>
 * 필터는 또한 사용자 IP와 같은 주요 요청 세부 정보를 기록하고 가로챈 요청에 대한 감사 추적을 제공합니다.
 * <p>
 * 참고:
 * - `getUserPrincipal` 메서드는 인증 서비스(Authentication Service)를 사용하여 인증된 사용자 ID와 이름을 가져와 연결합니다.
 * - 구현에서 사용된 MDC 키는 `MDCKey` 열거형 클래스에 사전 정의되어 있습니다.
 */
@Slf4j
@Component
public class MDCFilter extends OncePerRequestFilter {

    private final AuthService authService;

    /**
     * MDCFilter 클래스를 생성하는 생성자입니다. 이 클래스는 인증 서비스와 통합됩니다.
     * 이 필터는 로깅 목적으로 Mapped Diagnostic Context (MDC) 값을 관리하고 설정하며,
     * 디버깅 또는 진단 활동을 위한 문맥 정보를 로깅할 수 있도록 합니다.
     *
     * @param authService 사용자별 혹은 요청별 정보를 수집하여 MDC를 풍부하게
     *                    하는 데 사용되는 인증 서비스
     */
    public MDCFilter(AuthService authService) {
        this.authService = authService;
    }

    /**
     * HTTP 요청을 가로채고 사용자 세부정보, 트랜잭션 ID, 상관 ID, 사용자 IP 주소와 같은
     * 문맥 정보를 MDC에 추가합니다. 요청 처리가 완료되면 MDC를 정리하여
     * 스레드 컨텍스트 누출을 방지합니다.
     *
     * @param request     클라이언트 요청 데이터를 포함하는 HttpServletRequest 객체
     * @param response    응답 데이터를 포함할 HttpServletResponse 객체
     * @param filterChain 요청과 응답을 다음 필터로 전달하는 FilterChain 객체
     * @throws ServletException 요청 처리 중 오류가 발생하면 발생
     * @throws IOException      요청 처리 중 I/O 오류가 발생하면 발생
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 사용자 정보를 가져와 "userId@userName" 형식으로 MDC에 USER_NAME 키로 설정
            String user = getUserPrincipal();
            MDC.put(MDCKey.USER_NAME.getKey(), user);

            // 랜덤 트랜잭션 ID를 생성하여 MDC에 TRX_ID 키로 설정
            String trxId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put(MDCKey.TRX_ID.getKey(), trxId);

            // 요청 헤더에서 상관 ID를 가져와 MDC에 CORRELATION_ID 키로 설정 (값이 없을 경우 빈 문자열로 설정)
            String correlationId = request.getHeader(MDCKey.CORRELATION_ID.getKey());
            MDC.put(MDCKey.CORRELATION_ID.getKey(), correlationId == null ? "" : correlationId);

            // 클라이언트의 IP 주소를 가져와 MDC에 USER_IP 키로 설정
            String userIp = RequestUtil.getRemoteAddr(request);
            MDC.put(MDCKey.USER_IP.getKey(), userIp);

            // 클라이언트 IP 로그로 출력
            log.info("userip: {}", userIp);
            log.info("요청을 가로채고 MDC 문맥 정보를 설정합니다.");

            // 필터 체인으로 요청과 응답을 다음 필터로 전달
            filterChain.doFilter(request, response);
        } finally {
            // 요청 처리가 끝난 후 MDC 컨텍스트 정리
            MDC.clear();
        }
    }

    /**
     * 인증된 사용자의 정보를 "userId@userName" 형식의 문자열로 반환합니다.
     * - 사용자 ID와 사용자 이름을 "@"로 연결하여 반환합니다.
     * - 인증 서비스에서 사용자 정보를 가져오는 데 실패하면 빈 문자열을 반환합니다.
     *
     * @return "userId@userName" 형식의 문자열 (예: "testUser123@홍길동"),
     * 또는 사용자 정보를 가져오는 데 실패한 경우 빈 문자열
     */
    private String getUserPrincipal() {

        try {
            String userId = authService.getAuthOpt()
                .map(auth -> auth.getUserid())
                .orElse(""); // 사용자 ID 가져오기
            String userName = authService.getAuthOpt()
                .map(auth -> auth.getUsernm())
                .orElse(""); // 사용자 이름 가져오기
            return userId + "@" + userName; // ID와 이름을 연결하여 반환
        } catch (NullPointerException e) {
            log.warn("사용자 정보를 가져오는 데 실패했습니다. ", e);
            return ""; // 실패 시 빈 문자열 반환
        }
    }
}