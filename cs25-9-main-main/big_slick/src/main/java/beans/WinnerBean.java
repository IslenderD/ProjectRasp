package beans;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.event.WindowEvent;

/**
 * Who won, how much.
 */
public class WinnerBean {
    private String username;
    private int amount_won;
    private boolean isGame;

    /**
     * Constructor of a winnerBean.
     * @param username username of the player that won
     * @param amount_won how much they won
     * @param gameIsThing if the json is a gameJson, always false
     */
    public WinnerBean(String username, int amount_won, boolean isGame) {
        this.username = username;
        this.amount_won = amount_won;
        this.isGame = isGame;
    }

    /**
     * Constructor for JAX-RS.
     */
    public WinnerBean() {
        //
    }

    /**
     * Gets the username of the player.
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username (needed for JAX-RS).
     * @param username username of the player
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the amount won by the player.
     * @return amount won
     */
    public int getAmount_won() {
        return amount_won;
    }

    /**
     * Sets the amount won (needed for JAX-RS).
     * @param amount_won username of the player
     */
    public void setAmount_won(int amount_won) {
        this.amount_won = amount_won;
    }

    /**
     * Returns the value of gameIsThing.
     * @return value
     */
    public boolean isGame() {
        return isGame;
    }

    /**
     * Gives a value to gameIsThing.
     * @param game value being given
     */
    public void setGame(boolean game) {
        isGame = game;
    }
}
