package aor.paj.bean;

import aor.paj.dao.CategoryDao;
import aor.paj.dao.TaskDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.LoginDto;
import aor.paj.dto.User;
import aor.paj.entity.UserEntity;
import aor.paj.utils.EncryptHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserBeanTest {

    @Mock
    private UserDao userDao;


    @Mock
    private EncryptHelper encryptHelper;

    @InjectMocks
    private UserBean userBean;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }




    @Test
    void testUpdateUser_Successful() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setUsername("testUser");
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Doe");
        updatedUser.setPhoneNumber("987654321");
        updatedUser.setImgURL("https://newexample.com");
        updatedUser.setPassword("newPassword");
        UserEntity existingUser = new UserEntity();
        existingUser.setUsername("testUser");
        when(userDao.findUserByToken(anyString())).thenReturn(existingUser);
        when(userDao.update(existingUser)).thenReturn(true);

        // Act
        boolean updated = userBean.updateUser("validToken", updatedUser);

        // Assert
        assertTrue(updated);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setUsername("nonExistingUser");

        // Act
        boolean updated = userBean.updateUser("validToken", updatedUser);

        // Assert
        assertFalse(updated);
    }
}
