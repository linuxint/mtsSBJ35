package com.devkbil.mtssbj.search;

import com.devkbil.mtssbj.board.BoardReplyVO;
import com.devkbil.mtssbj.board.BoardService;
import com.devkbil.mtssbj.board.BoardVO;
import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.common.util.FileOperation;
import com.devkbil.mtssbj.common.util.FileUtil;
import com.devkbil.mtssbj.common.util.FileVO;
import com.devkbil.mtssbj.common.util.HostUtil;
import com.devkbil.mtssbj.config.EsConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.RequiredArgsConstructor;

/**
 * Elasticsearch 색인을 관리하는 컨트롤러 클래스입니다.
 * <p>
 * 이 클래스는 게시판, 댓글, 첨부파일 등 데이터를 Elasticsearch에 색인 작업과 관련된 로직을 포함합니다.
 * 또한, 텍스트 추출 및 색인 파일 관리 등을 수행합니다.
 * <p>
 * 색인
 * 1. 게시판
 * 2. 댓글
 * 3. 첨부파일
 */
@Controller
@EnableAsync
@EnableScheduling
@Configuration
@RequiredArgsConstructor
@Slf4j
@Tag(name = "IndexingController", description = "Elasticsearch 색인 작업을 관리하는 컨트롤러")
public class IndexingController {

    private final BoardService boardService;
    private final ElasticsearchOperations elasticsearchOperations;
    private final EsConfig esConfig;

    @Value("${batch.indexing.host}")
    private String indexingHost;

    @Value("${elasticsearch.clustername}")
    private String indexName;

    private String lastFile;

    /**
     * Elasticsearch 구성에서 인덱스 이름을 설정합니다.
     *
     * @param indexName 설정할 인덱스 이름. 이 이름은 마지막으로 처리된 정보를 저장하는
     *                  파일 경로를 결정합니다.
     */
    @Value("${elasticsearch.clustername}")
    public void setIndexName(String indexName) {
        this.indexName = indexName;
        this.lastFile = System.getProperty("user.dir") + "/elasticsearch/" + indexName + ".last";
    }

    @Value("${batch.indexing.file_ext}")
    private String fileExtention;

    private final Logger logBatch = LoggerFactory.getLogger("BATCH");

    private boolean isIndexing = false;
    private Properties lastFileProps = null;            // 마지막 색인값 보관

    /**
     * Elasticsearch 색인 작업을 실행합니다.
     * <p>
     * - 게시판 데이터 색인
     * - 댓글 색인
     * - 첨부파일 색인
     *
     * @throws IOException 색인 작업 중 입출력 문제 발생 시 처리
     */
    @Scheduled(cron = "${batch.indexingFile.cron}")
    @Operation(summary = "색인 작업 실행", description = "게시판, 댓글, 첨부파일 등 데이터를 순차적으로 Elasticsearch에 색인")
    public void indexingFile() throws IOException {
        // Elasticsearch 실행 여부 확인
        if (!esConfig.isElasticsearchRunning()) {
            log.info("Elasticsearch 서버가 실행되고 있지 않아 색인 작업을 중단합니다.");
            return;
        }

        // indexing host check
        if (!HostUtil.hostCheck(indexingHost)) {
            //return;
        }

        if (isIndexing) {
            return;
        }
        isIndexing = true;
        loadLastValue();
        // 첨부 파일 경로
        String filePath = System.getProperty("user.dir") + "/fileupload/";

        // ---------------------------- 게시판 변경글 --------------------------------
        String brdnoUpdate = getLastValue("brd_update"); // 변경색인 인덱스
        String brdno = getLastValue("brd"); // 이전 색인시 마지막 일자

        List<BoardVO> boardlist;

        if (!brdnoUpdate.equals(brdno)) {
            boardlist = (List<BoardVO>)boardService.selectBoards4Indexing(brdnoUpdate);

            // 배치 처리를 위한 리스트
            List<IndexQuery> indexQueries = new ArrayList<>();

            for (BoardVO el : boardlist) {
                brdnoUpdate = el.getBrdno();

                // 문서 업데이트를 위한 맵 생성
                Map<String, Object> document = new HashMap<>();
                document.put("bgno", el.getBgno());
                document.put("brdno", brdnoUpdate);
                document.put("brdtitle", el.getBrdtitle());
                document.put("brdmemo", el.getBrdmemo());
                document.put("brdwriter", el.getUsernm());
                document.put("userno", el.getUserno());
                document.put("regdate", el.getRegdate());
                document.put("regtime", el.getRegtime());
                document.put("brdhit", el.getBrdhit());

                // 인덱스 쿼리 생성 및 리스트에 추가
                IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(el.getBrdno())
                    .withObject(document)
                    .build();

                indexQueries.add(indexQuery);
            }

            // 배치 처리로 한 번에 색인
            if (!indexQueries.isEmpty()) {
                try {
                    elasticsearchOperations.bulkIndex(indexQueries, IndexCoordinates.of(indexName));
                } catch (Exception e) {
                    log.error("Bulk update error: " + e.getMessage());
                }
            }

            if (!boardlist.isEmpty()) {
                // 마지막 색인 이후의 댓글/ 첨부파일 중에서 게시글이 색인 된 것만 색인 해야 함. SQL문에서 field1참조  => logtash를 쓰지 않고 개발한 이유
                writeLastValue("brd_update", brdnoUpdate);
            }
            log.info("board indexed update : " + boardlist.size());
        }

        // ---------------------------- 게시판 신규글 --------------------------------
        boardlist = (List<BoardVO>)boardService.selectBoards4Indexing(brdno);

        // 배치 처리를 위한 리스트
        List<IndexQuery> indexQueries = new ArrayList<>();

        for (BoardVO el : boardlist) {
            brdno = el.getBrdno();

            // 문서 색인을 위한 맵 생성
            Map<String, Object> document = new HashMap<>();
            document.put("bgno", el.getBgno());
            document.put("brdno", brdno);
            document.put("brdtitle", el.getBrdtitle());
            document.put("brdmemo", el.getBrdmemo());
            document.put("brdwriter", el.getUsernm());
            document.put("userno", el.getUserno());
            document.put("regdate", el.getRegdate());
            document.put("regtime", el.getRegtime());
            document.put("brdhit", el.getBrdhit());

            // 인덱스 쿼리 생성 및 리스트에 추가
            IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(el.getBrdno())
                .withObject(document)
                .build();

            indexQueries.add(indexQuery);
        }

        // 배치 처리로 한 번에 색인
        if (!indexQueries.isEmpty()) {
            try {
                elasticsearchOperations.bulkIndex(indexQueries, IndexCoordinates.of(indexName));
            } catch (Exception e) {
                log.error("Bulk indexing error: " + e.getMessage());
            }
        }

        if (!boardlist.isEmpty()) {
            writeLastValue("brd", brdno);
        }

        log.info("board indexed : " + boardlist.size());

        // ---------------------------- 댓글 --------------------------------
        ExtFieldVO lastVO = new ExtFieldVO(); // 게시판, 댓글, 파일의 마지막 색인 값
        lastVO.setField1(brdno);
        lastVO.setField2(getLastValue("reply"));

        List<BoardReplyVO> replylist = (List<BoardReplyVO>)boardService.selectBoardReply4Indexing(lastVO);

        String reno = "";

        // 게시글별로 댓글을 그룹화
        Map<String, List<Map<String, Object>>> replyGroups = new HashMap<>();

        // 댓글 데이터 준비 및 그룹화
        for (BoardReplyVO el : replylist) {
            reno = el.getReno();

            Map<String, Object> replyMap = new HashMap<>();
            replyMap.put("reno", reno);
            replyMap.put("regdate", el.getRegdate());
            replyMap.put("rememo", el.getRememo());
            replyMap.put("usernm", el.getUsernm());
            replyMap.put("userno", el.getUserno());

            // 게시글 ID별로 댓글 그룹화
            if (!replyGroups.containsKey(el.getBrdno())) {
                replyGroups.put(el.getBrdno(), new ArrayList<>());
            }
            replyGroups.get(el.getBrdno()).add(replyMap);
        }

        // 배치 처리를 위한 리스트
        List<IndexQuery> replyIndexQueries = new ArrayList<>();

        // 게시글별로 처리
        for (Map.Entry<String, List<Map<String, Object>>> entry : replyGroups.entrySet()) {
            String boardId = entry.getKey();
            List<Map<String, Object>> replies = entry.getValue();

            try {
                // 기존 문서 가져오기
                Map<String, Object> document = elasticsearchOperations.get(boardId, Map.class, IndexCoordinates.of(indexName));

                if (document != null) {
                    // brdreply 배열이 없으면 생성
                    if (!document.containsKey("brdreply")) {
                        document.put("brdreply", new ArrayList<>());
                    }

                    // 기존 배열에 새 댓글들 추가
                    List<Map<String, Object>> existingReplies = (List<Map<String, Object>>)document.get("brdreply");
                    existingReplies.addAll(replies);

                    // 인덱스 쿼리 생성 및 리스트에 추가
                    IndexQuery indexQuery = new IndexQueryBuilder()
                        .withId(boardId)
                        .withObject(document)
                        .build();

                    replyIndexQueries.add(indexQuery);
                }
            } catch (Exception e) {
                log.error("Reply preparation error for board " + boardId + ": " + e.getMessage());
            }
        }

        // 배치 처리로 한 번에 색인
        if (!replyIndexQueries.isEmpty()) {
            try {
                elasticsearchOperations.bulkIndex(replyIndexQueries, IndexCoordinates.of(indexName));
            } catch (Exception e) {
                log.error("Bulk reply indexing error: " + e.getMessage());
            }
        }

        if (!replylist.isEmpty()) {
            writeLastValue("reply", reno); // 마지막 색인 정보 저장 - 댓글
        }

        logBatch.info("board reply indexed : " + replylist.size());

        // ---------------------------- 첨부파일 --------------------------------
        lastVO.setField2(getLastValue("file"));
        List<FileVO> filelist = (List<FileVO>)boardService.selectBoardFiles4Indexing(lastVO);

        String fileno = "";

        // 게시글별로 파일을 그룹화
        Map<String, List<Map<String, Object>>> fileGroups = new HashMap<>();

        // 파일 데이터 준비 및 그룹화
        for (FileVO el : filelist) {
            if (!fileExtention.contains(FileUtil.getFileExtension(el.getFilename()))) {
                continue; // 지정된 파일 형식만 색인
            }

            fileno = el.getFileno().toString();

            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("fileno", fileno);

            String realPath = FileUtil.getRealPath(filePath, el.getRealname());
            if (!FileOperation.fileExist(realPath)) {
                isIndexing = false;
                return;
            }

            realPath += el.getRealname();

            // 텍스트 추출 작업 수행
            String extractedText = convert(realPath);
            fileMap.put("filememo", extractedText);

            // 게시글 ID별로 파일 그룹화
            if (!fileGroups.containsKey(el.getParentPK())) {
                fileGroups.put(el.getParentPK(), new ArrayList<>());
            }
            fileGroups.get(el.getParentPK()).add(fileMap);
        }

        // 배치 처리를 위한 리스트
        List<IndexQuery> fileIndexQueries = new ArrayList<>();

        // 게시글별로 처리
        for (Map.Entry<String, List<Map<String, Object>>> entry : fileGroups.entrySet()) {
            String parentId = entry.getKey();
            List<Map<String, Object>> files = entry.getValue();

            try {
                // 기존 문서 가져오기
                Map<String, Object> document = elasticsearchOperations.get(parentId, Map.class, IndexCoordinates.of(indexName));

                if (document != null) {
                    // brdfiles 배열이 없으면 생성
                    if (!document.containsKey("brdfiles")) {
                        document.put("brdfiles", new ArrayList<>());
                    }

                    // 기존 배열에 새 파일들 추가
                    List<Map<String, Object>> existingFiles = (List<Map<String, Object>>)document.get("brdfiles");
                    existingFiles.addAll(files);

                    // 인덱스 쿼리 생성 및 리스트에 추가
                    IndexQuery indexQuery = new IndexQueryBuilder()
                        .withId(parentId)
                        .withObject(document)
                        .build();

                    fileIndexQueries.add(indexQuery);
                }
            } catch (Exception e) {
                logBatch.error("File preparation error for parent " + parentId + ": " + e.getMessage());
            }
        }

        // 배치 처리로 한 번에 색인
        if (!fileIndexQueries.isEmpty()) {
            try {
                elasticsearchOperations.bulkIndex(fileIndexQueries, IndexCoordinates.of(indexName));
            } catch (Exception e) {
                logBatch.error("Bulk file indexing error: " + e.getMessage());
            }
        }

        if (!filelist.isEmpty()) {
            writeLastValue("file", fileno); // 마지막 색인 정보 저장 - 파일
        }

        logBatch.info("board files indexed : " + filelist.size());

        isIndexing = false;
    }

    /**
     * Tika를 사용하여 파일에서 텍스트를 추출합니다.
     *
     * @param filename 텍스트를 추출하려는 파일 경로
     * @return 추출한 텍스트 내용
     */
    @Operation(summary = "파일 텍스트 추출", description = "Tika 라이브러리를 통해 파일에서 텍스트를 추출")
    private String extractTextFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            logBatch.error("file not exists : " + filename);
            return "";
        }
        String text = "";
        Tika tika = new Tika();
        try {
            text = tika.parseToString(file);            // binary contents => text contents
        } catch (IOException e) {
            logBatch.error(String.valueOf(e));
        } catch (TikaException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    /**
     * OCR 처리를 통해 이미지 또는 PDF 파일에서 텍스트를 추출합니다.
     *
     * @param fileName OCR 작업을 수행할 파일 경로
     * @return 추출된 텍스트 내용
     */
    @Operation(summary = "OCR 텍스트 추출", description = "Tesseract OCR 기반으로 이미지 및 PDF에서 텍스트 추출")
    public String convert(String fileName) {
        InputStream stream = null;
        String text = null;
        try {
            stream = new FileInputStream(fileName);

            TesseractOCRConfig config = new TesseractOCRConfig();
            config.setSkipOcr(false);
            config.setLanguage("kor");
            ParseContext context = new ParseContext();
            context.set(TesseractOCRConfig.class, config);

            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            parser.parse(stream, handler, metadata, context);
            text = handler.toString();
        } catch (Exception e) {
            logBatch.error(e.getMessage());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    logBatch.error("Error closing stream");
                }
            }
        }
        return text;
    }

    /**
     * 마지막으로 처리한 색인 데이터를 파일에서 읽어옵니다.
     * 이 메서드는 {@code Properties} 객체를 초기화한 뒤,
     * 지정된 파일에서 key-value 데이터를 로드합니다.
     * 처리 중 {@code IOException}이 발생하면 오류 메시지를 기록합니다.
     */
    @Operation(summary = "마지막 색인 정보 읽기", description = "마지막으로 처리한 색인 데이터를 파일에서 읽어옵니다.")
    private void loadLastValue() {
        lastFileProps = new Properties();
        try {
            FileInputStream lastFileIn = new FileInputStream(lastFile);
            lastFileProps.load(lastFileIn);
        } catch (IOException e) {
            logBatch.error(e.getMessage());
        }
    }

    /**
     * 마지막 색인 정보를 파일에 기록합니다.
     *
     * @param key   색인 키 이름
     * @param value 색인 키에 해당하는 값
     */
    @Operation(summary = "마지막 색인 정보 저장", description = "마지막 색인한 데이터 정보를 지정된 파일에 저장")
    private void writeLastValue(String key, String value) {
        lastFileProps.setProperty(key, value);    // 마지막 색인 일자 저장

        try {
            FileOutputStream lastFileOut = new FileOutputStream(lastFile);
            lastFileProps.store(lastFileOut, "Last Indexing");
        } catch (IOException e) {
            logBatch.error("writeLastValue : " + e.getMessage());
        }
    }

    /**
     * 데이터 종류별 마지막 색인 정보를 반환합니다.
     *
     * @param key 색인 키 이름
     * @return 마지막 색인 키에 대응하는 값
     */
    @Operation(summary = "마지막 색인 데이터 반환", description = "데이터 종류에 따른 마지막 색인 값을 반환")
    private String getLastValue(String key) {
        String value = lastFileProps.getProperty(key);
        if (value == null) {
            value = "0";
        }
        return value;
    }

}