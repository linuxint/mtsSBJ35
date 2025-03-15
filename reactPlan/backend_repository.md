# 리포지토리 계층 변경사항

## JPA에서 MyBatis로의 전환
### 주요 변경사항
1. **어노테이션 변경**
   - JPA: `@Repository`, `@Entity` → MyBatis: `@Mapper`
   - JPA 엔티티 매핑 → MyBatis `@Results` 매핑

2. **메서드 명명 규칙 변경**
   - findById → selectById
   - save → insert/update
   - delete → delete
   - findAll → selectList

3. **쿼리 처리 방식**
   - JPQL → MyBatis XML/어노테이션 기반 SQL
   - Criteria API → 동적 SQL
   - 페이징: Page<T> → RowBounds

4. **엔티티 관계 처리**
   - JPA 연관관계 매핑 → 명시적 조인 쿼리
   - 지연로딩/즉시로딩 → 필요한 데이터만 조회

5. **트랜잭션 처리**
   - JPA 영속성 컨텍스트 제거
   - MyBatis 세션 기반 처리

## 공통 구현 사항
1. **동적 쿼리 처리**
   ```xml
   <select id="selectList">
     SELECT * FROM table
     <where>
       <if test="condition != null">
         AND column = #{condition}
       </if>
     </where>
   </select>
   ```

2. **페이징 처리**
   ```java
   List<T> selectList(@Param("search") SearchVO search, 
                     @Param("rowBounds") RowBounds rowBounds);
   ```

3. **결과 매핑**
   ```java
   @Results({
       @Result(property = "entityId", column = "entity_id"),
       @Result(property = "name", column = "name")
   })
   ```

4. **일괄 처리**
   ```java
   int insertList(@Param("list") List<T> list);
   ```

## Code Mapper
### 변경 전
```java
@Repository
public interface CodeRepository extends JpaRepository<CodeEntity, String> {
    List<CodeEntity> findByGroupCode(String groupCode);
    Optional<CodeEntity> findById(String id);
    boolean existsByGroupCodeAndCode(String groupCode, String code);
}
```

### 변경 후
```java
@Mapper
public interface CodeMapper {
    List<CodeVO> selectCodeList(String groupCode);
    
    @Select("SELECT * FROM tb_code WHERE group_code = #{groupCode} ORDER BY sort_order")
    @Results({
        @Result(property = "codeId", column = "code_id"),
        @Result(property = "groupCode", column = "group_code"),
        @Result(property = "codeName", column = "code_name"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "useYn", column = "use_yn")
    })
    List<CodeVO> selectCodeListByGroupCode(String groupCode);
    
    CodeVO selectCode(String codeId);
    
    int insertCode(CodeVO code);
    
    int updateCode(CodeVO code);
    
    int deleteCode(String codeId);
    
    boolean existsByGroupCodeAndCode(@Param("groupCode") String groupCode, 
                                   @Param("code") String code);
}
```

## Board Mapper
### 변경 전
```java
@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    Page<BoardEntity> findAll(Specification<BoardEntity> spec, Pageable pageable);
    Optional<BoardEntity> findById(Long id);
    
    @Query("SELECT b FROM BoardEntity b LEFT JOIN FETCH b.files WHERE b.id = :id")
    Optional<BoardEntity> findByIdWithFiles(@Param("id") Long id);
}
```

### 변경 후
```java
@Mapper
public interface BoardMapper {
    List<BoardVO> selectBoardList(@Param("search") BoardSearchVO search, 
                                 @Param("rowBounds") RowBounds rowBounds);
    
    int selectBoardListCount(@Param("search") BoardSearchVO search);
    
    BoardVO selectBoard(Long boardId);
    
    BoardVO selectBoardWithFiles(Long boardId);
    
    List<BoardVO> selectBoardsByCategory(@Param("category") String category, 
                                       @Param("rowBounds") RowBounds rowBounds);
    
    int selectBoardsByCategoryCount(String category);
    
    int insertBoard(BoardVO board);
    
    int updateBoard(BoardVO board);
    
    int deleteBoard(Long boardId);
    
    int incrementViewCount(Long boardId);
}
```

## Member Mapper
### 변경 전
```java
@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    Optional<MemberEntity> findById(String id);
    Optional<MemberEntity> findByEmail(String email);
    
    @Query("SELECT m FROM MemberEntity m LEFT JOIN FETCH m.roles WHERE m.id = :id")
    Optional<MemberEntity> findByIdWithRoles(@Param("id") String id);
}
```

### 변경 후
```java
@Mapper
public interface MemberMapper {
    @Select("SELECT * FROM tb_member WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "password", column = "password"),
        @Result(property = "name", column = "name"),
        @Result(property = "email", column = "email"),
        @Result(property = "phone", column = "phone"),
        @Result(property = "status", column = "status"),
        @Result(property = "lastLoginAt", column = "last_login_at"),
        @Result(property = "refreshToken", column = "refresh_token")
    })
    MemberVO selectMemberById(String id);
    
    MemberVO selectMemberByEmail(String email);
    
    MemberVO selectMemberWithRoles(String id);
    
    MemberVO selectMemberByRefreshToken(String refreshToken);
    
    int insertMember(MemberVO member);
    
    int updateMember(MemberVO member);
    
    int deleteMember(String id);
    
    int updateLastLoginAt(@Param("id") String id, 
                         @Param("lastLoginAt") LocalDateTime lastLoginAt);
    
    List<MemberVO> selectInactiveMembers(@Param("date") LocalDateTime date);
    
    boolean existsByEmail(String email);
}
```

## File Mapper
### 변경 전
```java
@Mapper
public interface FileMapper {
    List<FileVO> selectFileList(Long boardId);
    void insertFile(FileVO file);
    void deleteFile(Long fileId);
    void deleteFilesByBoardId(Long boardId);
}
```

### 변경 후
```java
@Mapper
public interface FileMapper {
    List<FileVO> selectFilesByBoardId(Long boardId);
    
    FileVO selectFile(Long fileId);
    
    int insertFile(FileVO file);
    
    int deleteFile(Long fileId);
    
    int deleteFilesByBoardId(Long boardId);
    
    @Select("SELECT file_path FROM tb_file WHERE board_id = #{boardId}")
    List<String> selectFilePathsByBoardId(Long boardId);
    
    @Select("SELECT SUM(file_size) FROM tb_file WHERE board_id = #{boardId}")
    Long selectTotalFileSize(Long boardId);
}
```

## Reply Mapper
### 변경 전
```java
@Mapper
public interface ReplyMapper {
    List<ReplyVO> selectReplyList(Long boardId);
    void insertReply(ReplyVO reply);
    void deleteReply(Long replyId);
}
```

### 변경 후
```java
@Mapper
public interface ReplyMapper {
    List<ReplyVO> selectRepliesByBoardId(Long boardId);
    
    List<ReplyVO> selectRepliesWithMember(Long boardId);
    
    @Select("SELECT COUNT(*) FROM tb_reply WHERE board_id = #{boardId}")
    long selectReplyCount(Long boardId);
    
    int insertReply(ReplyVO reply);
    
    int updateReply(ReplyVO reply);
    
    int deleteReply(Long replyId);
    
    int deleteRepliesByBoardId(Long boardId);
}

## Department Mapper
### 변경 전
```java
@Repository
public interface DepartmentRepository {
    List<DepartmentVO> findAll();
    DepartmentVO findById(String id);
    void save(DepartmentVO department);
    void delete(String id);
}
```

### 변경 후
```java
@Mapper
public interface DepartmentMapper {
    
    @Select("SELECT * FROM tb_department ORDER BY sort_order")
    @Results({
        @Result(property = "departmentId", column = "department_id"),
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "departmentName", column = "department_name"),
        @Result(property = "sortOrder", column = "sort_order"),
        @Result(property = "useYn", column = "use_yn")
    })
    List<DepartmentVO> selectDepartmentList();
    
    @Select("SELECT * FROM tb_department WHERE department_id = #{departmentId}")
    DepartmentVO selectDepartment(String departmentId);
    
    List<DepartmentVO> selectDepartmentsByParentId(String parentId);
    
    int insertDepartment(DepartmentVO department);
    
    int updateDepartment(DepartmentVO department);
    
    int deleteDepartment(String departmentId);
    
    @Select("SELECT COUNT(*) FROM tb_department WHERE parent_id = #{departmentId}")
    int countChildDepartments(String departmentId);
    
    @Select("SELECT COUNT(*) FROM tb_member WHERE department_id = #{departmentId}")
    int countDepartmentMembers(String departmentId);
    
    List<DepartmentVO> selectDepartmentTree();
    
    int updateDepartmentOrder(@Param("departments") List<DepartmentOrderVO> departments);
}

## Connection Mapper
### 변경 전
```java
// 별도의 매퍼 인터페이스 없이 직접 SQL 실행
```

### 변경 후
```java
@Mapper
public interface ConnectionMapper {
    
    @Select("SELECT * FROM tb_connection WHERE server_id = #{serverId}")
    @Results({
        @Result(property = "connectionId", column = "connection_id"),
        @Result(property = "serverId", column = "server_id"),
        @Result(property = "connectionType", column = "connection_type"),
        @Result(property = "connectionString", column = "connection_string"),
        @Result(property = "username", column = "username"),
        @Result(property = "status", column = "status")
    })
    List<ConnectionVO> selectConnectionsByServerId(String serverId);
    
    ConnectionVO selectConnection(String connectionId);
    
    int insertConnection(ConnectionVO connection);
    
    int updateConnection(ConnectionVO connection);
    
    int deleteConnection(String connectionId);
    
    @Update("UPDATE tb_connection SET status = #{status} WHERE connection_id = #{connectionId}")
    int updateConnectionStatus(@Param("connectionId") String connectionId, @Param("status") String status);
    
    @Select("SELECT COUNT(*) FROM tb_connection WHERE server_id = #{serverId} AND status = 'ACTIVE'")
    int countActiveConnections(String serverId);
} 