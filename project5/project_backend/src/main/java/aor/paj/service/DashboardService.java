package aor.paj.service;

import aor.paj.bean.DashboardBean;
import aor.paj.bean.TaskBean;
import aor.paj.bean.UserBean;
import aor.paj.dao.CategoryDao;
import aor.paj.dto.DashboardDTO;
import aor.paj.dao.TaskDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.User;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/dashboard")
public class DashboardService {

    @Inject
    CategoryDao categoryDao;

    @Inject
    UserDao userDao;

    @Inject
    TaskDao taskDao;

    @Inject
    UserBean userBean;

    @Inject
    TaskBean taskBean;
    @Inject
    DashboardBean dashboardBean;



    @GET
    @Path("/{username}/tasksCountState")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTasks(@HeaderParam("token") String token, @PathParam("username") String username) {
        Response response;

        User user = userBean.getUserByUsername(username);

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();
        } else if (user != null) {

            Map<String, Long> totalTasks = dashboardBean.countTasksByState(username);
            response = Response.status(200).entity(totalTasks).build();
        } else {
            response = Response.status(400).entity("Failed to retrieve user").build();
        }
        return response;
    }

    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboardData(@HeaderParam("token") String token) {
        Response response;
        User user = userBean.getUserByToken(token);

       if(user != null && user.getTypeOfUser().equals("product_owner")){
           DashboardDTO dashboardDTO = new DashboardDTO();
           dashboardDTO.setTotalUsers(dashboardBean.getTotalUsersCount());
              dashboardDTO.setConfirmedUsers(dashboardBean.getConfirmedUsersCount());
              dashboardDTO.setUnconfirmedUsers(dashboardBean.getUnconfirmedUsersCount());
              dashboardDTO.setAverageTasksPerUser(dashboardBean.getAverageTasksPerUser());
                dashboardDTO.setCountTasksByState(dashboardBean.countTasksByStateForAllUsers());


                dashboardDTO.setMostFrequentCategories(dashboardBean.getCategoriesOrderedByTaskCount());
                dashboardDTO.setAverageTaskCompletionTime(dashboardBean.getAverageTaskCompletionTime());
                dashboardDTO.setCountUsersByRegistrationDate(dashboardBean.getUsersRegisteredOverTime());
                dashboardDTO.setCountTaksByConclusionDate(dashboardBean.getTasksConcludedOverTime());

                response = Response.status(200).entity(dashboardDTO).build();
       }else{
              response = Response.status(403).entity("User not authorized").build();
       }


        return response;
    }
}
