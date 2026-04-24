package endpoints.frontend;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import beans.UserBean;
import beans.PasswordChangeBean;
import resources.Exceptions.ConflictException409;
import resources.Security.Argon2Calls;
import resources.Security.SecurityChecks;
import static dao.AuthentificationDao.*;
import static dao.WalletDao.createWallet;
import static dao.WalletDao.deleteWallet;
import static resources.Security.Argon2Calls.*;
import static resources.CookieHandler.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import beans.*;
import static dao.WalletDao.*;

import static service.SendMove.*;


/**
 * Receives/sends data related with authentification to frontend. /backend/frontend/auth
 */
@Path("/auth")
public class Authentification {

    /// Gives the request to every endpoint.
    @Context
    private HttpServletRequest request;


    /**
     * Called when a POST request is received on /backend/frontend/auth/login. Checks if the given password is correct
     * with the one on the database.
     * @param credentials LoginBean containing username and password
     * @return If it was successful sends the cookie with it. If not sends why it was not.
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userLogin(UserBean credentials) {
        System.out.println("in userlogin");
        String hashedPassword;
        hashedPassword = getHashedPasswordByUsername(credentials.getUsername());
        if (credentials.getPassword()==null) {
            throw new BadRequestException("Lacks password");
        } else if (!verifyPassword(hashedPassword, credentials.getPassword())) {
            throw new NotAuthorizedException("Password is wrong");
        }
        HttpSession userLogin = request.getSession(true);
        userLogin.setAttribute("main_credential", credentials.getUsername());
        NewCookie userSessionCookie = new NewCookie.Builder(cookieLoginName).maxAge(24*60*60).value(userLogin.getId()).path(cookieLoginPath)
                .httpOnly(cookieLoginHTTPOnly).secure(cookieLoginSecure).sameSite(cookieLoginSameSite).build();
        return Response.ok().cookie(userSessionCookie).entity("Successful login").build();
    }


    /**
     * Called when a POST request is received on /backend/frontend/auth/change-password. Changes the password of a user
     * if the previous given password was correct and the new one follows the mins.
     * @param passwordChangeBean Bean with all the info for changing the password
     * @return Response to the frontEnd indicating if the operation was successful
     */
    @POST
    @Path("/change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response changePassword(PasswordChangeBean passwordChangeBean) {
        System.out.println("Inside changePasswor");
        String username = passwordChangeBean.getUsername();
        String session_password_hashed = getHashedPasswordByUsername(username);
        if (passwordChangeBean.getOldPassword().isBlank()) {
            throw new BadRequestException("Missing old password");
        }
        if (passwordChangeBean.getNewPassword().isBlank()) {
            throw new BadRequestException("Missing new password");
        }
        if (!Argon2Calls.verifyPassword(session_password_hashed, passwordChangeBean.getOldPassword())) {
            throw new NotAuthorizedException("Invalid old password");
        }
        SecurityChecks.followsAllMinimumRules(passwordChangeBean.getNewPassword());
        String hashedPassword = Argon2Calls.hashPassword(passwordChangeBean.getNewPassword());
        if (!updatePassword(username, hashedPassword)) {
            throw new WebApplicationException("Password update failed");
        }
        return Response.ok("Successfully changed password").build();
    }

    /**
     * Called when received a DELETE request to /backend/frontend/auth/logout. Logs the user out, destroying the cookie.
     * @return If it was successfully done the annihilation of the cookie
     */
    @DELETE
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    public Response logout() {
        System.out.println("inside logogut");
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new NotAuthorizedException("Session not founded, can't logout if not logged in");
        }
        session.invalidate();
        NewCookie cookieKiller = new NewCookie.Builder(cookieLoginName).maxAge(0) //maxAge(0)==delete now
                .path(cookieLoginPath)
                .httpOnly(cookieLoginHTTPOnly)
                .secure(cookieLoginSecure)
                .sameSite(cookieLoginSameSite)
                .build();
        return Response.ok("Successfully Logged out").cookie(cookieKiller).build();
    }

    /**
     * Called when received a POST request to /backend/frontend/auth/sign-in. Signs user in.
     * @param credentials UserBean with all the required information.
     * @return If the addition was successfully done. If not, why.
     */
    @POST
    @Path("/sign-in")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signIn(UserBean credentials) {
        System.out.println(credentials.getUsername());
        if (isUsernameAlreadyInDatabase(credentials.getUsername())) {
            throw new ConflictException409("Username already exists");
        }
        SecurityChecks.followsAllMinimumRules(credentials.getPassword());
        String hashedPassword = Argon2Calls.hashPassword(credentials.getPassword());
        if (!addUserToDatabase(credentials.getUsername(), hashedPassword)) {
            throw new WebApplicationException("User sign on failed");
        }
        if (!createWallet(credentials.getUsername())) {
            throw new WebApplicationException("Creation of wallet failed");
        }
        return Response.ok("Successfully signed on").build();
    }

    /**
     * Called when received a DELETE request to /backend/frontend/auth. Deletes (signs off) user from database.
     * @param credentials UserBean with all the credentials needed to sign off
     * @return If the deletion was successfully done. If not, why.
     */
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response signOff(UserBean credentials) {
        System.out.println("inside signoff");
        if (!isUsernameAlreadyInDatabase(credentials.getUsername())) {
            throw new ConflictException409("Username does not exist");
        }
        String session_password_hashed = getHashedPasswordByUsername(credentials.getUsername());
        if (!Argon2Calls.verifyPassword(session_password_hashed, credentials.getPassword())) {
            throw new NotAuthorizedException("Password is wrong");
        }
        String hashedPassword = Argon2Calls.hashPassword(credentials.getPassword());
        if (!deleteUserFromDatabase(credentials.getUsername(), hashedPassword)) {
            throw new WebApplicationException("User sign off failed");
        }
        if (!deleteWallet(credentials.getUsername())) {
            throw new WebApplicationException("Deletion of wallet failed");
        }
        return Response.ok("Successfully signed off").build();
    }

}
