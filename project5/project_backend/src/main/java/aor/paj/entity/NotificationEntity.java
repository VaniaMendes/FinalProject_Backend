package aor.paj.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "notification")

@NamedQuery(name = "Notification.findNotificationById", query = "SELECT n FROM NotificationEntity n WHERE n.id = :messageId")
@NamedQuery(name = "Notification.findAllNotifications", query = "SELECT n FROM NotificationEntity n")
@NamedQuery(name = "Notification.findNotificationsByUser", query = "SELECT n FROM NotificationEntity n WHERE n.receiver.username = :username ")
@NamedQuery(name = "Notification.findAllNotificationsByUserUnRead", query = "SELECT n FROM NotificationEntity n WHERE n.receiver.username=:username AND n.notificationRead = false")
@NamedQuery(name = "Notification.findUnreadNotificationsByUser", query = "SELECT n FROM NotificationEntity n WHERE n.receiver.username=:username AND n.notificationRead = false")

public class NotificationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name="content", nullable = false, updatable = true)
    private String content;

    @ManyToOne
    @JoinColumn(name="receiver", nullable = false, unique = false, updatable = false)
    private UserEntity receiver;
    @ManyToOne
    @JoinColumn(name="sender", nullable = false, unique = false, updatable = false)
    private UserEntity sender;


    @Column(name = "isRead", nullable = false, unique = false, updatable = true)
    private boolean notificationRead;
    @Column(name = "timestamp", nullable = false, unique = false, updatable = true)
    private String timestamp;

    public NotificationEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isNotificationRead() {
        return notificationRead;
    }

    public void setNotificationRead(boolean notificationRead) {
        this.notificationRead = notificationRead;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }
}

