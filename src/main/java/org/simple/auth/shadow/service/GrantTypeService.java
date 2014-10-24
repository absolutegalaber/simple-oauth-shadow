package org.simple.auth.shadow.service;

import com.google.common.base.Optional;
import org.absolutegalaber.simpleoauth.model.OAuthException;
import org.simple.auth.shadow.GrantType;
import org.simple.auth.shadow.OAuthRequestParameter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Peter Schneider-Manzell
 */
public class GrantTypeService implements IGrantTypeService{


    public GrantType fromRequest(HttpServletRequest request) throws OAuthException {
        Optional<String> grantType = OAuthRequestParameter.GRANT_TYPE.getValue(request);

        if (grantType.isPresent()) {
            GrantType result = GrantType.fromParamValue(grantType.get());
            if (result == null) {
                throw new OAuthException(String.format("Grant type [%s] is not defined!", grantType.get()));
            }
            return result;
        } else {
            return GrantType.IMPLICIT;
        }
    }
}
