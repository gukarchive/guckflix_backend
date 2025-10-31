package guckflix.backend.security.authen;

import java.util.Map;

public class GoogleUserInfo implements OAuth2Info{

    private String provider = "google";
    private String providerId;
    private String name;
    private String email;

    public GoogleUserInfo(Map<String, Object> attributes) {
        providerId = (String) attributes.get("sub");
        name = (String) attributes.get("name");
        email = (String) attributes.get("email");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return providerId;
    }

    @Override
    public String getName() {
        return getName();
    }

    @Override
    public String getEmail() {
        return email;
    }
}
