package aor.paj.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncryptHelperTest {


    @Test
    public void encryptPassword() {
        // Arrange
        EncryptHelper helper = build();
        String password = "123";
        String hashedPassword = "202cb962ac59075b964b07152d234b70";

        // Act
        String result = helper.encryptPassword(password);

        // Assert
       assertEquals(hashedPassword, result);
    }

    EncryptHelper build() {
        EncryptHelper encryptHelper = new EncryptHelper();
        return  encryptHelper;
    }



}