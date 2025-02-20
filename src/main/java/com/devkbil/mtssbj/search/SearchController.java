package com.devkbil.mtssbj.search;

import com.devkbil.mtssbj.common.util.DateUtil;
import com.devkbil.mtssbj.config.EsConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

/**
 * 검색 관련 기능을 관리하는 컨트롤러입니다.
 *
 * 이 컨트롤러는 Elasticsearch를 활용한 검색 작업(게시판, 댓글, 파일 등)을 수행하며,
 * 검색 결과를 Ajax 또는 HTML 형식으로 반환합니다.
 */
@Controller
@Slf4j
@Tag(name = "SearchController", description = "검색 컨트롤러 - 검색 관련 API 제공")
public class SearchController {

    // 기본 페이징 처리 시 한 페이지당 표시할 검색 결과 수
    static final Integer DISPLAY_COUNT = 5;

    // 검색 시 데이터를 가져올 필드 목록
    static final String[] INCLUDE_FIELDS = {"brdno", "userno", "regdate", "regtime", "brdtitle", "brdwriter", "brdmemo", "brdhit"};

    @Value("${elasticsearch.clustername}")
    private String indexName = ""; // Elasticsearch 색인 이름

    /**
     * 검색 페이지를 반환합니다.
     *
     * @param modelMap View에 전달할 모델 데이터.
     * @return 검색 페이지 뷰 이름 ("search/search")
     */
    @GetMapping("/search")
    @Operation(summary = "HTML 검색 페이지 반환", description = "검색 화면을 반환합니다.")
    public String search(ModelMap modelMap) {

        String today = DateUtil.date2Str(DateUtil.getToday()); // 오늘 날짜 계산
        modelMap.addAttribute("today", today); // 모델에 오늘 날짜 데이터 추가
        return "search/search";
    }

    /**
     * Ajax 요청을 통해 검색 작업을 수행하고 결과를 반환합니다.
     *
     * @param response HttpServletResponse 객체 (검색 결과 반환용).
     * @param searchVO 검색 조건을 담은 객체.
     */
    @PostMapping("/search4Ajax")
    @Operation(summary = "Ajax 기반 검색", description = "Elasticsearch를 통해 검색을 수행하고 JSON 형식으로 결과를 반환합니다.")
    public void search4Ajax(HttpServletResponse response, @ModelAttribute @Valid FullTextSearchVO searchVO) {

        EsConfig esConfig = new EsConfig();
        try (RestHighLevelClient client = esConfig.client()) {
            // Elasticsearch 실행 여부 확인
            if (!esConfig.isElasticsearchRunning(client)) {
                log.info("Elasticsearch 서버가 실행되지 않아 검색 작업을 중단합니다.");
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE); // 503 상태 반환
                return;
            }

            if ("".equals(searchVO.getSearchKeyword()) || !"".equals(indexName)) {
                return;
            }

            // Elasticsearch SearchSourceBuilder 설정
            String[] searchRange = searchVO.getSearchRange().split(",");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                    .highlighter(makeHighlightField(searchRange)) // 하이라이트 필드 설정
                    .fetchSource(INCLUDE_FIELDS, null) // 불러올 필드 지정
                    .from((searchVO.getPage() - 1) * DISPLAY_COUNT) // 페이징 처리
                    .size(DISPLAY_COUNT) // 한 페이지당 결과 수
                    .sort(new FieldSortBuilder("_score").order(SortOrder.DESC)) // 정렬 조건 추가
                    .sort(new FieldSortBuilder("brdno").order(SortOrder.DESC))
                    .query(makeQuery(searchRange, searchVO.getSearchKeyword().split(" "), searchVO)); // 검색 쿼리 생성

            // 그룹화 Aggregation 설정
            TermsAggregationBuilder aggregation = AggregationBuilders.terms("group_by_board").field("bgno");
            searchSourceBuilder.aggregation(aggregation);

            // 검색 요청 생성
            SearchRequest searchRequest = new SearchRequest(indexName).source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // 검색 결과를 JSON으로 반환
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(searchResponse.toString());
        } catch (IOException e) {
            log.error("Elasticsearch 검색 작업 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * 주어진 조건에 따라 검색 쿼리를 생성합니다.
     *
     * @param fields   검색 대상 필드 배열.
     * @param words    검색 키워드 배열.
     * @param searchVO 검색 옵션 및 조건.
     * @return BoolQueryBuilder Elasticsearch 검색 쿼리 객체.
     */
    private BoolQueryBuilder makeQuery(String[] fields, String[] words, FullTextSearchVO searchVO) {

        BoolQueryBuilder qb = QueryBuilders.boolQuery();

        // 검색 조건에 따라 QueryBuilder 구성
        String searchType = searchVO.getSearchType();
        if (searchType != null && !"".equals(searchType)) {
            qb.must(QueryBuilders.termQuery("bgno", searchType));
        }

        if (!"a".equals(searchVO.getSearchTerm())) {
            // 날짜 범위 조건 추가
            qb.must(QueryBuilders.rangeQuery("regdate").from(searchVO.getSearchTerm1()).to(searchVO.getSearchTerm2()));
        }

        // 키워드 검색 조건 추가
        for (String word : words) {            // 검색 키워드
            word = word.trim().toLowerCase();
            if (word.isEmpty()) {
                continue;
            }

            BoolQueryBuilder qb1 = QueryBuilders.boolQuery();
            for (String fld : fields) {
                if ("brdreply".equals(fld)) {
                    qb1.should(QueryBuilders.nestedQuery("brdreply", QueryBuilders.boolQuery()
                            .must(QueryBuilders.termQuery("brdreply.rememo", word)), ScoreMode.None));
                } else if ("brdfiles".equals(fld)) {
                    qb1.should(QueryBuilders.nestedQuery("brdfiles", QueryBuilders.boolQuery()
                            .must(QueryBuilders.termQuery("brdfiles.filememo", word)), ScoreMode.None));
                } else {
                    qb1.should(QueryBuilders.boolQuery().must(QueryBuilders.termQuery(fld, word)));
                }
            }
            qb.must(qb1);
        }
        return qb;
    }

    /**
     * 하이라이트 필드 설정.
     *
     * @param fields 하이라이트 적용 필드 목록.
     * @return HighlightBuilder Elasticsearch 하이라이트 설정 객체.
     */
    private HighlightBuilder makeHighlightField(String[] fields) {

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String fld : fields) {
            if ("brdreply".equals(fld) || "brdfiles".equals(fld)) {
                continue;    // 댓글, 첨부파일은 하이라이트 안함. 댓글, 첨부파일이 검색되어도 부모글이 출력되기 때문
            }
            highlightBuilder.field(new HighlightBuilder.Field(fld));
        }

        return highlightBuilder;
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
