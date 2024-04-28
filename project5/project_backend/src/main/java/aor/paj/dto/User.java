package aor.paj.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@XmlRootElement
public class User {
    @XmlElement
    private String username;
    @XmlElement
    private String password;
    @XmlElement
    private String email;
    @XmlElement
    private String firstName;
    @XmlElement
    private String lastName;
    @XmlElement
    private String phoneNumber;
    @XmlElement
    private String imgURL;
    @XmlElement
    private String token;
    @XmlElement
    private boolean isActive;
    @XmlElement
    private boolean isConfirmed;
    @XmlElement
    private String typeOfUser;
    @XmlElement
    private String tokenConfirmation;

    @XmlElement
    private LocalDate registerDate;
    @XmlElement
    private int sessionTimeout;


    public User(String username, String password, String email, String firstName, String lastName,
                String phoneNumber, String imgURL, String token) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.imgURL = imgURL;
        this.token = token;
        this.typeOfUser = getTypeOfUser();
        this.isActive = true;
        this.isConfirmed = isConfirmed();
        // Gerar um token de confirmação
        this.tokenConfirmation = generateTokenConfirmation();
        this.registerDate = null;
        this.sessionTimeout = getSessionTimeout();
    }

    public User() {
    }

    // Método para gerar um token de confirmação
    private String generateTokenConfirmation() {
        // Aqui você pode adicionar a lógica para gerar um token de confirmação
        // Por exemplo, você pode usar java.util.UUID para gerar um token único
        return UUID.randomUUID().toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(String typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public String getTokenConfirmation() {
        return tokenConfirmation;
    }

    public void setTokenConfirmation(String tokenConfirmation) {
        this.tokenConfirmation = tokenConfirmation;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
}