package guckflix.backend.security.authen;

import guckflix.backend.entity.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 왜 PrincipalDetails을 두는가? ->
 * PrincipalDetails로 UserDetails, OAuth2User 구현 객체를 만들지 않는 경우
 * 컨트롤러에서 @Authentication이 유저가 OAuth2User인지, UserDetails인지 검사하고 형변환을 계속 해야 함
 */
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    // 실제 회원 정보
    private Member member;

    // 구글에서 제공하는 회원 정보
    private Map<String, Object> attributes;

    // 일반 경로로 가입한 회원
    public PrincipalDetails(Member user) {
        this.member = user;
    }

    // Oauth2로 가입한 회원
    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }


    // 해당 user의 권한을 return
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_"+String.valueOf(member.getRole());
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUsername();
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
        return member.getUsername();
    }
}
