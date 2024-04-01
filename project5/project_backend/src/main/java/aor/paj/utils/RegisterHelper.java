package aor.paj.utils;

import aor.paj.bean.UserBean;
import aor.paj.dao.UserDao;
import aor.paj.dto.User;
import aor.paj.entity.UserEntity;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Singleton
public class RegisterHelper {
    @EJB
    private UserDao userDao;

    @Inject
    private EncryptHelper encryptHelper;

    @Inject
    UserBean userBean;

    public RegisterHelper() {
        try {
            this.m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    MessageDigest m;

    public boolean register(User userEntity) {
        if (userEntity == null || userEntity.getPassword() == null) {
            throw new IllegalArgumentException("User or password cannot be null");
        }

        UserEntity existingUser = userDao.findUserByUsername(userEntity.getUsername());
        if (existingUser != null) {
            // User já existe, lançar exceção ou retornar false
            throw new IllegalArgumentException("User already exists");
        } else {
            UserEntity newUserEntity = convertDtoToEntity(userEntity);
            userDao.persist(newUserEntity);
            return true;
        }
    }

    public boolean registerUserAlreadyExists(User userEntity) {
        if (userEntity == null || userEntity.getUsername() == null) {
            throw new IllegalArgumentException("User or username cannot be null");
        }

        UserEntity existingUser = userDao.findUserByUsername(userEntity.getUsername());
        if (existingUser != null) {
            // User já existe
            return false;
        } else {
            // User não existe
            return true;
        }
    }

    public boolean registerWithInvalidEmail(User userEntity) {
        if (userEntity == null || userEntity.getUsername() == null) {
            throw new IllegalArgumentException("User or username cannot be null");
        }

        UserEntity existingUser = userDao.findUserByUsername(userEntity.getUsername());
        if (existingUser != null) {

            return false;
        } else {

            if (userEntity.getEmail() == null || !isValidEmail(userEntity.getEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }

            UserEntity newUserEntity = convertDtoToEntity(userEntity);
            userDao.persist(newUserEntity);
            return true;
        }
    }

    public boolean registerWithAnExistingEmail(User userEntity) {
        if (userEntity == null || userEntity.getEmail() == null) {
            throw new IllegalArgumentException("User or email cannot be null");
        }

        UserEntity existingUserByEmail = userDao.findUserByEmail(userEntity.getEmail());
        if (existingUserByEmail != null) {
            return false;
        }

        UserEntity existingUserByUsername = userDao.findUserByUsername(userEntity.getUsername());
        if (existingUserByUsername != null) {
            return false;
        }

        UserEntity newUserEntity = convertDtoToEntity(userEntity);
        userDao.persist(newUserEntity);
        return true;
    }

    public boolean registerWithInvalidPhoneNumber(User userEntity) {
        if (userEntity == null || userEntity.getPhoneNumber() == null) {
            throw new IllegalArgumentException("User or phone number cannot be null");
        }

        if (!isValidPhoneNumber(userEntity.getPhoneNumber())) {
            throw new IllegalArgumentException("Invalid Phone Number");
        }

        // Verifica se o email já está em uso
        UserEntity existingUserByEmail = userDao.findUserByEmail(userEntity.getEmail());
        if (existingUserByEmail != null) {
            return false;
        }

        // Verifica se o nome de usuário já está em uso
        UserEntity existingUserByUsername = userDao.findUserByUsername(userEntity.getUsername());
        if (existingUserByUsername != null) {
            return false;
        }

        // Se estiver tudo  ok avança
        UserEntity newUserEntity = convertDtoToEntity(userEntity);
        userDao.persist(newUserEntity);
        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        boolean isValid = false;
        if(userBean.isValidPhoneNumber(phoneNumber)){
            isValid = true;
        }
        return isValid;
    }



    private boolean isValidEmail(String email) {
        boolean isValid = false;

        if(userBean.isValidEmail(email)){
            isValid = true;
        }
        return isValid;

    }


    public UserEntity convertDtoToEntity(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUsername());
        userEntity.setPassword(user.getPassword());
        userEntity.setToken(user.getToken());
        userEntity.setEmail(user.getEmail());
        userEntity.setPhoneNumber(user.getPhoneNumber());
        userEntity.setImgURL(user.getImgURL());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setTypeOfUser(user.getTypeOfUser());
        return userEntity;
    }
    public User convertEntityToDto(UserEntity userEntity) {
        if (userEntity == null) {
            throw new IllegalArgumentException("UserEntity cannot be null");
        }

        User user = new User();
        user.setUsername(userEntity.getUsername());
        user.setPassword(userEntity.getPassword());
        user.setToken(userEntity.getToken());
        user.setEmail(userEntity.getEmail());
        user.setPhoneNumber(userEntity.getPhoneNumber());
        user.setImgURL(userEntity.getImgURL());
        user.setFirstName(userEntity.getFirstName());
        user.setLastName(userEntity.getLastName());
        user.setTypeOfUser(userEntity.getTypeOfUser());
        return user;
    }

}
