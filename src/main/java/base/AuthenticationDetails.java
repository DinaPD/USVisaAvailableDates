package base;

import org.openqa.selenium.Cookie;

public class AuthenticationDetails {
    private Cookie authCookie;
    private String userAgent;

    public AuthenticationDetails(Cookie token, String userAgent) {
        this.authCookie = token;
        this.userAgent = userAgent;
    }

    public Cookie getAuthCookie() {
        return authCookie;
    }

    public void setAuthCookie(Cookie authCookie) {
        this.authCookie = authCookie;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
