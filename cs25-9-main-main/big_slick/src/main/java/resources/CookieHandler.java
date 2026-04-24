package resources;

import jakarta.ws.rs.core.NewCookie;

/**
 * Contains all the information used for the cookies.
 */
public interface CookieHandler {
    /// Name of the login cookie used for user sessions.
    String cookieLoginName = "BIGSLICKLOG";
    /// Path scope of the cookie, "/" means entire domain.
    String cookieLoginPath = "/";
    /// If true, cookie cannot be accessed via JavaScript (HTTP-only); if false it can.
    boolean cookieLoginHTTPOnly = true;
    /// If true, cookie is only sent over HTTPS connections; if false also to HTTP.
    boolean cookieLoginSecure = true;
    /// SameSite policy controlling cross-site cookie behavior (LAX allows top-level navigation).
    NewCookie.SameSite cookieLoginSameSite = NewCookie.SameSite.LAX; //Lax allows cookie to be possible to send between website
}
