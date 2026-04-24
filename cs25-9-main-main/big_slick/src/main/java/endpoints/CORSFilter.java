package endpoints;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * Enables CORS for requests from the frontend by setting allowed origins, headers, and methods. (apparently needed for communication with other localhost)
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {

    /**
     * Filter of what its allowed and what not on the requests.
     * Allows http://localhost:5173.
     * Allows browser to send cookies.
     * Allows origin, JSON, token and text/plain.
     * Allows headers GET POST DELETE HEAD
     * @param request Contains information about the incoming request
     * @param response Allows modification of the response
     * @throws IOException in case of IOException
     */
    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        response.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        response.getHeaders().putSingle("Access-Control-Allow-Headers", "*");
        response.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}