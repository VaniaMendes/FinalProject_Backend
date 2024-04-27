package aor.paj.utils;

import aor.paj.bean.UserBean;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;


@WebListener
public class SessionListener implements HttpSessionListener {


    private static final int SESSION_TIMEOUT = 3 * 60; //Definido o session  timeout para 30 min

    @Inject
    UserBean userBean;

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        session.setMaxInactiveInterval(SESSION_TIMEOUT);
        updateLastAccessedTimestamp(session);
        System.out.println("Session created");
    }


    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();

        long lastActivityTime = (long) session.getAttribute("lastActivityTime");
        long currentTime = System.currentTimeMillis();
        long sessionDuration = currentTime - lastActivityTime;
        if(sessionDuration >= (SESSION_TIMEOUT * 1000)){
            String token = (String) session.getAttribute("token");
            if (token != null) {
                userBean.logoutUser(token);
                System.out.println("Session timeout exceeded");
            }

        }

    }

    public void updateLastAccessedTimestamp(HttpSession session) {
        session.setAttribute("lastActivityTime", System.currentTimeMillis());
        System.out.println("Last activity time updated: " + session.getAttribute("lastActivityTime"));

    }
}

