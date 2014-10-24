package org.simple.auth.shadow.service

import org.absolutegalaber.simpleoauth.model.IClient
import org.absolutegalaber.simpleoauth.model.OAuthException
import org.simple.auth.shadow.DummyClient
import org.simple.auth.shadow.OAuthRequestParameter
import org.simple.auth.shadow.repository.IClientRepository
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

/**
 * @author Peter Schneider-Manzell
 */
class ClientServiceTest extends Specification {

    ClientService underTest
    IClientRepository clientRepositoryMock

    def setup() {
        underTest = new ClientService()
        clientRepositoryMock = Mock(IClientRepository)
        underTest.setClientRepository(clientRepositoryMock)
    }

    def "FromRequestWithEmptyClientId"() {
        when:
        HttpServletRequest req = Mock(HttpServletRequest)
        underTest.fromRequest(req)

        then:
        OAuthException ex = thrown()
    }

    def "FromRequestWithEmptyRequestUri"() {
        given:
        def clientId = "123"

        when:
        HttpServletRequest req = Mock(HttpServletRequest)
        req.getParameter(OAuthRequestParameter.CLIENT_ID.paramName) >> clientId
        underTest.fromRequest(req)

        then:
        OAuthException ex = thrown()
    }

    def "FromRequestWithWrongClientId"() {
        given:
        def clientId = "123"
        def redirectUri = "http://localhost/callback"

        when:
        HttpServletRequest req = Mock(HttpServletRequest)
        req.getParameter(OAuthRequestParameter.CLIENT_ID.paramName) >> clientId
        req.getParameter(OAuthRequestParameter.REDIRECT_URI.paramName) >> redirectUri
        underTest.fromRequest(req)

        then:
        OAuthException ex = thrown()
    }

    def "FromRequestWithWrongRedirectUri"() {
        given:
        def clientId = "123"
        def redirectUri = "http://localhost/callback"
        def wrongRedirectUri = "http://localhost:8080/callback"
        def dummyClient = new DummyClient()
        dummyClient.clientId = clientId
        dummyClient.callbackUrl = redirectUri

        when:
        HttpServletRequest req = Mock(HttpServletRequest)
        req.getParameter(OAuthRequestParameter.CLIENT_ID.paramName) >> clientId
        req.getParameter(OAuthRequestParameter.REDIRECT_URI.paramName) >> wrongRedirectUri

        clientRepositoryMock.load(clientId) >> dummyClient
        underTest.fromRequest(req)

        then:
        OAuthException ex = thrown()
    }

    def "FromRequest"() {
        given:
        def clientId = "123"
        def redirectUri = "http://localhost/callback"
        def dummyClient = new DummyClient()
        dummyClient.clientId = clientId
        dummyClient.callbackUrl = redirectUri

        when:
        HttpServletRequest req = Mock(HttpServletRequest)
        req.getParameter(OAuthRequestParameter.CLIENT_ID.paramName) >> clientId
        req.getParameter(OAuthRequestParameter.REDIRECT_URI.paramName) >> redirectUri

        clientRepositoryMock.load(clientId) >> dummyClient
        def result = underTest.fromRequest(req)

        then:
        result == dummyClient
    }

    def "RedirectUriFromRequest"() {
        given:
        String redirectUri = "http://localhost/callback"

        when:

        HttpServletRequest mockReq = Mock(HttpServletRequest)
        mockReq.getParameter(OAuthRequestParameter.REDIRECT_URI.paramName) >> redirectUri
        def result = underTest.redirectUriFromRequest(mockReq)

        then:
        result == redirectUri
    }

    def "ToSession"() {
        given:
        String clientId = "123"
        IClient dummyClient = new DummyClient()
        dummyClient.clientId = clientId
        HttpServletRequest mockReq = Mock(HttpServletRequest)
        HttpSession mockSession = Mock(HttpSession)

        when:

        underTest.toSession(mockReq,dummyClient)

        then:
        1*mockReq.getSession() >> mockSession
        1*mockSession.setAttribute(OAuthRequestParameter.CLIENT_ID.paramName,clientId)
    }

    def "FromSession"() {

    }


    def "RedirectUriToSession"() {
        given:
        String clientId = "123"
        String clientRedirectUri = "http://localhost/"
        String requestedRedirectUri = "http://localhost/callback"
        IClient dummyClient = new DummyClient()
        dummyClient.clientId = clientId
        dummyClient.callbackUrl = clientRedirectUri
        HttpServletRequest mockReq = Mock(HttpServletRequest)
        HttpSession mockSession = Mock(HttpSession)

        when:

        underTest.redirectUriToSession(mockReq,dummyClient,requestedRedirectUri)

        then:
        1*mockReq.getSession() >> mockSession
        1*mockSession.setAttribute("client_"+clientId+"_"+OAuthRequestParameter.REDIRECT_URI.paramName,requestedRedirectUri)
    }

    def "RedirectUriFromSession"() {
        given:
        String clientId = "123"
        String clientRedirectUri = "http://localhost/"
        String storedRedirectUri = "http://localhost/callback"
        IClient dummyClient = new DummyClient()
        dummyClient.clientId = clientId
        dummyClient.callbackUrl = clientRedirectUri
        HttpServletRequest mockReq = Mock(HttpServletRequest)
        HttpSession mockSession = Mock(HttpSession)

        when:
        String result = underTest.redirectUriFromSession(dummyClient,mockReq)

        then:
        1* mockReq.getSession() >> mockSession
        1* mockSession.getAttribute("client_"+clientId+"_"+OAuthRequestParameter.REDIRECT_URI.paramName) >> storedRedirectUri
        result == storedRedirectUri
    }
}
