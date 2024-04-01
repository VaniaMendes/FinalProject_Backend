package aor.paj.bean;

import aor.paj.dao.CategoryDao;
import aor.paj.dao.TaskDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.Category;
import aor.paj.dto.Task;
import aor.paj.entity.CategoryEntity;
import aor.paj.entity.TaskEntity;
import aor.paj.entity.UserEntity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
class TaskBeanTest {

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private UserDao userDao;

    @Mock
    private TaskDao taskDao;

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private TaskBean taskBean;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        userDao.setEm(mockEntityManager);

        when(userDao.findUserByToken("token")).thenReturn(new UserEntity());

        when(taskDao.findTaskByTitle(anyString())).thenReturn(null);
        when(taskDao.findTaskByTitle("existingTitle")).thenReturn(new TaskEntity());

        when(categoryDao.findCategoryById(anyLong())).thenReturn(new CategoryEntity());
    }

    @Test
    void testAddTask() {
        // Arrange
        Task task = new Task();
        String token = "token";
        String categoryId = "1";

        // Act
        boolean result = taskBean.addTask(token, task, categoryId);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsTaskTitleAvailable() {
        // Arrange
        Task task = new Task();
        task.setTitle("newTitle");

        // Act
        boolean result = taskBean.isTaskTitleAvailable(task);

        // Assert
        assertTrue(result);
    }

    @Test
    void testUpdateTask() {
        // Arrange
        String token = "token";
        String taskId = "1";
        Task task = new Task();
        String categoryId = "2";

        // Act
        boolean result = taskBean.updateTask(token, taskId, task, categoryId);

        // Assert
        assertFalse(result);
    }



    @Test
    void testUpdateTaskCategory() {
        // Arrange
        String token = "token";
        String taskId = "1";
        Category category = new Category();


        // Act
        boolean result = taskBean.updateTaskCategory(token, taskId, category);

        // Assert
        assertFalse(result);
    }

    @Test
    void testDeleteTasksByUsername() {
        // Arrange
        String username = "testUser";

        // Act
        boolean result = taskBean.deleteTasksByUsername(username);

        // Assert
        assertFalse(result);
    }



}
