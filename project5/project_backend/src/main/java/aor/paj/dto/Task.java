package aor.paj.dto;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@XmlRootElement
public class Task  {
    @XmlElement
    private long id;
    @XmlElement
    private String title;
    @XmlElement
    private String description;
    @XmlElement
    private LocalDate initialDate;
    @XmlElement
    private LocalDate endDate;
    @XmlElement
    private int priority;
    @XmlElement
    private String state;

    @XmlElement
    private boolean isActive;
    @XmlElement
    private User author;
    @XmlElement
    private Category category;

    public Task() {

        Date idTime=new Date();
        this.id =  idTime.getTime();
        this.title = null;
        this.description = null;
        this.initialDate = null;
        this.endDate = null;
        this.priority=100;
        this.state="toDo";
        this.author=null;
        this.category = null;
        this.isActive = true;
    }


    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getState() {
        return state;
    }

    public void changeState(String state) {
        this.state = state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {

        this.id =  id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate finalDate) {
        this.endDate = finalDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}