package com.devkbil.mtssbj.member;

import com.devkbil.mtssbj.admin.organ.DeptVO;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Collections;

/**
 * 사용자 정보를 나타내는 엔티티 클래스.
 * Spring Security의 UserDetails 인터페이스를 구현하여 사용자 인증 및 권한 정보를 제공합니다.
 */
@Schema(description = "사용자 정보 엔티티: UserVO")
@XmlRootElement(name = "UserVO")
@XmlType(propOrder = {"userno", "userid", "userpw", "usernm", "photo", "userrole", "userpos", "ip", "deptno", "deptnm", "photofile"})
@Getter
@Setter
@Entity(name = "com_user")
public class UserVO implements UserDetails {

    @Id
    @Schema(description = "사용자 번호 (Primary Key)", example = "1001")
    private String userno;

    @Schema(description = "사용자 ID (로그인에 사용)", example = "john_doe")
    @Column(unique = true)
    private String userid; // ID

    @Schema(description = "사용자 비밀번호", example = "1234")
    private String userpw;

    @Schema(description = "사용자 이름", example = "John Doe")
    private String usernm;

    @Schema(description = "사용자 권한 (Role)", example = "USER")
    private String userrole;

    @Schema(description = "메일서비스 역할", example = "Developer")
    private String userpos;

    @Schema(description = "Remember Me 기능 플래그", example = "true")
    @Transient
    private String remember;

    @Schema(description = "사용자 사진 URL", example = "/images/user_photo.png")
    private String photo;

    @Schema(description = "사용자 IP 주소", example = "192.168.1.1")
    @Transient
    private String ip; // 아이피

    @OneToOne
    @JoinColumn(name = "deptno")
    @Schema(description = "연관된 부서 정보 (DeptVO 엔티티 참조)")
    private DeptVO deptVO;

    @Schema(description = "부서 번호", example = "D001")
    @Transient
    private String deptno;

    @Schema(description = "부서 이름", example = "Development")
    @Transient
    private String deptnm;

    @Schema(description = "사용자 사진 파일 (MultipartFile로 업로드 가능)")
    @Transient
    private MultipartFile photofile; // 사진

    @Schema(description = "삭제 여부 (Y: 삭제, N: 정상)", example = "N")
    private String deleteflag;

    /**
     * 사용자의 권한 정보를 GrantedAuthority의 Collection 형태로 반환합니다.
     *
     * @return 역할 기반 권한 정보의 Collection
     */
    @Override
    @Schema(hidden = true) // Swagger 문서에서는 숨김
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 역할 기반 권한을 GrantedAuthority 형태로 변환
        return Collections.singletonList(() -> "ROLE_" + userrole);
    }

    @Override
    @Schema(hidden = true) // Swagger 문서에서는 숨김
    public String getPassword() {
        return this.userpw;
    }

    @Override
    @Schema(hidden = true) // Swagger 문서에서는 숨김
    public String getUsername() {
        return this.userid;
    }

    @Override
    @Schema(hidden = true) // Swagger 문서에서는 숨김
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Schema(hidden = true) // Swagger 문서에서는 숨김
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Schema(hidden = true) // Swagger 문서에서는 숨김
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Schema(hidden = true) // Swagger 문서에서는 숨김
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * 두 객체를 비교하여 같은 사용자 ID를 가지는 경우 동일한 객체로 판단합니다.
     *
     * @param obj 비교 대상 객체
     * @return 사용자 ID가 동일하면 true, 아니면 false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserVO) {
            return this.userid.equals(((UserVO) obj).userid);
        }
        return false;
    }
}
