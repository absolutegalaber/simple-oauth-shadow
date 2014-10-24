package org.simple.auth.shadow.service;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.absolutegalaber.simpleoauth.model.IClient;
import org.absolutegalaber.simpleoauth.model.INetworkToken;
import org.absolutegalaber.simpleoauth.model.OAuthException;
import org.simple.auth.shadow.model.IPersistentNetworkToken;
import org.simple.auth.shadow.model.IShadowToken;

import java.util.Collection;
import java.util.Date;


/**
 * @author Peter Schneider-Manzell
 */
@Slf4j
public class AuthService implements IAuthService {

    private static IRepositoryService repositoryService;


    @Override
    public IShadowToken getShadowToken(IClient client, IPersistentNetworkToken networkToken, String networkUserId, Collection<String> scopes) throws OAuthException {
        if (log.isDebugEnabled()) {
            log.debug("Loading shadow token for client {}, network token {} and networkUserId {}", client, networkToken, networkUserId);
        }
        return loadOrCreateShadowToken(networkToken.getAccountId(), client,scopes);
    }

    @Override
    public IShadowToken getShadowToken(String shadowAccessToken) {
        if (log.isDebugEnabled()) {
            log.debug("Loading shadow token by access token");
        }
        return repositoryService.getShadowTokenRepository().loadByAccessToken(shadowAccessToken);
    }

    @Override
    public IShadowToken loadOrCreateShadowToken(String accountId, IClient client, Collection<String> scopes) throws OAuthException {
        if (log.isDebugEnabled()) {
            log.debug("Loading or creating shadow token account {} and client {}", accountId, client);
        }
        IShadowToken token = repositoryService.getShadowTokenRepository().loadByAccountAndClient(accountId, client.clientId());
        if (token != null) {
            return token;
        }
        return createShadowToken(accountId, client,scopes);
    }

    protected IShadowToken createShadowToken(String accountId, IClient client, Collection<String> scopes) throws OAuthException {
        if (log.isDebugEnabled()) {
            log.debug("Creating shadow token account {} and client {}", accountId, client);
        }
        IShadowToken token = repositoryService.getShadowTokenRepository().createShadowToken(accountId, client,scopes);
        if (!isShadowTokenValid(token)) {
            throw new OAuthException("Invalid shadow token created by shadow token repository!");
        } else {
            return token;
        }
    }


    @Override
    public IPersistentNetworkToken createPersistentNetworkToken(String accountId, INetworkToken networkToken, String networkUserId) throws OAuthException {
        if (log.isDebugEnabled()) {
            log.debug("Creating persistent network token with account{}, networkToken {}, networkUserId {}", accountId, networkToken, networkUserId);
        }
        return repositoryService.getPersistenNetworkTokenRepository().create(accountId, networkUserId, networkToken);
    }


    @Override
    public boolean isShadowTokenValid(IShadowToken iShadowToken) {
        if (iShadowToken == null) {
            if (log.isDebugEnabled()) {
                log.debug("ShadowToken is invalid, because no shadow token presented");
            }
            return false;
        } else if (iShadowToken.getExpiresAt() == null) {
            if (log.isDebugEnabled()) {
                log.debug("ShadowToken is invalid, because no expiry date presented");
            }
            return false;
        } else if (iShadowToken.getAccountId() == null) {
            if (log.isDebugEnabled()) {
                log.debug("ShadowToken is invalid, because no account id presented");
            }
            return false;
        } else if (iShadowToken.getClientId() == null) {
            if (log.isDebugEnabled()) {
                log.debug("ShadowToken is invalid, because no client id presented");
            }
            return false;
        } else if (iShadowToken.getExpiresAt().before(new Date())) {
            if (log.isDebugEnabled()) {
                log.debug("ShadowToken is invalid, because it is outdated (expiry {})", iShadowToken.getExpiresAt());
            }
            return false;
        }
        return true;
    }

    public IPersistentNetworkToken persist(INetworkToken networkToken, String networkUserId, String accountId) {
        Preconditions.checkNotNull(accountId, "An account id must be provided");
        return repositoryService.getPersistenNetworkTokenRepository().create(accountId, networkUserId, networkToken);
    }


    public static void setRepositoryService(IRepositoryService repositoryService) {
        AuthService.repositoryService = repositoryService;
    }

    public static IRepositoryService getRepositoryService() {
        return repositoryService;
    }
}
