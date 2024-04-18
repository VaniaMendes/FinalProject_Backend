package aor.paj.dto;

import aor.paj.entity.UserEntity;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@XmlRootElement
public class NotificationDto {


    @XmlElement
    private long id;
    @XmlElement
    private String content;
    @XmlElement
    private User receiver;

    @XmlElement
    private LocalDateTime timestamp;
    @XmlElement
    private boolean notificationRead;

    public NotificationDto() {

        this.id = new Date().getTime();
        this.content = null;
        this.receiver = null;
        this.timestamp = LocalDateTime.now();
        this.notificationRead = false;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isNotificationRead() {
        return notificationRead;
    }

    public void setNotificationRead(boolean notificationRead) {
        this.notificationRead = notificationRead;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }
}
