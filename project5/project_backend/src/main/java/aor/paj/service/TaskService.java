package aor.paj.service;

import aor.paj.bean.TaskBean;
import aor.paj.bean.UserBean;
import aor.paj.dao.CategoryDao;
import aor.paj.dto.Category;
import aor.paj.dto.Task;
import aor.paj.dto.User;
import aor.paj.utils.SessionListener;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Path("/tasks")
public class TaskService {

    @Inject
    TaskBean taskBean;

    @Inject
    UserBean userBean;

    @Inject
    CategoryDao categoryDao;


    @Inject
    SessionListener webListenner;
    @Inject
    HttpServletRequest httpRequest;

    private static final Logger logger = LogManager.getLogger(TaskService.class);

    @POST
    @Path("/createTask")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addTask(@HeaderParam("token") String token, @HeaderParam("categoryId") String categoryId, Task task, @Context HttpServletRequest request) {
        LocalDate currentDate = LocalDate.now();

        Response response;

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (categoryId == null || categoryDao.findCategoryById(Long.parseLong(categoryId)) == null) {
            response = Response.status(422).entity("Invalid category").build();


        } else if (task.getInitialDate().isAfter(task.getEndDate())) {
            response = Response.status(422).entity("Initial date cannot be after the end date").build();

        }else if(task.getInitialDate().isBefore(currentDate)){
            response = Response.status(422).entity("Initial date cannot be before the current date").build();

        } else if (task.getPriority() != 100 && task.getPriority() != 200 && task.getPriority() != 300) {
            response = Response.status(422).entity("Priority can only be 100, 200 or 300").build();

        } else if (!task.getState().equals("toDo") && !task.getState().equals("doing") && !task.getState().equals("done")) {
            response = Response.status(422).entity("State can only be toDo, doing or done").build();

        } else if (taskBean.addTask(token,task, categoryId)) {
            logger.info("Task created with id: " + task.getId() + " by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());

            response = Response.status(200).entity("A new task is created").build();

        } else {
            logger.warn("Failed to create task by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            response = Response.status(400).entity("Failed to update task").build();
        }


        return response;
    }

    @PUT
    @Path("/updateTask")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTask(@HeaderParam("token") String token, @HeaderParam("categoryId") String categoryId,
                               @HeaderParam("taskId") String taskId, Task task, @Context HttpServletRequest request){

        Response response;

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (categoryDao.findCategoryById(Long.parseLong(categoryId)) == null) {
            response = Response.status(422).entity("Invalid category").build();


        } else if (task.getInitialDate().isAfter(task.getEndDate())) {
            response = Response.status(422).entity("Initial date cannot be after the end date").build();

        } else if (task.getPriority() != 100 && task.getPriority() != 200 && task.getPriority() != 300) {
           response = Response.status(422).entity("Priority can only be 100, 200 or 300").build();

        } else if (!task.getState().equals("toDo") && !task.getState().equals("doing") && !task.getState().equals("done")) {
            response = Response.status(422).entity("State can only be toDo, doing or done").build();

        } else if (taskBean.updateTask(token, taskId, task, categoryId)) {
            response = Response.status(200).entity("Task updated sucessfully").build();
            logger.info("Task updated with id: " + taskId + " by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());

        } else {
            logger.warn("Failed to update task by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            response = Response.status(400).entity("Failed to update task").build();
        }


        return response;
    }



    @PUT
    @Path("/{taskId}/updateCategory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTaskCategory(@HeaderParam("token") String token, @PathParam("taskId") String taskId, Category category, @Context HttpServletRequest request ) {
        Response response;
        User requestingUser = userBean.getUserByToken(token);

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (!requestingUser.getTypeOfUser().equals("Product Owner")) {
            response = Response.status(409).entity("You dont have permissions to edit that").build();

        } else if (taskBean.updateTaskCategory(token, taskId, category)) {
            logger.info("Task category changed with id: " + taskId + " by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            response = Response.status(200).entity("Task category changed successfully").build();

        } else {
            logger.warn("Failed to update task category by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            response = Response.status(400).entity("Task category update failed").build();
        }

        return response;
    }

    @PUT
    @Path("/{taskId}/status")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTaskStatus(@HeaderParam("token") String token, @PathParam("taskId") String taskId, @HeaderParam("newState") String state, @Context HttpServletRequest request){
        Response response;


        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (!state.equals("toDo") && !state.equals("doing") && !state.equals("done")) {
            response = Response.status(422).entity("State can only be toDo, doing or done").build();

        } else if (taskBean.updateTaskState(token, taskId, state)) {
            response = Response.status(200).entity("Task state updated successfully").build();
            logger.info("Task state changed with id: " + taskId + " by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());

        } else
            response = Response.status(400).entity("Failed to update task state").build();
        logger.warn("Failed to update task state by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());


        return response;
    }


    @PUT
    @Path("/{taskId}/softDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response softDeleteTask(@HeaderParam("token") String token, @PathParam("taskId") String taskId, @Context HttpServletRequest request) {
        Response response;


        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if(!userBean.getUserByToken(token).getTypeOfUser().equals("product_owner") && !userBean.getUserByToken(token).getTypeOfUser().equals("scrum_master")) {
            response = Response.status(409).entity("You dont have permissions to edit that").build();


        } else if (taskBean.updateTaskActiveState(token, taskId)) {
            logger.info("Task active state changed with id: " + taskId + " by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            response = Response.status(200).entity("Task active state updated successfully").build();

        } else {
            logger.warn("Failed to update task active state by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            response = Response.status(400).entity("Failed to update task active state").build();
        }


        return response;
    }

    @GET
    @Path("/getAllSoftDeletedTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getsoftDeletedTasks(@HeaderParam("token") String token) {
        Response response;

        ArrayList<Task> softDeletedTasks = taskBean.getSoftDeletedTasks();

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (!userBean.getUserByToken(token).getTypeOfUser().equals("scrum_master") && !userBean.getUserByToken(token).getTypeOfUser().equals("product_owner")) {
            response = Response.status(403).entity("You dont have permissions to do that").build();

        } else if (softDeletedTasks != null) {
            response = Response.status(200).entity(softDeletedTasks).build();

        } else {
            response = Response.status(400).entity("Failed to retrieve tasks").build();
        }


        return response;
    }

    @DELETE
    @Path("/deleteTasksByUsername/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAllTasksByUsername(@HeaderParam("token") String token, @PathParam("username") String username, @Context HttpServletRequest request){
        Response response;

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (!userBean.getUserByToken(token).getTypeOfUser().equals("product_owner")) {
            response = Response.status(403).entity("You dont have permissions to do that").build();

        } else if (taskBean.deleteTasksByUsername(username))  {
            logger.info("Task of user: " + username + " deleted by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            response = Response.status(200).entity("Tasks deleted successfully").build();

        } else {
            logger.warn("Failed to delete tasks of user: " + username + " by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            response = Response.status(400).entity("Failed to execute order").build();
        }


        return response;
    }

    @DELETE
    @Path("/{id}/hardDeleteTask")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hardDeleteTask(@HeaderParam("token") String token, @PathParam("id") String id, @Context HttpServletRequest request){
        Response response;

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (!userBean.getUserByToken(token).getTypeOfUser().equals("product_owner")) {
            response = Response.status(403).entity("You dont have permissions to do that").build();

        } else if (taskBean.hardDeleteTask(token, id))  {
            logger.info("Task deleted permanently with id: " + id + " by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            response = Response.status(200).entity("Task permanently deleted").build();

        } else {
            logger.warn("Failed to delete task permanently with id: " + id + " by user: " + userBean.getUserByToken(token).getUsername() + " at " + LocalDateTime.now() + " with IPAdress " + request.getRemoteAddr());
            response = Response.status(400).entity("Failed to execute order").build();
        }


        return response;
    }


    //Método para filtrar tasks por categoria
    @GET
    @Path("/getFilterTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTasksByCategory(@HeaderParam("token") String token, @QueryParam("username") String username,@QueryParam("category") long category) {
        User user = userBean.getUserByToken(token);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build();
        }

        if (!user.getTypeOfUser().equals("product_owner") && !user.getTypeOfUser().equals("scrum_master")) {
            return Response.status(Response.Status.FORBIDDEN).entity("You don't have permission to access this resource").build();
        }

        List<Task> userTasksByCategory = new ArrayList<>();
        if(username != null || category != 0){
            userTasksByCategory = taskBean.getFilterTasks(token, username, category);

            if (userTasksByCategory.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("No tasks found for this user or category").build();
            }
        }else {
            return Response.ok(userTasksByCategory).build();
        }

        return Response.ok(userTasksByCategory).build();
    }

    @GET
    @Path("/getAllTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTasks(@HeaderParam("token") String token) {
        Response response;

        ArrayList<Task> tasks = taskBean.getAllTasks(token);

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (tasks != null) {
            response = Response.status(200).entity(tasks).build();

        } else {
            response = Response.status(400).entity("Failed to retrieve tasks").build();
        }


        return response;
    }

    @GET
    @Path("getActiveTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getActiveTasks(@HeaderParam("token") String token) {
        Response response;

        ArrayList<Task> tasks = taskBean.getActiveTasks(token);

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (tasks != null) {
            response = Response.status(200).entity(tasks).build();

        } else {
            response = Response.status(400).entity("Failed to retrieve tasks").build();
        }


        return response;
    }


    @GET
    @Path("/myTasks")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMyTasks(@HeaderParam("token") String token){
        Response response;

        User user = userBean.getUserByToken(token);

        if(user == null){
            response = Response.status(403).entity("Invalid token").build();
        }else {
            ArrayList<Task> tasks = taskBean.getTasksByUsername(token);

            if (userBean.getUserByToken(token) == null) {
                response = Response.status(403).entity("Invalid token").build();

            } else if (tasks != null) {
                response = Response.status(200).entity(tasks).build();

            } else {
                response = Response.status(400).entity("This user has no tasks").build();
            }

        }

        return response;
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTask(@HeaderParam("token") String token, @PathParam("id") String id) {
        Response response;

        Task task = taskBean.getTaskAttributes(token, id);

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (task != null) {
            response = Response.status(200).entity(task).build();

        } else {
            response = Response.status(400).entity("Failed to retrieve task").build();
        }

        return response;
    }

    @GET
    @Path("/{username}/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserProfile(@HeaderParam("token") String token, @PathParam("username") String username) {
        Response response;

        User user = userBean.getUserByUsername(username);

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (user != null) {
                long totalTasks = taskBean.getTotalTasksByUsername(username);
                response = Response.status(200).entity(totalTasks).build();

        } else {
            response = Response.status(400).entity("Failed to retrieve user").build();
        }

        return response;
    }


}






