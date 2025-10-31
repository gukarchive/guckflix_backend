package guckflix.backend.security.authen;

import guckflix.backend.entity.Member;
import guckflix.backend.entity.enums.MemberRole;
import guckflix.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * OAuth2 (google, naver) 가입용
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository userRepository;

    // 구글로부터 받은 userRequest 데이터에 대한 후 처리 함수
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("getClientRegistration: " + userRequest.getClientRegistration()); // registrationId로 어떤 Oauth로 로그인할지 설정
        log.info("getAccessToken: " + userRequest.getAccessToken().getTokenValue());

        // 구글 로그인 성공 시 oauth-client 라이브러리가 code를 리턴받음 -> accessToken 요청
        // userRequest 정보로 loadUser() 호출 -> 구글로부터 회원 데이터를 받아옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes: " + oAuth2User.getAttributes());
        String requestProvider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Info oAuth2Info = null;
        if(requestProvider.equals("google")){
            oAuth2Info = new GoogleUserInfo(oAuth2User.getAttributes());
        }

        String provider = oAuth2Info.getProvider(); ; // ex)google, naver
        String providerId = oAuth2Info.getProviderId();// 구글, 네이버가 관리하는 pk ex)19239949129
        String username = provider+"_"+providerId; // ex) google_19239949129
        String password = passwordEncoder.encode("패스워드별의미없음");
        String email = oAuth2Info.getEmail();

        // 회원이 등록되어 있지 않으면 회원 가입
        List<Member> members = userRepository.findByUsername(username);
        if(members.size() == 0){
            Member member = Member.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(MemberRole.USER)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(member);
            return new PrincipalDetails(member, oAuth2User.getAttributes());
        }
        // 회원이면 그냥 return
        return new PrincipalDetails(members.get(0), oAuth2User.getAttributes());
    }
}
