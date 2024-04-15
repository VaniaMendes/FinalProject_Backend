package aor.paj.dto;

import aor.paj.entity.UserEntity;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDate;
import java.util.Date;

@XmlRootElement
public class NotificationDto {


    @XmlElement
    private long id;
    @XmlElement
    private String content;
    @XmlElement
    private UserEntity receiver;

    @XmlElement
    private LocalDate timestamp;
    @XmlElement
    private boolean read;

    public NotificationDto() {

        this.id = new Date().getTime();
        this.content = null;
        this.receiver = null;
        this.timestamp = LocalDate.now();
        this.read = false;
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

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity receiver) {
        this.receiver = receiver;
    }
}
