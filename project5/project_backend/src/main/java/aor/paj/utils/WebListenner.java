package aor.paj.utils;

import aor.paj.bean.UserBean;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;


@WebListener
public class WebListenner implements HttpSessionListener {

    @Inject
    UserBean userBean;
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //Define o tempo de x minutos (em segundos)
        se.getSession().setMaxInactiveInterval(20 * 60);
        System.out.println("Tempo de sessão iniciado");

        // Define o atributo de marca de tempo de última atividade
        HttpSession session = se.getSession();
        session.setAttribute("lastActivityTime", System.currentTimeMillis());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

        String token = (String) se.getSession().getAttribute("token");
        System.out.println("Token: " + token);
        if (token != null) {
            userBean.logoutUser(token);
            System.out.println("Session time out ultrapassado");
        }

    }

    // Método para atualizar o tempo de última atividade da sessão
    public void updateLastActivityTime(HttpSession session) {
        session.setAttribute("lastActivityTime", System.currentTimeMillis());
    }




}
