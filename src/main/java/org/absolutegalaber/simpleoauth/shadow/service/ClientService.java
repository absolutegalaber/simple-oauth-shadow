package org.absolutegalaber.simpleoauth.shadow.service;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.absolutegalaber.simpleoauth.model.IClient;
import org.absolutegalaber.simpleoauth.model.OAuthException;
import org.absolutegalaber.simpleoauth.shadow.GrantType;
import org.absolutegalaber.simpleoauth.shadow.OAuthRequestParameter;
import org.absolutegalaber.simpleoauth.shadow.repository.IClientRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author Peter Schneider-Manzell
 */
@Slf4j
public class ClientService implements IClientService {
    private static IClientRepository clientRepository;


    @Override
    public IClient fromRequest(HttpServletRequest req) throws OAuthException {
        log.info("Trying to detect client_id from request parameter {}", OAuthRequestParameter.CLIENT_ID.getParamName());
        Optional<String> clientId = OAuthRequestParameter.CLIENT_ID.getValue(req);
        log.info("Got clientId {}", clientId);
        if (!clientId.isPresent()) {
            throw new OAuthException(OAuthRequestParameter.CLIENT_ID.getParamName() + " is required!");
        }
        String redirectUri = redirectUriFromRequest(req);
        IClient client = fromClientId(clientId.get());
        checkRedirectURI(client, redirectUri);
        return client;
    }

    private void checkRedirectURI(IClient client, String presentedRedirectUri) throws OAuthException {
        if (!presentedRedirectUri.startsWith(client.callbackUrl())) {
            throw new OAuthException("Redirect does not match client's redirect!");
        }
    }

    public String redirectUriFromRequest(HttpServletRequest req) throws OAuthException {
        log.info("Trying to detect redirectUri from request parameter {}", OAuthRequestParameter.REDIRECT_URI.getParamName());
        Optional<String> redirectUri = OAuthRequestParameter.REDIRECT_URI.getValue(req);
        if (!redirectUri.isPresent()) {
            throw new OAuthException(OAuthRequestParameter.REDIRECT_URI.getParamName() + " is required!");
        }
        log.info("Detected redirectUri {} from request parameter {}", redirectUri.get(), OAuthRequestParameter.REDIRECT_URI.getParamName());
        return redirectUri.get();
    }

    private IClient fromClientId(String clientId) throws OAuthException {
        Preconditions.checkNotNull(clientId, OAuthRequestParameter.CLIENT_ID.getParamName() + " is required!");
        IClient client = clientRepository.load(clientId);
        if (client == null) {
            throw new OAuthException(String.format("No client with ID [%s] found!", clientId));
        }
        return client;
    }

    public void toSession(HttpServletRequest req, IClient client) {
        log.info("Storing client ID in session under key {}", OAuthRequestParameter.CLIENT_ID.getParamName());
        req.getSession().setAttribute(OAuthRequestParameter.CLIENT_ID.getParamName(), client.clientId());
    }

    public IClient fromSession(HttpServletRequest req) throws OAuthException {
        log.info("Trying to load client ID from session under key {}", OAuthRequestParameter.CLIENT_ID.getParamName());
        String clientId = (String) req.getSession().getAttribute(OAuthRequestParameter.CLIENT_ID.getParamName());
        log.info("Detected client ID {} from session under key {}", clientId, OAuthRequestParameter.CLIENT_ID.getParamName());
        return fromClientId(clientId);
    }

    public static void setClientRepository(IClientRepository clientRepository) {
        ClientService.clientRepository = clientRepository;
    }

    public void redirectUriToSession(HttpServletRequest req, IClient client, String redirectUri) {
        String key = getSessionRedirectKey(client);
        log.info("Storing client redirectURI {} in session under key {}", redirectUri, key);
        req.getSession().setAttribute(key, redirectUri);
    }

    private String getSessionRedirectKey(IClient client) {
        StringBuilder sb = new StringBuilder("client_");
        sb.append(client.clientId());
        sb.append("_");
        sb.append(OAuthRequestParameter.REDIRECT_URI.getParamName());
        return sb.toString();
    }


    public String redirectUriFromSession(IClient client, HttpServletRequest req) {
        String key = getSessionRedirectKey(client);
        log.info("Trying to load client redirectURI from session under key {}", key);
        String redirectUri = (String) req.getSession().getAttribute(key);
        Preconditions.checkNotNull(redirectUri, key + " is required but not found in session!");
        log.info("Detected redirectURI {} for key {} in session", redirectUri, key);
        return redirectUri;
    }


    private String getSessionKey(IClient client, OAuthRequestParameter oAuthRequestParameter) {
        StringBuilder sb = new StringBuilder("client_");
        sb.append(client.clientId());
        sb.append("_");
        sb.append(oAuthRequestParameter.getParamName());
        return sb.toString();
    }

    @Override
    public void toSession(IClient iClient, OAuthRequestParameter oAuthRequestParameter, String value, HttpServletRequest request) {
        String key = getSessionKey(iClient, oAuthRequestParameter);
        log.info("Adding value {} to session under key {}", value, key);
        request.getSession().setAttribute(key, value);
    }

    @Override
    public String fromSession(IClient iClient, OAuthRequestParameter oAuthRequestParameter, HttpServletRequest request) {
        log.info("Trying to load {} from session under key {}", oAuthRequestParameter, oAuthRequestParameter.getParamName());
        String key = getSessionKey(iClient, oAuthRequestParameter);
        String value = (String) request.getSession().getAttribute(key);
        log.info("Detected value {} from session under key {}", value, key);
        return value;
    }

    @Override
    public String fromRequest(GrantType grantType, OAuthRequestParameter oAuthRequestParameter, HttpServletRequest request) throws OAuthException {
        log.info("Trying to detect {} from request parameter {}", oAuthRequestParameter, oAuthRequestParameter.getParamName());
        Optional<String> rawValue = oAuthRequestParameter.getValue(request);
        if (!rawValue.isPresent()) {
            if (Arrays.asList(grantType.getRequiredParameters()).contains(rawValue)) {
                throw new OAuthException(oAuthRequestParameter.getParamName() + " is required!");
            } else {
                return null;
            }

        } else {
            log.info("Detected {} {} from request parameter {}", rawValue.get(), oAuthRequestParameter, oAuthRequestParameter.getParamName());
        }

        return rawValue.get();
    }
}
