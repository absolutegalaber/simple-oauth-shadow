package org.simple.auth.shadow.filter

import org.apache.http.HttpStatus
import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Peter Schneider-Manzell
 */
class ProtectResourceFilterTest extends Specification {

    ProtectResourceFilter underTest = new ProtectResourceFilter();

    def "DoFilterWithNoAccount"() {
        setup:
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter  outputCapture = new PrintWriter(outputStream);
        HttpServletRequest mockReq =  Mock(HttpServletRequest)
        HttpServletResponse mockResp =  Mock(HttpServletResponse)
        FilterChain mockFilterChain = Mock(FilterChain)


        when:
        underTest.doFilter(mockReq,mockResp,mockFilterChain)

        then:
        1* mockReq.getAttribute("org.simple.auth.shadow.filter.ShadowTokenFilter_account_id") >> null
        1*mockResp.getWriter() >> outputCapture
        1*mockResp.setStatus(HttpStatus.SC_FORBIDDEN);
        outputStream.toString() == "{\"error\":\"access_denied\"}"
    }

    def "DoFilterWithAccount"() {
        setup:
        HttpServletRequest mockReq =  Mock(HttpServletRequest)
        HttpServletResponse mockResp =  Mock(HttpServletResponse)
        FilterChain mockFilterChain = Mock(FilterChain)


        when:
        underTest.doFilter(mockReq,mockResp,mockFilterChain)

        then:
        1* mockReq.getAttribute("org.simple.auth.shadow.filter.ShadowTokenFilter_account_id") >> "123"
        1*mockFilterChain.doFilter(*_)
    }
}
