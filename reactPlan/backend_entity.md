# VO 클래스 변경사항

## 공통 사항
1. JPA 엔티티에서 MyBatis VO로 전환
2. 기본적인 getter/setter 구현
3. toString, equals, hashCode 메서드 구현
4. 직렬화 지원을 위한 Serializable 구현
5. Builder 패턴 적용 (선택적)

## Code VO
### 변경 전
```java
public class CodeVO {
    private String codeId;
    private String groupCode;
    private String code;
    private String codeName;
    private String description;
    private Integer sortOrder;
    private String useYn;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    
    // Getters and Setters
}
```

### 변경 후
```java
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeVO implements Serializable {
    private String codeId;
    private String groupCode;
    private String code;
    private String codeName;
    private String description;
    private Integer sortOrder;
    private String useYn;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
```

## Board VO
### 변경 전
```java
public class BoardVO {
    private Long boardId;
    private String category;
    private String title;
    private String content;
    private Integer viewCount;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private List<FileVO> files;
}
```

### 변경 후
```java
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardVO implements Serializable {
    private Long boardId;
    private String category;
    private String title;
    private String content;
    private Integer viewCount;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private List<FileVO> files;
    private List<ReplyVO> replies;
    
    public void addViewCount() {
        this.viewCount = (this.viewCount == null ? 1 : this.viewCount + 1);
    }
}
```

## Member VO
### 변경 전
```java
public class MemberVO {
    private String id;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime lastLoginAt;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
```

### 변경 후
```java
@Getter
@Setter
@ToString(exclude = "password")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO implements Serializable {
    private String id;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String status;
    private LocalDateTime lastLoginAt;
    private String refreshToken;
    private List<String> roles;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    
    @JsonIgnore
    public String getPassword() {
        return password;
    }
}
```

## File VO
### 변경 전
```java
public class FileVO {
    private Long fileId;
    private Long boardId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String createdBy;
    private LocalDateTime createdAt;
}
```

### 변경 후
```java
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileVO implements Serializable {
    private Long fileId;
    private Long boardId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String contentType;
    private String createdBy;
    private LocalDateTime createdAt;
}
```

## Reply VO
### 변경 전
```java
public class ReplyVO {
    private Long replyId;
    private Long boardId;
    private String content;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
```

### 변경 후
```java
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyVO implements Serializable {
    private Long replyId;
    private Long boardId;
    private String content;
    private String memberId;
    private String memberName;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
```