package com.devkbil.mtssbj.sql;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SqlXmlLoader {

    private static final Map<String, QueryInfo> SQL_MAP = new HashMap<>(); // SQL ID를 저장하는 맵

    private static final ThreadLocal<DocumentBuilder> DOCUMENT_BUILDER = ThreadLocal.withInitial(
        () -> {
            // 다중 스레드 환경에서도 XML 파서를 안전하게 사용할 수 있도록 ThreadLocal로 관리
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                return factory.newDocumentBuilder();
            } catch (Exception e) {
                throw new IllegalStateException("DocumentBuilder 생성 중 오류 발생", e);
            }
        });

    public static void loadSqlQueries(String mapperLocations) {
        try {
            // XML 리소스 로드
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(mapperLocations); // 경로에서 리소스 가져오기

            Arrays.stream(resources).parallel().forEach(SqlXmlLoader::parseXml); // 모든 XML 파일을 병렬 처리
            log.debug("총 SQL Queries 로드: {}", SQL_MAP.size()); // 로드된 SQL 전체 수 출력
        } catch (Exception e) {
            log.error("SQL XML 파일 로딩 중 오류 발생: {}", mapperLocations, e);
            throw new RuntimeException("SQL XML 파일 로드 실패", e); // 실패 시 예외 발생
        }
    }

    private static void parseXml(Resource resource) {
        try {
            // XML 파일 파싱
            DocumentBuilder builder = DOCUMENT_BUILDER.get(); // ThreadLocal에서 DocumentBuilder 가져오기
            Document document = builder.parse(resource.getInputStream()); // XML 문서 파싱

            String namespace = document.getDocumentElement().getAttribute("namespace"); // Namespace 추출
            NodeList nodeList = document.getElementsByTagName("*"); // 모든 노드 가져옴

            for (int i = 0; i < nodeList.getLength(); i++) {
                var node = nodeList.item(i);
                if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    String tagName = node.getNodeName();
                    if (Set.of("select", "insert", "update", "delete").contains(tagName)) {
                        addSqlIdToMap(namespace, node); // 유효한 SQL 태그 처리
                    }
                }
            }
        } catch (Exception e) {
            log.error("XML 파일 파싱 실패: {}", resource.getFilename(), e); // 실패 시 오류 로그 기록
        }
    }

    private static void addSqlIdToMap(String namespace, org.w3c.dom.Node node) {
        try {
            // SQL ID와 관련된 속성을 맵에 저장
            String id = node.getAttributes().getNamedItem("id").getNodeValue(); // SQL ID 추출
            String parameterType = Optional.ofNullable(node.getAttributes().getNamedItem("parameterType"))
                .map(attr -> attr.getNodeValue())
                .orElse(null); // Parameter 타입 추출
            String resultType = Optional.ofNullable(node.getAttributes().getNamedItem("resultType"))
                .map(attr -> attr.getNodeValue())
                .orElse(null); // Result 타입 추출
            SQL_MAP.put(
                namespace + "." + id,
                new QueryInfo(id, parameterType, resultType, namespace)); // SQL 맵에 저장
            log.debug("SQL 등록됨: {} (namespace: {})", id, namespace); // 디버그 로그
        } catch (Exception e) {
            log.error("SQL ID 등록 중 오류 발생. namespace: {}, node: {}", namespace, node, e); // 실패 시 오류 처리
        }
    }

    public static Set<String> getAllSqlIds() {
        return SQL_MAP.keySet(); // Namespace 포함 SQL ID 반환
    }

    public static Set<String> getPureSqlIds() {
        // Namespace를 제외한 SQL ID만 추출하여 반환
        return SQL_MAP.keySet()
            .stream()
            .map(id -> id.contains(".") ? id.split("\\.")[1] : id)
            .collect(Collectors.toSet());
    }

    public static Set<String> getNamespaces() {
        // 모든 Namespace 반환
        return SQL_MAP.values()
            .stream()
            .map(QueryInfo::getNamespace)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public static class QueryInfo {
        private final String id; // SQL ID
        private final String parameterType; // Parameter 타입
        private final String resultType; // 결과 타입
        private final String namespace; // Namespace

        public QueryInfo(String id, String parameterType, String resultType, String namespace) {
            this.id = id;
            this.parameterType = parameterType;
            this.resultType = resultType;
            this.namespace = namespace;
        }

        public String getNamespace() {
            return namespace; // Namespace 반환
        }
    }
}
