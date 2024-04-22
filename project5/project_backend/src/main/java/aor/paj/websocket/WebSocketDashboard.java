package aor.paj.websocket;

import aor.paj.bean.DashboardBean;
import aor.paj.dto.DashboardDTO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
@ServerEndpoint(value = "/websocket/dashboard/{token}")
public class WebSocketDashboard {

    @Inject
    DashboardBean dashboardBean;
    HashMap<String, Session> sessions = new HashMap<String, Session>();


    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token) {
        System.out.println("A new WebSocket session for dashboard is opened for client with token: " + token);
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

    @OnMessage
    public void toDoOnMessage(String msg) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        mapper.registerModule(module);


        try {

            // Desserializar a mensagem em um objeto DashboardDTO
            DashboardDTO dashboardDTO = mapper.readValue(msg, DashboardDTO.class);

            // Serializar o objeto DashboardDTO para JSON
            String jsonDashboardDTO = mapper.writeValueAsString(dashboardDTO);
            System.out.println("DashboardDTO: " + jsonDashboardDTO);


            for (Session session : sessions.values()) {
                if (session.isOpen() ) {
                    try {
                        session.getBasicRemote().sendObject(jsonDashboardDTO);
                        System.out.println("Dashboard enviada para a sessão: " + session.getId());
                    } catch (IOException e) {
                        System.out.println("Error sending dashboard to session: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Erro ao desserializar a mensagem: " + e.getMessage());
        }

    }


}
