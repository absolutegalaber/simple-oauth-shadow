package org.absolutegalaber.simpleoauth.shadow

import org.absolutegalaber.simpleoauth.model.BasicUserProfile
import org.absolutegalaber.simpleoauth.model.INetworkToken
import org.absolutegalaber.simpleoauth.model.OAuthException
import org.absolutegalaber.simpleoauth.model.ProfileAware


/**
 * @author Peter Schneider-Manzell
 */
class DummyProfileAwareNetwork extends DummyNetwork implements ProfileAware {



    @Override
    BasicUserProfile loadUserProfile(INetworkToken token) throws OAuthException {
        throw new IllegalStateException("Not implemented")
    }

    @Override
    String getProfileUrl() {
        throw new IllegalStateException("Not implemented")
    }
}
