package aor.paj.utils;

import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;

@WebFilter("/*")
public class ActivityFilter implements Filter {

    @Inject
    SessionListener sessionListener;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            if (request instanceof HttpServletRequest) {
                HttpSession session = ((HttpServletRequest) request).getSession(true);
                if (session != null) {
                    sessionListener.updateLastAccessedTimestamp(session);
                }
            }
            chain.doFilter(request, response);
        }


}
