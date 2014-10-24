package org.simple.auth.shadow

import com.google.common.base.Optional
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * @author Peter Schneider-Manzell
 */
class OAuthRequestParameterTest extends Specification {
    def "GetParamName"() {
       expect:
       requestParam.getParamName() == expectedParamName

       where:
       requestParam|expectedParamName
       OAuthRequestParameter.CLIENT_ID|"client_id"
       OAuthRequestParameter.CLIENT_SECRET|"client_secret"
       OAuthRequestParameter.GRANT_TYPE|"grant_type"
       OAuthRequestParameter.REDIRECT_URI|"redirect_uri"
       OAuthRequestParameter.REFRESH_TOKEN|"refresh_token"
       OAuthRequestParameter.SCOPE|"scope"
       OAuthRequestParameter.STATE|"state"
    }

    def "GetValue"() {
      given:
      HttpServletRequest mockReq = Mock(HttpServletRequest)
      OAuthRequestParameter param = OAuthRequestParameter.CLIENT_ID

      when:
      def clientIdValue =Optional.of("123456")
      mockReq.getParameter(param.getParamName()) >> clientIdValue.get()
      def result = param.getValue(mockReq)

      then:
      result == clientIdValue

    }
}
