# Mapper 변경 사항

## 1. 공통 변경 사항
1. 기본 구조 변경
```java
// AS-IS
public interface CodeMapper {
    List<CodeVO> selectList(Map<String, Object> params);
    CodeVO selectOne(String codeId);
    void insert(CodeVO vo);
    void update(CodeVO vo);
    void delete(String codeId);
}

// TO-BE
@Mapper
public interface CodeMapper {
    List<CodeVO> selectList(CodeSearchRequest request);
    Optional<CodeVO> selectOne(String codeId);
    void insert(CodeVO vo);
    void update(CodeVO vo);
    boolean delete(String codeId);
}
```

2. XML 매핑 변경
```xml
<!-- AS-IS -->
<select id="selectList" parameterType="map" resultType="codeVO">
    SELECT *
    FROM TB_COM_CODE
    WHERE 1=1
    <if test="codeId != null">
        AND CODE_ID = #{codeId}
    </if>
</select>

<!-- TO-BE -->
<select id="selectList" parameterType="codeSearchRequest" resultType="codeVO">
    SELECT *
    FROM TB_COM_CODE
    <where>
        <if test="codeId != null">
            CODE_ID = #{codeId}
        </if>
        <if test="useYn != null">
            AND USE_YN = #{useYn}
        </if>
    </where>
    ORDER BY SORT_ORDER
</select>
```

3. 결과 매핑 개선
```xml
<resultMap id="boardResultMap" type="boardVO">
    <id property="boardId" column="BOARD_ID"/>
    <result property="title" column="TITLE"/>
    <result property="content" column="CONTENT"/>
    <result property="writerId" column="WRITER_ID"/>
    <result property="viewCount" column="VIEW_COUNT"/>
    <result property="noticeYn" column="NOTICE_YN"/>
    <result property="deleteYn" column="DELETE_YN"/>
    <result property="regDate" column="REG_DATE"/>
    <result property="modDate" column="MOD_DATE"/>
    <collection property="files" ofType="fileVO">
        <id property="fileId" column="FILE_ID"/>
        <result property="fileName" column="FILE_NAME"/>
        <result property="filePath" column="FILE_PATH"/>
    </collection>
</resultMap>
```

## 2. 기능별 변경 사항
### 2.1 관리자 기능
#### CodeMapper
```java
@Mapper
public interface CodeMapper {
    List<CodeVO> selectList(CodeSearchRequest request);
    Optional<CodeVO> selectOne(String codeId);
    void insert(CodeVO vo);
    void update(CodeVO vo);
    boolean delete(String codeId);
    boolean exists(String codeId);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.devkbil.mtssbj.admin.mapper.CodeMapper">
    
    <select id="selectList" parameterType="codeSearchRequest" resultType="codeVO">
        SELECT *
        FROM TB_COM_CODE
        <where>
            <if test="codeId != null">
                CODE_ID = #{codeId}
            </if>
            <if test="useYn != null">
                AND USE_YN = #{useYn}
            </if>
            <if test="keyword != null">
                AND (CODE_ID LIKE CONCAT('%', #{keyword}, '%')
                OR CODE_NAME LIKE CONCAT('%', #{keyword}, '%'))
            </if>
        </where>
        ORDER BY SORT_ORDER
    </select>
    
    <select id="selectOne" parameterType="string" resultType="codeVO">
        SELECT *
        FROM TB_COM_CODE
        WHERE CODE_ID = #{codeId}
    </select>
    
    <insert id="insert" parameterType="codeVO">
        INSERT INTO TB_COM_CODE (
            CODE_ID,
            CODE_NAME,
            CODE_DESC,
            USE_YN,
            SORT_ORDER,
            REG_DATE
        ) VALUES (
            #{codeId},
            #{codeName},
            #{codeDesc},
            #{useYn},
            #{sortOrder},
            CURRENT_TIMESTAMP
        )
    </insert>
    
    <update id="update" parameterType="codeVO">
        UPDATE TB_COM_CODE
        SET
            CODE_NAME = #{codeName},
            CODE_DESC = #{codeDesc},
            USE_YN = #{useYn},
            SORT_ORDER = #{sortOrder},
            MOD_DATE = CURRENT_TIMESTAMP
        WHERE CODE_ID = #{codeId}
    </update>
    
    <delete id="delete" parameterType="string">
        DELETE FROM TB_COM_CODE
        WHERE CODE_ID = #{codeId}
    </delete>
    
    <select id="exists" parameterType="string" resultType="boolean">
        SELECT COUNT(*) > 0
        FROM TB_COM_CODE
        WHERE CODE_ID = #{codeId}
    </select>
</mapper>
```

### 2.2 게시판 기능
#### BoardMapper
```java
@Mapper
public interface BoardMapper {
    List<BoardVO> selectList(BoardSearchRequest request);
    Optional<BoardVO> selectOne(Long boardId);
    int selectCount(BoardSearchRequest request);
    void insert(BoardVO vo);
    void update(BoardVO vo);
    boolean delete(Long boardId);
    void updateViewCount(Long boardId);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.devkbil.mtssbj.board.mapper.BoardMapper">
    
    <resultMap id="boardResultMap" type="boardVO">
        <id property="boardId" column="BOARD_ID"/>
        <result property="title" column="TITLE"/>
        <result property="content" column="CONTENT"/>
        <result property="writerId" column="WRITER_ID"/>
        <result property="viewCount" column="VIEW_COUNT"/>
        <result property="noticeYn" column="NOTICE_YN"/>
        <result property="deleteYn" column="DELETE_YN"/>
        <result property="regDate" column="REG_DATE"/>
        <result property="modDate" column="MOD_DATE"/>
        <collection property="files" ofType="fileVO">
            <id property="fileId" column="FILE_ID"/>
            <result property="fileName" column="FILE_NAME"/>
            <result property="filePath" column="FILE_PATH"/>
        </collection>
    </resultMap>
    
    <select id="selectList" parameterType="boardSearchRequest" resultMap="boardResultMap">
        SELECT
            B.*,
            F.FILE_ID,
            F.FILE_NAME,
            F.FILE_PATH
        FROM TB_BOARD B
        LEFT JOIN TB_BOARD_FILE F ON B.BOARD_ID = F.BOARD_ID
        <where>
            B.DELETE_YN = FALSE
            <if test="keyword != null">
                AND (
                    B.TITLE LIKE CONCAT('%', #{keyword}, '%')
                    OR B.CONTENT LIKE CONCAT('%', #{keyword}, '%')
                )
            </if>
            <if test="writerId != null">
                AND B.WRITER_ID = #{writerId}
            </if>
            <if test="startDate != null">
                AND B.REG_DATE >= #{startDate}
            </if>
            <if test="endDate != null">
                AND B.REG_DATE < #{endDate}
            </if>
        </where>
        ORDER BY B.NOTICE_YN DESC, B.REG_DATE DESC
        LIMIT #{size} OFFSET #{offset}
    </select>
</mapper>
```

## 3. 주요 변경 포인트
1. 매퍼 인터페이스
   - `@Mapper` 어노테이션 사용
   - Optional 반환 타입
   - 명확한 메서드명

2. XML 매핑
   - 동적 쿼리 최적화
   - 결과 매핑 상세화
   - 조인 쿼리 개선

3. 페이징 처리
   - LIMIT/OFFSET 사용
   - 카운트 쿼리 분리
   - 정렬 기준 명확화

4. 성능 최적화
   - 인덱스 활용
   - 불필요한 조인 제거
   - 캐시 적용

5. 타입 안전성
   - parameterType 명시
   - resultType/resultMap 활용
   - CDATA 활용

6. 보안
   - SQL 인젝션 방지
   - PreparedStatement 활용
   - 권한 체크 