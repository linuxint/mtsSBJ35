# Util 변경 사항

## 1. 공통 변경 사항
1. 기본 구조 변경
```java
// AS-IS: 정적 메서드 중심
public class DateUtil {
    public static String formatDate(Date date) {
        // ...
    }
}

// TO-BE: 객체 지향적 설계
@Component
@Slf4j
public class DateCore {
    public String formatDate(LocalDateTime date) {
        // ...
    }
}
```

2. 날짜 처리 변경
```java
// AS-IS: java.util.Date 사용
public class DateUtil {
    public static Date parseDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.parse(dateStr);
    }
}

// TO-BE: java.time 패키지 사용
public class DateCore {
    public LocalDateTime parseDate(String dateStr) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
    }
}
```

3. 예외 처리 개선
```java
// AS-IS
public class FileUtil {
    public static void copyFile(String src, String dest) {
        try {
            // 파일 복사 로직
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// TO-BE
public class FileCore {
    public void copyFile(String src, String dest) {
        try {
            // 파일 복사 로직
        } catch (IOException e) {
            log.error("파일 복사 중 오류 발생", e);
            throw new FileProcessException("파일 복사 실패", e);
        }
    }
}
```

## 2. 기능별 변경 사항
### 2.1 날짜 관련 유틸리티
#### DateCore
```java
@Component
@Slf4j
public class DateCore {
    private final DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public String formatDate(LocalDateTime date) {
        return date != null ? date.format(defaultFormatter) : "";
    }
    
    public LocalDateTime parseDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, defaultFormatter);
        } catch (DateTimeParseException e) {
            log.error("날짜 파싱 오류", e);
            throw new DateParseException("날짜 형식이 올바르지 않습니다.", e);
        }
    }
    
    public LocalDateTime getStartOfDay(LocalDateTime date) {
        return date.toLocalDate().atStartOfDay();
    }
    
    public LocalDateTime getEndOfDay(LocalDateTime date) {
        return date.toLocalDate().atTime(LocalTime.MAX);
    }
    
    public boolean isWeekend(LocalDateTime date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
}
```

#### DateVOMapper
```java
@Component
@Slf4j
public class DateVOMapper {
    public DateVO toDateVO(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        
        DateVO vo = new DateVO();
        vo.setYear(date.getYear());
        vo.setMonth(date.getMonthValue());
        vo.setDay(date.getDayOfMonth());
        vo.setDayOfWeek(date.getDayOfWeek().getValue());
        return vo;
    }
    
    public LocalDateTime toLocalDateTime(DateVO vo) {
        if (vo == null) {
            return null;
        }
        
        return LocalDateTime.of(vo.getYear(), vo.getMonth(), vo.getDay(), 0, 0);
    }
}
```

### 2.2 파일 관련 유틸리티
#### FileCore
```java
@Component
@Slf4j
public class FileCore {
    private final String uploadPath;
    
    public FileCore(@Value("${app.upload.path}") String uploadPath) {
        this.uploadPath = uploadPath;
    }
    
    public String saveFile(MultipartFile file, String dirPath) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;
            
            Path targetPath = Paths.get(uploadPath, dirPath, newFilename);
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            return dirPath + "/" + newFilename;
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생", e);
            throw new FileProcessException("파일 저장 실패", e);
        }
    }
    
    public void deleteFile(String filePath) {
        try {
            Path targetPath = Paths.get(uploadPath, filePath);
            Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생", e);
            throw new FileProcessException("파일 삭제 실패", e);
        }
    }
    
    public String getContentType(String filename) {
        return URLConnection.guessContentTypeFromName(filename);
    }
    
    public boolean isImageFile(String filename) {
        String contentType = getContentType(filename);
        return contentType != null && contentType.startsWith("image/");
    }
}
```

### 2.3 문자열 관련 유틸리티
#### StringCore
```java
@Component
@Slf4j
public class StringCore {
    public String nullToEmpty(String str) {
        return str != null ? str : "";
    }
    
    public String maskPersonalInfo(String str, PersonalInfoType type) {
        if (str == null) {
            return "";
        }
        
        switch (type) {
            case PHONE:
                return maskPhone(str);
            case EMAIL:
                return maskEmail(str);
            case NAME:
                return maskName(str);
            default:
                return str;
        }
    }
    
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        return phone.substring(0, phone.length() - 4) + "****";
    }
    
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        
        if (name.length() <= 2) {
            return name + "@" + domain;
        }
        
        return name.substring(0, 2) + "***@" + domain;
    }
}
```

## 3. 주요 변경 포인트
1. 객체 지향적 설계
   - 정적 메서드 지양
   - 의존성 주입 활용
   - 단일 책임 원칙 준수

2. 예외 처리
   - 구체적인 예외 클래스
   - 로깅 전략
   - 예외 전환

3. 타입 안전성
   - java.time 패키지 사용
   - 제네릭 활용
   - 불변 객체 활용

4. 설정 관리
   - 외부 설정 주입
   - 환경별 설정 분리
   - 기본값 처리

5. 성능 최적화
   - 캐시 활용
   - 불필요한 객체 생성 방지
   - 리소스 관리

6. 보안
   - 파일 업로드 검증
   - 개인정보 마스킹
   - XSS 방지 