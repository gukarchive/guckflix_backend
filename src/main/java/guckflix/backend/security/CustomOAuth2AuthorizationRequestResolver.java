package guckflix.backend.security;

import io.netty.handler.codec.base64.Base64Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultOAuth2AuthorizationRequestResolver;

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {

        OAuth2AuthorizationRequest resolve = defaultOAuth2AuthorizationRequestResolver.resolve(request);
        if(resolve == null){
            return null;
        }

        String state = request.getParameter("state");
        if(state == null){
            state = "/";
        }

        return OAuth2AuthorizationRequest
                .from(resolve)
                .state(state)
                .build();
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {

        return OAuth2AuthorizationRequest
                .from(defaultOAuth2AuthorizationRequestResolver.resolve(request, clientRegistrationId))
                .state(request.getParameter("state"))
                .build();
    }

}
