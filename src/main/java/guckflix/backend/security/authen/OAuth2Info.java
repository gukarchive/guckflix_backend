package guckflix.backend.security.authen;

public interface OAuth2Info {
    public String getProvider();
    public String getProviderId();
    public String getName();
    public String getEmail();
}