package org.absolutegalaber.simpleoauth.shadow.service;

import org.absolutegalaber.simpleoauth.model.OAuthException;
import org.absolutegalaber.simpleoauth.shadow.GrantType;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Peter Schneider-Manzell
 */
public interface IGrantTypeService {

    GrantType fromRequest(HttpServletRequest request) throws OAuthException;
}
