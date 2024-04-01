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
    void testLoginDB_IncorrectPassword() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testUser");
        loginDto.setPassword("wrongPassword");
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testUser");
        userEntity.setPassword("encryptedPassword");
        when(userDao.findUserByUsername("testUser")).thenReturn(userEntity);
        when(encryptHelper.encryptPassword("wrongPassword")).thenReturn("wrongEncryptedPassword");

        // Act
        String token = userBean.loginDB(loginDto);

        // Assert
        assertNull(token);
    }

    @Test
    void testLoginDB_UserNotFound() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("nonExistingUser");
        loginDto.setPassword("testPassword");
        when(userDao.findUserByUsername("nonExistingUser")).thenReturn(null);

        // Act
        String token = userBean.loginDB(loginDto);

        // Assert
        assertNull(token);
    }

    @Test
    void testRegister_Successful() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("password");
        when(userDao.findUserByUsername("newUser")).thenReturn(null);
        when(encryptHelper.encryptPassword("password")).thenReturn("encryptedPassword");

        // Act
        boolean registered = userBean.register(newUser);

        // Assert
        assertTrue(registered);
    }

    @Test
    void testRegister_UserAlreadyExists() {
        // Arrange
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        existingUser.setPassword("password");
        when(userDao.findUserByUsername("existingUser")).thenReturn(new UserEntity());

        // Act
        boolean registered = userBean.register(existingUser);

        // Assert
        assertFalse(registered);
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
