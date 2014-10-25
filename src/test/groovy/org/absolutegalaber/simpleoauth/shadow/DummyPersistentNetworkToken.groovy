package org.absolutegalaber.simpleoauth.shadow

import groovy.transform.ToString
import org.absolutegalaber.simpleoauth.shadow.model.IPersistentNetworkToken

/**
 * @author Peter Schneider-Manzell
 */
@ToString
class DummyPersistentNetworkToken implements  IPersistentNetworkToken{
    String network;
    String accountId
    String networkUserId
    String accessToken
    String tokenSecret
    String refreshToken
    Date expiresAt

}
