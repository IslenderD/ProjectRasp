package resources.Exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * Exception 409 (Conflict). Request can't be done due to conflict with source.
 */
public class ConflictException409 extends WebApplicationException {

    /**
     * Constructor of a conflictException error (409; request can't be done due to conflict with source).
     * @param message message being sent.
     */
    public ConflictException409(String message) {
        super(message, Response.Status.CONFLICT);
    }
}
