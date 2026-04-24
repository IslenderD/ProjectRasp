package beans;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Virtual representation of the game.
 */
public class GameBean {
    private int id;
    private List<PersonBean> peopleOnGame;
    private String host;
    private String turn;
    private int pot;
    private boolean isGame;

    /**
     * Constructor of the gameBean.
     * @param id id of the game (always the same for prototype)
     * @param peopleOnGame array with all the personBean playing.
     * @param host who is the host (username)
     * @param turn who has the current turn (username)
     * @param pot current total pot of the game
     * @param gameIsThing if the json is a gameJson, always true
     */
    public GameBean(int id, List<PersonBean> peopleOnGame, String host, String turn, int pot, boolean isGame) {
        this.id = id;
        this.isGame = isGame;
        this.peopleOnGame = peopleOnGame;
        this.host = host;
        this.turn = turn;
        this.pot = pot;
    }

    /**
     * Constructor for JAX-RS.
     */
    public GameBean() {
        //
    }

    /**
     * Gets the id of the game, on the prototype will be always the same.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the game (needed for JAX-RS).
     * @param id id of the game
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the people playing on the game.
     * @return array with all the player's info
     */
    public List<PersonBean> getPeopleOnGame() {
        return peopleOnGame;
    }

    /**
     * Sets the people playing on the game.
     * @param peopleOnGame array with all the people
     */
    public void setPeopleOnGame(List<PersonBean> peopleOnGame) {
        this.peopleOnGame = peopleOnGame;
    }

    /**
     * Gets who is the current player turn.
     * @return player whose turn is
     */
    public String getTurn() {
        return turn;
    }

    /**
     * Gives value to the turn (needed for JAX-RS).
     * @param turn value given to the variable.
     */
    public void setHisTurn(String turn) {
        this.turn = turn;
    }

    /**
     * Gets who is the host on the game.
     * @return player who is host
     */
    public String getHost() {
        return host;
    }

    /**
     * Gives value to the host (needed for JAX-RS).
     * @param host value given to the variable.
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Gets the pot of the game.
     * @return pot amount
     */
    public int getPot() {
        return pot;
    }

    /**
     * Gives the value to the pot (needed for JAX-RS).
     * @param pot value given to the variable
     */
    public void setPot(int pot) {
        this.pot = pot;
    }

    /**
     * Returns the value of isGame.
     * @return value
     */
    public boolean isGame() {
        return isGame;
    }

    /**
     * Gives a value to isGame.
     * @param game value being given
     */
    public void setGame(boolean game) {
        isGame = game;
    }
}
