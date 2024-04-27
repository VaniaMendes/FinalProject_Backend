package aor.paj.service;

import aor.paj.bean.MessageBean;
import aor.paj.bean.UserBean;
import aor.paj.dto.MessageDto;
import aor.paj.dto.User;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

@Path("/messages")
public class MessageService {
   
    @Inject
    MessageBean messageBean;
    @Inject
    UserBean userBean;

    private static final Logger logger = LogManager.getLogger(MessageBean.class);

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


        return Response.ok(messages).build();
    }

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(@HeaderParam("token") String token, MessageDto messageDto, @Context HttpServletRequest request) {

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
            logger.info("Message sent by " + user.getUsername() + " to " + messageDto.getReceiver() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            return response = Response.ok().entity("Message sented").build();

        } else {
            logger.warn("Failed to send message by " + user.getUsername() + " to " + messageDto.getReceiver() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
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
            logger.info("Message marked as read by " + user.getUsername() + " at " + LocalDateTime.now());

            return response = Response.ok().entity("Message marked as read").build();

        } else {
            return response = Response.status(Response.Status.BAD_REQUEST).entity("Message could not be marked as read").build();
        }
    }
}
