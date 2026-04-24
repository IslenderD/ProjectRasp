package resources;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static dao.WalletDao.resetAmount;

/**
 * Code related to the timer to restart your chips.
 */
public class Timer {

    /// Creates one single thread that runs on background.
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    /// How much it has to wait (in hours).
    private final static int AMOUNT_OF_TIMER = 24;

    /**
     * Creates a timer on a background thread.
     * @param username username of that will get the money after the timer ends
     */
    public static void startTimer(String username) {
        scheduler.schedule(()-> resetAmount(username), AMOUNT_OF_TIMER, TimeUnit.HOURS);
    }
}
