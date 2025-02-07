package com.devkbil.mtssbj.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SqlXmlLoader {

    // SQL ID 캐싱: SQL ID -> INPUT, OUTPUT 정보 저장
    private static final Map<String, QueryInfo> SQL_MAP = new HashMap<>();

    /**
     * SQL XML 파일에서 Query ID 로드
     * @param mapperLocations MyBatis 매퍼 경로 (예: "classpath:mapper/oracle/*.xml")
     */
    public static void loadSqlQueries(String mapperLocations) {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(mapperLocations);

            for (Resource resource : resources) {
                parseXml(resource);
            }
            log.debug("Loaded SQL Queries: {}", SQL_MAP.keySet());
        } catch (Exception e) {
            log.error("Error loading SQL XML files from {}", mapperLocations, e);
            throw new RuntimeException("Failed to load SQL XML files", e);
        }
    }

    /**
     * SQL XML 파일 파싱 후 SQL ID 저장
     */
    // SQL ID 저장 로직에 디버깅 추가
    private static void parseXml(Resource resource) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            var document = builder.parse(resource.getInputStream());

            // namespace 정보 추출
            String namespace = document.getDocumentElement().getAttribute("namespace");
            log.debug("Parsed namespace: {}", namespace);

            var nodeList = document.getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); i++) {
                var node = nodeList.item(i);
                if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    String tagName = node.getNodeName();

                    if (Set.of("select", "insert", "update", "delete").contains(tagName)) {
                        String id = node.getAttributes().getNamedItem("id").getNodeValue();
                        String parameterType = node.getAttributes().getNamedItem("parameterType") != null
                                ? node.getAttributes().getNamedItem("parameterType").getNodeValue()
                                : null;

                        String resultType = node.getAttributes().getNamedItem("resultType") != null
                                ? node.getAttributes().getNamedItem("resultType").getNodeValue()
                                : null;

                        SQL_MAP.put(namespace + "." + id, new QueryInfo(id, parameterType, resultType, namespace));
                        log.debug("SQL ID 등록됨: {} (namespace: {})", id, namespace);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing SQL XML file: {}", resource.getFilename(), e);
        }
    }

    /**
     * 전체 SQL ID 반환 (Namespace 포함)
     */
    public static Set<String> getAllSqlIds() {
        return SQL_MAP.keySet();
    }

    /**
     * Namespace를 제외한 순수 SQL ID 반환
     */
    public static Set<String> getPureSqlIds() {
        return SQL_MAP.keySet().stream()
                      .map(id -> id.contains(".") ? id.substring(id.indexOf(".") + 1) : id) // Namespace 제거
                      .collect(Collectors.toSet());
    }

    /**
     * 전체 namespace 반환
     */
    public static Set<String> getNamespaces() {
        return SQL_MAP.values().stream()
                      .map(QueryInfo::getNamespace)
                      .filter(namespace -> namespace != null && !namespace.isEmpty())
                      .collect(Collectors.toSet());
    }

    /**
     * 쿼리 정보: SQL ID, 파라미터 타입, 결과 타입, namespace
     */
    public static class QueryInfo {
        private final String id;
        private final String parameterType;
        private final String resultType;
        private final String namespace;

        public QueryInfo(String id, String parameterType, String resultType, String namespace) {
            this.id = id;
            this.parameterType = parameterType;
            this.resultType = resultType;
            this.namespace = namespace;
        }

        public String getId() {
            return id;
        }

        public String getParameterType() {
            return parameterType;
        }

        public String getResultType() {
            return resultType;
        }

        public String getNamespace() {
            return namespace;
        }

        @Override
        public String toString() {
            return "QueryInfo{" +
                    "id='" + id + '\'' +
                    ", parameterType='" + parameterType + '\'' +
                    ", resultType='" + resultType + '\'' +
                    ", namespace='" + namespace + '\'' +
                    '}';
        }
    }
}