package aor.paj.utils;

import aor.paj.bean.UserBean;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;


@WebListener
public class WebListenner implements ServletContextListener {

    @Inject
    UserBean userBean;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //Define o tempo de 15 minutos (em segundos)
        sce.getServletContext().setSessionTimeout(15 * 60);
        System.out.println("Tempo de sessão definido para 15 minutos");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Sessio time out ultrapassado");
    }
}
