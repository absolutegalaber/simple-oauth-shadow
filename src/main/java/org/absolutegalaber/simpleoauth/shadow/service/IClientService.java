package org.absolutegalaber.simpleoauth.shadow.service;

import org.absolutegalaber.simpleoauth.model.IClient;
import org.absolutegalaber.simpleoauth.model.OAuthException;
import org.absolutegalaber.simpleoauth.shadow.GrantType;
import org.absolutegalaber.simpleoauth.shadow.OAuthRequestParameter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Peter Schneider-Manzell
 */
public interface IClientService {

    IClient fromRequest(HttpServletRequest req) throws OAuthException;


    void toSession(HttpServletRequest req, IClient client);

    IClient fromSession(HttpServletRequest req) throws OAuthException;


    void toSession(IClient iClient, OAuthRequestParameter oAuthRequestParameter, String value, HttpServletRequest request);

    String fromSession(IClient iClient, OAuthRequestParameter oAuthRequestParameter, HttpServletRequest request);

    String fromRequest(GrantType grantType, OAuthRequestParameter oAuthRequestParameter, HttpServletRequest request) throws OAuthException;
}
