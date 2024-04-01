package aor.paj.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Date;


@XmlRootElement
public class Category {

    @XmlElement
    private long idCategory;
    @XmlElement
    private String title;
    @XmlElement
    private String description;
    @XmlElement
    private User author;

    public Category() {
        Date idTime=new Date();
        this.idCategory =  idTime.getTime();
        this.title = null;
        this.description = null;
        this.author = null;
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

    public long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(long idCategory) {
        this.idCategory = idCategory;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}


