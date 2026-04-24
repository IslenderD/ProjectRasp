package beans;


/**
 * Bean with all the info needed to start a game.
 */

public class CreateGameBean {

    private String username;
    private int smallBlindSize;
    private int maxBuyIn;

    /**
     * Constructor of a createGameBean.
     * @param username username of the host
     * @param smallBlindSize small Blind Size
     * @param maxBuyIn max amount a person can join with
     */
    public CreateGameBean(String username, int smallBlindSize, int maxBuyIn) {
        this.username = username;
        this.smallBlindSize = smallBlindSize;
        this.maxBuyIn = maxBuyIn;
    }

    /**
     * Constructor for the JAX-RS.
     */
    public CreateGameBean() {
        //
    }

    /**
     * Sets the username of the host (needed for JAX-RS).
     * @param username username of the host
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the host.
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the max amount a person can join with.
     * @return maxBuyIn
     */
    public int getMaxBuyIn() {
        return maxBuyIn;
    }

    /**
     * Returns the small blind size.
     * @return smallBlindSize
     */
    public int getSmallBlindSize() {
        return smallBlindSize;
    }

    /**
     * Sets the maxBuyIn (needed for JAX-RS).
     * @param maxBuyIn amount for the maxBuyIn
     */
    public void setMaxBuyIn(int maxBuyIn) {
        this.maxBuyIn = maxBuyIn;
    }

    /**
     * Sets the smallBlindSize (needed for JAX-RS).
     * @param smallBlindSize amount given to the smallBlindSize
     */
    public void setSmallBlindSize(int smallBlindSize) {
        this.smallBlindSize = smallBlindSize;
    }
}
