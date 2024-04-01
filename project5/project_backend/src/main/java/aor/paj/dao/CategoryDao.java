package aor.paj.dao;

import aor.paj.entity.CategoryEntity;
import aor.paj.entity.TaskEntity;
import aor.paj.entity.UserEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;

@Stateless
public class CategoryDao  extends AbstractDao<CategoryEntity> {

    public CategoryDao() {
        super(CategoryEntity.class);
    }


    public CategoryEntity findCategoryById(long id) {
        try {
            return (CategoryEntity) em.createNamedQuery("Category.findCategoryById").setParameter("categoryId", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public CategoryEntity findCategoryByTitle(String title) {
        try {
            return (CategoryEntity) em.createNamedQuery("Category.findCategoryByTitle").setParameter("title", title)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }

    }

    public ArrayList<CategoryEntity> findAllCategories() {
        try {
            ArrayList<CategoryEntity> categoryEntities = (ArrayList<CategoryEntity>) em.createNamedQuery("Category.findAllCategories").getResultList();
            return categoryEntities;
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<CategoryEntity> findCategoriesByUser(UserEntity userEntity) {
        try {
            ArrayList<CategoryEntity> categoryEntities = (ArrayList<CategoryEntity>) em.createNamedQuery("Category.findCategoryByUser").setParameter("owner", userEntity).getResultList();
            return categoryEntities;
        } catch (Exception e) {
            return null;
        }
    }
}
