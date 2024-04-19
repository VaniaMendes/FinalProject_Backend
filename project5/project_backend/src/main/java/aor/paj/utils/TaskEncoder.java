package aor.paj.utils;

import aor.paj.dto.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TaskEncoder implements Encoder.Text<Task> {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public String encode(Task task) throws EncodeException {
        try {
            return objectMapper.writeValueAsString(task);
        } catch (JsonProcessingException e) {
            throw new EncodeException(task, e.getMessage(), e);
        }
    }

    @Override
    public void init(EndpointConfig config) {
        // no op
    }

    @Override
    public void destroy() {
        // no op
    }
}
