package guckflix.backend.security.authen;

import guckflix.backend.entity.Member;
import guckflix.backend.entity.enums.MemberRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 왜 PrincipalDetails을 두는가? ->
 * PrincipalDetails로 UserDetails, OAuth2User 구현 객체를 만들지 않는 경우
 * 컨트롤러에서 @Authentication이 유저가 OAuth2User인지, UserDetails인지 검사하고 형변환을 계속 해야 함
 */
@Data
@NoArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {

    // 회원 정보, Redis 역직렬화 대상 필드
    private String username;
    private Long id;
    private MemberRole role;

    // 구글에서 제공하는 회원 기타 정보
    private Map<String, Object> attributes;

    // 일반 경로로 가입한 회원
    public PrincipalDetails(Member member) {
        this.id = member.getId();
        this.role = member.getRole();
        this.username = member.getUsername();
    }

    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.id = member.getId();
        this.role = member.getRole();
        this.username = member.getUsername();
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    // 해당 user의 권한을 return
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return this.username;
    }
}
