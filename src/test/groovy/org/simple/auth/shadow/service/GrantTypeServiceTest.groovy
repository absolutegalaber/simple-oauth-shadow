package org.simple.auth.shadow.service

import org.absolutegalaber.simpleoauth.model.OAuthException
import org.simple.auth.shadow.GrantType
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * @author Peter Schneider-Manzell
 */
class GrantTypeServiceTest extends Specification {

    GrantTypeService underTest;
    HttpServletRequest mockRequest

    def setup(){
        underTest = new GrantTypeService();
        mockRequest = Mock(HttpServletRequest)
    }

    def "FromRequest"() {
       when:
       mockRequest.getParameter("grant_type") >> "client_credentials"
       GrantType result = underTest.fromRequest(mockRequest)

       then:
       result == GrantType.CLIENT_CREDENTIALS
    }

    def "FromRequestWrongParameter"() {
        when:
        mockRequest.getParameter("grant_type") >> "not_existing"
        underTest.fromRequest(mockRequest)

        then:
        OAuthException ex = thrown()
    }

    def "FromRequestEmptyParameter"() {
        when:
        GrantType result = underTest.fromRequest(mockRequest)

        then:
        result == GrantType.IMPLICIT
    }
}
