package com.devkbil.mtssbj.sign;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * 결재 정보를 담는 VO 클래스입니다.
 * <p>
 * 이 클래스는 결재 단계, 결재 결과, 결재자 정보 등 결재 프로세스에 필요한
 * 주요 데이터를 저장 및 전달합니다. 또한 여러 메서드에서 사용될
 * 결재 관련 객체의 구조를 정의합니다.
 *
 * <p>Swagger를 이용한 API 문서화 및 XML 직렬화를 지원합니다.</p>
 */
@Schema(description = "결재 정보를 나타내는 VO 클래스 : SignVO")
@XmlRootElement(name = "SignVO") // XML의 루트 엘리먼트 정의
@XmlType(propOrder = {"ssno", "docno", "ssstep", "sstype", "ssresult", "sscomment", "receivedate", "signdate", "userno",
        "usernm", "userpos"}) // XML 직렬화 순서 지정
@Getter
@Setter
public class SignVO {

    @Schema(description = "결재번호", example = "SIGN2023001")
    private String ssno;        // 결재번호 (결재 고유 식별자)

    @Schema(description = "문서번호", example = "DOC2023001")
    private String docno;        // 문서번호 (해당 결재가 속한 문서의 고유 번호)

    @Schema(description = "결재단계", example = "1", allowableValues = {"0", "1", "2", "3"})
    private String ssstep;    // 결재단계 (0: 초안, 1: 검토, 2: 승인, 3: 완료)

    @Schema(description = "결재종류", example = "1", allowableValues = {"0", "1", "2", "3"})
    private String sstype;        // 결재종류 (결재의 타입: 0: 기안, 1: 합의, 2: 결재, 3: 참조)

    @Schema(description = "결재결과", example = "1", allowableValues = {"0", "1", "2"})
    private String ssresult;    // 결재결과 (현재 상태: 0: 대기, 1: 승인, 2: 반려)

    @Schema(description = "결재 의견", example = "이 문서는 적합합니다.")
    private String sscomment;    // 결재 코멘트 (결재자가 남긴 의견)

    @Schema(description = "받은 일자", example = "2023-10-10")
    private String receivedate; // 받은 일자 (결재 프로세스 시작일)

    @Schema(description = "결재 완료 일자", example = "2023-10-11")
    private String signdate; // 결재 완료 일자

    @Schema(description = "결재자 번호", example = "USER001")
    private String userno; // 사용자 번호 (결재자 고유 식별자)

    @Schema(description = "결재자 이름", example = "홍길동")
    private String usernm; // 사용자 이름 (결재자의 이름)

    @Schema(description = "결재자 직책", example = "01", allowableValues = {"01", "02", "03"})
    private String userpos;    // 공통코드 직책 (결재자의 직위 정보)
}
