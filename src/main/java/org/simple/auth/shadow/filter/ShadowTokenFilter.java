package org.simple.auth.shadow.filter;

import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.simple.auth.shadow.model.IShadowToken;
import org.simple.auth.shadow.service.AuthService;
import org.simple.auth.shadow.service.IAuthService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Peter Schneider-Manzell
 */
@Slf4j
public class ShadowTokenFilter implements Filter {

    public static final String REQ_ACCOUNT_ID_KEY = "org.simple.auth.shadow.filter.ShadowTokenFilter_account_id";
    public static final String REQ_CLIENT_ID_KEY = "org.simple.auth.shadow.filter.ShadowTokenFilter_client_id";
    public static final String REQ_SCOPES_KEY = "org.simple.auth.shadow.filter.ShadowTokenFilter_scopes";
    private IAuthService authService = new AuthService();
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int ACCESS_TOKEN_START_INDEX = BEARER_PREFIX.length();
    private static String headerName = "Authorization";


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String userDefinedHeadername = filterConfig.getInitParameter("header-name");
        if (userDefinedHeadername != null) {
            headerName = userDefinedHeadername;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Optional<String> authorizationHeader = extractAuthorizationHeader(((HttpServletRequest) request));
        Optional<String> accessToken = extractAccessToken(authorizationHeader);
        IShadowToken iShadowToken = null;
        if (accessToken.isPresent()) {
            log.info("Found access token {}", accessToken.get());
            iShadowToken = authService.getShadowToken(accessToken.get());
            log.info("Found shadow token {}", iShadowToken);
            if (authService.isShadowTokenValid(iShadowToken)) {
                setClientId(request, iShadowToken.getClientId());
                setAccountId(request, iShadowToken.getAccountId());
                setScopes(request, iShadowToken.getScopes());

            }
        }

        chain.doFilter(request, response);
    }

    public static boolean hasAuthorizationHeader(HttpServletRequest request) {
        return Optional.fromNullable(request.getHeader(headerName)).isPresent();
    }


    protected Optional<String> extractAuthorizationHeader(HttpServletRequest request) {
        return Optional.fromNullable(request.getHeader(headerName));
    }


    protected Optional<String> extractAccessToken(Optional<String> headerWithBearer) {
        if (headerWithBearer.isPresent() && headerWithBearer.get().startsWith(BEARER_PREFIX)) {
            return Optional.of(headerWithBearer.get().substring(ACCESS_TOKEN_START_INDEX));
        }
        return Optional.absent();
    }

    @Override
    public void destroy() {

    }

    public static String getAccountId(HttpServletRequest req) {
        return (String) req.getAttribute(REQ_ACCOUNT_ID_KEY);
    }

    public static String getClientId(HttpServletRequest req) {
        return (String) req.getAttribute(REQ_CLIENT_ID_KEY);
    }

    public void setClientId(ServletRequest req, String clientId) {
        req.setAttribute(REQ_CLIENT_ID_KEY, clientId);
    }

    public void setAccountId(ServletRequest req, String accountId) {
        req.setAttribute(REQ_ACCOUNT_ID_KEY, accountId);
    }

    public static Collection<String> getScopes(HttpServletRequest req) {
        return (Collection<String>) req.getAttribute(REQ_SCOPES_KEY);
    }

    public void setScopes(ServletRequest req, Collection<String> scopes) {
        req.setAttribute(REQ_SCOPES_KEY, scopes);
    }

    public void setAuthService(IAuthService authService) {
        this.authService = authService;
    }
}
