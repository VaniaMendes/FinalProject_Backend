package aor.paj.bean;

import aor.paj.dao.NotificationDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.MessageDto;
import aor.paj.dto.NotificationDto;
import aor.paj.entity.MessageEntity;
import aor.paj.entity.NotificationEntity;
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
public class NotificationBean {

    @Inject
    UserDao userDao;
    @Inject
    NotificationDao notificationDao;
    public NotificationBean() {

    }

    public boolean createNotification( String content, String receiverUsername) {
        UserEntity receiver = userDao.findUserByUsername(receiverUsername);

        if (receiver == null) {
            return false;
        }

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setContent(content);
        notificationDto.setReceiver(receiver);
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setId(notificationDto.getId());
        notificationEntity.setContent(notificationDto.getContent());
        notificationEntity.setReceiver(receiver);
        notificationEntity.setRead(false);

        // Converter LocalDate para LocalDateTime e formatar como string
        LocalDateTime timestamp = LocalDateTime.now(); // assume que a hora é 00:00:00
        String formattedTimestamp = timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        notificationEntity.setTimestamp(formattedTimestamp);

        try {
            notificationDao.saveNotification(notificationEntity);
            return true;
        } catch (Exception e) {
            System.out.println("Erro ao persistir a notificação: " + e.getMessage());
            return false;
        }
    }

}
