package resources.Exceptions;

/**
 * Custom exception for changeable errors or no connection error (0).
 */
public class HttpRequestException extends Exception{
    private final int statusCode;

    /**
     * Constructor of a httpRequestException (changeable exception).
     * @param message message being sent.
     * @param statusCode type of error that happened; custom-> 0==no connection was made (IOException, InterruptedException, etc.)
     */
    public HttpRequestException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }


}
