package aor.paj.service;

import aor.paj.bean.DashboardBean;
import aor.paj.bean.TaskBean;
import aor.paj.bean.UserBean;
import aor.paj.dao.CategoryDao;
import aor.paj.dto.DashboardDTO;
import aor.paj.dao.TaskDao;
import aor.paj.dao.UserDao;
import aor.paj.dto.User;
import aor.paj.utils.WebListenner;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;
import org.apache.logging.log4j.*;

@Path("/dashboard")
public class DashboardService {


    @Inject
    UserBean userBean;

    @Inject
    DashboardBean dashboardBean;

    @Inject
    WebListenner webListenner;
    @Inject
    HttpServletRequest httpRequest;
    private static final Logger logger = LogManager.getLogger(TaskBean.class);


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
            logger.info("Tasks by state consulted by user: " + user.getUsername());
            response = Response.status(200).entity(totalTasks).build();
        } else {
            response = Response.status(400).entity("Failed to retrieve user").build();
        }
        //Atualiza a última atividade da sessão
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            webListenner.updateLastActivityTime(session);
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
                DashboardDTO dashboardDTO = dashboardBean.createDashboardData();

                logger.info("Dashboard consulted by user: " + user.getUsername());

                response = Response.status(200).entity(dashboardDTO).build();
       }else{
              response = Response.status(403).entity("User not authorized").build();
              logger.error("User not authorized to consult dashboard" + user.getUsername());
       }

        //Atualiza a última atividade da sessão
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            webListenner.updateLastActivityTime(session);
        }
        return response;
    }
}
