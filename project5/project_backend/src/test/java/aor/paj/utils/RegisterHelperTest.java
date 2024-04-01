package aor.paj.utils;

import aor.paj.bean.UserBean;
import aor.paj.dao.UserDao;
import aor.paj.dto.User;
import aor.paj.entity.UserEntity;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterHelperTest {

    @Mock
    private UserDao userDao;

    @Mock
    private EncryptHelper encryptHelper;

    @Mock
    private UserBean userBean;

    @InjectMocks
    private RegisterHelper registerHelper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void register_Should_ReturnTrue_When_UserDoesNotExist() {
        // Arrange
        User user = new User();
        user.setPassword("vaniaaaa");
        user.setUsername("vaniaaaa");
        user.setEmail("emaiaaal@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("123456789");
        user.setImgURL("https://example.com/image.jpg");
        user.setToken("token");
        user.setActive(true);
        user.setTypeOfUser("regular");

        // Mocking behavior of userDao and encryptHelper
        when(userDao.findUserByUsername(user.getUsername())).thenReturn(null);
        when(encryptHelper.encryptPassword(user.getPassword())).thenReturn("hashedPassword");

        // Act
        boolean result = registerHelper.register(user);

        // Assert
        assertTrue(result);
        verify(userDao, times(1)).persist(any(UserEntity.class));
    }


    @Test
    public void register_Should_ReturnFalse_When_UserExists() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("password123");

        // Mockando o comportamento do userDao para retornar um usuário existente
        when(userDao.findUserByUsername(user.getUsername())).thenReturn(new UserEntity());

        // Act
        boolean result = registerHelper.registerUserAlreadyExists(user);

        // Assert
        assertFalse(result);
        verify(userDao, never()).persist(any(UserEntity.class));
    }


    @Test
    public void register_Should_ThrowException_When_EmailIsInvalid() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("password123");
        user.setEmail("invalid-email");

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerHelper.registerWithInvalidEmail(user);
        });

        assertEquals("Invalid email format", exception.getMessage());
        verify(userDao, never()).persist(any(UserEntity.class));
    }


    @Test
    public void register_Should_ReturnFalse_When_EmailExists() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("password123");
        user.setEmail("john.doe@example.com");

        // Mocking behavior of userDao
        when(userDao.findUserByUsername(user.getUsername())).thenReturn(null);
        when(userDao.findUserByEmail(user.getEmail())).thenReturn(new UserEntity());

        // Act
        boolean result = registerHelper.registerWithAnExistingEmail(user);

        // Assert
        assertFalse(result);
        verify(userDao, never()).persist(any(UserEntity.class));
    }


    @Test
    public void register_Should_ThrowException_When_PhoneNumberIsInvalid() {
        // Arrange
        User user = new User();
        user.setUsername("john_doe");
        user.setPassword("password123");
        user.setPhoneNumber("3456");

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerHelper.registerWithInvalidPhoneNumber(user);
        });

        assertEquals("Invalid Phone Number", exception.getMessage());
        verify(userDao, never()).persist(any(UserEntity.class));
    }

}








