package beans;

/**
 * Contains all credentials of a login.
 */
public class UserBean {
    private String username;
    private String password;

    /**
     * Creates a loginBean.
     * @param username username of the user (can be null)
     * @param password password, not hashed
     */
    public UserBean(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Constructor for JAX-RS.
     */
    public UserBean() {
        //
    }

    /**
     * Returns username of user.
     * @return username (can be null)
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gives value to the username (needed for JAX-RS).
     * @param username value given to the variable.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Return password of user.
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gives value to the password (needed for JAX-RS).
     * @param password value given to the variable.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
