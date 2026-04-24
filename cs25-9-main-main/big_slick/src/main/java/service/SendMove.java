package service;

import beans.*;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Sends moves.
 */
public class SendMove {

    /**
     * Sends a moveBean to the other backend (raspberryPi).
     * @param moveBean Bean with all the information being sent.
     * @return 1==success; 0==No connection was made; other== error that happened.
     */
    public static int sendingMove(MoveBean moveBean) {
        try {
            HttpRequest request = HttpHandler.sendJson("/move", moveBean);
            HttpResponse<String> response = HttpHandler.getClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return 1;
            } else {
                return response.statusCode();
            }
        } catch (InterruptedException | IOException e) {
            return 0;
        }
    }

    /**
     * Sends a personBean to the other backend (raspberryPi).
     * @param personBean Bean with all the information being sent.
     * @return 1== success; 0==No connection was made; other == error happened
     */
    public static int sendPlayerJoined(PersonBean personBean) {
        try {
            HttpRequest request = HttpHandler.sendJson("/join", personBean);
            HttpResponse<String> response = HttpHandler.getClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return 1;
            } else {
                return response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            return 0;
        }
    }

    /**
     * Sends a leaveBean to the other backend (raspberryPi).
     * @param leaveBean Bean with all the information needed.
     * @return If send was successful.
     */
    public static int sendPlayerLeft(LeaveBean leaveBean) {
        try {
            HttpRequest request = HttpHandler.sendJson("/leave", leaveBean);
            HttpResponse<String> response = HttpHandler.getClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return 1;
            } else {
                return response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            return 0;
        }
    }

    /**
     * Sends a createGameBean to the other backend (raspberryPi).
     * @param createGameBean Bean with all the info needed
     * @return If send was successful
     */
    public static int sendCreateGame(CreateGameBean createGameBean) {
        try {
            HttpRequest request = HttpHandler.sendJson("/create", createGameBean);
            HttpResponse<String> response = HttpHandler.getClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return 1;
            } else {
                return response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            return 0;
        }
    }

    static void main() {
        MoveBean moveBean = new MoveBean("Domingo", "Fold", -1);
        int respond = sendingMove(moveBean);
        System.out.println(respond);
    }
}
