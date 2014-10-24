package org.simple.auth.shadow;


/**
 * @author Peter Schneider-Manzell
 */
public enum GrantType {
    CLIENT_CREDENTIALS("client_credentials", new OAuthRequestParameter[]{OAuthRequestParameter.CLIENT_ID, OAuthRequestParameter.CLIENT_SECRET, OAuthRequestParameter.SCOPE}),
    REFRESH_TOKEN("refresh_token", new OAuthRequestParameter[]{OAuthRequestParameter.REFRESH_TOKEN}),
    IMPLICIT("implicit", new OAuthRequestParameter[]{OAuthRequestParameter.REDIRECT_URI, OAuthRequestParameter.CLIENT_ID, OAuthRequestParameter.SCOPE});
    private final String paramValue;
    private final OAuthRequestParameter[] requiredParameters;

    private GrantType(String paramValue, OAuthRequestParameter[] requiredParameters) {
        this.paramValue = paramValue;
        this.requiredParameters = requiredParameters;
    }

    public String getParamValue() {
        return paramValue;
    }

    public OAuthRequestParameter[] getRequiredParameters() {
        return requiredParameters;
    }

    public static GrantType fromParamValue(String paramValue) {
        for (GrantType grantType : values()) {
            if (grantType.getParamValue().equals(paramValue)) {
                return grantType;
            }
        }
        return null;
    }
}
