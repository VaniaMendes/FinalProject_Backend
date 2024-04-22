package aor.paj.service;

import aor.paj.bean.NotificationBean;
import aor.paj.bean.UserBean;

import aor.paj.dto.NotificationDto;
import aor.paj.dto.User;

import aor.paj.utils.WebListenner;
import aor.paj.websocket.WebSocketMessage;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/notification")
public class NotificationService {

    @Inject
    NotificationBean notificationBean;
    @Inject
    UserBean userBean;
    @Inject
    WebSocketMessage webSocketMessage;
    @Inject
    WebListenner webListenner;
    @Inject
    HttpServletRequest httpRequest;

    @GET
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications(@HeaderParam("token") String token) {
        User user = userBean.getUserByToken(token);
        if(user != null){
            List<NotificationDto> notifications = notificationBean.getNotificationsByToken(token);
            //Atualiza a última atividade da sessão
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                webListenner.updateLastActivityTime(session);
            }
            return Response.ok(notifications).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
        }
    }

    @PUT
    @Path("/read")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response markedAsRead(@HeaderParam("token") String token) {
        User user = userBean.getUserByToken(token);
        if(user != null){
            if(notificationBean.markNotificationAsRead(token)){
                //Atualiza a última atividade da sessão
                HttpSession session = httpRequest.getSession(false);
                if (session != null) {
                    webListenner.updateLastActivityTime(session);
                }
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Notification not found").build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
        }

    }

    @GET
    @Path("/unread")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnreadNotifications(@HeaderParam("token") String token) {
        User user = userBean.getUserByToken(token);
        if(user != null){
            List<NotificationDto> notifications = notificationBean.getUnreadNotificationsByToken(token);
            //Atualiza a última atividade da sessão
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                webListenner.updateLastActivityTime(session);
            }
            return Response.ok(notifications).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
        }
    }
}
