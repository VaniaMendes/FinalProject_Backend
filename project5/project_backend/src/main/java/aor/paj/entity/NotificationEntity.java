package aor.paj.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "notification")

@NamedQuery(name = "Notification.findMessageById", query = "SELECT n FROM MessageEntity n WHERE n.id = :messageId")
@NamedQuery(name = "Notification.findAllMessages", query = "SELECT n FROM MessageEntity n")
@NamedQuery(name = "Notification.findMessagesByUser", query = "SELECT n FROM MessageEntity n WHERE n.sender = :username OR n.receiver = :username")


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


    @Column(name = "isRead", nullable = false, unique = false, updatable = true)
    private boolean read;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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
}

