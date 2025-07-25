package com.devkbil.mtssbj.search;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NestedQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;

import com.devkbil.common.util.DateUtil;
import com.devkbil.mtssbj.config.EsConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 검색 관련 기능을 관리하는 컨트롤러입니다.
 * <p>
 * 이 컨트롤러는 Elasticsearch를 활용한 검색 작업(게시판, 댓글, 파일 등)을 수행하며,
 * 검색 결과를 Ajax 또는 HTML 형식으로 반환합니다.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "SearchController", description = "검색 컨트롤러 - 검색 관련 API 제공")
public class SearchController {

    // 기본 페이징 처리 시 한 페이지당 표시할 검색 결과 수
    static final Integer DISPLAY_COUNT = 5;

    // 검색 시 데이터를 가져올 필드 목록
    static final String[] INCLUDE_FIELDS = {"brdno", "userno", "regdate", "regtime", "brdtitle", "brdwriter", "brdmemo", "brdhit"};

    // 검색 결과 캐시를 위한 맵 (키: 검색 조건의 해시코드, 값: 검색 결과)
    private final Map<String, Map<String, Object>> searchCache = new HashMap<>();

    // 캐시 만료 시간 (밀리초)
    private static final long CACHE_EXPIRY_TIME = 60000; // 1분

    // 캐시 타임스탬프를 저장하는 맵
    private final Map<String, Long> cacheTimestamps = new HashMap<>();

    @Value("${elasticsearch.clustername}")
    private String indexName = ""; // Elasticsearch 색인 이름

    private final ElasticsearchOperations elasticsearchOperations;
    private final EsConfig esConfig;

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
     * 검색 조건에 대한 캐시 키를 생성합니다.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @return 캐시 키 문자열
     */
    private String generateCacheKey(FullTextSearchVO searchVO) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(searchVO.getSearchKeyword())
                 .append("_").append(searchVO.getSearchRange())
                 .append("_").append(searchVO.getSearchType())
                 .append("_").append(searchVO.getSearchTerm())
                 .append("_").append(searchVO.getSearchTerm1())
                 .append("_").append(searchVO.getSearchTerm2())
                 .append("_").append(searchVO.getPage());
        return keyBuilder.toString();
    }

    /**
     * 캐시에서 검색 결과를 가져오거나, 캐시가 없으면 검색을 수행합니다.
     *
     * @param searchVO 검색 조건을 담은 객체.
     * @return ResponseEntity 검색 결과를 담은 ResponseEntity 객체
     */
    @GetMapping("/search4Ajax")
    @ResponseBody
    @Operation(summary = "Ajax 기반 검색", description = "Elasticsearch를 통해 검색을 수행하고 JSON 형식으로 결과를 반환합니다.")
    public ResponseEntity<Map<String, Object>> search4Ajax(@ModelAttribute @Valid FullTextSearchVO searchVO) {
        // Elasticsearch 실행 여부 확인
        if (!esConfig.isElasticsearchRunning()) {
            log.info("Elasticsearch 서버가 실행되지 않아 검색 작업을 중단합니다.");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        if ("".equals(searchVO.getSearchKeyword()) && !"".equals(indexName)) {
            return ResponseEntity.ok(Map.of());
        }

        // 캐시 키 생성
        String cacheKey = generateCacheKey(searchVO);

        // 캐시에서 결과 확인
        if (searchCache.containsKey(cacheKey)) {
            Long timestamp = cacheTimestamps.get(cacheKey);
            long currentTime = System.currentTimeMillis();

            // 캐시가 유효한지 확인 (만료 시간 이내인지)
            if (timestamp != null && (currentTime - timestamp) < CACHE_EXPIRY_TIME) {
                log.info("캐시에서 검색 결과 반환: {}", cacheKey);
                return ResponseEntity.ok(searchCache.get(cacheKey));
            } else {
                // 만료된 캐시 항목 제거
                searchCache.remove(cacheKey);
                cacheTimestamps.remove(cacheKey);
            }
        }

        try {
            // 검색 범위 설정
            String[] searchRange = searchVO.getSearchRange().split(",");

            // 검색 쿼리 생성
            NativeQueryBuilder queryBuilder = new NativeQueryBuilder();

            // 페이징 설정
            queryBuilder.withPageable(PageRequest.of(searchVO.getPage() - 1, DISPLAY_COUNT));

            // 정렬 설정
            queryBuilder.withSort(b -> b.field(f -> f.field("_score").order(SortOrder.Desc)));
            queryBuilder.withSort(b -> b.field(f -> f.field("brdno").order(SortOrder.Desc)));

            // 필드 설정
            queryBuilder.withFields(INCLUDE_FIELDS);

            // 검색 쿼리 설정
            queryBuilder.withQuery(makeQuery(searchRange, searchVO.getSearchKeyword().split("\\s+"), searchVO));

            // 그룹화 Aggregation 설정
            queryBuilder.withAggregation("group_by_board",
                Aggregation.of(a -> a.terms(
                    TermsAggregation.of(t -> t.field("bgno"))
                ))
            );

            // 검색 시작 시간 기록
            long startTime = System.currentTimeMillis();

            // 검색 실행
            SearchHits<Map> searchHits = elasticsearchOperations.search(
                queryBuilder.build(),
                Map.class,
                IndexCoordinates.of(indexName)
            );

            // 검색 소요 시간 계산
            long searchTime = System.currentTimeMillis() - startTime;

            // 검색 결과를 JSON으로 변환
            Map<String, Object> result = new HashMap<>();
            result.put("took", searchTime);
            result.put("timed_out", false);

            // hits 정보 구성
            Map<String, Object> hitsInfo = new HashMap<>();
            hitsInfo.put("total", Map.of("value", searchHits.getTotalHits(), "relation", "eq"));
            hitsInfo.put("max_score", searchHits.getMaxScore());

            // hits 배열 구성
            List<Map<String, Object>> hitsList = new ArrayList<>();
            for (SearchHit<Map> hit : searchHits) {
                Map<String, Object> hitMap = new HashMap<>();
                hitMap.put("_index", indexName);
                hitMap.put("_id", hit.getId());
                hitMap.put("_score", hit.getScore());

                // 검색 키워드로 하이라이팅 처리
                Map<String, Object> source = new HashMap<>(hit.getContent());
                String[] keywords = searchVO.getSearchKeyword().split("\\s+");
                for (String keyword : keywords) {
                    if (!StringUtils.hasText(keyword)) {
                        continue;
                    }

                    // 제목 하이라이팅
                    if (source.containsKey("brdtitle") && source.get("brdtitle") != null) {
                        String title = source.get("brdtitle").toString();
                        source.put("brdtitle", title.replaceAll("(?i)" + keyword, "<em>" + keyword + "</em>"));
                    }

                    // 내용 하이라이팅
                    if (source.containsKey("brdmemo") && source.get("brdmemo") != null) {
                        String memo = source.get("brdmemo").toString();
                        source.put("brdmemo", memo.replaceAll("(?i)" + keyword, "<em>" + keyword + "</em>"));
                    }

                    // 작성자 하이라이팅
                    if (source.containsKey("brdwriter") && source.get("brdwriter") != null) {
                        String writer = source.get("brdwriter").toString();
                        source.put("brdwriter", writer.replaceAll("(?i)" + keyword, "<em>" + keyword + "</em>"));
                    }
                }

                hitMap.put("_source", source);
                hitsList.add(hitMap);
            }
            hitsInfo.put("hits", hitsList);
            result.put("hits", hitsInfo);

            // 결과를 캐시에 저장
            searchCache.put(cacheKey, result);
            cacheTimestamps.put(cacheKey, System.currentTimeMillis());
            log.info("검색 결과를 캐시에 저장: {}", cacheKey);

            // 검색 결과를 ResponseEntity로 반환
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Elasticsearch 검색 작업 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 주어진 조건에 따라 검색 쿼리를 생성합니다.
     *
     * @param fields   검색 대상 필드 배열.
     * @param words    검색 키워드 배열.
     * @param searchVO 검색 옵션 및 조건.
     * @return co.elastic.clients.elasticsearch._types.query_dsl.Query 검색 쿼리 객체.
     */
    private co.elastic.clients.elasticsearch._types.query_dsl.Query makeQuery(String[] fields, String[] words, FullTextSearchVO searchVO) {
        // BoolQuery 생성
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        // 검색 조건에 따라 QueryBuilder 구성
        String searchType = searchVO.getSearchType();
        if (searchType != null && !"".equals(searchType)) {
            // 게시판 타입 조건 추가
            boolQuery.must(TermQuery.of(t -> t.field("bgno").value(searchType))._toQuery());
        }

        // 날짜 범위 조건 처리
        // 'a'는 날짜 범위 사용을 의미함
        if ("a".equals(searchVO.getSearchTerm())) {
            String startDate = searchVO.getSearchTerm1();
            String endDate = searchVO.getSearchTerm2();

            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                log.info("날짜 범위 검색 사용: {} ~ {}", startDate, endDate);
                // 날짜 범위 쿼리는 추후 구현 예정
            }
        }

        // 키워드 검색 조건 추가
        for (String word : words) {
            final String processedWord = word.trim().toLowerCase(Locale.ROOT);
            if (processedWord.isEmpty()) {
                continue;
            }

            BoolQuery.Builder fieldBoolQuery = new BoolQuery.Builder();

            for (String fld : fields) {
                if ("brdreply".equals(fld)) {
                    // 댓글 검색
                    fieldBoolQuery.should(
                        NestedQuery.of(n ->
                            n.path("brdreply")
                                .query(q ->
                                    q.bool(b ->
                                        b.must(m ->
                                            m.match(t ->
                                                t.field("brdreply.rememo").query(processedWord)
                                            )
                                        )
                                    )
                                )
                        )._toQuery()
                    );
                } else if ("brdfiles".equals(fld)) {
                    // 첨부파일 검색
                    fieldBoolQuery.should(
                        NestedQuery.of(n ->
                            n.path("brdfiles")
                                .query(q ->
                                    q.bool(b ->
                                        b.must(m ->
                                            m.match(t ->
                                                t.field("brdfiles.filememo").query(processedWord)
                                            )
                                        )
                                    )
                                )
                        )._toQuery()
                    );
                } else {
                    // 일반 필드 검색
                    fieldBoolQuery.should(
                        BoolQuery.of(b ->
                            b.must(m ->
                                m.match(t ->
                                    t.field(fld).query(processedWord)
                                )
                            )
                        )._toQuery()
                    );
                }
            }

            boolQuery.must(fieldBoolQuery.build()._toQuery());
        }

        return boolQuery.build()._toQuery();
    }

}