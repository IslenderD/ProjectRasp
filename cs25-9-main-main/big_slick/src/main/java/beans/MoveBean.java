package beans;

/**
 * Virtual representation of a move. Meant to be used backend to Raspberry Pi.
 */
public class MoveBean {
    private String username;
    private String typeOfMove;
    private int amountBet;

    /**
     * Creates a moveBean.
     * @param username username of the player
     * @param typeOfMove type of move made by player (Check, Bet, Fold) (Call==Bet(0), Raise==Bet(x>0))
     * @param amountBet if bet, how much (typeOfMove!=Bet -> amountBet==-1)
     */
    public MoveBean(String username, String typeOfMove, int amountBet) {
        this.username = username;
        this.typeOfMove = typeOfMove;
        this.amountBet = amountBet;
    }

    /**
     * Constructor for JAX-RS.
     */
    public MoveBean() {
        //
    }

    /**
     * Gets username that did the move.
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
     * How much was bet on the move.
     * @return amount that was bet
     */
    public int getAmountBet() {
        return amountBet;
    }

    /**
     * Gives value to the amountBet (needed for JAX-RS).
     * @param amountBet value given to the variable.
     */
    public void setAmountBet(int amountBet) {
        this.amountBet = amountBet;
    }

    /**
     * Type of move performed by the player.
     * @return type of move
     */
    public String getTypeOfMove() {
        return typeOfMove;
    }

    /**
     * Gives value to the typeOfMove (needed for JAX-RS).
     * @param typeOfMove value given to the variable.
     */
    public void setTypeOfMove(String typeOfMove) {
        this.typeOfMove = typeOfMove;
    }
}
