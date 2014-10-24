package org.simple.auth.shadow.servlet

import org.absolutegalaber.simpleoauth.model.IClient
import org.absolutegalaber.simpleoauth.model.Network
import org.absolutegalaber.simpleoauth.model.OAuthException
import org.apache.http.HttpStatus
import org.simple.auth.shadow.*
import org.simple.auth.shadow.service.IClientService
import org.simple.auth.shadow.service.IGrantTypeService
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Peter Schneider-Manzell
 */
class ShadowRedirectServletTest extends Specification {

    ShadowRedirectServlet underTest
    IClientService clientServiceMock
    IGrantTypeService grantTypeServiceMock

    def setup() {
        clientServiceMock = Mock(IClientService)
        grantTypeServiceMock = Mock(IGrantTypeService)
        underTest = new ShadowRedirectServlet();
        underTest.clientService = clientServiceMock
        underTest.grantTypeService = grantTypeServiceMock
    }

    def "checkRequiredParametersWithNoParameters"() {
        given:
        HttpServletRequest reqMock = Mock(HttpServletRequest)

        when:

        underTest.checkRequiredParameters(GrantType.IMPLICIT,reqMock)

        then:
        OAuthException ex = thrown()

    }

    def "checkRequiredParametersWithAllParameters"() {
        given:
        HttpServletRequest reqMock = Mock(HttpServletRequest)

        when:

        underTest.checkRequiredParameters(GrantType.IMPLICIT,reqMock)

        then:
        for (OAuthRequestParameter oAuthRequestParameter : GrantType.IMPLICIT.requiredParameters) {
            1 * reqMock.getParameter(oAuthRequestParameter.paramName) >> "dummyparamvalue"
        }

        then:
        noExceptionThrown()
    }

    def "OnError"() {
        given:
        HttpServletRequest reqMock = Mock(HttpServletRequest)
        HttpServletResponse respMock = Mock(HttpServletResponse)
        String message = "Dummy exception"
        OAuthException e = new OAuthException(message)
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);


        when:
        underTest.onError(e, reqMock, respMock)

        then:
        1 * respMock.getWriter() >> pw
        1 * respMock.setStatus(HttpStatus.SC_BAD_REQUEST)
        sw.toString() == "{\"error\":\"invalid_request\",\"error_description\":\"" + message + "\"}"

    }

    def "beforeRedirectNotProfileAwareNetwork"() {
        given:
        HttpServletRequest reqMock = Mock(HttpServletRequest)
        HttpServletResponse respMock = Mock(HttpServletResponse)
        Network networkMock = new DummyNetwork()

        when:
        underTest.beforeRedirect(reqMock, respMock, networkMock)

        then:
        OAuthException ex = thrown()

    }

    def "beforeRedirect"() {
        given:
        HttpServletRequest reqMock = Mock(HttpServletRequest)
        HttpServletResponse respMock = Mock(HttpServletResponse)
        Network dummyNetwork = new DummyProfileAwareNetwork()
        IClient dummyClient = new DummyClient()


        when:
        underTest.beforeRedirect(reqMock, respMock, dummyNetwork)

        then:
        1 * grantTypeServiceMock.fromRequest(reqMock) >> GrantType.IMPLICIT
        for (OAuthRequestParameter oAuthRequestParameter : GrantType.IMPLICIT.requiredParameters) {
            1 * reqMock.getParameter(oAuthRequestParameter.paramName) >> "param_value_for_" + oAuthRequestParameter.getParamName()
        }
        1 * clientServiceMock.fromRequest(reqMock) >> dummyClient
        1 * clientServiceMock.toSession(reqMock, dummyClient)

        1 * clientServiceMock.fromRequest(GrantType.IMPLICIT,OAuthRequestParameter.REDIRECT_URI,reqMock) >> "param_value_for_" + OAuthRequestParameter.REDIRECT_URI.getParamName()
        1 * clientServiceMock.toSession(dummyClient,OAuthRequestParameter.REDIRECT_URI,"param_value_for_" + OAuthRequestParameter.REDIRECT_URI.getParamName(),reqMock)

        1 * clientServiceMock.fromRequest(GrantType.IMPLICIT,OAuthRequestParameter.SCOPE,reqMock) >> "param_value_for_" + OAuthRequestParameter.SCOPE.getParamName()
        1 * clientServiceMock.toSession(dummyClient,OAuthRequestParameter.SCOPE,"param_value_for_" + OAuthRequestParameter.SCOPE.getParamName(),reqMock)

    }
}
