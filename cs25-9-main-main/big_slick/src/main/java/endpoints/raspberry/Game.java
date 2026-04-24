package endpoints.raspberry;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import static dao.AuthentificationDao.*;
import beans.*;
import static dao.WalletDao.*;
import static service.SendMove.*;

import static websocket.GameSocket.sendGame;
import static websocket.GameSocket.sendWinner;

/**
 * Receives/sends data related with people to raspberry. backend/game
 */
@Path("/game")
public class Game{

    /**
     * Called when received a POST request to backend/game/turn. Receives a gameBean from the raspberry pi, sends it to the frontEnd
     * @param gameBean contains all the information about the game
     * @return if it was successful, why not if not
     */
    @POST
    @Path("/turn")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response game_intercommunicate(GameBean gameBean) {
        System.out.println("inside game_Intercommunication");
        boolean sendGame = sendGame(gameBean);
        if (!sendGame) {
            return new InternalServerErrorException("Communication with frontEnd not possible|Game").getResponse();
        }
        return Response.ok().build();
    }


    /**
     * Called when received a POST request to backend/raspberry/game/win. Receives a winnerBean from raspberry pi, sends it to the frontEnd
     * @param winnerBean contains all information about the winner
     * @return if it was successful, why not if not
     */
    @POST
    @Path("/win")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response winner_intercommunicate(WinnerBean winnerBean) {
        System.out.println("Winner intercommunicate");
        boolean sendWinner = sendWinner(winnerBean);
        if (!sendWinner) {
            throw new InternalServerErrorException("Communication with frontEnd not possible|Winner");
        }
        return Response.ok().build();
    }
}
