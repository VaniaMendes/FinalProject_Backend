package aor.paj.utils;

import aor.paj.bean.UserBean;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;


@WebListener
public class WebListenner implements HttpSessionListener {

    @Inject
    UserBean userBean;
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //Define o tempo de 15 minutos (em segundos)
        se.getSession().setMaxInactiveInterval(2 * 60);
        System.out.println("Tempo de sessão iniciado");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Aqui você pode chamar sua função de logout.
        String token = (String) se.getSession().getAttribute("token");
        if (token != null) {
            userBean.logoutUser(token);
        }

        System.out.println("Session time out ultrapassado");
    }
}
