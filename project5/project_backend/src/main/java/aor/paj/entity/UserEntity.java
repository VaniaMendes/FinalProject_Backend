package aor.paj.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="user")
@NamedQuery(name = "User.findUserByUsername", query = "SELECT u FROM UserEntity u WHERE u.username = :username")
@NamedQuery(name = "User.findUserByEmail", query = "SELECT u FROM UserEntity u WHERE u.email = :email")
@NamedQuery(name = "User.findUserByToken", query = "SELECT DISTINCT u FROM UserEntity u WHERE u.token = :token")
@NamedQuery(name = "User.findAllUsers", query = "SELECT u FROM UserEntity u " )
@NamedQuery(name="User.findUserByName", query = "SELECT u FROM UserEntity u WHERE u.firstName=:name OR u.lastName = :name")
@NamedQuery(name="User.findUserByTokenConfirmation", query = "SELECT u FROM UserEntity u WHERE u.tokenConfirmation = :tokenConfirmation")
@NamedQuery(name="User.findUserByNameStartingWith", query = "SELECT u FROM UserEntity u WHERE LOWER (u.firstName) LIKE LOWER (:prefix) OR LOWER (u.lastName) LIKE LOWER (:prefix)")

@NamedQuery(name="User.findUserByEmailStartingWith", query = "SELECT u FROM UserEntity u WHERE LOWER (u.email) LIKE LOWER (:prefix)")
@NamedQuery(name="User.totalUsers" , query = "SELECT COUNT(u) FROM UserEntity u WHERE u.username NOT IN ('admin', 'deletedUser')")
@NamedQuery(name="User.confirmedUsers", query = "SELECT COUNT(u) FROM UserEntity u WHERE u.isConfirmed = true AND u.username NOT IN ('admin', 'deletedUser')")
@NamedQuery(name="User.unconfirmedUsers", query = "SELECT COUNT(u) FROM UserEntity u WHERE u.isConfirmed = false AND u.username NOT IN ('admin', 'deletedUser')")
public class UserEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "username", nullable = false, unique = false, updatable = true)
	private String username;

	@Column(name = "email", nullable = false, unique = true, updatable = true)
	private String email;

	//user's name
	@Column(name = "firstName", nullable = false, unique = false, updatable = true)
	private String firstName;

	@Column(name = "lastName", nullable = false, unique = false, updatable = true)
	private String lastName;

	@Column(name = "phoneNumber", nullable = false, unique = false, updatable = true)
	private String phoneNumber;

	@Column(name = "token", nullable = true, unique = true, updatable = true)
	private String token;

	@Column(name = "password", nullable = false, unique = false, updatable = true)
	private String password;


	@Column(name = "imgURL", nullable = false, unique = false, updatable = true)
	private String imgURL;
	@Column(name = "isActive", nullable = false, unique = false, updatable = true)
	private boolean isActive;

	@Column(name = "isConfirmed", nullable = false, unique = false, updatable = true)
	private boolean isConfirmed;

	@Column(name = "typeOfUser", nullable = false, unique = false, updatable = true)
	private String typeOfUser;
	@Column(name = "token_Confirmation", nullable = true, unique = true, updatable = true)
	private String tokenConfirmation;

	@Column(name = "registerDate", nullable = true, unique = false, updatable = true)
	private LocalDate registerDate;
	@Column(name = "sessionTimeout", nullable = false, unique = false, updatable = true)
	private int sessionTimeout;


	@OneToMany(mappedBy = "owner")
	private Set<TaskEntity> tasks;
	@OneToMany(mappedBy = "sender")
	private Set<MessageEntity> messagesSent;

	@OneToMany(mappedBy = "receiver")
	private Set<MessageEntity> messagesReceived;


	//default empty constructor
	public UserEntity() {
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	public Set<TaskEntity> getTasks() {
		return tasks;
	}

	public void setTasks(Set<TaskEntity> tasks) {
		this.tasks = tasks;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public boolean getIsActive() {
		return isActive;
	}
	public boolean getIsConfirmed() {
		return isConfirmed;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getTypeOfUser() {
		return typeOfUser;
	}

	public void setTypeOfUser(String typeOfUser) {
		this.typeOfUser = typeOfUser;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public boolean isConfirmed() {
		return isConfirmed;
	}

	public void setConfirmed(boolean confirmed) {
		isConfirmed = confirmed;
	}

	public void setTokenConfirmation(String tokenConfirmation) {
		this.tokenConfirmation = tokenConfirmation;
	}

	public String getTokenConfirmation() {
		return tokenConfirmation;
	}

	public LocalDate getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(LocalDate registerDate) {
		this.registerDate = registerDate;
	}

	public Set<MessageEntity> getMessagesSent() {
		return messagesSent;
	}

	public void setMessagesSent(Set<MessageEntity> messagesSent) {
		this.messagesSent = messagesSent;
	}

	public Set<MessageEntity> getMessagesReceived() {
		return messagesReceived;
	}

	public void setMessagesReceived(Set<MessageEntity> messagesReceived) {
		this.messagesReceived = messagesReceived;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
}

