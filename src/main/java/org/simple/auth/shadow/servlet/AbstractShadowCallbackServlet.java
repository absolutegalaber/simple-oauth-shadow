package org.simple.auth.shadow.servlet;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.absolutegalaber.simpleoauth.model.BasicUserProfile;
import org.absolutegalaber.simpleoauth.model.IClient;
import org.absolutegalaber.simpleoauth.model.INetworkToken;
import org.absolutegalaber.simpleoauth.model.OAuthException;
import org.absolutegalaber.simpleoauth.servlet.AbstractProfileLoadingAuthorizationCallback;
import org.simple.auth.shadow.OAuthRequestParameter;
import org.simple.auth.shadow.model.IPersistentNetworkToken;
import org.simple.auth.shadow.model.IShadowToken;
import org.simple.auth.shadow.service.AuthService;
import org.simple.auth.shadow.service.ClientService;
import org.simple.auth.shadow.service.IAuthService;
import org.simple.auth.shadow.service.IClientService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author Peter Schneider-Manzell
 */
@Slf4j
public abstract class AbstractShadowCallbackServlet extends AbstractProfileLoadingAuthorizationCallback {

    protected IClientService clientService = new ClientService();
    protected IAuthService authService = new AuthService();


    @Override
    public void onProfileLoaded(INetworkToken accessToken, BasicUserProfile userProfile, HttpServletRequest req, HttpServletResponse resp) throws OAuthException, IOException {
        log.info("Trying to detect client...");
        IClient client = clientService.fromSession(req);
        log.info("Found client, creating shadow token");
        String accountId = connectWithAccount(accessToken, userProfile, req);
        Preconditions.checkNotNull(accountId, "An account Id must be provided!");
        IPersistentNetworkToken persistentNetworkToken = authService.persist(accessToken, userProfile.getNetworkId(), accountId);
        String scopesRaw = clientService.fromSession(client, OAuthRequestParameter.SCOPE, req);
        Set<String> scopes = new HashSet<>();
        if (scopesRaw != null) {
            for (String scope : Splitter.on(" ").split(scopesRaw)) {
                scopes.add(scope);
            }
        }
        IShadowToken token = authService.getShadowToken(client, persistentNetworkToken, userProfile.getNetworkId(), scopes);
        redirect(client, token, req, resp);
    }

    /**
     * Override this if you require account semantics.
     *
     * @param accessToken The network Token obtained from a Network (a.k.a. IdentityProvider).
     * @param userProfile The BasicUserProfile obtained from a Network (a.k.a. IdentityProvider).
     * @param request     The oroginal HttpServlet Callback Request.
     * @return A Account Id to be stored with the token and shadow token, if account semantics are required / desired.
     */
    protected abstract String connectWithAccount(INetworkToken accessToken, BasicUserProfile userProfile, HttpServletRequest request);

    protected void redirect(IClient client, IShadowToken token, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.info("Generating redirect URI...");
        String redirectURI = generateRedirectURI(client, token, req);
        log.info("Redirecting to {}", redirectURI);
        resp.sendRedirect(redirectURI);
    }

    private String generateRedirectURI(IClient client, IShadowToken token, HttpServletRequest req) {
        String baseURI = clientService.fromSession(client, OAuthRequestParameter.REDIRECT_URI, req);
        Map<String, String> parametersToAppend = generateQueryParams(token);
        StringBuilder finalRedirectURI = new StringBuilder(baseURI);
        String concatChar = "?";
        if (baseURI.contains(concatChar)) {
            concatChar = "&";
        }
        for (Map.Entry<String, String> parameter : parametersToAppend.entrySet()) {
            finalRedirectURI.append(concatChar);
            finalRedirectURI.append(parameter.getKey());
            finalRedirectURI.append("=");
            finalRedirectURI.append(parameter.getValue());
            concatChar = "&";
        }
        return finalRedirectURI.toString();
    }

    protected Map<String, String> generateQueryParams(IShadowToken token) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("access_token", token.getAccessToken());
        parameters.put("expires_in", Long.toString(getTimeDifferenceInSeconds(new Date(), token.getExpiresAt())));
        return parameters;
    }

    protected long getTimeDifferenceInSeconds(Date start, Date end) {
        long nowInSeconds = start.getTime() / 1000;
        long expirationInSeconds = end.getTime() / 1000;
        return expirationInSeconds - nowInSeconds;
    }

    @Override
    public void onError(Exception authException, HttpServletRequest req, HttpServletResponse resp) {
        log.error("An error occured", authException);
    }
}
