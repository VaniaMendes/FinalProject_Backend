package aor.paj.utils;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/session")
public class SessionTimeOut {

    @Inject
    private ServletContext servletContext;

    @Inject
    private HttpServletRequest request;



    @POST
    @Path("/timeout")
    public void sessionTimeOut(@QueryParam("timeout") int timeout) {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(timeout * 60); // Convertendo minutos para segundos

        // Se necessário, você pode também configurar o tempo limite da sessão no contexto da aplicação
        servletContext.setSessionTimeout(timeout * 60); // Convertendo minutos para segundos
    }
}
