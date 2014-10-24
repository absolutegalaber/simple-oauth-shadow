package org.simple.auth.shadow;

import com.google.common.base.Optional;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Peter Schneider-Manzell
 */
public enum OAuthRequestParameter {

    GRANT_TYPE("grant_type", true), REDIRECT_URI("redirect_uri", true), SCOPE("scope", true), STATE("state", true), CLIENT_ID("client_id", false), CLIENT_SECRET("client_secret", false), REFRESH_TOKEN("refresh_token", true);

    private final String paramName;
    private final boolean persistPrefixedToSession;

    private OAuthRequestParameter(String paramName, boolean persistPrefixedToSession) {
        this.paramName = paramName;
        this.persistPrefixedToSession = persistPrefixedToSession;
    }

    public String getParamName() {
        return paramName;
    }

    public boolean isPersistPrefixedToSession() {
        return persistPrefixedToSession;
    }

    public Optional<String> getValue(HttpServletRequest req) {
        return Optional.fromNullable(req.getParameter(getParamName()));
    }
}
