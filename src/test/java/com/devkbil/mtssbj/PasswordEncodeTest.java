package com.devkbil.mtssbj;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordEncodeTest {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        testEncoding();
    }

    void testPasswordMatches() {
        String rawPassword = "1234"; // 실제 로그인 시 입력하는 비밀번호
        String encodedPasswordFromDB = "$2a$10$3zq2cZ....";
        // ↑ 여기에 실제 DB에서 조회된 userpw 값을 넣어주세요

        boolean matches = encoder.matches(rawPassword, encodedPasswordFromDB);

        System.out.println("비밀번호 비교 결과 = " + matches);
        assertThat(matches).isTrue(); // 실제 틀리면 false로 검증 가능
    }

    public static void testEncoding() {
        String rawPassword = "1234";
        String encoded = encoder.encode(rawPassword);

        System.out.println("새로 인코딩된 비밀번호 = " + encoded);
        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
    }

    void checkIfStoredPasswordIsBCrypt() {
        String encodedPasswordFromDB = "1234"; // 예: DB에 저장된 평문 비밀번호
        boolean isBCrypt = encodedPasswordFromDB.startsWith("$2a$") ||
                encodedPasswordFromDB.startsWith("$2b$") ||
                encodedPasswordFromDB.startsWith("$2y$");

        System.out.println("BCrypt 여부 = " + isBCrypt);
        assertThat(isBCrypt).isTrue(); // BCrypt가 아닐 경우 테스트 실패
    }
}
