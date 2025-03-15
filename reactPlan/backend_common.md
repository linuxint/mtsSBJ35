# 공통 모듈 변경사항

## HTML Character Escapes
### 변경 전
```java
public class HtmlCharacterEscapes extends CharacterEscapes {
    private final int[] asciiEscapes;
    
    public HtmlCharacterEscapes() {
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['&'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
    }
    
    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }
    
    @Override
    public SerializableString getEscapeSequence(int ch) {
        return new SerializedString(StringEscapeUtils.escapeHtml4(String.valueOf((char) ch)));
    }
}
```

### 변경 후
```java
public class HtmlEscapeUtil {
    public static String escapeHtml(String unsafe) {
        if (unsafe == null) {
            return null;
        }
        return unsafe
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#039;");
    }
    
    public static String unescapeHtml(String safe) {
        if (safe == null) {
            return null;
        }
        return safe
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#039;", "'");
    }
}
```

## Date Util
### 변경 전
```java
public class DateUtil {
    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
```

### 변경 후
```java
public class DateUtil {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    public static String formatDate(Date date) {
        return formatDate(date, DEFAULT_DATE_FORMAT);
    }
    
    public static String formatDateTime(Date date) {
        return formatDate(date, DEFAULT_DATETIME_FORMAT);
    }
    
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, DEFAULT_DATE_FORMAT);
    }
    
    public static Date parseDateTime(String dateStr) {
        return parseDate(dateStr, DEFAULT_DATETIME_FORMAT);
    }
    
    public static Date parseDate(String dateStr, String format) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("날짜 형식이 올바르지 않습니다.", e);
        }
    }
    
    public static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }
    
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
```

## File Util
### 변경 전
```java
public class FileUtil {
    public static String uploadFile(MultipartFile file, String uploadPath) throws IOException {
        String originalFilename = file.getOriginalFilename();
        file.transferTo(new File(uploadPath + originalFilename));
        return originalFilename;
    }
}
```

### 변경 후
```java
public class FileUtil {
    public static String uploadFile(MultipartFile file, String uploadPath) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + "." + extension;
        
        File targetFile = new File(uploadPath, newFilename);
        FileUtils.forceMkdirParent(targetFile);
        
        file.transferTo(targetFile);
        
        return newFilename;
    }
    
    public static void deleteFile(String filePath) {
        if (StringUtils.isNotEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
    
    public static String getFileExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }
    
    public static boolean isImageFile(String filename) {
        String ext = getFileExtension(filename).toLowerCase();
        return Arrays.asList("jpg", "jpeg", "png", "gif").contains(ext);
    }
    
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        return file.exists() ? file.length() : 0L;
    }
}
```

## String Util
### 변경 전
```java
public class NewStringUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }
    
    public static String maskPersonalInfo(String value, String type) {
        if (isEmpty(value)) return value;
        
        switch (type) {
            case "name":
                return value.substring(0, 1) + "*".repeat(value.length() - 1);
            case "phone":
                return value.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-****-$3");
            case "email":
                return value.replaceAll("(\\w+)@(\\w+)", "$1****@$2");
            default:
                return value;
        }
    }
}
```

### 변경 후
```typescript
// utils/string.ts
export const isEmpty = (str: string | null | undefined): boolean => {
    return !str || str.trim().length === 0;
};

export type MaskingType = 'name' | 'phone' | 'email';

export const maskPersonalInfo = (value: string, type: MaskingType): string => {
    if (isEmpty(value)) return value;
    
    switch (type) {
        case 'name':
            return value.charAt(0) + '*'.repeat(value.length - 1);
        case 'phone':
            return value.replace(/(\d{3})(\d{3,4})(\d{4})/, '$1-****-$3');
        case 'email':
            return value.replace(/(\w+)@(\w+)/, '$1****@$2');
        default:
            return value;
    }
};

export const truncate = (str: string, maxLength: number): string => {
    if (str.length <= maxLength) return str;
    return str.substring(0, maxLength) + '...';
};
```

## Tree Maker
### 변경 전
```java
public class TreeMaker {
    public static List<TreeVO> makeTree(List<TreeVO> nodes) {
        List<TreeVO> rootNodes = new ArrayList<>();
        
        for (TreeVO node : nodes) {
            if (node.getParentId() == null) {
                rootNodes.add(node);
            } else {
                TreeVO parent = findById(nodes, node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }
        
        return rootNodes;
    }
    
    private static TreeVO findById(List<TreeVO> nodes, String id) {
        return nodes.stream()
                   .filter(node -> id.equals(node.getId()))
                   .findFirst()
                   .orElse(null);
    }
}
```

### 변경 후
```typescript
// utils/tree.ts
interface TreeNode {
    id: string;
    parentId: string | null;
    children: TreeNode[];
    [key: string]: any;
}

export const buildTree = <T extends TreeNode>(nodes: T[]): T[] => {
    const nodeMap = new Map<string, T>();
    const rootNodes: T[] = [];
    
    // 노드 맵 생성
    nodes.forEach(node => {
        node.children = [];
        nodeMap.set(node.id, node);
    });
    
    // 트리 구조 생성
    nodes.forEach(node => {
        if (node.parentId === null) {
            rootNodes.push(node);
        } else {
            const parent = nodeMap.get(node.parentId);
            if (parent) {
                parent.children.push(node);
            }
        }
    });
    
    return rootNodes;
};

export const findNodeById = <T extends TreeNode>(
    nodes: T[],
    id: string
): T | undefined => {
    for (const node of nodes) {
        if (node.id === id) return node;
        if (node.children.length > 0) {
            const found = findNodeById(node.children as T[], id);
            if (found) return found;
        }
    }
    return undefined;
};

export const flattenTree = <T extends TreeNode>(
    nodes: T[],
    level: number = 0
): (T & { level: number })[] => {
    return nodes.reduce((acc, node) => {
        return [
            ...acc,
            { ...node, level },
            ...flattenTree(node.children as T[], level + 1)
        ];
    }, [] as (T & { level: number })[]);
};
```

## JSON Util
### 변경 전
```java
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
    
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }
    
    public static <T> T fromJson(String json, TypeReference<T> typeRef) throws JsonProcessingException {
        return objectMapper.readValue(json, typeRef);
    }
}
```

### 변경 후
```typescript
// utils/json.ts
export const parseJSON = <T>(value: string, fallback: T): T => {
    try {
        return JSON.parse(value);
    } catch {
        return fallback;
    }
};

export const stringifyJSON = (value: any): string => {
    try {
        return JSON.stringify(value);
    } catch {
        return '';
    }
};

export const isValidJSON = (value: string): boolean => {
    try {
        JSON.parse(value);
        return true;
    } catch {
        return false;
    }
};
```

## JWT Util
### 변경 전
```java
public class JwtUtil {
    private static final String SECRET_KEY = "your-secret-key";
    private static final long EXPIRATION_TIME = 86400000; // 24시간
    
    public static String generateToken(String userId) {
        return Jwts.builder()
                   .setSubject(userId)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                   .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                   .compact();
    }
    
    public static String getUserIdFromToken(String token) {
        return Jwts.parser()
                   .setSigningKey(SECRET_KEY)
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }
    
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 변경 후
```typescript
// utils/jwt.ts
import jwtDecode, { JwtPayload } from 'jwt-decode';

interface CustomJwtPayload extends JwtPayload {
    userId: string;
    roles: string[];
}

export const getToken = (): string | null => {
    return localStorage.getItem('token');
};

export const setToken = (token: string): void => {
    localStorage.setItem('token', token);
};

export const removeToken = (): void => {
    localStorage.removeItem('token');
};

export const decodeToken = (token: string): CustomJwtPayload | null => {
    try {
        return jwtDecode<CustomJwtPayload>(token);
    } catch {
        return null;
    }
};

export const isTokenValid = (token: string): boolean => {
    try {
        const decoded = decodeToken(token);
        if (!decoded) return false;
        
        const currentTime = Date.now() / 1000;
        return decoded.exp ? decoded.exp > currentTime : false;
    } catch {
        return false;
    }
};

export const getUserInfo = (): CustomJwtPayload | null => {
    const token = getToken();
    if (!token) return null;
    
    return decodeToken(token);
};
```

## Masking Util
### 변경 전
```java
public class MaskingUtil {
    public static String maskName(String name) {
        if (name == null) return null;
        if (name.length() <= 1) return name;
        
        return name.charAt(0) + "*".repeat(name.length() - 1);
    }
    
    public static String maskPhoneNumber(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("(\\d{3})(\\d{3,4})(\\d{4})", "$1-****-$3");
    }
    
    public static String maskEmail(String email) {
        if (email == null) return null;
        return email.replaceAll("(\\w+)@(\\w+)", "$1****@$2");
    }
}
```

### 변경 후
```typescript
// utils/masking.ts
export type MaskingType = 'name' | 'phone' | 'email' | 'custom';

interface MaskingOptions {
    type: MaskingType;
    pattern?: RegExp;
    replacement?: string;
}

export const maskValue = (value: string, options: MaskingOptions): string => {
    if (!value) return value;
    
    switch (options.type) {
        case 'name':
            return value.charAt(0) + '*'.repeat(value.length - 1);
            
        case 'phone':
            return value.replace(/(\d{3})(\d{3,4})(\d{4})/, '$1-****-$3');
            
        case 'email':
            return value.replace(/(\w+)@(\w+)/, '$1****@$2');
            
        case 'custom':
            if (!options.pattern || !options.replacement) {
                throw new Error('Custom masking requires pattern and replacement');
            }
            return value.replace(options.pattern, options.replacement);
            
        default:
            return value;
    }
};

export const maskObject = <T extends Record<string, any>>(
    obj: T,
    fields: Record<keyof T, MaskingOptions>
): T => {
    const maskedObj = { ...obj };
    
    Object.entries(fields).forEach(([key, options]) => {
        if (maskedObj[key] && typeof maskedObj[key] === 'string') {
            maskedObj[key] = maskValue(maskedObj[key], options);
        }
    });
    
    return maskedObj;
};
```

## 파일 처리
### FileDownload
- 스트리밍 방식 적용
- 대용량 파일 처리 개선
- 보안 검사 강화

### Upload4ckeditor
- REST API 기반 업로드
- 이미지 처리 최적화
- 보안 검증 추가

## 유틸리티
### LocalDateFormatter
- Java 8 DateTime API 활용
- 타임존 처리 개선
- 포맷팅 패턴 확장

### TransactionProfiler
- 성능 모니터링 강화
- 로깅 개선
- 메트릭 수집 추가

### RepositoryProfiler
- 쿼리 성능 분석
- 캐시 히트율 모니터링
- 병목 지점 식별

## 이벤트 처리
### CustomApplicationEvent
- 이벤트 발행 구조 개선
- 비동기 처리 적용
- 에러 처리 강화

## 보안
### JwtRequestFilter
- 토큰 검증 로직 강화
- 리프레시 토큰 처리
- 권한 체크 개선

## 국제화
### LocaleMessage
- 메시지 소스 관리 개선
- 캐시 적용
- 동적 언어 변경 지원

## 페이징
### PagingVO
- React 컴포넌트 연동
- 동적 페이지 크기
- 정렬 기능 추가 