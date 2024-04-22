package aor.paj.utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Path("/session")
public class SessionTimeOut {
    @GET
    public Response getSessionTimeOut(@Context HttpServletRequest request)
    {

        HttpSession session = request.getSession(false);
        if (session == null) {
            // The session has been invalidated due to inactivity. Redirect to logout.
            return Response.status(Response.Status.UNAUTHORIZED).entity("Session has expired. Please log in again.").build();
        }
        
        return Response.ok().build();
    }
}

