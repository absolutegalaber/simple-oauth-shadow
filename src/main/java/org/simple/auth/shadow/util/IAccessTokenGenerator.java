package org.simple.auth.shadow.util;

import java.io.Serializable;

/**
 * @author Peter Schneider-Manzell
 */
public interface IAccessTokenGenerator {

    String generateAccessToken(String clientId, Serializable acountId, String grantType);
}
