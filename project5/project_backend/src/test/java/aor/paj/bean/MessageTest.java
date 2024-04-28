package aor.paj.bean;
import aor.paj.dao.MessageDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.MessageDto;
import aor.paj.dto.User;
import aor.paj.entity.MessageEntity;
import aor.paj.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MessageTest {

    @Mock
    MessageDao messageDao;

    @Mock
    UserBean userBean;
    @Mock
    UserDao userDao;

    @InjectMocks
    MessageBean messageBean;

    @Test
    public void testGetMessagesBetweenUsers() {
        // Arrange
        String token = "token";
        String username2 = "username2";
        UserEntity userEntity1 = new UserEntity();
        UserEntity userEntity2 = new UserEntity();
        when(userDao.findUserByToken(token)).thenReturn(userEntity1);
        when(userDao.findUserByUsername(username2)).thenReturn(userEntity2);

        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        when(messageDao.findMessagesBetweenUsers(userEntity1, userEntity2)).thenReturn(Arrays.asList(messageEntity));

        // Act
        List<MessageDto> result = messageBean.getMessagesBetweenUsers(token, username2);

        // Assert
        assertEquals(1, result.size());
    }


    @Test
    public void testMarkMessagesAsRead() {
        // Arrange
        String token = "token";
        long id = 1L;
        String username = "username";
        UserEntity receiver = new UserEntity();
        UserEntity sender = new UserEntity();
        when(userDao.findUserByToken(token)).thenReturn(receiver);
        when(userDao.findUserByUsername(username)).thenReturn(sender);
        MessageEntity message = new MessageEntity();
        message.setSender(sender); // Definir o remetente da mensagem
        when(messageDao.findMessagesUnReadBetweenUsers(receiver, sender)).thenReturn(Arrays.asList(message));

        // Act
        boolean result = messageBean.markMessagesAsRead(token, id, username);

        // Assert
        assertTrue(result);
    }



    }
