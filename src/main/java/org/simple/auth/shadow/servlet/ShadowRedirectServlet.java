package org.simple.auth.shadow.servlet;

import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.absolutegalaber.simpleoauth.model.IClient;
import org.absolutegalaber.simpleoauth.model.Network;
import org.absolutegalaber.simpleoauth.model.OAuthException;
import org.absolutegalaber.simpleoauth.servlet.AbstractAuthorizationRedirect;
import org.apache.http.HttpStatus;
import org.simple.auth.shadow.GrantType;
import org.simple.auth.shadow.OAuthRequestParameter;
import org.simple.auth.shadow.service.ClientService;
import org.simple.auth.shadow.service.GrantTypeService;
import org.simple.auth.shadow.service.IClientService;
import org.simple.auth.shadow.service.IGrantTypeService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Peter Schneider-Manzell
 */
@Slf4j
public class ShadowRedirectServlet extends AbstractAuthorizationRedirect {


    IClientService clientService = new ClientService();
    IGrantTypeService grantTypeService = new GrantTypeService();

    @Override
    public void beforeRedirect(HttpServletRequest req, HttpServletResponse resp, Network network) throws OAuthException {
        super.beforeRedirect(req, resp, network);
        if (!network.isProfileAware()) {
            throw new OAuthException(network.getName() + " is not configured to load profiles");
        }
        GrantType grantType = grantTypeService.fromRequest(req);
        if (log.isDebugEnabled()) {
            log.debug("Detected grantType {}", grantType);
        }
        checkRequiredParameters(grantType, req);
        checkAndStoreClientInformation(grantType, req);
    }

    protected void checkRequiredParameters(GrantType grantType, HttpServletRequest req) throws OAuthException {
        if (log.isDebugEnabled()) {
            log.debug("Checking  required oauth parameters...");
        }

        for (OAuthRequestParameter oAuthRequestParameter : grantType.getRequiredParameters()) {
            Optional<String> paramValue = oAuthRequestParameter.getValue(req);
            if (!paramValue.isPresent()) {
                throw new OAuthException(String.format("Missing parameter value for parameter [%s]", oAuthRequestParameter.getParamName()));
            }
        }
    }

    @Override
    public void onError(Exception authException, HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.warn("Exception in redirect servlet", authException);
            resp.setStatus(HttpStatus.SC_BAD_REQUEST);
            PrintWriter writer = resp.getWriter();
            writer.write("{\"error\":\"invalid_request\",\"error_description\":\"" + authException.getMessage() + "\"}");
            writer.flush();
            writer.close();

        } catch (IOException e) {
            log.error("Could not write error message to stream!", e);
        }
    }

    private void checkAndStoreClientInformation(GrantType grantType, HttpServletRequest req) throws OAuthException {
        IClient client = clientService.fromRequest(req);
        clientService.toSession(req, client);
        for (OAuthRequestParameter oAuthRequestParameter : grantType.getRequiredParameters()) {
            if (oAuthRequestParameter.isPersistPrefixedToSession()) {
                String value = clientService.fromRequest(grantType, oAuthRequestParameter, req);
                clientService.toSession(client, oAuthRequestParameter, value, req);
            }
        }
    }


}
