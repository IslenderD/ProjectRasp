package websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import beans.GameBean;
import beans.WinnerBean;
import resources.Encoders.GameEncoder;
import resources.Encoders.WinnerEncoder;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Sends information backend to frontend.
 */
@ServerEndpoint(value ="/chat",
        encoders = { WinnerEncoder.class, GameEncoder.class })
public class GameSocket {

    /// contains every session connected to the backend. ConcurrentHashMap it's a secure multithread hashmap
    protected static Set<Session> sessionCurrent = ConcurrentHashMap.newKeySet();

    /**
     * When connection is created.
     * @param session channel of communication between frontEnd and backEnd
     */
    @OnOpen
    public void onOpen(Session session){
        sessionCurrent.add(session);
        System.out.println("Connection made!");
    }

    /**
     * When Connection is closed, calls this.
     */
    @OnClose
    public void onClose(Session session) {
        sessionCurrent.remove(session);
        System.out.println("Connection lost");
    }

    /**
     * When error happens on the HttpConnection.
     * @param throwable error
     */
    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        System.out.println("Error happened");
    }

    /// Prints if the result of sending the Bean was successful.
    private static final SendHandler sendHandler = sendResult -> {
        if (sendResult.getException() ==null) {
            System.out.println("Success");
        } else {
            System.out.println("Error occur " + sendResult.getException().getMessage());
        }
    };

    /**
     * Sends your personal information of the winner to the frontEnd.
     * @param winnerBean includes the information of the player that won
     * @return if it was successful
     */
    public static boolean sendWinner(WinnerBean winnerBean) {
        System.out.println("Inside sendWinner");
        if (sessionCurrent.isEmpty()) {
            return false;
        }
        System.out.println("There was a session, sending the winner bean");
        for (Session s : sessionCurrent) {
            if (s!=null && s.isOpen()) {
                s.getAsyncRemote().sendObject(winnerBean, sendHandler);
            }
        }
        return true;
    }

    /**
     * Sends your personal information on the game to the frontEnd.
     * @param gameBean includes the information of the game
     * @return if it was successful
     */
    public static boolean sendGame(GameBean gameBean) {
        System.out.println("Inside sendGame");
        if (sessionCurrent.isEmpty()) {
            return false;
        }
        for (Session s : sessionCurrent) {
            if (s!=null && s.isOpen()) {
                s.getAsyncRemote().sendObject(gameBean, sendHandler);
            }
        }
        return true;
    }

}
