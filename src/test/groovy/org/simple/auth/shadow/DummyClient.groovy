package org.simple.auth.shadow

import groovy.transform.ToString
import org.absolutegalaber.simpleoauth.model.IClient

/**
 * @author Peter Schneider-Manzell
 */
@ToString
class DummyClient implements IClient{

    String clientId
    String secret
    String callbackUrl
    String state
    Collection<String> scope

    @Override
    String clientId() {
        return clientId
    }

    @Override
    String secret() {
        return secret
    }

    @Override
    String callbackUrl() {
        return callbackUrl
    }

    @Override
    String state() {
        return state
    }

    @Override
    Collection<String> scope() {
        return scope
    }
}
