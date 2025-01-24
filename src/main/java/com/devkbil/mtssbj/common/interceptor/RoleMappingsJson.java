package com.devkbil.mtssbj.common.interceptor;

/**
 * RoleMappingsJson 클래스는 URL과 역할(Role)을 기반으로 성공 또는 실패 URL 정보를 제공하기 위해,
 * JSON 형식의 Role-to-URL 매핑 데이터를 관리합니다.
 *
 * ### 주요 기능 ###
 * 1. URL별로 관리해야 할 역할(Role) 정보를 JSON 데이터로 정의.
 * 2. 성공 URL과 에러 URL을 명시하여 필요한 URL 전환 구현.
 * 3. 외부 파일 없이도 코드 내 JSON 데이터를 임베디드 방식으로 관리 가능.
 *
 * ### 사용 예 ###
 * - 특정 요청 URL("/profile")에 대해 사용자가 "ADMIN"인 경우 성공 URL("adminProfile")이 반환되고,
 *   비로그인(ANONYMOUS) 상태에서는 에러 URL("loginPage")로 전환.
 *
 * ### 확장성 ###
 * JSON 형식을 사용하므로, 데이터를 외부 JSON 파일이나 데이터베이스로 이전하거나 추가적으로 매핑을 늘릴 수 있는 확장성을 제공합니다.
 */

public class RoleMappingsJson {

    public static final String ROLE_MAPPINGS_JSON = """
            {
                "/adBoardGroupList": {
                    "ADMIN": {
                        "successUrl": "admin/board/BoardGroupList",
                        "errorUrl": "adminErrorPage"
                    },
                    "USER": {
                        "successUrl": null,
                        "errorUrl": null
                    },
                    "GUEST": {
                        "successUrl": null,
                        "errorUrl": null
                    },
                    "ANONYMOUS": {
                        "successUrl": null,
                        "errorUrl": "member/memberLogin"
                    }
                },
                "/profile": {
                    "ADMIN": {
                        "successUrl": "adminProfile",
                        "errorUrl": "adminErrorPage"
                    },
                    "USER": {
                        "successUrl": "userProfile",
                        "errorUrl": "userErrorPage"
                    },
                    "GUEST": {
                        "successUrl": null,
                        "errorUrl": "guestLoginPrompt"
                    },
                    "ANONYMOUS": {
                        "successUrl": null,
                        "errorUrl": "loginPage"
                    }
                },
                "/settings": {
                    "ADMIN": {
                        "successUrl": "adminSettings",
                        "errorUrl": "adminErrorPage"
                    },
                    "USER": {
                        "successUrl": "userSettings",
                        "errorUrl": "userErrorPage"
                    },
                    "GUEST": {
                        "successUrl": null,
                        "errorUrl": "guestLoginPrompt"
                    },
                    "ANONYMOUS": {
                        "successUrl": null,
                        "errorUrl": "loginPage"
                    }
                }
            }
            """;

}