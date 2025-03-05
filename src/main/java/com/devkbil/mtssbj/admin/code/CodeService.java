package com.devkbil.mtssbj.admin.code;

import com.devkbil.mtssbj.search.SearchVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 이 서비스 클래스는 코드를 관리하고 엑셀 파일 작업을 수행하는 메서드를 제공합니다.
 * 생성자는 Lombok의 @RequiredArgsConstructor를 사용하여 자동 생성됩니다.
 * 내부적으로 SqlSessionTemplate을 의존성 주입받아 사용합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 엑셀 파일 처리 메서드
     *
     * @param inputStream 업로드된 엑셀 파일의 InputStream
     * @return 처리 결과 메시지
     */
    public String processExcelFile(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // 유효성 체크: 빈 시트나 헤더 미존재 시 오류 반환
            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) {
                return "업로드된 엑셀 파일에 헤더 또는 데이터가 없습니다.";
            }

            // 1. 첫 번째 행을 헤더로 간주하고 유효성 확인
            Row headerRow = sheet.getRow(0); // 첫 번째 행
            if (!isValidHeaderRow(headerRow)) {
                return "엑셀 파일의 헤더가 예상값(CLASSNO, CODECD, CODENM)과 일치하지 않습니다.";
            }

            // 2. 엑셀 데이터를 읽어 Java 객체로 변환 (헤더 제외)
            List<CodeVO> excelData = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 1부터 시작 (헤더 제외)
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                CodeVO codeVO = new CodeVO();
                codeVO.setClassno(getCellValue(row.getCell(0)));
                codeVO.setCodecd(getCellValue(row.getCell(1)));
                codeVO.setCodenm(getCellValue(row.getCell(2)));
                excelData.add(codeVO);
            }

            // 3. 기존 데이터 조회
            Map<String, CodeVO> existingData = new HashMap<>();
            for (CodeVO code : selectCodeListAll()) {
                String key = code.getClassno() + "-" + code.getCodecd();
                existingData.put(key, code);
            }

            // 4. 데이터 비교 및 처리 (업데이트/삽입 구분)
            for (CodeVO uploadedCode : excelData) {
                String key = uploadedCode.getClassno() + "-" + uploadedCode.getCodecd();
                if (existingData.containsKey(key)) {
                    // 기존 데이터가 있으면 업데이트 처리
                    insertCode("U", uploadedCode);
                } else {
                    // 기존 데이터가 없으면 삽입 처리
                    insertCode("C", uploadedCode);
                }
            }

            return "엑셀 데이터 처리가 성공적으로 완료되었습니다.";
        } catch (Exception e) {
            log.error("엑셀 파일 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("엑셀 데이터 처리 중 오류 발생", e);
        }
    }

    /**
     * 헤더 행 유효성 검사
     *
     * @param headerRow 엑셀 헤더 행
     * @return 헤더 유효 여부
     */
    public boolean isValidHeaderRow(Row headerRow) {
        if (headerRow == null) {
            return false;
        }

        String header1 = getCellValue(headerRow.getCell(0)).trim();
        String header2 = getCellValue(headerRow.getCell(1)).trim();
        String header3 = getCellValue(headerRow.getCell(2)).trim();

        // 예상되는 헤더 값 (CLASSNO, CODECD, CODENM)
        return "CLASSNO".equalsIgnoreCase(header1) && "CODECD".equalsIgnoreCase(header2) && "CODENM".equalsIgnoreCase(header3);
    }

    /**
     * 셀 데이터 값을 String으로 추출
     *
     * @param cell 셀 객체
     * @return 셀의 문자열 값
     */
    public String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int)cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }

    /**
     * 공통 코드 개수 조회
     * - 특정 검색 조건에 따라 코드의 총 개수를 반환합니다.
     *
     * @param param 검색 조건 객체 (SearchVO)
     * @return Integer 공통 코드 개수
     */
    public int selectCodeCount(SearchVO param) {
        return sqlSession.selectOne("selectCodeCount", param);
    }

    /**
     * 데이터베이스에서 모든 CodeVO 객체를 조회합니다.
     *
     * @return 모든 CodeVO 객체가 포함된 리스트
     */
    public List<CodeVO> selectCodeListAll() {
        return sqlSession.selectList("selectCodeListAll");
    }

    /**
     * 공통 코드 리스트 조회
     * - 특정 검색 조건에 따라 공통 코드 리스트를 반환합니다.
     *
     * @param param 검색 조건 객체 (SearchVO)
     * @return List 공통 코드 리스트
     */
    public List<?> selectCodeList(SearchVO param) {
        return sqlSession.selectList("selectCodeList", param);
    }

    /**
     * 지정된 작업 유형에 따라 데이터베이스에 코드를 삽입 또는 업데이트합니다.
     * 작업 유형이 "U"인 경우 기존 코드를 업데이트합니다. 
     * 그렇지 않으면 중복 여부를 확인한 후 새 코드를 삽입합니다.
     *
     * @param codeFormType 수행할 작업 유형. "U"는 업데이트를 나타내며,
     *                     다른 값은 삽입 작업을 나타냅니다.
     * @param param 삽입하거나 업데이트할 코드 세부 정보가 포함된 CodeVO 객체.
     *              이 객체는 null이 될 수 없습니다.
     * @return 데이터베이스에서 영향을 받은 행 수를 반환합니다.
     * @throws IllegalArgumentException param 객체가 null이거나 삽입 시 코드가 이미 존재할 경우
     * @throws RuntimeException 예상치 못한 오류가 발생한 경우
     */
    @Transactional
    public int insertCode(String codeFormType, CodeVO param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new IllegalArgumentException("CodeVO 객체는 null일 수 없습니다.");
        }

        int affectedRows;

        try {
            if ("U".equals(codeFormType)) {
                // 코드 업데이트
                affectedRows = sqlSession.update("updateCode", param);
            } else {
                // 코드 중복 확인 후 코드 삽입
                if (sqlSession.selectOne("selectCodeOne", param) != null) {
                    throw new IllegalArgumentException("이미 존재하는 코드입니다.");
                }
                affectedRows = sqlSession.insert("insertCode", param);
            }
            return affectedRows;
        } catch (Exception e) {
            log.error("코드 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("코드 추가 작업 중 오류 발생", e);
        }
    }

    /**
     * 공통 코드 단건 조회
     * - 특정 코드의 상세 정보를 조회합니다.
     *
     * @param param 조회할 코드 정보 (CodeVO)
     * @return CodeVO 조회된 공통 코드 정보
     */
    public CodeVO selectCodeOne(CodeVO param) {
        return sqlSession.selectOne("selectCodeOne", param);
    }

    /**
     * 주어진 매개변수를 기준으로 데이터베이스에서 단일 코드 항목을 삭제합니다.
     *
     * @param param 삭제할 코드의 세부 정보를 포함하는 CodeVO 객체
     * @return 삭제 작업으로 인해 영향을 받은 행 수를 나타내는 정수
     */
    @Transactional
    public int deleteCodeOne(CodeVO param) {
        return sqlSession.delete("deleteCodeOne", param);
    }
}