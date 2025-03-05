package com.devkbil.mtssbj;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncodeTest {

    public static void main(String[] args) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = "1234"; // 인코딩 전 메서드
        String encdoePassword1; // 인코딩된 메서드
        String encdoePassword2; // 똑같은 비밀번호 데이터를 encdoe()메서드를 사용했을 때 동일한 인코딩된 값이 나오는지 확인하기 위해 추가

        encdoePassword1 = passwordEncoder.encode(rawPassword);
        encdoePassword2 = passwordEncoder.encode(rawPassword);

        // 인코딩된 패스워드 출력
        System.out.println("encdoePassword1 : " + encdoePassword1);
        System.out.println(" encdoePassword2 : " + encdoePassword2);

        String truePassowrd = "vam123";
        String falsePassword = "asdfjlasf";

        System.out.println(
            "truePassword verify : " + passwordEncoder.matches(truePassowrd, encdoePassword1));
        System.out.println(
            "falsePassword verify : " + passwordEncoder.matches(falsePassword, encdoePassword1));
    }
}
