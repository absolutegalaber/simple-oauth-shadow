package org.simple.auth.shadow.filter;

import org.apache.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Peter Schneider-Manzell
 */
public class ProtectResourceFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String accountId = ShadowTokenFilter.getAccountId((javax.servlet.http.HttpServletRequest) request);
        if (accountId == null) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.SC_FORBIDDEN);
            PrintWriter printWriter = httpServletResponse.getWriter();
            printWriter.write("{\"error\":\"access_denied\"}");
            printWriter.flush();
            printWriter.close();
            return;
        }
        else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
