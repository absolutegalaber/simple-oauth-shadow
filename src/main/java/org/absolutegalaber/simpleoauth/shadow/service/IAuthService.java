package org.absolutegalaber.simpleoauth.shadow.service;

import org.absolutegalaber.simpleoauth.model.IClient;
import org.absolutegalaber.simpleoauth.model.INetworkToken;
import org.absolutegalaber.simpleoauth.model.OAuthException;
import org.absolutegalaber.simpleoauth.shadow.model.IPersistentNetworkToken;
import org.absolutegalaber.simpleoauth.shadow.model.IShadowToken;

import java.util.Collection;

/**
 * @author Peter Schneider-Manzell
 */
public interface IAuthService {

    IShadowToken getShadowToken(IClient client, IPersistentNetworkToken networkToken, String networkUserId,Collection<String> scopes) throws OAuthException;

    IShadowToken getShadowToken(String shadowAccessToken);

    IShadowToken loadOrCreateShadowToken(String accountId, IClient client, Collection<String> scopes) throws OAuthException;

    IPersistentNetworkToken createPersistentNetworkToken(String account, INetworkToken networkToken, String networkUserId) throws OAuthException;

    public IPersistentNetworkToken persist(INetworkToken networkToken, String networkUserId, String accountId);

    boolean isShadowTokenValid(IShadowToken iShadowToken);
}
