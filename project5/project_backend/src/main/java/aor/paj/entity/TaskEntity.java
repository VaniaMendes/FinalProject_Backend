package aor.paj.entity;

import aor.paj.dto.Category;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name="task")
@NamedQuery(name="Task.findTaskById", query="SELECT a FROM TaskEntity a WHERE a.id = :idTask")
@NamedQuery(name="Task.findTaskByTitle", query="SELECT a FROM TaskEntity a WHERE a.title = :title")
@NamedQuery(name="Task.findTaskByUser", query="SELECT a FROM TaskEntity a WHERE a.owner = :owner")
@NamedQuery(name = "Task.findActiveTasks", query = "SELECT a FROM TaskEntity a WHERE a.isActive = true")
@NamedQuery(name = "Task.findSoftDeletedTasks", query = "SELECT a FROM TaskEntity a WHERE a.isActive = false")
@NamedQuery(name = "Task.findTasksByCategory", query = "SELECT t FROM TaskEntity t WHERE t.category = :category")
@NamedQuery(name = "Task.findTaskByCategoryName", query =" SELECT t FROM TaskEntity t JOIN t.category c WHERE c.title = :categoryName")
@NamedQuery(name = "Task.findAllTasks", query = "SELECT t FROM TaskEntity t")
@NamedQuery(name = "Task.findFilterTasks", query = "SELECT t FROM TaskEntity t WHERE t.category.idCategory = :category AND t.owner.username=:username")
@NamedQuery(name="Task.findTaskByUserNameFilter", query="SELECT a FROM TaskEntity a WHERE a.owner.username = :username")
@NamedQuery(name = "Task.findTasksByCategoryFilter", query = "SELECT t FROM TaskEntity t WHERE t.category.idCategory = :category")
@NamedQuery(name = "Task.findTotalTasksByUSer", query = "SELECT COUNT(t) FROM TaskEntity t WHERE t.owner.username = :username")
@NamedQuery(name = "Task.countTasksByStateForUser", query = "SELECT COUNT(t) FROM TaskEntity t WHERE t.owner.username = :username AND t.state = :state AND t.isActive")
@NamedQuery(name= "Task.findCategoriesOrderedByTaskCount", query = "SELECT t.category FROM TaskEntity t GROUP BY t.category ORDER BY COUNT(t) DESC")

@NamedQuery(name = "Task.findCompletedTasks", query = "SELECT t FROM TaskEntity t WHERE t.state = 'done'")

@NamedQuery(name = "Task.countTasksByTitle", query = "SELECT COUNT(t) FROM TaskEntity t WHERE t.category.title = :title")

public class TaskEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column (name="id", nullable = false, unique = true, updatable = false)
	private long id;

	@Column (name="title", nullable = false, unique = true, updatable = true)
	private String title;

	@Column (name="description", nullable = true, unique = false, length = 65535, columnDefinition = "TEXT")
	private String description;

	@Column(name="initialDate", nullable = false, unique = false, updatable = true)
	private LocalDate initialDate;

	@Column(name="endDate", nullable = false, unique = false, updatable = true)
	private LocalDate endDate;
	@Column(name="conclusionDate", nullable = true, unique = false, updatable = true)
	private LocalDate conslusionDate;

	@Column(name="startDate", nullable = true, unique = false, updatable = true)
	private LocalDate startDate;
	@Column(name="priority", nullable = false, unique = false, updatable = true)
	private int priority;

	@Column(name="state", nullable = false, unique = false, updatable = true)
	private String state;

	@Column(name="is_Active", nullable = false, unique = false, updatable = true)
	private boolean isActive;

	@ManyToOne
	@JoinColumn(name="category", nullable = false, unique = false, updatable = true)
	private CategoryEntity category;

	//Owning Side User - Activity
	@ManyToOne
	@JoinColumn(name="author", nullable = false, unique = false, updatable = true)
	private UserEntity owner;

	public TaskEntity() {
		
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public LocalDate getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(LocalDate initialDate) {
		this.initialDate = initialDate;
	}

	public UserEntity getOwner() {
		return owner;
	}

	public void setOwner(UserEntity owner) {
		this.owner = owner;
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

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
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

	public void setState(String state) {
		this.state = state;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public CategoryEntity getCategory() {
		return category;
	}

	public void setCategory(CategoryEntity category) {
		this.category = category;
	}

	public LocalDate getConslusionDate() {
		return conslusionDate;
	}

	public void setConslusionDate(LocalDate conslusionDate) {
		this.conslusionDate = conslusionDate;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
}
	
    