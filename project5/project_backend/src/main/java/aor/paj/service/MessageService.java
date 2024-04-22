package aor.paj.service;

import aor.paj.bean.MessageBean;
import aor.paj.bean.UserBean;
import aor.paj.dao.MessageDao;
import aor.paj.dto.MessageDto;
import aor.paj.dto.User;
import aor.paj.entity.MessageEntity;
import aor.paj.entity.UserEntity;
import aor.paj.utils.WebListenner;
import aor.paj.websocket.LocalDateTimeAdapter;
import aor.paj.websocket.WebSocketMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.mail.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;

@Path("/messages")
public class MessageService {
   
    @Inject
    MessageBean messageBean;
    @Inject
    UserBean userBean;
    @Inject
    WebSocketMessage webSocketMessage;
    @Inject
    WebListenner webListenner;
    @Inject
    HttpServletRequest httpRequest;

    @GET
    @Path("/{user2}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessagesBetweenUsers(@HeaderParam("token") String token, @PathParam("user2") String username2) {

        User user = userBean.getUserByToken(token);

        if(user == null){
            return Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
        }

        List<MessageDto> messages = messageBean.getMessagesBetweenUsers(token, username2);

        if (messages == null || messages.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No messages found between these users").build();
        }

        //Atualiza a última atividade da sessão
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            webListenner.updateLastActivityTime(session);
        }
        return Response.ok(messages).build();
    }

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(@HeaderParam("token") String token, MessageDto messageDto) {

        User user= userBean.getUserByToken(token);
        Response response;

        if(user == null){
            return response = Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
        }
        if(messageDto == null){
            return response = Response.status(Response.Status.BAD_REQUEST).entity("Message not found").build();
        }


        //Criação de uma nova mensagem e guardada na base de dados
        boolean messageSend = messageBean.sendMessage(token, messageDto);


        if (messageSend) {

            //Atualiza a última atividade da sessão
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                webListenner.updateLastActivityTime(session);
            }
            return response = Response.ok().entity("Message sented").build();

        } else {
            return response = Response.status(Response.Status.BAD_REQUEST).entity("Message could not be sent").build();
        }
    }

    @PUT
    @Path("/read/{id}")
    @Consumes (MediaType.APPLICATION_JSON)
    public Response markMessageAsRead(@HeaderParam("token") String token, @PathParam("id") Long id, @QueryParam("username") String username) {
        User user = userBean.getUserByToken(token);
        Response response;

        if (user == null) {
            return response = Response.status(Response.Status.UNAUTHORIZED).entity("User not logged in").build();
        }

        boolean messageRead = messageBean.markMessagesAsRead(token,id, username);


        if (messageRead) {
            //Atualiza a última atividade da sessão
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                webListenner.updateLastActivityTime(session);
            }
            return response = Response.ok().entity("Message marked as read").build();

        } else {
            return response = Response.status(Response.Status.BAD_REQUEST).entity("Message could not be marked as read").build();
        }
    }
}
