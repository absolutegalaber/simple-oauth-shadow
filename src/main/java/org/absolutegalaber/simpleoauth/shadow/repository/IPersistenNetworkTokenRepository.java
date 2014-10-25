package org.absolutegalaber.simpleoauth.shadow.repository;

import org.absolutegalaber.simpleoauth.model.INetworkToken;
import org.absolutegalaber.simpleoauth.shadow.model.IPersistentNetworkToken;

/**
 * @author Peter Schneider-Manzell
 */
public interface IPersistenNetworkTokenRepository<T extends IPersistentNetworkToken, N extends INetworkToken> {

    T load(String network, String networkUserId);


    T create(String accountId, String networkUserid, N networkToken);

}
