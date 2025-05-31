package com.devkbil.mtssbj.calendar;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.devkbil.common.util.HttpUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 공공데이터포털 API를 이용하여 음력 정보와 공휴일 정보를 조회하는 서비스
 * <p>
 * 이 서비스는 다음 API를 활용합니다:
 * <ul>
 *   <li>음력정보 API: 양력 날짜에 대한 음력 정보 조회</li>
 *   <li>특일정보 API: 국경일, 공휴일 등 특정 날짜 정보 조회</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LunarCalendarService {

    /** 날짜 포맷터 (yyyyMMdd) */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** 연월 포맷터 (yyyyMM) */
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    /** 기본 응답 인코딩 */
    private static final String DEFAULT_ENCODING = "UTF-8";

    /** REST 템플릿 */
    private final RestTemplate restTemplate;

    /** 공공데이터포털 API 도메인 */
    @Value("${data.go.kr.api.domain}")
    private String apiDomain;

    /** 공공데이터포털 API 서비스 키 */
    @Value("${data.go.kr.api.servicekey}")
    private String apiKey;

    /** 공휴일 정보 조회 서비스 경로 */
    @Value("${data.go.kr.api.holidayservice}")
    private String holidayServicePath;

    /** 음력 정보 조회 서비스 경로 */
    @Value("${data.go.kr.api.lunarservice}")
    private String lunarServicePath;

    /**
     * 특정 날짜의 음력 정보를 조회합니다.
     *
     * @param date 조회할 날짜 (yyyy-MM-dd 형식)
     * @return 음력 정보를 담은 Map (lunarYear, lunarMonth, lunarDay, lunarLeap)
     */
    public Map<String, String> getLunarDate(LocalDate date) {
        try {
            // 공공데이터포털 음력 정보 API 호출
            Map<String, String> lunarInfo = fetchLunarInfoFromApi(date);
            if (!lunarInfo.isEmpty()) {
                return lunarInfo;
            }
        } catch (Exception e) {
            log.error("음력 정보 조회 중 오류 발생 ({}): {}", date, e.getMessage());
            // 예외 처리는 아래 기본값 생성 로직으로 계속 진행
        }

        // API 호출 실패 또는 예외 발생 시 기본값 반환
        return createFallbackLunarInfo(date);
    }

    /**
     * 공공데이터포털 API를 이용해 음력 정보를 조회합니다.
     *
     * @param date 조회할 날짜
     * @return 음력 정보를 담은 Map
     */
    private Map<String, String> fetchLunarInfoFromApi(LocalDate date) {
        Map<String, String> lunarInfo = new HashMap<>();

        try {
            URI uri = UriComponentsBuilder.fromUriString(apiDomain)
                .path(lunarServicePath)
                .queryParam("serviceKey", apiKey)
                .queryParam("solYear", String.valueOf(date.getYear()))
                .queryParam("solMonth", String.format("%02d", date.getMonthValue()))
                .queryParam("solDay", String.format("%02d", date.getDayOfMonth()))
                .build(true) // 인코딩 방지
                .toUri();

            log.debug("음력 정보 API 요청: {}", uri);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/xml");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            // XML 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(response.getBody())));
            doc.getDocumentElement().normalize();

            // item 요소 찾기
            NodeList itemList = doc.getElementsByTagName("item");

            return parseLunarInfoFromApiResponse(itemList, date, lunarInfo);
        } catch (Exception e) {
            log.error("음력 정보 API 호출 중 오류: {}", e.getMessage(), e);
            return lunarInfo; // 빈 맵 반환
        }
    }

    /**
     * API 응답에서 음력 정보를 파싱합니다.
     * 
     * @param itemList API 응답의 item 노드 리스트
     * @param date 조회한 날짜
     * @param lunarInfo 음력 정보를 저장할 맵
     * @return 파싱된 음력 정보
     */
    private Map<String, String> parseLunarInfoFromApiResponse(NodeList itemList, LocalDate date, Map<String, String> lunarInfo) {
        try {
            if (itemList.getLength() > 0) {
                Node itemNode = itemList.item(0);

                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;

                    // 음력 정보 추출
                    int lunarYear = Integer.parseInt(getElementValue(itemElement, "lunYear"));
                    int lunarMonth = Integer.parseInt(getElementValue(itemElement, "lunMonth"));
                    int lunarDay = Integer.parseInt(getElementValue(itemElement, "lunDay"));
                    String lunarLeap = "1".equals(getElementValue(itemElement, "lunLeapmonth")) ? "Y" : "N";

                    lunarInfo.put("lunarYear", String.valueOf(lunarYear));
                    lunarInfo.put("lunarMonth", String.valueOf(lunarMonth));
                    lunarInfo.put("lunarDay", String.valueOf(lunarDay));
                    lunarInfo.put("lunarLeap", lunarLeap);

                    log.debug("Lunar date for {}: {}-{}-{}, leap: {}", 
                            date, lunarYear, lunarMonth, lunarDay, lunarLeap);
                }
            } else {
                log.warn("No lunar date information found for {}, using fallback values", date);

                // API에서 정보를 찾을 수 없는 경우 기본값 설정
                // 음력 정보가 없는 경우 양력 날짜를 그대로 사용
                int lunarYear = date.getYear();
                int lunarMonth = date.getMonthValue();
                int lunarDay = date.getDayOfMonth();
                String lunarLeap = "N"; // 기본적으로 윤달이 아님으로 설정

                lunarInfo.put("lunarYear", String.valueOf(lunarYear));
                lunarInfo.put("lunarMonth", String.valueOf(lunarMonth));
                lunarInfo.put("lunarDay", String.valueOf(lunarDay));
                lunarInfo.put("lunarLeap", lunarLeap);

                log.debug("Using fallback lunar date for {}: {}-{}-{}, leap: {}", 
                        date, lunarYear, lunarMonth, lunarDay, lunarLeap);
            }
        } catch (Exception e) {
            log.error("Error fetching lunar date for {}: {}", date, e.getMessage(), e);

            // 예외 발생 시에도 기본값 설정
            int lunarYear = date.getYear();
            int lunarMonth = date.getMonthValue();
            int lunarDay = date.getDayOfMonth();
            String lunarLeap = "N";

            lunarInfo.put("lunarYear", String.valueOf(lunarYear));
            lunarInfo.put("lunarMonth", String.valueOf(lunarMonth));
            lunarInfo.put("lunarDay", String.valueOf(lunarDay));
            lunarInfo.put("lunarLeap", lunarLeap);

            log.debug("Using fallback lunar date after error for {}: {}-{}-{}, leap: {}", 
                    date, lunarYear, lunarMonth, lunarDay, lunarLeap);
        }

        return lunarInfo;
    }

    /**
     * XML 요소에서 지정된 태그의 값을 추출합니다.
     * 
     * @param element 부모 요소
     * @param tagName 태그 이름
     * @return 태그 값 (없으면 빈 문자열)
     */
    private String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node.hasChildNodes()) {
                return node.getFirstChild().getNodeValue();
            }
        }
        return "";
    }

    /**
     * API 호출 실패 시 사용할 기본 음력 정보를 생성합니다.
     * 음력 정보가 없는 경우 양력 날짜를 그대로 사용합니다.
     * 
     * @param date 양력 날짜
     * @return 기본 음력 정보
     */
    private Map<String, String> createFallbackLunarInfo(LocalDate date) {
        Map<String, String> lunarInfo = new HashMap<>();

        int lunarYear = date.getYear();
        int lunarMonth = date.getMonthValue();
        int lunarDay = date.getDayOfMonth();
        String lunarLeap = "N";

        lunarInfo.put("lunarYear", String.valueOf(lunarYear));
        lunarInfo.put("lunarMonth", String.valueOf(lunarMonth));
        lunarInfo.put("lunarDay", String.valueOf(lunarDay));
        lunarInfo.put("lunarLeap", lunarLeap);

        log.debug("기본 음력 정보 사용 ({}): {}-{}-{}, 윤달: {}", 
                date, lunarYear, lunarMonth, lunarDay, lunarLeap);

        return lunarInfo;
    }

    /**
     * 공공데이터포털 API를 사용하여 특정 날짜의 음력 정보를 조회합니다.
     *
     * @param date 조회할 날짜
     * @return 음력 정보를 담은 Map (lunarYear, lunarMonth, lunarDay, lunarLeap)
     */
    private Map<String, String> getGovernmentLunarDate(LocalDate date) {
        Map<String, String> lunarInfo = new HashMap<>();

        try {
            // 공공데이터포털 음력 정보 API URI 구성
            StringBuilder urlBuilder = new StringBuilder(apiDomain);
            urlBuilder.append(lunarServicePath);
            urlBuilder.append("?serviceKey=" + apiKey); // 인코딩 방지를 위해 직접 추가
            urlBuilder.append("&solYear=" + date.getYear());
            urlBuilder.append("&solMonth=" + String.format("%02d", date.getMonthValue()));
            urlBuilder.append("&solDay=" + String.format("%02d", date.getDayOfMonth()));

            URL url = new URL(urlBuilder.toString());
            log.debug("Requesting lunar date for {}: {}", date, url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");
            conn.setRequestProperty("Accept", "application/xml");

            int responseCode = conn.getResponseCode();
            log.debug("Response code from data.go.kr: {}", responseCode);

            BufferedReader rd;
            if (responseCode >= 200 && responseCode <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
                StringBuilder errorContent = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    errorContent.append(line);
                }
                rd.close();
                log.error("Error response from data.go.kr: {}", errorContent.toString());
                return lunarInfo;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            String response = sb.toString();
            log.debug("Response from data.go.kr: {}", response);

            // XML 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(response)));
            doc.getDocumentElement().normalize();

            // 공공데이터포털 API 오류 응답 확인
            NodeList errMsgList = doc.getElementsByTagName("errMsg");
            NodeList returnAuthMsgList = doc.getElementsByTagName("returnAuthMsg");

            if (errMsgList.getLength() > 0 || returnAuthMsgList.getLength() > 0) {
                String errMsg = errMsgList.getLength() > 0 ? errMsgList.item(0).getTextContent() : "";
                String returnAuthMsg = returnAuthMsgList.getLength() > 0 ? returnAuthMsgList.item(0).getTextContent() : "";

                log.error("공공데이터포털 API 오류: {} - {}", errMsg, returnAuthMsg);
                return lunarInfo;
            }

            // 응답에서 item 요소 찾기
            NodeList itemList = doc.getElementsByTagName("item");

            if (itemList.getLength() > 0) {
                Node itemNode = itemList.item(0);

                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;

                    // 음력 정보 추출 (태그 이름은 API 문서 참조)
                    String lunYear = getElementValue(itemElement, "lunYear");
                    String lunMonth = getElementValue(itemElement, "lunMonth");
                    String lunDay = getElementValue(itemElement, "lunDay");
                    String lunLeapmonth = getElementValue(itemElement, "lunLeapmonth");

                    if (!lunYear.isEmpty() && !lunMonth.isEmpty() && !lunDay.isEmpty()) {
                        lunarInfo.put("lunarYear", lunYear);
                        lunarInfo.put("lunarMonth", lunMonth);
                        lunarInfo.put("lunarDay", lunDay);
                        lunarInfo.put("lunarLeap", "1".equals(lunLeapmonth) ? "Y" : "N");

                        log.debug("Lunar date from data.go.kr for {}: {}-{}-{}, leap: {}", 
                                date, lunYear, lunMonth, lunDay, lunLeapmonth);
                        return lunarInfo;
                    }
                }
            }

            log.debug("No lunar data found from data.go.kr API for {}", date);

        } catch (Exception e) {
            log.error("Error fetching lunar date from data.go.kr for {}: {}", date, e.getMessage(), e);
        }

        return lunarInfo; // 빈 맵 반환 (실패 시)
    }

    /**
     * 특정 월의 모든 날짜에 대한 음력 정보를 일괄 조회합니다.
     * 
     * @param year 조회할 연도
     * @param month 조회할 월
     * @return 날짜별 음력 정보를 담은 Map (key: "yyyyMMdd", value: 음력 정보 Map)
     */
    public Map<String, Map<String, String>> getLunarDatesForMonth(int year, int month) {
        Map<String, Map<String, String>> monthLunarInfo = new HashMap<>();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        log.debug("{}-{} 월의 음력 정보 일괄 조회 시작 ({} ~ {})", 
                year, month, startDate, endDate);

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Map<String, String> lunarInfo = getLunarDate(currentDate);
            monthLunarInfo.put(currentDate.format(DATE_FORMATTER), lunarInfo);
            currentDate = currentDate.plusDays(1);
        }

        log.debug("{}-{} 월의 음력 정보 일괄 조회 완료 (총 {}개 날짜)", 
                year, month, monthLunarInfo.size());
        return monthLunarInfo;
    }

    /**
     * 특정 날짜의 공휴일 정보를 조회합니다.
     *
     * @param date 조회할 날짜
     * @return 공휴일 정보를 담은 Map (isHoliday, holidayName)
     */
    public Map<String, String> getHolidayInfo(LocalDate date) {
        Map<String, String> holidayInfo = new HashMap<>();
        holidayInfo.put("isHoliday", "N"); // 기본값은 공휴일이 아님
        holidayInfo.put("holidayName", "");

        try {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.warn("공공데이터포털 서비스 키가 설정되지 않았습니다. 공휴일 정보를 조회할 수 없습니다.");
                return holidayInfo;
            }

            // 서비스 키는 인코딩하지 않고 그대로 사용 (이미 인코딩된 상태임)
            URI uri = UriComponentsBuilder.fromUriString(apiDomain)
                    .path(holidayServicePath)
                    .queryParam("serviceKey", apiKey)
                    .queryParam("solYear", String.valueOf(date.getYear()))
                    .queryParam("solMonth", String.format("%02d", date.getMonthValue()))
                    .queryParam("solDay", String.format("%02d", date.getDayOfMonth()))
                    .build(false) // 파라미터 인코딩하지 않음
                    .toUri();

            log.debug("Requesting holiday info for {}: {}", date, uri);

            ResponseEntity<String> response;
            try {
                // HTTP 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                // HttpUtil에서 기본 헤더 가져오기
                org.apache.http.Header[] apacheHeaders = HttpUtil.getHeaders();
                for (org.apache.http.Header header : apacheHeaders) {
                    headers.add(header.getName(), header.getValue());
                }
                headers.add("Accept", "application/xml");

                HttpEntity<String> entity = new HttpEntity<>(headers);

                // 헤더를 포함한 요청 보내기
                response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

                log.debug("API 응답 상태 코드: {}", response.getStatusCode());
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                log.error("API 호출 오류: {}, 상태 코드: {}, 응답: {}", 
                        e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
                return holidayInfo;
            } catch (Exception e) {
                log.error("API 호출 중 예외 발생: {}", e.getMessage(), e);
                return holidayInfo;
            }

            String responseBody = response.getBody();
            if (responseBody == null || responseBody.isEmpty()) {
                log.warn("API 응답이 비어있습니다.");
                return holidayInfo;
            }

            // XML 파싱
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(responseBody)));
            doc.getDocumentElement().normalize();

            // 공공데이터포털 API 오류 응답 확인
            NodeList errMsgList = doc.getElementsByTagName("errMsg");
            NodeList returnAuthMsgList = doc.getElementsByTagName("returnAuthMsg");
            NodeList returnReasonCodeList = doc.getElementsByTagName("returnReasonCode");

            if (errMsgList.getLength() > 0 || returnAuthMsgList.getLength() > 0) {
                String errMsg = errMsgList.getLength() > 0 ? errMsgList.item(0).getTextContent() : "";
                String returnAuthMsg = returnAuthMsgList.getLength() > 0 ? returnAuthMsgList.item(0).getTextContent() : "";
                String returnReasonCode = returnReasonCodeList.getLength() > 0 ? returnReasonCodeList.item(0).getTextContent() : "";

                log.error("공공데이터포털 API 오류: {} - {} (코드: {})", errMsg, returnAuthMsg, returnReasonCode);

                if ("SERVICE_KEY_IS_NOT_REGISTERED_ERROR".equals(returnAuthMsg)) {
                    log.error("서비스 키가 등록되지 않았거나 유효하지 않습니다. 공공데이터포털에서 서비스 키를 확인하세요.");
                }

                return holidayInfo;
            }

            // 일반 API 응답 코드 확인
            NodeList resultCodeList = doc.getElementsByTagName("resultCode");
            if (resultCodeList.getLength() > 0) {
                String resultCode = resultCodeList.item(0).getTextContent();
                if (!"00".equals(resultCode)) {
                    NodeList resultMsgList = doc.getElementsByTagName("resultMsg");
                    String resultMsg = resultMsgList.getLength() > 0 ? resultMsgList.item(0).getTextContent() : "알 수 없는 오류";
                    log.error("API 오류 응답: {} - {}", resultCode, resultMsg);
                    return holidayInfo;
                }
            }

            // 응답에서 item 요소 찾기
            NodeList itemList = doc.getElementsByTagName("item");

            if (itemList.getLength() > 0) {
                Node itemNode = itemList.item(0);

                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;

                    // 공휴일 정보 추출
                    String dateName = getElementValue(itemElement, "dateName");

                    if (dateName != null && !dateName.isEmpty()) {
                        holidayInfo.put("isHoliday", "Y");
                        holidayInfo.put("holidayName", dateName);
                        log.debug("Holiday found for {}: {}", date, dateName);
                    }
                }
            } else {
                log.debug("No holiday found for {}", date);
            }

        } catch (Exception e) {
            log.error("Error fetching holiday info for {}: {}", date, e.getMessage(), e);
        }

        return holidayInfo;
    }

    /**
     * 특정 월의 모든 날짜에 대한 공휴일 정보를 일괄 조회합니다.
     * 
     * @param year 조회할 연도
     * @param month 조회할 월
     * @return 날짜별 공휴일 정보를 담은 Map (key: "yyyyMMdd", value: 공휴일 정보 Map)
     */
    public Map<String, Map<String, String>> getHolidaysForMonth(int year, int month) {
        Map<String, Map<String, String>> monthHolidayInfo = new HashMap<>();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        log.debug("{}-{} 월의 공휴일 정보 일괄 조회 시작 ({} ~ {})", 
                year, month, startDate, endDate);

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Map<String, String> holidayInfo = getHolidayInfo(currentDate);
            monthHolidayInfo.put(currentDate.format(DATE_FORMATTER), holidayInfo);
            currentDate = currentDate.plusDays(1);
        }

        log.debug("{}-{} 월의 공휴일 정보 일괄 조회 완료 (총 {}개 날짜)", 
                year, month, monthHolidayInfo.size());
        return monthHolidayInfo;
    }

    /**
     * 공휴일 정보 API URL을 생성합니다.
     *
     * @param date 조회할 날짜
     * @return API URL 문자열
     */
    private String buildHolidayApiUrl(LocalDate date) {
        StringBuilder urlBuilder = new StringBuilder(apiDomain);
        urlBuilder.append(holidayServicePath);
        urlBuilder.append("?serviceKey=").append(apiKey); // 인코딩 방지를 위해 직접 추가
        urlBuilder.append("&solYear=").append(date.getYear());
        urlBuilder.append("&solMonth=").append(String.format("%02d", date.getMonthValue()));
        urlBuilder.append("&solDay=").append(String.format("%02d", date.getDayOfMonth()));
        return urlBuilder.toString();
    }

    /**
     * API 응답 XML을 파싱하여 공휴일 정보를 추출합니다.
     *
     * @param response API 응답 XML 문자열
     * @param date 조회한 날짜 (로깅용)
     * @return 공휴일 정보를 담은 Map
     * @throws Exception 파싱 오류 발생 시
     */
    private Map<String, String> parseHolidayApiResponse(String response, LocalDate date) throws Exception {
        Map<String, String> holidayInfo = new HashMap<>();
        holidayInfo.put("isHoliday", "N"); // 기본값은 공휴일이 아님
        holidayInfo.put("holidayName", "");

        // XML 파싱
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(response)));
        doc.getDocumentElement().normalize();

        // 오류 응답 확인
        if (hasApiError(doc)) {
            return holidayInfo; // 기본 정보 반환
        }

        // 응답에서 item 요소 찾기
        NodeList itemList = doc.getElementsByTagName("item");

        if (itemList.getLength() > 0) {
            Node itemNode = itemList.item(0);

            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) itemNode;

                // 공휴일 정보 추출
                String dateName = getElementValue(itemElement, "dateName");

                if (dateName != null && !dateName.isEmpty()) {
                    holidayInfo.put("isHoliday", "Y");
                    holidayInfo.put("holidayName", dateName);
                    log.debug("공휴일 정보 조회 성공 ({}): {}", date, dateName);
                }
            }
        } else {
            log.debug("API에서 공휴일 정보를 찾을 수 없음 ({})", date);
        }

        return holidayInfo;
    }

    /**
     * API 응답에서 오류 여부를 확인합니다.
     * 
     * @param doc 파싱된 XML 문서
     * @return 오류가 있으면 true, 없으면 false
     */
    private boolean hasApiError(Document doc) {
        // 공공데이터포털 API 오류 응답 확인
        NodeList errMsgList = doc.getElementsByTagName("errMsg");
        NodeList returnAuthMsgList = doc.getElementsByTagName("returnAuthMsg");

        if (errMsgList.getLength() > 0 || returnAuthMsgList.getLength() > 0) {
            String errMsg = errMsgList.getLength() > 0 ? errMsgList.item(0).getTextContent() : "";
            String returnAuthMsg = returnAuthMsgList.getLength() > 0 ? returnAuthMsgList.item(0).getTextContent() : "";

            log.error("공공데이터포털 API 오류: {} - {}", errMsg, returnAuthMsg);
            return true;
        }

        // 일반 API 응답 코드 확인
        NodeList resultCodeList = doc.getElementsByTagName("resultCode");
        if (resultCodeList.getLength() > 0) {
            String resultCode = resultCodeList.item(0).getTextContent();
            if (!"00".equals(resultCode)) {
                NodeList resultMsgList = doc.getElementsByTagName("resultMsg");
                String resultMsg = resultMsgList.getLength() > 0 ? resultMsgList.item(0).getTextContent() : "알 수 없는 오류";
                log.error("API 오류 응답: {} - {}", resultCode, resultMsg);
                return true;
            }
        }

        return false;
    }
}
