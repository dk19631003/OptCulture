
package org.mq.loyalty.security;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
/**
* Filter required by concurrent session handling package.
* <p>
* This filter performs two functions. First, it calls
* {@link org.springframework.security.core.session.SessionRegistry#refreshLastRequest(String)} for each request
* so that registered sessions always have a correct "last update" date/time. Second, it retrieves a
* {@link org.springframework.security.core.session.SessionInformation} from the <code>SessionRegistry</code>
* for each request and checks if the session has been marked as expired.
* If it has been marked as expired, the configured logout handlers will be called (as happens with
* {@link org.springframework.security.web.authentication.logout.LogoutFilter}), typically to invalidate the session.
* A redirect to the expiredURL specified will be performed, and the session invalidation will cause an
* {@link org.springframework.security.web.session.HttpSessionDestroyedEvent} to be published via the
* {@link org.springframework.security.web.session.HttpSessionEventPublisher} registered in <code>web.xml</code>.</p>
*
* @author Ben Alex
*/
public class ConcurrentSessionFilter extends GenericFilterBean {
//~ Instance fields ================================================================================================
private SessionRegistry sessionRegistry;
private String expiredUrl;
private LogoutHandler[] handlers = new LogoutHandler[] {new SecurityContextLogoutHandler()};
private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//~ Methods ========================================================================================================
public ConcurrentSessionFilter(SessionRegistry sessionRegistry) {
Assert.notNull(sessionRegistry, "SessionRegistry required");
this.sessionRegistry = sessionRegistry;
}
public ConcurrentSessionFilter(SessionRegistry sessionRegistry, String expiredUrl) {
Assert.notNull(sessionRegistry, "SessionRegistry required");
Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl),
expiredUrl + " isn't a valid redirect URL");
this.sessionRegistry = sessionRegistry;
this.expiredUrl = expiredUrl;
}
@Override
public void afterPropertiesSet() {
Assert.notNull(sessionRegistry, "SessionRegistry required");
Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl),
expiredUrl + " isn't a valid redirect URL");
}
public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
throws IOException, ServletException {
HttpServletRequest request = (HttpServletRequest) req;
HttpServletResponse response = (HttpServletResponse) res;
HttpSession session = request.getSession(false);
if (session != null) {
SessionInformation info = sessionRegistry.getSessionInformation(session.getId());
if (info != null) {
if (info.isExpired()) {
// Expired - abort processing
doLogout(request, response);
String targetUrl = determineExpiredUrl(request, info);
if (targetUrl != null) {
redirectStrategy.sendRedirect(request, response, targetUrl);
return;
} else {
response.getWriter().print("This session has been expired (possibly due to multiple concurrent " +
"logins being attempted as the same user).");
response.flushBuffer();
}
return;
} else {
// Non-expired - update last request date/time
sessionRegistry.refreshLastRequest(info.getSessionId());
}
}
}
chain.doFilter(request, response);
}
protected String determineExpiredUrl(HttpServletRequest request, SessionInformation info) {
return expiredUrl;
}
private void doLogout(HttpServletRequest request, HttpServletResponse response) {
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
for (LogoutHandler handler : handlers) {
handler.logout(request, response, auth);
}
}
public void setLogoutHandlers(LogoutHandler[] handlers) {
Assert.notNull(handlers);
this.handlers = handlers;
}
public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
this.redirectStrategy = redirectStrategy;
}
}