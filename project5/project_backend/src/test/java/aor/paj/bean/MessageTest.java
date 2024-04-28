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
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
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
    public void testGetMessagesBetweenUsers_ReturnsEmptyListWhenNoMessagesFound() {
        // Arrange
        String token = "token";
        String username2 = "username2";
        UserEntity userEntity1 = new UserEntity();
        UserEntity userEntity2 = new UserEntity();
        when(userDao.findUserByToken(token)).thenReturn(userEntity1);
        when(userDao.findUserByUsername(username2)).thenReturn(userEntity2);

        // Simula as mensagens entre dois users
        when(messageDao.findMessagesBetweenUsers(userEntity1, userEntity2)).thenReturn(Collections.emptyList());

        // Act
        List<MessageDto> result = messageBean.getMessagesBetweenUsers(token, username2);

        // Assert
        assertTrue(result.isEmpty());
    }


    @Test
    public void testUpdateSenderMessage_WithNullMessageEntity_ReturnsFalse() {
        // Arrange
        MessageEntity messageEntity = null;

        // Act
        boolean result = messageBean.updateSenderMessage(messageEntity);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testUpdateSenderMessage_WithValidMessageEntity_ReturnsTrue() {
        // Arrange
        MessageEntity messageEntity = new MessageEntity();

        when(messageDao.updateMessage(messageEntity)).thenReturn(true);

        // Act
        boolean result = messageBean.updateSenderMessage(messageEntity);

        // Assert
        assertTrue(result);
        assertTrue(messageEntity.isMessageRead());
    }



}
