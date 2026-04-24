package endpoints;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Application;

/**
 * Sets first path on all endpoints /backend/... .
 */
@ApplicationPath("/backend")
public class MainPath extends Application {
    //
}
