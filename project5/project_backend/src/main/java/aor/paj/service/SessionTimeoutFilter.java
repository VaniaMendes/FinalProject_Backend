package aor.paj.service;
import aor.paj.dto.User;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class SessionTimeoutFilter implements Filter {

    private int timeout;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String timeoutString = filterConfig.getInitParameter("timeout");
        timeout = Integer.parseInt(timeoutString);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            long lastAccessTime = session.getLastAccessedTime();
            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastAccessTime) > timeout) {
                session.invalidate();

                ((HttpServletResponse) response).sendRedirect("login.jsp"); // Redirecionar para a página de logout
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}