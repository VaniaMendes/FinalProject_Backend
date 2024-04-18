package aor.paj.websocket;

import aor.paj.dto.MessageDto;
import aor.paj.dto.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.ejb.Singleton;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Singleton
@ServerEndpoint("/websocket/notifier/{token}")
public class Notifier {
    HashMap<String, Session> sessions = new HashMap<String, Session>();

    public void send(String token, String msg) {
        Session session = sessions.get(token);
        if (session != null) {
            System.out.println("sending.......... " + msg);
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                System.out.println("Something went wrong!");
            }
        }
    }

    public void sendToAll(String msg) {

        for (Session session : sessions.values()) {
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                System.out.println("Erro ao enviar mensagem: " + e.getMessage());
            }
        }
    }

    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token) {
        System.out.println("A new WebSocket session is opened for client with token: " + token);
        sessions.put(token, session);

        session.setMaxIdleTimeout(24 * 60 * 60 * 1000);
    }

    @OnClose
    public void toDoOnClose(Session session, CloseReason closeReason) {
        System.out.println("Sessão fechada com CloseCode: " + closeReason.getCloseCode() + ": " + closeReason.getReasonPhrase());

        // Cria uma nova lista para evitar a ConcurrentModificationException
        List<String> sessionIdsToRemove = new ArrayList<>();

        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            if (entry.getValue().equals(session)) {
                sessionIdsToRemove.add(entry.getKey());
            }
        }

        // Remove as sessões fora do loop de iteração
        for (String sessionId : sessionIdsToRemove) {
            sessions.remove(sessionId);
        }
    }




    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error in session " + session.getId() + ": " + throwable.getMessage());
    }

}

