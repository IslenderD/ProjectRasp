package beans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Virtual representation of a player. Meant to be used from sending backend to frontend.
 */
public class PersonBean {
    private String username;
    private int amount_bet;
    private boolean isFolded;

    /**
     * Creates a personBean.
     * @param username username of the player
     * @param amount_bet quantity currently being bet.
     * @param isFolded if the player folded in this round.
     */
    public PersonBean(String username, int amount_bet, boolean isFolded) {
        this.username = username;
        this.amount_bet = amount_bet;
        this.isFolded = isFolded;
    }

    /**
     * Constructor needed for JAX-RS.
     */
    public PersonBean() {
        //
    }

    /**
     * Gets if player folded in this round.
     * @return if player folded
     */
    @JsonProperty("isFolded")
    public boolean isFolded() {
        return isFolded;
    }

    /**
     * Gives value to isFolded.
     * @param folded value given to the variable
     */
    @JsonProperty("isFolded")
    public void setFolded(boolean folded) {
        isFolded = folded;
    }

    /**
     * Gets username of player.
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
     * Gets the amount bet.
     * @return amount bet
     */
    public int getAmount_bet() {return amount_bet;}

    /**
     * Gives value to the amount_bet (needed for JAX-RS).
     * @param amount_bet value given to the variable.
     */
    public void setAmount_bet(int amount_bet) {
        this.amount_bet = amount_bet;
    }
}
