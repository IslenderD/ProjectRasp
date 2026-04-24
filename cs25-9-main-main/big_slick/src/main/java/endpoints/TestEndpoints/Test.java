package endpoints.TestEndpoints;

import beans.UserBean;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Endpoints meant to be called for testing. backend/test
 */
@Path("/test") // this means your endpoint is /backend/test
public class Test {

    /**
     * Called when received a GET request ot backend/test.
     * @return Hello World! :D
     */
    @GET
    @Produces("text/plain")
    public String hello() {
        System.out.println("Hello World");
        return "Hello, World!";
    }

    /**
     * Called to test json application.
     * @param userBean userbean (just for testing idk)
     * @return the username
     */
    @POST
    @Path("/json")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/plain")
    public String acceptJson(UserBean userBean) {
        System.out.println(userBean.getUsername());
        System.out.println(userBean.getPassword());
        return userBean.getUsername();
    }

    /**
     * Called when received a POST request to backend/test/{name}.
     * @param name name for testing
     * @return a nice message
     */
    @POST
    @Path("/{name}")
    @Produces("text/plain")
    public String customWelcome(@PathParam("name") String name) {

        System.out.println("inside custom welcome");
    return "Welcome " + name + "!";
    }
}