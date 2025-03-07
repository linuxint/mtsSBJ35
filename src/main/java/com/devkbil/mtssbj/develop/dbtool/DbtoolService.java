package com.devkbil.mtssbj.develop.dbtool;

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
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 데이터베이스와의 상호작용을 처리하고 특정 데이터 형식 변환 및 유효성 검사를 수행하는 서비스 클래스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DbtoolService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 지정된 테이블에서 데이터를 조회하고 필요한 컬럼 데이터 타입을 처리합니다.
     * NUMBER, DATE, TIMESTAMP, CLOB, BLOB와 같은 특정 데이터 타입을 변환하거나
     * 적절한 인코딩을 수행합니다.
     *
     * @param tableName 데이터를 조회할 테이블 이름
     * @return 테이블의 각 행을 나타내는 Map의 리스트. 각 Map은 컬럼명을 키로 하고
     *         해당 처리된 값을 값으로 가집니다.
     * @throws IllegalArgumentException 테이블의 컬럼 정보를 찾을 수 없는 경우
     */
    public List<Map<String, Object>> getTableData(String tableName) {
        // 1. 테이블의 컬럼 순서와 데이터 타입 정보 가져오기
        List<Map<String, String>> columnsInfo = getColumnsForTable(tableName);
        if (columnsInfo.isEmpty()) {
            throw new IllegalArgumentException("테이블 " + tableName + "의 컬럼 정보를 찾을 수 없습니다.");
        }

        // 2. 컬럼 처리: SQL 변환
        List<String> processedColumns = columnsInfo.stream()
                .map(colInfo -> {
                    String columnName = colInfo.get("name");
                    String dataType = colInfo.get("type");

                    // 2-1. 숫자 타입 처리 (NUMBER)
                    if (dataType.startsWith("NUMBER")) {
//                        return "COALESCE(TO_CHAR(" + columnName + "), '') AS " + columnName;
                        return columnName;
                    }

                    // 2-2. TIMESTAMP 및 DATE 타입 처리
                    if ("DATE".equalsIgnoreCase(dataType) || dataType.startsWith("TIMESTAMP")) {
//                        return "COALESCE(TO_CHAR(" + columnName + ", 'YYYY-MM-DD HH24:MI:SS.FF6'), '') AS " + columnName;
                        return columnName;
                    }

                    // 2-3. CLOB 처리
                    if ("CLOB".equalsIgnoreCase(dataType)) {
//                        return "COALESCE(DBMS_LOB.SUBSTR(" + columnName + ", 4000), '') AS " + columnName;
                        return columnName;
                    }

                    // 2-4. BLOB 처리
                    if ("BLOB".equalsIgnoreCase(dataType)) {
                        return columnName; // BLOB은 후에 Base64 변환
                    }

                    // 2-5. 문자열 처리
                    if (dataType.startsWith("VARCHAR") || dataType.startsWith("CHAR")) {
//                        return "COALESCE(" + columnName + ", '') AS " + columnName;
                        return columnName;
                    }

                    // 2-6. 기타 처리
//                    return "COALESCE(" + columnName + ", '') AS " + columnName;
                    return columnName;
                })
                .collect(Collectors.toList());

        // 3. MyBatis 쿼리 파라미터 생성
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tableName", tableName);
        paramMap.put("columns", processedColumns);

        // 4. 쿼리 실행 및 결과 조회
        List<Map<String, Object>> tableData = sqlSession.selectList("dbtool.selectTableData", paramMap);

        // 5. BLOB 데이터 후처리 (Base64 인코딩)
        List<Map<String, Object>> fullyProcessedData = processBlobData(tableData, columnsInfo);

        return fullyProcessedData;
    }

    /**
     * 테이블의 BLOB 데이터를 Base64로 인코딩된 문자열로 변환
     *
     * @param tableData 테이블의 각 행을 나타내는 맵의 리스트
     *                  키는 컬럼명이고 값은 해당 데이터입니다.
     * @param columnsInfo 테이블 컬럼의 메타데이터 정보를 포함하는 맵의 리스트
     *                    각 맵은 컬럼명과 데이터타입과 같은 상세 정보를 포함합니다.
     * @return BLOB 타입 컬럼은 Base64 인코딩된 문자열로 변환되고
     *         나머지 컬럼은 변경없이 유지된 맵의 리스트
     */
    public List<Map<String, Object>> processBlobData(List<Map<String, Object>> tableData, List<Map<String, String>> columnsInfo) {
        return tableData.stream().map(row -> {
            Map<String, Object> processedRow = new HashMap<>();
            row.forEach((columnName, value) -> {
                Optional<Map<String, String>> columnInfo = columnsInfo.stream()
                        .filter(info -> info.get("name").equals(columnName))
                        .findFirst();

                if (columnInfo.isPresent() && "BLOB".equalsIgnoreCase(columnInfo.get().get("type")) && value instanceof Blob) {
                    processedRow.put(columnName, convertBlobToBase64((Blob) value)); // Blob -> Base64 변환
                } else {
                    processedRow.put(columnName, value); // 그대로 유지
                }
            });
            return processedRow;
        }).collect(Collectors.toList());
    }

    /**
     * BLOB 데이터를 Base64 문자열로 변환
     *
     * @param blob BLOB 객체
     * @return Base64로 인코딩된 문자열
     */
    private String convertBlobToBase64(Blob blob) {
        try {
            if (blob == null || blob.length() == 0) {
                return "";
            }

            // BLOB 데이터를 바이트 배열로 변환
            byte[] blobBytes = blob.getBytes(1, (int) blob.length());

            // Base64로 인코딩 후 반환
            return Base64.getEncoder().encodeToString(blobBytes);
        } catch (Exception e) {
            log.error("Failed to convert BLOB to Base64: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * 주어진 테이블의 컬럼 목록과 데이터 타입을 데이터베이스 메타데이터에서 조회합니다.
     *
     * @param tableName 컬럼 정보를 조회할 테이블명
     * @return 각 컬럼의 이름과 데이터 타입 정보를 담은 Map 리스트
     */
    private List<Map<String, String>> getColumnsForTable(String tableName) {
        // 데이터베이스 메타데이터에서 컬럼 목록과 데이터 타입 가져오기
        List<Map<String, String>> columns = new ArrayList<>();
        try (Connection connection = sqlSession.getSqlSessionFactory().openSession().getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getColumns(null, null, tableName.toUpperCase(), null);
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("TYPE_NAME"); // 데이터 타입 가져오기
                Map<String, String> columnInfo = new HashMap<>();
                columnInfo.put("name", columnName);
                columnInfo.put("type", dataType);
                columns.add(columnInfo);
            }
        } catch (SQLException e) {
            log.error("Error fetching columns for table {}: {}", tableName, e.getMessage(), e);
        }
        return columns;
    }

    /**
     * 동적 엑셀 파일을 처리하고 파일 내용을 기반으로 데이터 유효성 검사 및
     * 데이터베이스 작업을 수행합니다.
     * <p>
     * 이 메서드는 엑셀 파일을 읽어 테이블명, 키 컬럼, 컬럼명 및 데이터 행을 추출하고,
     * 데이터베이스 테이블과 대조하여 검증한 후 데이터를 삽입하거나 갱신합니다.
     *
     * @param inputStream 처리할 엑셀 파일의 입력 스트림
     * @return 처리 성공 여부나 특정 오류 메시지를 포함한 처리 결과 메시지
     */
    public String processDynamicExcelFile(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // 1. 테이블 이름 추출 (첫 번째 줄)
            String tableName = getCellValue(sheet.getRow(0).getCell(0)).trim();
            if (ObjectUtils.isEmpty(tableName)) {
                return "테이블 이름이 없습니다.";
            }

            // 2. 기준 키 컬럼 추출 (두 번째 줄)
            Row keyRow = sheet.getRow(1);
            List<String> keyColumns = new ArrayList<>();
            for (Cell cell : keyRow) {
                String keyColumn = getCellValue(cell).trim();
                if (!ObjectUtils.isEmpty(keyColumn)) {
                    keyColumns.add(keyColumn);
                }
            }
            if (keyColumns.isEmpty()) {
                return "기준 키 컬럼이 없습니다.";
            }

            // 3. 컬럼 이름(헤더) 추출 (세 번째 줄)
            Row headerRow = sheet.getRow(2);
            List<String> columnNames = new ArrayList<>();
            for (Cell cell : headerRow) {
                String columnName = getCellValue(cell).trim();
                if (!ObjectUtils.isEmpty(columnName)) {
                    columnNames.add(columnName);
                }
            }
            if (columnNames.isEmpty()) {
                return "컬럼 정보가 없습니다.";
            }

            // 4. 데이터 읽기 (네 번째 줄부터)
            List<Map<String, Object>> rowDataList = new ArrayList<>();
            for (int i = 3; i <= sheet.getLastRowNum(); i++) { // 데이터 시작
                Row dataRow = sheet.getRow(i);
                if (dataRow == null) {
                    continue;
                }

                Map<String, Object> rowData = new HashMap<>();
                for (int j = 0; j < columnNames.size(); j++) {
                    rowData.put(columnNames.get(j), getCellValue(dataRow.getCell(j)));
                }
                rowDataList.add(rowData);
            }

            // 5. 테이블 및 컬럼 검증
            DbtoolVO param = new DbtoolVO();
            param.setTableName(tableName);

            List<DbtoolVO> dbColumns = sqlSession.selectList("dbtool.selectTableLayout", param);
            List<String> dbColumnNames = dbColumns.stream().map(DbtoolVO::getColumnName).collect(Collectors.toList());

            if (!dbColumnNames.containsAll(columnNames)) {
                return "엑셀 컬럼 정보가 데이터베이스 테이블과 일치하지 않습니다.";
            }

            // 6. 데이터 삽입 또는 업데이트 처리
            for (Map<String, Object> rowData : rowDataList) {
                compareAndSaveData(tableName, keyColumns, columnNames, rowData);
            }

            return "엑셀 데이터 처리가 성공적으로 완료되었습니다.";
        } catch (Exception e) {
            log.error("엑셀 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("엑셀 데이터 처리 중 오류 발생", e);
        }
    }

    /**
     * 데이터베이스 레코드를 비교 및 저장 처리
     *
     * @param tableName 데이터를 저장할 테이블 이름
     * @param keyColumns 기준 키가 되는 컬럼명 목록
     * @param columns 테이블의 전체 컬럼명 목록
     * @param rowData 저장할 데이터 행의 값
     */
    @Transactional
    public void compareAndSaveData(String tableName, List<String> keyColumns, List<String> columns, Map<String, Object> rowData) {
        // 1. 기준 키 값을 조합하여 Map 생성
        Map<String, Object> keyValues = new HashMap<>();
        for (String keyColumn : keyColumns) {
            keyValues.put(keyColumn, rowData.get(keyColumn));
        }

        // 2. 데이터 존재 여부 확인
        Integer existingDataCount = sqlSession.selectOne("dbtool.checkExistingData", Map.of(
                "tableName", tableName,
                "keyColumns", keyColumns,
                "keyValues", keyValues
        ));
        if (existingDataCount != null && existingDataCount > 0) {
            // 3. 기존 데이터 존재 → 업데이트 (기준 키 컬럼 사용)
            sqlSession.update("dbtool.dynamicUpdate", Map.of(
                    "tableName", tableName,
                    "columns", columns,
                    "data", rowData,
                    "keyColumns", keyColumns
            ));
        } else {
            // 4. 새로운 데이터 → 삽입
            sqlSession.insert("dbtool.dynamicInsert", Map.of(
                    "tableName", tableName,
                    "columns", columns,
                    "data", rowData
            ));
        }
    }

    /**
     * 데이터 삽입 또는 업데이트
     *
     * @param tableName  테이블 이름
     * @param columns    컬럼 리스트
     * @param rowData    행 데이터
     */
    public void insertOrUpdateData(String tableName, List<String> columns, Map<String, Object> rowData) {
        Integer count = sqlSession.selectOne("dbtool.selectRowCount", Collections.singletonMap("tableName", tableName));
        if (count != null && count > 0) {
            sqlSession.update("dbtool.dynamicUpdate", Map.of("tableName", tableName, "columns", columns, "data", rowData));
        } else {
            sqlSession.insert("dbtool.dynamicInsert", Map.of("tableName", tableName, "columns", columns, "data", rowData));
        }
    }

    private String getCellValue(Cell cell) {
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
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 테이블 레이아웃 정보 조회
     * - 사용자가 요청한 테이블과 컬럼 정보를 기반으로 데이터베이스에서 레이아웃을 조회합니다.
     *
     * @param param 테이블 이름, 컬럼 이름 등 조회 조건이 포함된 객체
     * @return 테이블 및 컬럼 레이아웃 정보 리스트
     */
    public List<DbtoolVO> selectTabeLayout(DbtoolVO param) {
        return sqlSession.selectList("selectTableLayout", param);
    }

}