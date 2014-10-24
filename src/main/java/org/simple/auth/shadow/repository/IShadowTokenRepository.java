package org.simple.auth.shadow.repository;

import org.absolutegalaber.simpleoauth.model.IClient;
import org.simple.auth.shadow.model.IShadowToken;

import java.util.Collection;


/**
 * @author Peter Schneider-Manzell
 */
public interface IShadowTokenRepository<T extends IShadowToken> {


    T loadByAccessToken(String accessToken);

    T loadByRefreshToken(String refreshToken);

    T loadByAccountAndClient(String accountId, String clientId);

    T createShadowToken(String accountId, IClient client, Collection<String> scopes);
}
