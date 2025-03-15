# VO/DTO 변경 사항

## 1. 공통 변경 사항
1. 기본 구조 변경
```java
// AS-IS: VO만 사용
public class CodeVO {
    private String codeId;
    private String codeName;
    // ...
}

// TO-BE: VO와 DTO 분리
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeVO {
    private String codeId;
    private String codeName;
    // ...
}

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeDTO {
    private String codeId;
    private String codeName;
    // ...
}
```

2. 요청/응답 DTO 분리
```java
// 요청 DTO
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeSaveRequest {
    @NotBlank
    private String codeId;
    
    @NotBlank
    private String codeName;
    // ...
}

// 응답 DTO
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeResponse {
    private String codeId;
    private String codeName;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    // ...
}
```

3. 검증 규칙 추가
```java
@Getter @Setter
public class BoardSaveRequest {
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
    
    @Size(max = 5, message = "파일은 최대 5개까지 첨부 가능합니다.")
    private List<MultipartFile> files;
}
```

## 2. 기능별 변경 사항
### 2.1 관리자 기능
#### 코드 관리
```java
// VO
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeVO {
    private String codeId;
    private String codeName;
    private String codeDesc;
    private boolean useYn;
    private int sortOrder;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}

// 요청 DTO
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeSaveRequest {
    @NotBlank(message = "코드 ID는 필수입니다.")
    @Pattern(regexp = "^[A-Z0-9_]{1,20}$", message = "코드 ID는 영문 대문자, 숫자, 언더스코어만 사용 가능합니다.")
    private String codeId;
    
    @NotBlank(message = "코드명은 필수입니다.")
    @Size(max = 100, message = "코드명은 100자를 초과할 수 없습니다.")
    private String codeName;
    
    private String codeDesc;
    private boolean useYn;
    private int sortOrder;
}

// 응답 DTO
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeDTO {
    private String codeId;
    private String codeName;
    private String codeDesc;
    private boolean useYn;
    private int sortOrder;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
```

### 2.2 인증 기능
```java
// 요청 DTO
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}

// 응답 DTO
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
}
```

### 2.3 게시판 기능
```java
// VO
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardVO {
    private Long boardId;
    private String title;
    private String content;
    private String writerId;
    private int viewCount;
    private boolean noticeYn;
    private boolean deleteYn;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private List<FileVO> files;
}

// 요청 DTO
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardSaveRequest {
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
    
    private boolean noticeYn;
    
    @Size(max = 5, message = "파일은 최대 5개까지 첨부 가능합니다.")
    private List<MultipartFile> files;
}

// 응답 DTO
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {
    private Long boardId;
    private String title;
    private String content;
    private String writerId;
    private String writerName;
    private int viewCount;
    private boolean noticeYn;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private List<FileDTO> files;
}
```

## 3. 주요 변경 포인트
1. 계층 분리
   - VO: 데이터베이스 매핑용
   - DTO: API 요청/응답용
   - Request/Response 분리

2. 검증 규칙
   - Bean Validation 사용
   - 커스텀 검증 어노테이션
   - 메시지 정의

3. 롬복 활용
   - `@Getter`, `@Setter`
   - `@Builder`
   - `@NoArgsConstructor`, `@AllArgsConstructor`

4. 타입 안전성
   - Primitive 타입 대신 Wrapper 클래스
   - Enum 활용
   - LocalDateTime 사용

5. 명명 규칙
   - VO 접미사
   - Request/Response 접미사
   - DTO 접미사

6. 보안
   - 민감 정보 제외
   - XSS 방지
   - 직렬화 제어 