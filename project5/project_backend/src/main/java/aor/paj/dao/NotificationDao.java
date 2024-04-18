package aor.paj.dao;

import aor.paj.entity.MessageEntity;
import aor.paj.entity.NotificationEntity;
import aor.paj.entity.UserEntity;
import jakarta.ejb.Singleton;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class NotificationDao extends AbstractDao<MessageEntity>{
    public NotificationDao() {
        super(MessageEntity.class);
    }


    public List<NotificationEntity> findUnreadNotificationsByUser(UserEntity userEntity) {
        try {
            return em.createNamedQuery("Notification.findUnreadNotificationsByUser", NotificationEntity.class).setParameter("username", userEntity.getUsername()).getResultList();
        } catch (Exception e) {
            return null;
        }
    }
    public List<NotificationEntity> findAllNotificationsByUserUnRead(UserEntity userEntity) {
        try {
            return em.createNamedQuery("Notification.findAllNotificationsByUserUnRead", NotificationEntity.class).setParameter("username", userEntity.getUsername()).getResultList();

        } catch (Exception e) {
            return null;
        }

    }


    public List<NotificationEntity> findNotificationsByUser(UserEntity userEntity) {
        try {
            return em.createNamedQuery("Notification.findNotificationsByUser", NotificationEntity.class)
                    .setParameter("username", userEntity.getUsername())
                    .getResultList();
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }


    public boolean saveNotification(NotificationEntity notificationEntity) {
        try {
            em.merge(notificationEntity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
