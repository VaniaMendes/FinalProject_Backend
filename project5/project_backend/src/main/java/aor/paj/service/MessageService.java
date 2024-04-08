package aor.paj.service;

import aor.paj.bean.MessageBean;
import aor.paj.bean.UserBean;
import aor.paj.dao.MessageDao;
import aor.paj.dto.MessageDto;
import aor.paj.dto.User;
import aor.paj.entity.MessageEntity;
import aor.paj.entity.UserEntity;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.mail.Message;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/messages")
public class MessageService {
    @Inject
    private MessageDao messageDao;
    @Inject
    MessageBean messageBean;
    @Inject
    UserBean userBean;

    @GET
    @Path("/{user2}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessagesBetweenUsers(@HeaderParam("token") String token, @PathParam("user2") String username2) {


        List<MessageDto> messages = messageBean.getMessagesBetweenUsers(token, username2);

        if (messages == null || messages.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No messages found between these users").build();
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

        boolean messageSend = messageBean.sendMessage(token, messageDto);
        System.out.println(messageSend);

        if (messageSend) {
            return response = Response.ok().entity("Message sented").build();

        } else {
            return response = Response.status(Response.Status.BAD_REQUEST).entity("Message could not be sent").build();
        }
    }
}
