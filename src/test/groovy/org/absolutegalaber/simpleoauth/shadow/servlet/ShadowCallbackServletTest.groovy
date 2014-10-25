package org.absolutegalaber.simpleoauth.shadow.servlet

import org.absolutegalaber.simpleoauth.model.BasicUserProfile
import org.absolutegalaber.simpleoauth.model.IClient
import org.absolutegalaber.simpleoauth.model.INetworkToken
import org.absolutegalaber.simpleoauth.shadow.DummyClient
import org.absolutegalaber.simpleoauth.shadow.DummyPersistentNetworkToken
import org.absolutegalaber.simpleoauth.shadow.DummyShadowToken
import org.absolutegalaber.simpleoauth.shadow.DummyUserProfile
import org.absolutegalaber.simpleoauth.shadow.OAuthRequestParameter
import org.absolutegalaber.simpleoauth.shadow.model.IPersistentNetworkToken
import org.absolutegalaber.simpleoauth.shadow.model.IShadowToken
import org.absolutegalaber.simpleoauth.shadow.service.IAuthService
import org.absolutegalaber.simpleoauth.shadow.service.IClientService
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Peter Schneider-Manzell
 */
class ShadowCallbackServletTest extends Specification {

    AbstractShadowCallbackServlet underTest
    IAuthService authServiceMock
    IClientService clientServiceMock

    def setup() {
        underTest = new AbstractShadowCallbackServlet() {
            @Override
            protected String connectWithAccount(INetworkToken accessToken, BasicUserProfile userProfile, HttpServletRequest request) {
                return "accountId"
            }
        }
        authServiceMock = Mock(IAuthService)
        clientServiceMock = Mock(IClientService)
        underTest.authService = authServiceMock
        underTest.clientService = clientServiceMock
    }


    def "OnProfileLoaded"() {
        given:
        HttpServletRequest mockReq = Mock(HttpServletRequest)
        HttpServletResponse mockResp = Mock(HttpServletResponse)
        INetworkToken accessToken = new DummyShadowToken()
        BasicUserProfile userProfile = new DummyUserProfile()
        IPersistentNetworkToken persistentToken = new DummyPersistentNetworkToken()
        userProfile.networkId = "testuser@network.de"
        IClient client = new DummyClient()
        IShadowToken shadowToken = new DummyShadowToken()
        shadowToken.accessToken = "1234567"
        shadowToken.expiresAt = new Date() + 1

        when:
        underTest.onProfileLoaded(accessToken, userProfile, mockReq, mockResp)


        then:
        1 * clientServiceMock.fromSession(mockReq) >> client
        1 * authServiceMock.persist(accessToken, userProfile.getNetworkId(), "accountId") >> persistentToken
        1 * authServiceMock.getShadowToken(client, persistentToken, userProfile.getNetworkId(),_) >> shadowToken
        1 * clientServiceMock.fromSession(client,OAuthRequestParameter.REDIRECT_URI, mockReq) >> "http://localhost:8080"
        1 * mockResp.sendRedirect(_)

    }

    def "Redirect"() {
        given:
        HttpServletRequest mockReq = Mock(HttpServletRequest)
        HttpServletResponse mockResp = Mock(HttpServletResponse)
        String expectedRedirectUrl = "http://localhost:8080/callback?expires_in=86400&access_token=12345"
        IClient client = new DummyClient()
        IShadowToken shadowToken = new DummyShadowToken()
        Calendar cal= Calendar.getInstance()
        cal.add(Calendar.SECOND,86400)
        shadowToken.expiresAt = cal.getTime()
        shadowToken.setAccessToken("12345")

        String storedRedirectURI = "http://localhost:8080/callback"

        when:
        underTest.redirect(client, shadowToken, mockReq, mockResp)

        then:
        1 * clientServiceMock.fromSession(client,OAuthRequestParameter.REDIRECT_URI, mockReq) >> storedRedirectURI
        1 * mockResp.sendRedirect(expectedRedirectUrl)
    }

    def "GenerateQueryParams"() {
        given:
        IShadowToken shadowToken = new DummyShadowToken()
        shadowToken.accessToken = "123467"
        shadowToken.expiresAt = new Date() + 1

        when:
        Map<String, String> result = underTest.generateQueryParams(shadowToken)

        then:
        result.containsKey("expires_in")
        result.containsKey("access_token")
    }

    def "GetTimeDifferenceInSeconds"() {
        given:
        Date start = new Date()
        Date end = start +1

        when:
        long result = underTest.getTimeDifferenceInSeconds(start, end)
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(start)
        calendar.add(Calendar.SECOND,(int)result)

        then:
        calendar.getTime() == end

    }

    def "OnError"() {

    }
}
