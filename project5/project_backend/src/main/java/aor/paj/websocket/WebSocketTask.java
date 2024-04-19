package aor.paj.websocket;

import aor.paj.bean.DashboardBean;
import aor.paj.dto.DashboardDTO;
import aor.paj.dto.Task;
import aor.paj.utils.LocalDateSerializer;
import aor.paj.utils.TaskEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;


import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Singleton
@ServerEndpoint(value = "/websocket/updateTask/{token}", encoders = {TaskEncoder.class})

public class WebSocketTask {


    HashMap<String, Session> sessions = new HashMap <String, Session>();

    @OnOpen
    public void toDoOnOpen(Session session, @PathParam("token") String token) {
        System.out.println("A new WebSocket session for taskUpdate is opened for client with token: " + token);
        sessions.put(token, session);

        session.setMaxIdleTimeout(24 * 60 * 60 * 1000);

    }

    @OnMessage
    public void toDoOnMessage(String taskEdited) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer());
        mapper.registerModule(module);

        try {
            
            Task taskToSend = mapper.readValue(taskEdited, Task.class);


            // Envia o objeto Task para cada sessão WebSocket estabelecida
            for (Session session : sessions.values()) {
                session.getBasicRemote().sendObject(taskToSend);
                System.out.println("Task enviada");

            }
        } catch (IOException | EncodeException e) {
            System.out.println("Erro ao enviar a tarefa para o WebSocket: " + e.getMessage());
        }

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
}
