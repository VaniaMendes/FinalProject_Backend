package aor.paj.dao;

import aor.paj.entity.MessageEntity;
import aor.paj.entity.TaskEntity;
import aor.paj.entity.UserEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.util.List;

@Stateless
public class MessageDao extends AbstractDao<MessageEntity>{
    public MessageDao() {
        super(MessageEntity.class);
    }


    public MessageEntity findMessageById(long id) {
        try {
            return (MessageEntity) em.createNamedQuery("Message.findMessageById").setParameter("messageId", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public List<MessageEntity> findAllMessages() {
        try {
            List<MessageEntity> messageEntities = em.createNamedQuery("Message.findAllMessages").getResultList();
            return messageEntities;
        } catch (Exception e) {
            return null;
        }
    }


    public List<MessageEntity> findMessagesByUser(UserEntity userEntity) {
        try {
            List<MessageEntity> messageEntities = em.createNamedQuery("Message.findMessagesByUser").setParameter("username", userEntity.getUsername()).getResultList();
            return messageEntities;
        } catch (Exception e) {
            return null;
        }
    }


    public List<MessageEntity> findMessagesBetweenUsers(UserEntity user1, UserEntity user2) {
        try {
            List<MessageEntity> messageEntities = em.createNamedQuery("Message.findMessagesBetweenUsers").setParameter("username1", user1.getUsername()).setParameter("username2", user2.getUsername()).getResultList();
            return messageEntities;
        } catch (Exception e) {
            return null;
        }
    }
    public List<MessageEntity> findMessagesUnReadBetweenUsers(UserEntity user1, UserEntity user2) {
        try {
            List<MessageEntity> messageEntities = em.createNamedQuery("Message.findMessagesBetweenUsers").setParameter("username1", user1.getUsername()).setParameter("username2", user2.getUsername()).getResultList();
            return messageEntities;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean createMessage(MessageEntity messageEntity) {
        try {
            em.merge(messageEntity);

         return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateMessage(MessageEntity messageEntity){
        try {
            em.merge(messageEntity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
