# 리포지토리 계층 변경사항

## 공통 사항
1. 동적 쿼리 처리를 위한 MyBatis 동적 SQL 활용
2. 페이징 처리를 위한 RowBounds 사용
3. 캐시 적용을 위한 MyBatis 캐시 설정
4. 일관된 명명 규칙 적용
5. 결과 매핑 최적화

## Code Mapper
### 변경 전
```java
@Mapper
public interface CodeMapper {
    List<CodeVO> selectCodeList();
    CodeVO selectCode(String codeId);
    void insertCode(CodeVO code);
    void updateCode(CodeVO code);
    void deleteCode(String codeId);
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
    
    boolean existsByGroupCodeAndCode(@Param("groupCode") String groupCode, @Param("code") String code);
}
```

## Board Mapper
### 변경 전
```java
@Mapper
public interface BoardMapper {
    List<BoardVO> selectBoardList(BoardSearchVO search);
    BoardVO selectBoard(Long boardId);
    void insertBoard(BoardVO board);
    void updateBoard(BoardVO board);
    void deleteBoard(Long boardId);
}
```

### 변경 후
```java
@Mapper
public interface BoardMapper {
    List<BoardVO> selectBoardList(@Param("search") BoardSearchVO search, @Param("rowBounds") RowBounds rowBounds);
    
    int selectBoardListCount(@Param("search") BoardSearchVO search);
    
    BoardVO selectBoard(Long boardId);
    
    BoardVO selectBoardWithFiles(Long boardId);
    
    List<BoardVO> selectBoardsByCategory(@Param("category") String category, @Param("rowBounds") RowBounds rowBounds);
    
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
@Mapper
public interface MemberMapper {
    MemberVO selectMemberById(String id);
    void insertMember(MemberVO member);
    void updateMember(MemberVO member);
    void deleteMember(String id);
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
    
    int updateLastLoginAt(@Param("id") String id, @Param("lastLoginAt") LocalDateTime lastLoginAt);
    
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