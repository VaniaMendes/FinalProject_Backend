package aor.paj.bean;

import aor.paj.dao.NotificationDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.NotificationDto;
import aor.paj.entity.NotificationEntity;
import aor.paj.entity.UserEntity;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.logging.log4j.*;


@Singleton
public class NotificationBean {

    @Inject
    UserDao userDao;
    @Inject
    NotificationDao notificationDao;
    @Inject
    UserBean userBean;

    private static final Logger logger = LogManager.getLogger(TaskBean.class);
    public NotificationBean() {

    }

    public boolean createNotification(String token, String content, String receiverUsername) {
        UserEntity receiver = userDao.findUserByUsername(receiverUsername);
        UserEntity sender = userDao.findUserByToken(token);

        if (receiver == null) {
            return false;
        }

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setContent(content);
        notificationDto.setReceiver(userBean.convertUserEntityToDTOforMessage(receiver));
        notificationDto.setSender(userBean.convertUserEntityToDTOforMessage(sender));
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setId(notificationDto.getId());
        notificationEntity.setContent(notificationDto.getContent());
        notificationEntity.setReceiver(receiver);
        notificationEntity.setSender(sender);
        notificationEntity.setNotificationRead(false);

        // Converter LocalDate para LocalDateTime e formatar como string
        LocalDateTime timestamp = LocalDateTime.now();
        String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        notificationEntity.setTimestamp(formattedTimestamp);

        try {
            notificationDao.saveNotification(notificationEntity);
            logger.info("Notification created: " + notificationEntity.toString() + " and sended by " + sender.getUsername() + " to " + receiver.getUsername());
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao persistir a notificação: " + e.getMessage());
            return false;
        }
    }

    //Vai buscar as notificações de um utilizador

    public List<NotificationDto> getNotificationsByToken(String token) {
        UserEntity receiver = userDao.findUserByToken(token);
        if (receiver == null) {
            return null;
        }

        List<NotificationEntity> notificationEntities = notificationDao.findNotificationsByUser(receiver);
        List<NotificationDto> notificationDtos = new ArrayList<>();
        if (notificationEntities != null) {
            for (NotificationEntity notificationEntity : notificationEntities) {
                NotificationDto notificationDto = new NotificationDto();
                notificationDto.setId(notificationEntity.getId());
                notificationDto.setContent(notificationEntity.getContent());
                notificationDto.setReceiver(userBean.convertUserEntityToDTOforMessage(notificationEntity.getReceiver()));
                notificationDto.setSender(userBean.convertUserEntityToDTOforMessage(notificationEntity.getSender()));
                LocalDateTime timestamp = LocalDateTime.parse(notificationEntity.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                notificationDto.setTimestamp(timestamp);
                notificationDto.setNotificationRead(notificationEntity.isNotificationRead());

                notificationDtos.add(notificationDto);
            }
            notificationDtos.sort(Comparator.comparing(NotificationDto::getId).reversed());
        }
        return notificationDtos;
    }


    public boolean markNotificationAsRead(String token) {
        UserEntity userEntity = userDao.findUserByToken(token);

        if (userEntity == null) {
            return false;
        }
        List<NotificationEntity> notificationsList = notificationDao.findAllNotificationsByUserUnRead(userEntity);

        boolean atLeastOneNotificationRead = false;
        for (NotificationEntity notificationEntity : notificationsList) {
            notificationEntity.setNotificationRead(true);
            notificationDao.saveNotification(notificationEntity);
            atLeastOneNotificationRead=true;
        }
        return atLeastOneNotificationRead;

    }

    public List<NotificationDto> getUnreadNotificationsByToken(String token) {
        UserEntity receiver = userDao.findUserByToken(token);
        if (receiver == null) {
            return null;
        }

        List<NotificationEntity> notificationEntities = notificationDao.findUnreadNotificationsByUser(receiver);

        List<NotificationDto> notificationDtos = new ArrayList<>();

        for(NotificationEntity notificationEntity : notificationEntities){
            notificationDtos.add(convertMessageEntitytoDto(notificationEntity));

        }
        return notificationDtos;
    }

    public NotificationDto convertMessageEntitytoDto (NotificationEntity notificationEntity){
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(notificationEntity.getId());
        notificationDto.setContent(notificationEntity.getContent());

        notificationDto.setReceiver(userBean.convertUserEntityToDTOforMessage(notificationEntity.getReceiver()));
        notificationDto.setNotificationRead(notificationEntity.isNotificationRead());
        LocalDateTime timestamp = LocalDateTime.parse(notificationEntity.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        notificationDto.setTimestamp(timestamp);

        return notificationDto;
    }

}
