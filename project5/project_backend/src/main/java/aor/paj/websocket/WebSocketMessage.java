package aor.paj.websocket;

import aor.paj.bean.NotificationBean;
import aor.paj.bean.UserBean;
import aor.paj.dto.MessageDto;
import aor.paj.dto.User;
import aor.paj.entity.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;


import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@Singleton
@ServerEndpoint("/websocket/message/{token}")
public class WebSocketMessage {

    @Inject
    UserBean userbean;
    @Inject
    Notifier notifier;
    @Inject
    NotificationBean notificationBean;

    HashMap<String, Session> sessions = new HashMap <String, Session>();

    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token) {
        System.out.println("A new WebSocket session for chat is opened for client with token: " + token);
        sessions.put(token, session);

        session.setMaxIdleTimeout(24 * 60 * 60 * 1000);
    }

    @OnClose
    public void toDoOnClose(Session session, CloseReason reason){
        System.out.println("Websocket session is closed with CloseCode: "+
                reason.getCloseCode() + ": "+reason.getReasonPhrase());

        Iterator<Map.Entry<String, Session>> iterator = sessions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Session> entry = iterator.next();
            if (entry.getValue() == session) {
                iterator.remove(); // Remover usando o iterador
                break; // Parar a iteração após encontrar e remover a sessão
            }
        }
    }

    @OnMessage
    public void toDoOnMessage(String msg) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        mapper.registerModule(module);

        try {
            MessageDto message = mapper.readValue(msg, MessageDto.class);
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            mapper.registerModule(module);
            System.out.println(message);

            User receiverUser = message.getReceiver();
            User senderUser = message.getSender();

            String tokenReceiver = (receiverUser).getToken();
            String tokenSender = (senderUser).getToken();

            Session receiverSession = sessions.get(tokenReceiver);
            Session senderSession = sessions.get(tokenSender);
            System.out.println("A new message is received: " + message.getContent());
            if (receiverSession != null && senderSession != null){
                System.out.println("Message sent to both sender and receiver");
                try {

                    receiverSession.getBasicRemote().sendObject(msg);
                    senderSession.getBasicRemote().sendObject(msg);
                    System.out.println("Message sent to both sender and receiver");
                } catch (IOException e) {
                    System.out.println("Something went wrong!");
                } catch (EncodeException e) {
                    throw new RuntimeException(e);
                }
            } else if (receiverSession == null && senderSession != null) {
                try {
                    senderSession.getBasicRemote().sendObject(msg);

                    //só envia notificação se receiver não estiver no chat
                    String messagetoSend = "You received a new message from  " + senderUser.getUsername();
                    notificationBean.createNotification(tokenSender, messagetoSend, receiverUser.getUsername());
                    notifier.send(receiverUser.getToken(), messagetoSend);
                } catch (IOException e) {
                    System.out.println("Something went wrong!");
                } catch (EncodeException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao desserializar a mensagem: " + e.getMessage());
        }

    }

}
