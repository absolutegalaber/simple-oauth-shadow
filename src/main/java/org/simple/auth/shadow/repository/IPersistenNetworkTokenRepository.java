package org.simple.auth.shadow.repository;

import org.absolutegalaber.simpleoauth.model.INetworkToken;
import org.simple.auth.shadow.model.IPersistentNetworkToken;

/**
 * @author Peter Schneider-Manzell
 */
public interface IPersistenNetworkTokenRepository<T extends IPersistentNetworkToken, N extends INetworkToken> {

    T load(String network, String networkUserId);


    T create(String accountId, String networkUserid, N networkToken);

}
