# DTO 계층 변경사항

## 공통 사항
1. 엔티티와 DTO 분리
2. Request/Response DTO 구분
3. 유효성 검사 어노테이션 추가
4. Builder 패턴 적용
5. API 문서화를 위한 어노테이션 추가

## Code DTO
### 변경 전
```java
// 별도의 DTO 없이 VO를 그대로 사용
public class CodeVO {
    private String codeId;
    private String groupCode;
    private String code;
    private String codeName;
    private String description;
    private Integer sortOrder;
    private String useYn;
    // ...
}
```

### 변경 후
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeRequest {
    @NotBlank(message = "코드 ID는 필수입니다.")
    @Size(max = 20, message = "코드 ID는 20자를 초과할 수 없습니다.")
    private String codeId;
    
    @NotBlank(message = "그룹 코드는 필수입니다.")
    @Size(max = 20, message = "그룹 코드는 20자를 초과할 수 없습니다.")
    private String groupCode;
    
    @NotBlank(message = "코드는 필수입니다.")
    @Size(max = 20, message = "코드는 20자를 초과할 수 없습니다.")
    private String code;
    
    @NotBlank(message = "코드명은 필수입니다.")
    @Size(max = 100, message = "코드명은 100자를 초과할 수 없습니다.")
    private String codeName;
    
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
    private String description;
    
    private Integer sortOrder;
    
    private Boolean useYn;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeResponse {
    private String codeId;
    private String groupCode;
    private String code;
    private String codeName;
    private String description;
    private Integer sortOrder;
    private Boolean useYn;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
```

## Board DTO
### 변경 전
```java
// 별도의 DTO 없이 VO를 그대로 사용
public class BoardVO {
    private Long boardId;
    private String category;
    private String title;
    private String content;
    private Integer viewCount;
    private List<FileVO> files;
    // ...
}
```

### 변경 후
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequest {
    @NotBlank(message = "카테고리는 필수입니다.")
    @Size(max = 20, message = "카테고리는 20자를 초과할 수 없습니다.")
    private String category;
    
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse {
    private Long id;
    private String category;
    private String title;
    private String content;
    private Integer viewCount;
    private List<FileResponse> files;
    private List<ReplyResponse> replies;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardSearchRequest {
    private String category;
    private String keyword;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
```

## Member DTO
### 변경 전
```java
// 별도의 DTO 없이 VO를 그대로 사용
public class MemberVO {
    private String id;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String status;
    // ...
}
```

### 변경 후
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "아이디는 필수입니다.")
    private String id;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private MemberStatus status;
    private LocalDateTime lastLoginAt;
    private Set<String> roles;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequest {
    @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다.")
    private String name;
    
    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다.")
    private String phone;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {
    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;
    
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상이며, 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String newPassword;
}
```

## File DTO
### 변경 전
```java
// 별도의 DTO 없이 VO를 그대로 사용
public class FileVO {
    private Long fileId;
    private Long boardId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    // ...
}
```

### 변경 후
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private Long id;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private String createdBy;
    private LocalDateTime createdAt;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String contentType;
}
```

## Reply DTO
### 변경 전
```java
// 별도의 DTO 없이 VO를 그대로 사용
public class ReplyVO {
    private Long replyId;
    private Long boardId;
    private String content;
    // ...
}
```

### 변경 후
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyRequest {
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponse {
    private Long id;
    private String content;
    private MemberResponse member;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
} 