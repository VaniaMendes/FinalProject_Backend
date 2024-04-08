package aor.paj.bean;

import aor.paj.dao.MessageDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.MessageDto;
import aor.paj.dto.User;
import aor.paj.entity.MessageEntity;
import aor.paj.entity.UserEntity;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Singleton
public class MessageBean {

    @Inject
    MessageDao messageDao;
    @Inject
    UserDao userDao;

    public MessageBean(){

    }

    public List<MessageDto> getMessagesBetweenUsers(String token, String username2) {
        UserEntity userEntity1 = userDao.findUserByToken(token);
        UserEntity userEntity2 = userDao.findUserByUsername(username2);

        List<MessageEntity> messageEntities = messageDao.findMessagesBetweenUsers(userEntity1, userEntity2);

        List<MessageDto> messageDtos = new ArrayList<>();
        if (messageEntities != null) {
            for (MessageEntity messageEntity : messageEntities) {
                MessageDto messageDto = new MessageDto();
                messageDto.setId(messageEntity.getId());
                messageDto.setContent(messageEntity.getContent());

                User sender = new User();
                sender.setUsername(messageEntity.getSender().getUsername());
                messageDto.setSender(sender);

                User receiver = new User();
                receiver.setUsername(messageEntity.getReceiver().getUsername());
                messageDto.setReceiver(receiver);

                messageDto.setRead(messageEntity.isRead());
                messageDto.setTimestamp(LocalDate.now());

                messageDtos.sort(Comparator.comparing(MessageDto::getTimestamp).reversed());
            }
        }

        return messageDtos;
    }

    public boolean sendMessage(String token, MessageDto messageDto) {
        UserEntity sender = userDao.findUserByToken(token);
        UserEntity receiver = userDao.findUserByUsername(messageDto.getReceiver().getUsername());

        if (sender == null || receiver == null) {
            return false;
        }
        if(sender.getUsername().equals(receiver.getUsername())){
            return false;
        }

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setId(messageDto.getId());
        messageEntity.setContent(messageDto.getContent());
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);
        messageEntity.setRead(false);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        messageEntity.setTimestamp(dateTime.format(formatter));

        try {
            messageDao.createMessage(messageEntity);
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao persistir a mensagem: " + e.getMessage());
            return false;
        }
    }
}
