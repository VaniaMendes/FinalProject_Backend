package aor.paj.bean;
import aor.paj.websocket.WebSocketMessage;


import aor.paj.dao.MessageDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.MessageDto;
import aor.paj.entity.MessageEntity;
import aor.paj.entity.UserEntity;
import aor.paj.websocket.Notifier;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Singleton
public class MessageBean {

    @Inject
    MessageDao messageDao;
    @Inject
    UserBean userBean;
    @Inject
    UserDao userDao;
    @Inject
    Notifier notifier;
    @Inject
    WebSocketMessage webSocketMessage;
    @Inject
    NotificationBean notificationBean;


    public MessageBean(){

    }

    public List<MessageDto> getMessagesBetweenUsers(String token, String username2) {
        UserEntity userEntity1 = userDao.findUserByToken(token);
        UserEntity userEntity2 = userDao.findUserByUsername(username2);

        List<MessageEntity> messageEntities = messageDao.findMessagesBetweenUsers(userEntity1, userEntity2);

        List<MessageDto> messageDtos = new ArrayList<>();
        if (messageEntities != null && !messageEntities.isEmpty()) {
            for (MessageEntity messageEntity : messageEntities) {
                MessageDto messageDto = new MessageDto();
                messageDto.setId(messageEntity.getId());
                messageDto.setContent(messageEntity.getContent());
                messageDto.setSender(userBean.convertUserEntityToDTOforMessage(messageEntity.getSender()));
                messageDto.setReceiver(userBean.convertUserEntityToDTOforMessage(messageEntity.getReceiver()));

                messageDto.setMessageRead(messageEntity.isMessageRead());
                // Converter a String do timestamp para LocalDateTime
                LocalDateTime timestamp = LocalDateTime.parse(messageEntity.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                messageDto.setTimestamp(timestamp);


                messageDtos.add(messageDto);
            }
            messageDtos.sort(Comparator.comparing(MessageDto::getId));
        }


        return messageDtos;
    }

    public boolean sendMessage(String token, MessageDto messageDto) {
        UserEntity sender = userDao.findUserByToken(token);
        UserEntity receiver = userDao.findUserByUsername(messageDto.getReceiver().getUsername());

        if (sender == null || receiver == null) {
            return false;
        }
        if (sender.getUsername().equals(receiver.getUsername())) {
            return false;
        }

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(messageDto.getId());
        messageEntity.setContent(messageDto.getContent());
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);
        messageEntity.setMessageRead(false);

        // Converter LocalDateTime converter para String
        LocalDateTime timestamp = messageDto.getTimestamp();
        String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        messageEntity.setTimestamp(formattedTimestamp);

        try {
            messageDao.createMessage(messageEntity);
            System.out.println("mensagem persistida");

            // Enviar a mensagem para o WebSocket
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            try {
                String jsonMsg = mapper.writeValueAsString(convertMessageEntityToDto(messageEntity));
                System.out.println("Serialized message: " + jsonMsg);
                webSocketMessage.toDoOnMessage(jsonMsg);
            } catch (Exception e) {
                System.out.println("Erro ao serializar a mensagem: " + e.getMessage());
            }

            System.out.println("mensagem enviada");

            return true;
        } catch (Exception e) {
            System.out.println("Erro ao persistir a mensagem: " + e.getMessage());
            return false;
        }
    }


    public boolean markMessagesAsRead(String token, long id, String username) {
        UserEntity receiver = userDao.findUserByToken(token);
        UserEntity sender = userDao.findUserByUsername(username);

        List<MessageEntity> messageEntities = messageDao.findMessagesUnReadBetweenUsers(receiver, sender);
        System.out.println("Mensagens não lidas: " + messageEntities.size());

        boolean atLeastOneMessageRead = false;
        for (MessageEntity messageEntity : messageEntities) {
            if (messageEntity.getId() <= id) {
                if (messageEntity.getSender().equals(sender)) {
                    if (updateSenderMessage(messageEntity)) {

                        // Enviar a mensagem para o WebSocket
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.registerModule(new JavaTimeModule());

                        try {
                            String jsonMsg = mapper.writeValueAsString(convertMessageEntityToDto(messageEntity));
                            System.out.println("Serialized message: " + jsonMsg);
                            webSocketMessage.toDoOnMessage(jsonMsg);
                        } catch (Exception e) {
                            System.out.println("Erro ao serializar a mensagem: " + e.getMessage());
                        }
                        atLeastOneMessageRead = true;
                    }
                }
            }
        }

        return atLeastOneMessageRead;
    }


    public boolean updateSenderMessage(MessageEntity messageEntity) {

        if (messageEntity == null) {
            return false;
        } else {
            messageEntity.setMessageRead(true);
            return messageDao.updateMessage(messageEntity);
        }
    }



        public MessageDto convertMessageEntityToDto(MessageEntity messageEntity) {
            MessageDto messageDto = new MessageDto();
            messageDto.setId(messageEntity.getId());
            messageDto.setContent(messageEntity.getContent());
            messageDto.setSender(userBean.convertUserEntityToDTOforMessage(messageEntity.getSender()));
            messageDto.setReceiver(userBean.convertUserEntityToDTOforMessage(messageEntity.getReceiver()));
            messageDto.setMessageRead(messageEntity.isMessageRead());
            // Converter a String do timestamp para LocalDateTime
            LocalDateTime timestamp = LocalDateTime.parse(messageEntity.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            messageDto.setTimestamp(timestamp);

            return messageDto;
        }

}
