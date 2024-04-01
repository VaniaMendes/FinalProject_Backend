package aor.paj.service;


import aor.paj.bean.CategoryBean;
import aor.paj.bean.TaskBean;
import aor.paj.bean.UserBean;
import aor.paj.dto.Category;
import aor.paj.dto.Task;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;



@Path("/categories")
public class CategoryService {

    @Inject
    TaskBean taskBean;

    @Inject
    UserBean userBean;

    @Inject
    CategoryBean categoryBean;


    @POST
    @Path("/createCategory")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addCategory(@HeaderParam("token") String token, Category category) {
        Response response;

        if (!categoryBean.isUserAllowedToInteractWithCategories(token)) {
            response = Response.status(403).entity("You dont have permissions to do that").build();

        } else if (category.getTitle() == null || category.getTitle().isEmpty()) {
            response = Response.status(422).entity("Category needs to be filled").build();


        } else if (!categoryBean.isCategoryTitleAvailable(category)) {
            response = Response.status(422).entity("Category name already in use").build();

        } else if (categoryBean.addCategory(token, category)) {
            response = Response.status(200).entity("A new category is created").build();

        } else {
            response = Response.status(403).entity("Invalid Token").build();
        }

        return response;
    }

    @PUT
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCategory(@HeaderParam("token") String token, @PathParam("id") String id, Category category) {

        Response response;

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (!categoryBean.isUserAllowedToInteractWithCategories(token)) {
            response = Response.status(403).entity("You dont have enough permissions").build();

        } else if (category.getTitle().trim().isEmpty()) {
            response = Response.status(422).entity("Title is required").build();

        } else if (!categoryBean.isCategoryTitleAvailableToUpdate(category)) {
            response = Response.status(422).entity("Title not available").build();

        } else if (categoryBean.updateCategory(token, id, category)) {
            response = Response.status(200).entity("Category updated sucessfully").build();

        } else
            response = Response.status(400).entity("Failed to update category").build();



        return response;
    }

    @DELETE
    @Path("/delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteCategory(@HeaderParam("token") String token, @PathParam("id") String id) {

        Response response;

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (!categoryBean.isUserAllowedToInteractWithCategories(token)) {
            response = Response.status(422).entity("You dont have enough permissions").build();

        } else if (categoryBean.isCategoryInUse(id)) {
            response = Response.status(422).entity("There are tasks with this category, cant delete it.").build();

        } else if (categoryBean.deleteCategory(token, id)) {
            response = Response.status(200).entity("Category deleted successfully").build();

        } else {
            response = Response.status(400).entity("Failed to delete category").build();
        }

        return response;
    }

    @GET
    @Path("/getAllCategories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCategories(@HeaderParam("token") String token) {
        Response response;

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (categoryBean.getAllCategories(token) == null) {
            response = Response.status(400).entity("Failed to retrieve categories").build();

        } else {
            response = Response.status(200).entity(categoryBean.getAllCategories(token)).build();
        }

        return response;
    }

    @GET
    @Path("/getCategoryById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategoryById(@HeaderParam("token") String token, @PathParam("id") String id) {
        Response response;

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (!categoryBean.isUserAllowedToInteractWithCategories(token)) {
            response = Response.status(422).entity("You dont have enough permissions").build();

        } else if (categoryBean.getCategoryById(token, id) == null) {
            response = Response.status(400).entity("Failed to retrieve category").build();

        } else {
            response = Response.status(200).entity(categoryBean.getCategoryById(token, id)).build();
        }

        return response;
    }

    @GET
    @Path("/category/{title}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCategoryIDByTitle(@HeaderParam("token") String token, @PathParam("title") String title) {
        Response response;

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();


        } else if (categoryBean.getCategoryIdByTitle(token, title) == null) {
            response = Response.status(400).entity("Failed to retrieve category").build();

        } else {
            response = Response.status(200).entity(categoryBean.getCategoryIdByTitle(token, title)).build();
        }

        return response;
    }

}




