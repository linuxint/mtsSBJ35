package com.devkbil.mtssbj.search;

import com.devkbil.mtssbj.board.BoardReplyVO;
import com.devkbil.mtssbj.board.BoardService;
import com.devkbil.mtssbj.board.BoardVO;
import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.common.LocaleMessage;
import com.devkbil.mtssbj.common.util.FileUtil;
import com.devkbil.mtssbj.common.util.FileVO;
import com.devkbil.mtssbj.common.util.HostUtil;
import com.devkbil.mtssbj.config.EsConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static org.elasticsearch.xcontent.XContentFactory.jsonBuilder;

/**
 * Elasticsearch 색인을 관리하는 컨트롤러 클래스입니다.
 *
 * 이 클래스는 게시판, 댓글, 첨부파일 등 데이터를 Elasticsearch에 색인 작업과 관련된 로직을 포함합니다.
 * 또한, 텍스트 추출 및 색인 파일 관리 등을 수행합니다.
 *
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
@Tag(name = "IndexingController", description = "Elasticsearch 색인 작업을 관리하는 컨트롤러")
public class IndexingController {

    final LocaleMessage localeMessage;
    private final BoardService boardService;

    @Value("${batch.indexing.host}")
    private String indexingHost;

    @Value("${elasticsearch.clustername}")
    private String indexName;

    private String lastFile;

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
     *
     * - 게시판 데이터 색인
     * - 댓글 색인
     * - 첨부파일 색인
     *
     * @throws IOException 색인 작업 중 입출력 문제 발생 시 처리
     */

    @Scheduled(cron = "${batch.indexingFile.cron}")
    @Operation(summary = "색인 작업 실행", description = "게시판, 댓글, 첨부파일 등 데이터를 순차적으로 Elasticsearch에 색인")
    public void indexingFile() throws IOException {

        EsConfig esConfig = new EsConfig();
        try (RestHighLevelClient client = esConfig.client()) {
            // Elasticsearch 실행 여부 확인
            if (!esConfig.isElasticsearchRunning(client)) {
                logBatch.info("Elasticsearch 서버가 실행되고 있지 않아 색인 작업을 중단합니다.");
                return;
            }
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
        String filePath = System.getProperty("user.dir") + "/fileupload/"; //localeMessage.getMessage("info.filePath") + "/";  //  첨부 파일 경로

        // ---------------------------- elasticsearch connection --------------------------------
        RestHighLevelClient client = esConfig.client();

        // ---------------------------- 게시판 변경글 --------------------------------
        String brdnoUpdate = getLastValue("brd_update"); // 변경색인 인덱스
        String brdno = getLastValue("brd"); // 이전 색인시 마지막 일자

        List<BoardVO> boardlist;

        if (!brdnoUpdate.equals(brdno)) {
            boardlist = (List<BoardVO>) boardService.selectBoards4Indexing(brdnoUpdate);
            UpdateRequest updateRequest;
            for (BoardVO el : boardlist) {
                brdnoUpdate = el.getBrdno();
                updateRequest = new UpdateRequest()
                        .index(indexName)
                        .id(el.getBrdno())
                        .doc(jsonBuilder()
                                .startObject()
                                .field("bgno", el.getBgno())
                                .field("brdno", brdnoUpdate)
                                .field("brdtitle", el.getBrdtitle())
                                .field("brdmemo", el.getBrdmemo())
                                .field("brdwriter", el.getUsernm())
                                .field("userno", el.getUserno())
                                .field("regdate", el.getRegdate())
                                .field("regtime", el.getRegtime())
                                .field("brdhit", el.getBrdhit())
                                .endObject());

                try {
                    client.update(updateRequest, RequestOptions.DEFAULT);
                } catch (IOException | ElasticsearchStatusException e) {
                    logBatch.error("indexRequest : " + e.getMessage());
                }
            }

            if (!boardlist.isEmpty()) {
                // 마지막 색인 이후의 댓글/ 첨부파일 중에서 게시글이 색인 된 것만 색인 해야 함. SQL문에서 field1참조  => logtash를 쓰지 않고 개발한 이유
                writeLastValue("brd_update", brdnoUpdate);
            }
            logBatch.info("board indexed update : " + boardlist.size());
        }

        // ---------------------------- 게시판 신규글 --------------------------------
        boardlist = (List<BoardVO>) boardService.selectBoards4Indexing(brdno);
        for (BoardVO el : boardlist) {
            brdno = el.getBrdno();
            IndexRequest indexRequest = new IndexRequest(indexName)
                    .id(el.getBrdno())
                    .source("bgno", el.getBgno(),
                            "brdno", brdno,
                            "brdtitle", el.getBrdtitle(),
                            "brdmemo", el.getBrdmemo(),
                            "brdwriter", el.getUsernm(),
                            "userno", el.getUserno(),
                            "regdate", el.getRegdate(),
                            "regtime", el.getRegtime(),
                            "brdhit", el.getBrdhit()
                    );

            try {
                client.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException | ElasticsearchStatusException e) {
                logBatch.error("indexRequest : " + e.getMessage());
            }
        }

        if (!boardlist.isEmpty()) {
            writeLastValue("brd", brdno); // 마지막 색인 이후의 댓글/ 첨부파일 중에서 게시글이 색인 된 것만 색인 해야 함. SQL문에서 field1참조  => logtash를 쓰지 않고 개발한 이유
        }

        logBatch.info("board indexed : " + boardlist.size());

        // ---------------------------- 댓글 --------------------------------
        ExtFieldVO lastVO = new ExtFieldVO(); // 게시판, 댓글, 파일의 마지막 색인 값
        lastVO.setField1(brdno);
        lastVO.setField2(getLastValue("reply"));

        List<BoardReplyVO> replylist = (List<BoardReplyVO>) boardService.selectBoardReply4Indexing(lastVO);

        String reno = "";
        Map<String, Object> replyMap = new ConcurrentHashMap<String, Object>();
        for (BoardReplyVO el : replylist) {
            reno = el.getReno();
            replyMap.put("reno", reno);
            replyMap.put("regdate", el.getRegdate());
            replyMap.put("rememo", el.getRememo());
            replyMap.put("usernm", el.getUsernm());
            replyMap.put("userno", el.getUserno());

            Map<String, Object> singletonMap = Collections.singletonMap("reply", replyMap);

            UpdateRequest updateRequest = new UpdateRequest()
                    .index(indexName)
                    .id(el.getBrdno())
                    .script(new Script(ScriptType.INLINE, "painless",
                            "if (ctx._source.brdreply == null) {ctx._source.brdreply=[]} ctx._source.brdreply.add(params.reply)",
                            singletonMap));

            try {
                client.update(updateRequest, RequestOptions.DEFAULT);
            } catch (IOException | ElasticsearchStatusException e) {
                logBatch.error("updateCommit : " + e.getMessage());
            }
        }

        if (!replylist.isEmpty()) {
            writeLastValue("reply", reno); // 마지막 색인  정보 저장 - 댓글
        }

        logBatch.info("board reply indexed : " + replylist.size());

        // ---------------------------- 첨부파일 --------------------------------
        lastVO.setField2(getLastValue("file"));
        List<FileVO> filelist = (List<FileVO>) boardService.selectBoardFiles4Indexing(lastVO);

        String fileno = "";
        Map<String, Object> fileMap = new ConcurrentHashMap<String, Object>();
        for (FileVO el : filelist) {
            if (!fileExtention.contains(FileUtil.getFileExtension(el.getFilename()))) {
                continue; // 지정된 파일 형식만 색인
            }
            fileno = el.getFileno().toString();
            fileMap.put("fileno", fileno);

            String realPath = FileUtil.getRealPath(filePath, el.getRealname());
            if (!FileUtil.fileExist(realPath)) {
                isIndexing = false;
                return;
            }

            realPath += el.getRealname();
            fileMap.put("filememo", convert(realPath));

            Map<String, Object> singletonMap = Collections.singletonMap("file", fileMap);

            UpdateRequest updateRequest = new UpdateRequest()
                    .index(indexName)
                    .id(el.getParentPK())
                    .script(new Script(ScriptType.INLINE, "painless",
                            "if (ctx._source.brdfiles == null) {ctx._source.brdfiles=[]} ctx._source.brdfiles.add(params.file)",
                            singletonMap));
            try {
                client.update(updateRequest, RequestOptions.DEFAULT);
            } catch (IOException | ElasticsearchStatusException e) {
                logBatch.error("updateCommit : " + e.getMessage());
            }
        }
        if (!filelist.isEmpty()) {
            writeLastValue("file", fileno); // 마지막 색인  정보 저장 - 댓글
        }

        logBatch.info("board files indexed : " + filelist.size());

        try {
            client.close();
        } catch (IOException e) {
            logBatch.error(e.getMessage());
        }
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

    // ---------------------------------------------------------------------------

    /**
     * 마지막 색인 정보를 파일에서 읽어옵니다.
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

    // ---------------------------------------------------------------------------
    /*
    // 공통 환경으로 변경
    public RestHighLevelClient createConnection() {
        final CredentialsProvider credentialProvider = new BasicCredentialsProvider();
        credentialProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "manager"));

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                ).setHttpClientConfigCallback(
                        httpAsyncClientBuilder -> {
                            HttpAsyncClientBuilder httpAsyncClientBuilder1 = httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialProvider);
                            return httpAsyncClientBuilder1;
                        }
                )
        );
    }
     */
    // ---------------------------------------------------------------------------
}
