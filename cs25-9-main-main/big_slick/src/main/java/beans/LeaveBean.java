package beans;

/**
 * Bean with all the information to leave/kick a player.
 */
public class LeaveBean {
    private String personLeaving;
    private String personWhoLeftThem;
    private int amountWon;

    /**
     * Constructor of a leaveBean.
     * @param personLeaving person leaving the game
     * @param personWhoLeftThem who made the person leave the game
     * @param amountWon amount the player receives when leaving
     */
    public LeaveBean(String personLeaving, String personWhoLeftThem, int amountWon) {
        this.personLeaving = personLeaving;
        this.personWhoLeftThem = personWhoLeftThem;
        this.amountWon = amountWon;
    }

    /**
     * Constructor for JAX-RS.
     */
    public LeaveBean() {
        //
    }

    /**
     * Gets the username of the player leaving.
     * @return personLeaving
     */
    public String getPersonLeaving() {
        return personLeaving;
    }

    /**
     * Sets the person leaving of the game (needed for JAX-RS).
     * @param personLeaving username of the user leaving
     */
    public void setPersonLeaving(String personLeaving) {
        this.personLeaving = personLeaving;
    }

    /**
     * Gets the username of the player making the other player leave.
     * @return personWhoLeftThem
     */
    public String getPersonWhoLeftThem() {
        return personWhoLeftThem;
    }

    /**
     * Sets the person making the other player leave of the game (needed for JAX-RS).
     * @param personWhoLeftThem username of the user that it's making the other leave
     */
    public void setPersonWhoLeftThem(String personWhoLeftThem) {
        this.personWhoLeftThem = personWhoLeftThem;
    }

    public int getAmountWon() {
        return amountWon;
    }

    public void setAmountWon(int amountWon) {
        this.amountWon = amountWon;
    }
}
