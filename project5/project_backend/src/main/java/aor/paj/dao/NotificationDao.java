package aor.paj.dao;

import aor.paj.entity.MessageEntity;
import aor.paj.entity.NotificationEntity;
import jakarta.ejb.Singleton;
import jakarta.ejb.Stateless;

@Stateless
public class NotificationDao extends AbstractDao<MessageEntity>{
    public NotificationDao() {
        super(MessageEntity.class);
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
