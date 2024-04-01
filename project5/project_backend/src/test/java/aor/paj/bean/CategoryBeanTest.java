package aor.paj.bean;

import aor.paj.dao.AbstractDao;
import aor.paj.dao.CategoryDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.Category;

import aor.paj.entity.CategoryEntity;
import aor.paj.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
class CategoryBeanTest {

    @Mock
    private EntityManager mockEntityManager;

    @Mock
    private UserDao userDao;

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryBean categoryBean;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        userDao.setEm(mockEntityManager);

        Query mockQuery = Mockito.mock(Query.class);
        when(mockEntityManager.createNamedQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(new UserEntity());

        when(userDao.findUserByToken("token")).thenReturn(new UserEntity());
    }


    @Test
    void testUpdateCategory() {
        // Arrange
        String token = "token";
        String id = "1";
        Category category = new Category();


        when(categoryDao.findCategoryById(anyLong())).thenReturn(new CategoryEntity());

        // Act
        boolean result = categoryBean.updateCategory(token, id, category);

        // Assert
        assertTrue(result);
    }

    @Test
    void testDeleteCategory() {
        // Arrange
        String token = "token";
        String id = "1";


        when(categoryDao.findCategoryById(anyLong())).thenReturn(new  CategoryEntity());

        // Act
        boolean result = categoryBean.deleteCategory(token, id);

        // Assert
        assertTrue(result);
    }


    @Test
    void testDeleteCategory_CategoryNotFound() {
        // Arrange
        String token = "token";
        String id = "1";


        when(categoryDao.findCategoryById(anyLong())).thenReturn(null);

        // Act
        boolean result = categoryBean.deleteCategory(token, id);

        // Assert
        assertFalse(result);
    }

    @Test
    void testUpdateCategory_CategoryNotFound() {
        // Arrange
        String token = "token";
        String id = "1";
        Category category = new Category();


        when(categoryDao.findCategoryById(anyLong())).thenReturn(null);

        // Act
        boolean result = categoryBean.updateCategory(token, id, category);

        // Assert
        assertFalse(result);
    }


}