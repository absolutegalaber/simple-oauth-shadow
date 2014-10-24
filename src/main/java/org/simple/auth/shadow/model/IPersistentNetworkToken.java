package org.simple.auth.shadow.model;


import org.absolutegalaber.simpleoauth.model.INetworkToken;

/**
 * @author Peter Schneider-Manzell
 */
public interface IPersistentNetworkToken extends INetworkToken {

    String getNetworkUserId();
    String getAccountId();
}