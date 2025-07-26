# @Mask(패턴지정) 및 자동 로그 마스킹 사용법

---

## 1. DTO/VO 필드에 @Mask 어노테이션 적용

### 1) 정규식 패턴 직접 지정

```java
import com.devkbil.mtssbj.common.masking.Mask;

public class UserVO {
    @Mask(pattern = "\\d{6}-\\d{7}") // 주민번호 패턴
    private String ssn;

    @Mask(pattern = "\\d{3}-\\d{4}-\\d{4}") // 전화번호 패턴
    private String phone;
}
```
- 해당 필드 값이 로그에 출력될 때, 지정한 정규식에 매칭되는 부분이 ****로 마스킹됩니다.

---

### 2) Enum 타입 기반 마스킹(기존 방식도 그대로 사용 가능)

```java
import com.devkbil.mtssbj.common.masking.Mask;
import com.devkbil.mtssbj.common.masking.MaskingType;

public class UserVO {
    @Mask(type = MaskingType.EMAIL)
    private String email;
}
```
- Enum 기반 마스킹은 기존과 동일하게 동작합니다.

---

## 2. 로그 출력 시 자동 마스킹

- 별도의 마스킹 호출 없이, 평소처럼 SLF4J 로그 메서드를 사용하면 됩니다.

```java
log.info("사용자 정보: {}", userVO);
```
- userVO의 @Mask가 붙은 필드는 자동으로 마스킹된 값이 로그에 출력됩니다.
- 리스트/배열 등도 각 객체별로 마스킹이 적용됩니다.

---

## 3. 혼합 사용 예시

```java
public class UserVO {
    @Mask(pattern = "\\d{6}-\\d{7}") // 주민번호
    private String ssn;

    @Mask(type = MaskingType.EMAIL) // 이메일
    private String email;

    @Mask // 기본값: 이름 마스킹
    private String name;
}
```

---

## 4. 주의사항 및 팁

- 정규식 패턴이 지정되면 pattern이 우선 적용되고, 없으면 type 기반 마스킹이 적용됩니다.
- 로그에 VO/DTO를 직접 넘기면(@Mask가 붙은 필드만 자동 마스킹) 별도의 logback maskPattern 설정 없이도 개인정보 보호가 가능합니다.
- API 응답 등에는 기존 MaskingAspect가 적용되어 별도 마스킹이 이루어집니다.

---

## 5. 예시 결과

```java
UserVO user = new UserVO();
user.setSsn("900101-1234567");
user.setEmail("test@example.com");
user.setName("홍길동");

log.info("user: {}", user);
```

**로그 출력 예시:**
```
user: {"ssn":"****","email":"t***@example.com","name":"홍*동"}
```

---

궁금한 점이나 추가 확장(예: 커스텀 마스킹 문자, 복합 객체 지원 등)이 필요하면 언제든 문의해 주세요! 