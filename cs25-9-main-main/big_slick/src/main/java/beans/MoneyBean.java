package beans;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Bean made for sending the money to the player.
 */
public class MoneyBean {
    private int amount;
    private boolean timer_started;

    /**
     * Constructor of a moneyBean.
     * @param amount amount of money being sent
     * @param timer_started if the timer started in this instance
     */
    public MoneyBean(int amount, boolean timer_started) {
        this.amount = amount;
        this.timer_started = timer_started;
    }

    /**
     * Returns the amount of money being sent to the player.
     * @return amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of money being sent.
     * @param amount amount being set
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Gets if the timer started when the bean was created.
     * @return if timer started
     */
    @JsonProperty("timer_started")
    public boolean isTimer_started() {
        return timer_started;
    }

    /**
     * Sets if the timer started.
     * @param timer_started boolean specifying if it changed.
     */
    @JsonProperty("timer_started")
    public void setTimer_started(boolean timer_started) {
        this.timer_started = timer_started;
    }
}
