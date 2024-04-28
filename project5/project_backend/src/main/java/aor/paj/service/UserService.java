package aor.paj.service;

import aor.paj.bean.TaskBean;
import aor.paj.bean.UserBean;
import aor.paj.dto.LoginDto;
import aor.paj.dto.User;
import aor.paj.dto.UserDetails;

import aor.paj.entity.UserEntity;
import aor.paj.utils.EncryptHelper;
import aor.paj.utils.SessionListener;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Path("/users")
public class UserService {

    @Inject
    UserBean userBean;

    @Inject
    TaskBean taskBean;

    @Inject
    EncryptHelper encryptHelper;
    @Inject
    SessionListener webListenner;
    @Inject
    HttpServletRequest httpRequest;
    private static final Logger logger = LogManager.getLogger(UserBean.class);



    @POST
    @Path("/addUserDB")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addUserToDB(User user, @Context HttpServletRequest request) {

        Response response;

        boolean isFieldEmpty = userBean.isAnyFieldEmpty(user);
        boolean isEmailValid = userBean.isEmailValid(user.getEmail());
        boolean isUsernameAvailable = userBean.isUsernameAvailable(user.getUsername());
        boolean isImageValid = userBean.isImageUrlValid(user.getImgURL());
        boolean isPhoneValid = userBean.isPhoneNumberValid(user.getPhoneNumber());

        boolean tokenConfirmation = userBean.register(user);

        if (isFieldEmpty) {
            response = Response.status(422).entity("There's an empty field. ALl fields must be filled in").build();

        } else if (!isEmailValid) {
            response = Response.status(422).entity("Invalid email").build();

        } else if (!isUsernameAvailable) {
            response = Response.status(Response.Status.CONFLICT).entity("Username already in use").build(); //status code 409

        } else if (!isImageValid) {
            response = Response.status(422).entity("Image URL invalid").build(); //400

        } else if (!isPhoneValid) {
            response = Response.status(422).entity("Invalid phone number").build();

        }else if(tokenConfirmation){
            response = Response.status(Response.Status.CREATED).entity("User registered successfully").build();
        } else {
            response = Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong").build(); //status code 400

        }


        logger.info("User registered successfully at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserByToken(@HeaderParam("token") String token) {

        Response response;
        if (token != null) {

            User user = userBean.getUserByToken(token);

            if (user != null) {
                response = Response.ok(user).build();
            } else {

                response = Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {

            response= Response.status(Response.Status.BAD_REQUEST).build();
        }

        return response;

    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserByToken(@HeaderParam("token") String token, @HeaderParam("username") String username) {
        Response response;

        if (token != null) {

            User user = userBean.getUserByToken(token);

            if (user != null) {
                User userFind = userBean.getUserByUsername(username);
                response = Response.ok(userFind).build();
            } else {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }

        } else {

            response = Response.status(Response.Status.BAD_REQUEST).build();
        }

        return response;
    }

    @GET
    @Path("/userByTokenConfirmation")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserByTokenConfirmation (@QueryParam("tokenConfirmation") String tokenConfirmation) {
            Response response;

            if (tokenConfirmation != null) {
                User userFind = userBean.getUserByTokenConfirmation(tokenConfirmation);
                response = Response.ok(userFind).build();
            } else {
                response = Response.status(Response.Status.NOT_FOUND).build();
            }


            return response;
        }

    //Endpoint para filtrar o nome do user na tabela de users
    @GET
    @Path("/filterByName")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserByName(@HeaderParam("token") String token, @QueryParam("prefix") String prefix) {
        if (token != null) {
            User user = userBean.getUserByToken(token);
            if (user != null) {
                List<User> users = userBean.getUsersByFirstName(prefix);
                if (users != null && !users.isEmpty()) {
                    return Response.ok(users).build();
                } else {
                    // Retorna uma array vazia se nenhum usuário for encontrado para que no frontend nao dê erro ao imprimir a tabela
                    return Response.ok(Collections.emptyList()).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized access").build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing token").build();
        }
    }

    @GET
    @Path("/filterByEmail")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserByEmail(@HeaderParam("token") String token, @QueryParam("prefix") String prefix) {
        if (token != null) {
            User user = userBean.getUserByToken(token);
            if (user != null) {
                List<User> users = userBean.getUsersByEmail(prefix);
                if (users != null && !users.isEmpty()) {
                    return Response.ok(users).build();
                } else {
                    // Retorna uma array vazia se nenhum email for encontrado para que no frontend nao dê erro ao imprimir a tabela
                    return Response.ok(Collections.emptyList()).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized access").build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing token").build();
        }
    }


    //Altera a password do utilizador no link enviado por email
    @PUT
    @Path("/changePassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(@HeaderParam("email") String email, @QueryParam("password1") String password1, @QueryParam("password2") String password2, @Context HttpServletRequest request) {
        if (email == null || password1 == null || password2 == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing parameters").build();
        }

        User user = userBean.getUserByEmail(email);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Email not valid").build();
        }

        if (password1.equals(user.getPassword())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Password is the same").build();
        }

        if (!password1.equals(password2)) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Passwords don't match").build();
        }

        boolean changed = userBean.changePassword(email, password1);
        if (changed) {
            logger.info("Password changed successfully at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(Response.Status.OK).entity("Password changed successfully").build();
        } else {
            logger.warn("Failed to change password at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to change password").build();
        }
    }




    //Endpoint só para enviar o email de recuperação da password
    @GET
    @Path("/passwordRecovery")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response passwordRecovery(@QueryParam("email") String email, @Context HttpServletRequest request) {
        boolean sent = userBean.passwordRecovery(email);
        if (sent) {
            logger.info("Password recovery email sent to " + email + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(Response.Status.OK).entity("Please check your email box").build();
        } else {
            logger.warn("Failed to send password recovery email to " + email + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(Response.Status.BAD_REQUEST).entity("Email not sent").build();
        }
    }

    @PUT
    @Path("/confirmationAccount")
    public Response confirmAccount(@QueryParam("token") String tokenConfirmation, @Context HttpServletRequest request) {
        boolean confirmed = userBean.confirmUser(tokenConfirmation);
        if (confirmed) {
            logger.info("User confirmed successfully at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(Response.Status.OK).entity("User confirmed successfully").build();
        } else {
            logger.warn("Failed to confirm user at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid confirmation token").build();
        }
    }
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@HeaderParam("token") String token) {
        List<User> allUsers = userBean.getAllUsers();

        if (allUsers != null && !allUsers.isEmpty()) {
            return Response.ok(allUsers).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No users found").build();
        }
    }

    @GET
    @Path("/activeUsers")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getActiveUsers(@HeaderParam("token") String token) {

        User userRequest = userBean.getUserByToken(token);
        if (userRequest != null ) {
            List<User> activeUsers = userBean.getActiveUsers(token);

            if (activeUsers != null && !activeUsers.isEmpty()) {
                return Response.ok(activeUsers).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("No users found").build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized access").build();
        }

    }

    @GET
    @Path("/inactiveUsers")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getInactiveUsers(@HeaderParam("token") String token) {

        User userRequest = userBean.getUserByToken(token);
        if (userRequest != null && (userRequest.getTypeOfUser().equals("product_owner"))) {
            List<User> inactiveUsers = userBean.getInactiveUsers(token);

            if (inactiveUsers != null && !inactiveUsers.isEmpty()) {
                return Response.ok(inactiveUsers).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("No users found").build();
            }
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized access").build();
        }

    }


    @PUT
    @Path("/restoreUser/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response restoreUser(@HeaderParam("token") String token, @PathParam("username") String username, @Context HttpServletRequest request) {
        User user = userBean.getUserByToken(token);
        if (user != null && (user.getTypeOfUser().equals("product_owner"))) {
            boolean restored = userBean.restoreUser(username);
            if (restored) {
                logger.info("User restored successfully by " + user.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
                return Response.status(Response.Status.OK).entity("User restored successfully").build();
            }
            logger.warn("Failed to restore user by " + user.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to restore user").build();
        }

        return Response.status(Response.Status.FORBIDDEN).entity("Forbidden").build();
    }

    @PUT
    @Path("/updateSessionTimeout")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAllUsers(@HeaderParam("token") String token, @QueryParam("sessionTimeout") int sessionTimeout, @Context HttpServletRequest request) {
        User user = userBean.getUserByToken(token);
        if(user != null && user.getTypeOfUser().equals("product_owner")){
            userBean.updateSessionTimeout(token,sessionTimeout);
            logger.info("Session timeout updated successfully by " + user.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(Response.Status.OK).entity("Session timeout updated successfully").build();
        }

        return Response.status(Response.Status.OK).entity("All users updated successfully").build();
    }

    @PUT
    @Path("/deleteUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteUser(@HeaderParam("token") String token, @HeaderParam("username") String username, @Context HttpServletRequest request) {
        User user = userBean.getUserByToken(token);
        if (user != null && (user.getTypeOfUser().equals("product_owner"))) {
            boolean deleted = userBean.removeUser(username);
            if (deleted) {

                logger.info("User deleted (inactivated) successfully by " + user.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
                return Response.status(Response.Status.OK).entity("User deleted successfully").build();
            }

            logger.warn("Failed to delete user by " + user.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Forbidden").build();
        }
        return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
    }

    @DELETE
    @Path("/removeUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeUser(@HeaderParam("token") String token, @HeaderParam("username") String username, @Context HttpServletRequest request) {
        User user = userBean.getUserByToken(token);
        if (user != null && (user.getTypeOfUser().equals("product_owner"))) {
            boolean deleted = userBean.deletePermanentlyUser(username);
            if (deleted) {
                logger.info("User deleted successfully by " + user.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
                return Response.status(Response.Status.OK).entity("User deleted successfully").build();
            }
            logger.warn("Failed to delete user by " + user.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Forbidden").build();
        }
        logger.error("User not found by " + user.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
        return Response.status(Response.Status.NOT_FOUND).entity("User with this token is not found").build();
    }


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginDto user, @Context HttpServletRequest request) {

        String token = userBean.loginDB(user, request);
        if (token != null) {
            // Criar um objeto JSON contendo apenas o token
            JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("token", token)
                    .build();
            // Retornar a resposta com o token
            logger.info("User logged in successfully " + " with username " + user.getUsername() + "at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(200).entity(jsonResponse).build();

        } else {
            logger.warn("Failed to login at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            return Response.status(403).entity("{\"error\": \"Somethin went wrong\"}").build();
        }
    }

    @PUT
    @Path("/updateProfile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@HeaderParam("token") String token, User updatedUser,  @Context HttpServletRequest request) {
        User user = userBean.getUserByToken(token);

        if (user != null) {
            if (updatedUser.getEmail() != null) {
                if (!userBean.isEmailValid(updatedUser.getEmail())) {
                    return Response.status(422).entity("Invalid email").build();
                } else if (!userBean.emailAvailable(updatedUser.getEmail())) {
                    return Response.status(422).entity("Email allready exists").build();
                } else {
                    user.setEmail(updatedUser.getEmail());
                }
            }
            if (updatedUser.getFirstName() != null) {
                user.setFirstName(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null) {
                user.setLastName(updatedUser.getLastName());
            }
            if (updatedUser.getPhoneNumber() != null) {
                if (!userBean.isPhoneNumberValid(updatedUser.getPhoneNumber())) {
                    return Response.status(422).entity("Invalid phone number").build();
                } else {
                    user.setPhoneNumber(updatedUser.getPhoneNumber());
                }
            }
            if (updatedUser.getImgURL() != null) {
                if (!userBean.isImageUrlValid(updatedUser.getImgURL())) {
                    return Response.status(422).entity("Image URL invalid").build();
                } else {
                    user.setImgURL(updatedUser.getImgURL());
                }
            }

            if (updatedUser.getPassword() != null) {
                String plainPassword = updatedUser.getPassword();
                String hashedPassword = encryptHelper.encryptPassword(plainPassword);
                user.setPassword(hashedPassword);
            }


            boolean updatedUSer = userBean.updateUser(token, user);
            if (updatedUSer) {
                logger.info("User updated successfully by " + user.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());

                return Response.status(Response.Status.OK).entity(user).build();
            } else {
                logger.warn("Failed to update user by " + user.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to update user").build();
            }
        } else {
            return Response.status(401).entity("Invalid credentials").build();
        }
    }

    @PUT
    @Path("/updateProfilePO")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserByPO(@HeaderParam("token") String token, @HeaderParam("username") String username, User updatedUser, @Context HttpServletRequest request) {
        User userRequest = userBean.getUserByToken(token);
        User beModified = userBean.getUserByUsername(username);


        if (userRequest != null && (userRequest.getTypeOfUser()).equals("product_owner")) {
            if (updatedUser.getEmail() != null) {
                if (!userBean.isEmailValid(updatedUser.getEmail())) {
                    return Response.status(422).entity("Invalid email").build();
                } else if (!userBean.emailAvailable(updatedUser.getEmail())) {
                    return Response.status(422).entity("Email allready exists").build();
                } else {
                    beModified.setEmail(updatedUser.getEmail());
                }
            }
            if (updatedUser.getFirstName() != null) {
                beModified.setFirstName(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null) {
                beModified.setLastName(updatedUser.getLastName());
            }
            if (updatedUser.getPhoneNumber() != null) {
                if (!userBean.isPhoneNumberValid(updatedUser.getPhoneNumber())) {
                    return Response.status(422).entity("Invalid phone number").build();
                } else {
                    beModified.setPhoneNumber(updatedUser.getPhoneNumber());
                }
            }
            if (updatedUser.getImgURL() != null) {
                if (!userBean.isImageUrlValid(updatedUser.getImgURL())) {
                    return Response.status(422).entity("Image URL invalid").build();
                } else {
                    beModified.setImgURL(updatedUser.getImgURL());
                }
            }

            if (updatedUser.getTypeOfUser() != null) {
                beModified.setTypeOfUser(updatedUser.getTypeOfUser());
            }

            boolean updatedUSer = userBean.updateUserByPO(token, username, beModified);
            if (updatedUSer) {
                logger.info("User updated successfully by " + userRequest.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
                return Response.status(200).entity(beModified).build();
            } else {
                logger.warn("Failed to update user by " + userRequest.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
                return Response.status(406).entity("Failed to update user").build();
            }
        } else {
            return Response.status(401).entity("Invalid credentials").build();
        }
    }

    @PUT
    @Path("/addUserByPO")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserByPO(@HeaderParam("token") String token, User user, @Context HttpServletRequest request) {
        User userRequest = userBean.getUserByToken(token);
        Response response;

        if (userRequest != null && userRequest.getTypeOfUser().equals("product_owner")) {
            boolean isFieldEmpty = userBean.isAnyFieldEmpty(user);
            boolean isEmailValid = userBean.isEmailValid(user.getEmail());
            boolean isUsernameAvailable = userBean.isUsernameAvailable(user.getUsername());
            boolean isImageValid = userBean.isImageUrlValid(user.getImgURL());
            boolean isPhoneValid = userBean.isPhoneNumberValid(user.getPhoneNumber());


            if (isFieldEmpty) {
                response = Response.status(422).entity("There's an empty field. ALl fields must be filled in").build();

            } else if (!isEmailValid) {
                response = Response.status(422).entity("Invalid email").build();

            } else if (!isUsernameAvailable) {
                response = Response.status(Response.Status.CONFLICT).entity("Username already in use").build();

            } else if (!isImageValid) {
                response = Response.status(422).entity("Image URL invalid").build();

            } else if (!isPhoneValid) {
                response = Response.status(422).entity("Invalid phone number").build();

            } else if (userBean.registerByPO(token, user)) {
                logger.info("A new User was registered successfully by " + userRequest.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
                response = Response.status(Response.Status.CREATED).entity("User registered successfully").build();

            } else {
                logger.warn("Failed to register user by " + userRequest.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
                response = Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong").build();

            }
        } else {
            logger.error("User not found by " + userRequest.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());
            response = Response.status(Response.Status.UNAUTHORIZED).entity("You don´t have permission").build();
        }

        return response;
    }


    @PUT
    @Path("/{username}/updateUserRole")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserRole(@HeaderParam("token") String token, @PathParam("username") String username, String newRole) {
        Response response;

        JsonObject jsonObject = Json.createReader(new StringReader(newRole)).readObject();
        String newRoleConverted = jsonObject.getString("typeOfUser");

        if (userBean.getUserByToken(token) == null) {
            response = Response.status(403).entity("Invalid token").build();

        } else if (!userBean.getUserByToken(token).getTypeOfUser().equals("product_owner")) {
            response = Response.status(403).entity("You dont have permission to do that").build();

        } else if (!newRoleConverted.equals("developer") && !newRoleConverted.equals("scrum_master") && !newRoleConverted.equals("product_owner")) {
            response = Response.status(403).entity("Invalid role").build();

        } else if (userBean.updateUserRole(username, newRoleConverted)) {
            response = Response.status(200).entity("User role updated").build();

        } else {
            response = Response.status(401).entity("Role couldnt be updated").build();
        }

        return response;
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logoutValidate(@HeaderParam("token") String token, @Context HttpServletRequest request) {
        User userRequest = userBean.getUserByToken(token);

        if (userRequest == null) {
            return Response.status(401).entity("Failed").build();
        }

        userBean.logoutUser(token);

        logger.info("User logged out successfully by " + userRequest.getUsername() + " at " + LocalDateTime.now() + " from IPAdress: " + request.getRemoteAddr());

        return Response.status(200).entity(" Logout successful").build();
    }

    @GET
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsern(@PathParam("username") String username) {
        UserDetails userRequested = userBean.getUserDetails(username);
        if (userRequested == null) return Response.status(400).entity("Failed").build();
        return Response.status(200).entity(userRequested).build();
    }

}


