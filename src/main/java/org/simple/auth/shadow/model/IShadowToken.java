package org.simple.auth.shadow.model;


import org.absolutegalaber.simpleoauth.model.INetworkToken;

import java.util.Collection;
import java.util.Date;

/**
 * @author Peter Schneider-Manzell
 */
public interface IShadowToken extends INetworkToken {

    String getAccountId();

    String getClientId();

    Collection<String> getScopes();

    void setAccountId(String id);

    void setAccessToken(String accessToken);

    void setRefreshToken(String accessToken);

    void setExpiresAt(Date expiration);

    void setClientId(String clientId);
}
