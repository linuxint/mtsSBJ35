package com.devkbil.mtssbj.develop.dbtool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.sql.*;
import java.util.Base64;
import java.util.Map;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 데이터베이스 도구 서비스
 * - 데이터베이스 테이블 및 컬럼 레이아웃 정보를 조회합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DbtoolService {

    private final SqlSessionTemplate sqlSession;

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

    // 4. BLOB 데이터를 Base64로 변환
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
     * 동적 테이블 처리를 위한 엑셀 데이터 처리
     *
     * @param inputStream 엑셀 파일 InputStream
     * @return 처리 결과 메시지
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
                if (dataRow == null) continue;

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
