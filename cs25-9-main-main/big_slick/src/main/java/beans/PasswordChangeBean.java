package beans;

/**
 * Bean that includes all necessary information to change password.
 */
public class PasswordChangeBean {
    private String username;
    private String oldPassword;
    private String newPassword;

    /**
     * Creates a new passwordChangeBean.
     * @param username username of the user
     * @param oldPassword old password that wants to be changed
     * @param newPassword new password that will be changed to
     */
    public PasswordChangeBean(String username, String oldPassword, String newPassword) {
        this.username = username;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    /**
     * Constructor needed for JAX-RS.
     */
    public PasswordChangeBean() {
        //
    }

    /**
     * Gets username of user.
     * @return username
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
     * Gets the password it's going to be changed to.
     * @return new password
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Gives value to the newPassword (needed for JAX-RS).
     * @param newPassword value given to the variable.
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * Gets the old password that it's going to be checked.
     * @return old password
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Gives value to the oldPassword (needed for JAX-RS).
     * @param oldPassword value given to the variable.
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
}
