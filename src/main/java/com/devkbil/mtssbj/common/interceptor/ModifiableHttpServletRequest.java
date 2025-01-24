package com.devkbil.mtssbj.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * ModifiableHttpServletRequest는 HttpServletRequestWrapper를 확장하여,
 * 기존 요청의 파라미터 값을 수정하거나 새로운 값을 추가할 수 있는 유틸리티 클래스입니다.
 *
 * ### 주요 기능 ###
 * 1. **요청 파라미터 수정**:
 *    - 기존 요청(HttpServletRequest)의 파라미터를 복사하고 이를 수정할 수 있는 메서드를 제공.
 *    - `setParameter` 메서드를 통해 단일 및 다중 파라미터 값을 추가하거나 변경 가능.
 *
 * 2. **읽기 전용 보장**:
 *    - 요청 파라미터를 수정한 후에도, `getParameterMap`과 같은 메서드를 통해
 *      읽기 전용 `Map` 또는 복사된 데이터를 반환하여 데이터 안전성을 보장.
 *
 * 3. **기존 요청의 일관성 유지**:
 *    - 원본 `HttpServletRequest` 객체는 변경되지 않으며,
 *      새롭게 감싼 ModifiableHttpServletRequest 객체에서 수정 기능이 제공.
 *
 * ### 사용 예 ###
 * - **필터 또는 인터셉터에서 요청 파라미터 수정**:
 *   - 특정 요청 로직에 따라 동적으로 요청 파라미터를 추가하거나 변경해야 하는 경우.
 *   - 예를 들어, 보안 토큰, 인증 정보, 동적 쿼리 파라미터 추가 등을 처리.
 *
 * - **테스트 환경에서 요청 객체 조작**:
 *   - Servlet 요청 객체를 테스트할 때 파라미터 값을 동적으로 설정해 테스트 가능.
 *
 * ### 설계 이점 ###
 * - 직접적인 HttpServletRequest 객체의 수정 없이 감싸기(Wrapping) 방식을 통해
 *   HTTP 요청의 무결성을 유지하면서도 동적 수정이 가능.
 */
public class ModifiableHttpServletRequest extends HttpServletRequestWrapper {
    private final Map<String, String[]> params;

    /**
     * 생성자: 기존 요청 파라미터를 저장
     *
     * @param request 원본 HttpServletRequest
     */
    public ModifiableHttpServletRequest(HttpServletRequest request) {
        super(request);
        // 기존 파라미터를 복사하여 저장
        this.params = new HashMap<>(request.getParameterMap());
    }

    /**
     * 특정 파라미터의 첫 번째 값을 반환합니다.
     *
     * @param name 파라미터 이름
     * @return 첫 번째 값 (존재하지 않을 경우 null)
     */

    @Override
    public String getParameter(String name) {
        String[] paramArray = getParameterValues(name);
        return !ObjectUtils.isEmpty(paramArray) ? paramArray[0] : null;
    }

    /**
     * 현재 요청 파라미터의 읽기 전용 맵을 반환합니다.
     *
     * @return 요청 파라미터 맵 (읽기 전용)
     */

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(params);
    }

    /**
     * 현재 요청 파라미터 이름의 목록을 반환합니다.
     *
     * @return 요청 파라미터 이름의 Enumeration
     */

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    /**
     * 특정 파라미터 이름에 해당하는 모든 값(배열)을 반환합니다.
     *
     * @param name 파라미터 이름
     * @return 파라미터 값 배열 (존재하지 않을 경우 null)
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = params.get(name);
        return !ObjectUtils.isEmpty(values) ? values.clone() : null; // 배열 복사본 반환
    }

    /**
     * 단일 파라미터 값을 추가하거나 수정합니다.
     *
     * @param name 파라미터 이름
     * @param value 파라미터 값
     */
    public void setParameter(String name, String value) {
        params.put(name, new String[]{value});
    }

    /**
     * 다중 파라미터 값을 추가하거나 수정합니다.
     *
     * @param name 파라미터 이름
     * @param values 파라미터 값 배열
     */
    public void setParameter(String name, String[] values) {
        if (!ObjectUtils.isEmpty(values)) {
            params.put(name, values.clone()); // 복사본 저장으로 안전성 향상
        } else {
            params.remove(name);
        }
    }
}