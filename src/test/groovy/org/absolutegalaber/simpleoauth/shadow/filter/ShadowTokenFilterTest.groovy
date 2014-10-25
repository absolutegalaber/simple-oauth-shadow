package org.absolutegalaber.simpleoauth.shadow.filter

import com.google.common.base.Optional
import org.absolutegalaber.simpleoauth.shadow.DummyShadowToken
import org.absolutegalaber.simpleoauth.shadow.service.IAuthService
import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Peter Schneider-Manzell
 */
class ShadowTokenFilterTest extends Specification {

    ShadowTokenFilter underTest = new ShadowTokenFilter();

    def "DoFilterWithValidShadowToken"() {
        setup:
        String token = "1234567890"
        String authHeader = "Bearer "+token
        String accountId = "account_id_123"
        String clientId = "client_id_123"
        HttpServletRequest mockReq =  Mock(HttpServletRequest)
        HttpServletResponse mockResp =  Mock(HttpServletResponse)
        FilterChain mockFilterChain = Mock(FilterChain)
        IAuthService mockAuthService = Mock(IAuthService)
        underTest.setAuthService(mockAuthService)
        DummyShadowToken iShadowToken = new DummyShadowToken();
        iShadowToken.accessToken = token
        iShadowToken.expiresAt = new Date()+1
        iShadowToken.accountId = accountId
        iShadowToken.clientId = clientId


        when:

        underTest.doFilter(mockReq,mockResp,mockFilterChain)

        then:
        1*mockReq.getHeader("Authorization") >> authHeader
        1*mockAuthService.getShadowToken(token) >> iShadowToken
        1*mockAuthService.isShadowTokenValid(iShadowToken) >> true
        1*mockReq.setAttribute(ShadowTokenFilter.REQ_ACCOUNT_ID_KEY,accountId)
        1*mockReq.setAttribute(ShadowTokenFilter.REQ_CLIENT_ID_KEY,clientId)
        1*mockFilterChain.doFilter(*_)
    }

    def "DoFilterWithNoAuthorizationHeader"() {
        setup:
        HttpServletRequest mockReq =  Mock(HttpServletRequest)
        HttpServletResponse mockResp =  Mock(HttpServletResponse)
        FilterChain mockFilterChain = Mock(FilterChain)
        IAuthService mockAuthService = Mock(IAuthService)
        underTest.setAuthService(mockAuthService)


        when:
        underTest.doFilter(mockReq,mockResp,mockFilterChain)

        then:
        1* mockReq.getHeader("Authorization") >> null
        1*mockFilterChain.doFilter(*_)
    }

    def "ExtractAccessToken"() {
       setup:
       String token = "testtoken"

       when:
       Optional<String> authorizationHeader = Optional.of("Bearer "+token);

       then:
       Optional<String> extractedToken = underTest.extractAccessToken(authorizationHeader);
       extractedToken.isPresent()
       token == extractedToken.get()
    }

    def "extractAuthorizationHeader"() {
        setup:
        HttpServletRequest mockReq =  Mock(HttpServletRequest)
        String dummyAuthorizationHeader = "dummy auth header"

        when:
        mockReq.getHeader("Authorization") >> dummyAuthorizationHeader

        then:
        Optional<String> authHeader = underTest.extractAuthorizationHeader(mockReq)
        authHeader.isPresent()
        dummyAuthorizationHeader == authHeader.get()
    }


    def "GetClientIdMissingClientId"() {
        setup:
        HttpServletRequest mockReq =  Mock(HttpServletRequest)

        when:
        String clientId =  ShadowTokenFilter.getClientId(mockReq)

        then:
       clientId == null

    }

    def "GetClientId"() {
        setup:
        HttpServletRequest mockReq =  Mock(HttpServletRequest)


        when:
        mockReq.getAttribute(ShadowTokenFilter.REQ_CLIENT_ID_KEY) >> "test_client_id"
        String clientId = ShadowTokenFilter.getClientId(mockReq)

        then:
        clientId == "test_client_id"
    }

    def "GetAccountIdMissingAccountId"() {
        setup:
        HttpServletRequest mockReq =  Mock(HttpServletRequest)

        when:
        String accountId =  ShadowTokenFilter.getAccountId(mockReq)

        then:
        accountId == null

    }

    def "GetAccountId"() {
        setup:
        HttpServletRequest mockReq =  Mock(HttpServletRequest)


        when:
        mockReq.getAttribute(ShadowTokenFilter.REQ_ACCOUNT_ID_KEY) >> "test_account_id"
        String accountId = ShadowTokenFilter.getAccountId(mockReq)

        then:
        accountId == "test_account_id"
    }


}
