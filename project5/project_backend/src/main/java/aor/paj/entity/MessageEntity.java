package aor.paj.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "messages")

@NamedQuery(name = "Message.findMessageById", query = "SELECT m FROM MessageEntity m WHERE m.id = :messageId")
@NamedQuery(name = "Message.findAllMessages", query = "SELECT m FROM MessageEntity m")
@NamedQuery(name = "Message.findMessagesByUser", query = "SELECT m FROM MessageEntity m WHERE m.sender = :username OR m.receiver = :username")

@NamedQuery(name = "Message.findMessagesBetweenUsers", query = "SELECT m FROM MessageEntity m WHERE m.sender.username = :username1 AND m.receiver.username = :username1 OR m.sender.username = :username2 AND m.receiver.username = :username2")
public class MessageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name="content", nullable = false, updatable = true)
    private String content;

    @ManyToOne
    @JoinColumn(name="sender", nullable = false, unique = false, updatable = false)
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name="receiver", nullable = false, unique = false, updatable = false)
    private UserEntity receiver;


    @Column(name = "isRead", nullable = false, unique = false, updatable = true)
    private boolean read;
    @Column(name = "timestamp", nullable = false, unique = false, updatable = true)
    private String timestamp;

    public MessageEntity() {
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

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
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
}
