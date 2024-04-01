package aor.paj.entity;

import aor.paj.dto.User;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="category")
@NamedQuery(name = "Category.findCategoryByTitle", query = "SELECT u FROM CategoryEntity u WHERE u.title = :title")
@NamedQuery(name = "Category.findCategoryById", query = "SELECT u FROM CategoryEntity u WHERE u.idCategory = :categoryId")
@NamedQuery(name = "Category.findAllCategories", query = "SELECT u FROM CategoryEntity u")
@NamedQuery(name = "Category.findCategoryByUser", query = "SELECT u FROM CategoryEntity u WHERE u.owner = :owner")

public class CategoryEntity implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="id_Category", nullable=false, unique = false, updatable = true)
    private long idCategory;

    @Column(name="title", nullable=false, unique = true, updatable = true)
    private String title;
    @Column(name="description", nullable=false, unique = false, updatable = true)
    private String description;

    @ManyToOne
    @JoinColumn(name="author", nullable = false, unique = false, updatable = true)
    private UserEntity owner;

    //default empty constructor
    public CategoryEntity() {}

    public long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(long idCategory) {
        this.idCategory = idCategory;
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


    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }
}
