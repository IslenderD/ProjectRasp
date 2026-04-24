package endpoints.frontend;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.beans.*;
import resources.Exceptions.HttpRequestException;
import static resources.Timer.startTimer;

import beans.*;
import static dao.WalletDao.*;
import static service.SendMove.*;

/**
 * Receives/sends data related with moves of player to frontend. backend/frontend/move
 */
@Path("/move")
public class Move{

    private static boolean timer_running = false;

    /**
     * Called when receiving a POST request to /backend/frontend/move. Receives a move from the front end, sends it to the other backend.
     * @param moveBean bean with the information needed.
     * @return if it was successful, why it was not if not.
     * @throws HttpRequestException special httpRequest which changes depending on the error that happened.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendMove(MoveBean moveBean) throws HttpRequestException {
        System.out.println("Inside sendMove");
        int proceed = sendingMove(moveBean);
        if (proceed == 0) {
            throw new HttpRequestException("Request didn't reach server", proceed);
        } else if (proceed != 1) {
            throw new HttpRequestException("Error happened: " + proceed, proceed);
        }
        return Response.ok("Move sent correctly").build();
    }

    /**
     * Called when receiving a POST request /backend/frontend/move/joinPlayer. Sends a player that joined the game to the other backend.
     * @param personBean personBean with the information about the player that joined
     * @return if it was successful, why it was not if not
     * @throws HttpRequestException special httpRequest which changes depending on the error that happened.
     */
    @POST
    @Path("/joinPlayer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendJoinPlayer(PersonBean personBean) throws HttpRequestException {
        System.out.println("Inside join player");
        int proceed = sendPlayerJoined(personBean);
        if (proceed == 0) {
            throw new HttpRequestException("Request didn't reach server", proceed);
        } else if (proceed!=1) {
            throw new HttpRequestException("Error happened: " + proceed, proceed);
        }
        if (!changeAmount(personBean.getUsername(), -personBean.getAmount_bet())) {
            throw new InternalServerErrorException("Communication with database not possible|SQL");
        }
        return Response.ok("Player sent correctly").build();
    }

    /**
     * Called when receiving a DELETE request /backend/frontend/move/leavePlayer. Sends a player that left the game to the other backend.
     * @param leaveBean personBean with the information about the player that left
     * @return if it was successful, why it was not if not
     * @throws HttpRequestException special httpRequest which changes depending on the error that happened.
     */
    @DELETE
    @Path("/leavePlayer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendLeavePlayer(LeaveBean leaveBean) throws HttpRequestException {
        System.out.println("inside sendLeave");
        int proceed = sendPlayerLeft(leaveBean);
        if (proceed == 0) {
            throw new HttpRequestException("Request didn't reach server", proceed);
        } else if (proceed!=1) {
            throw new HttpRequestException("Error happened: " + proceed, proceed);
        }
        if (!changeAmount(leaveBean.getPersonLeaving(), leaveBean.getAmountWon())) {
            throw new InternalServerErrorException("Communication with database not possible|SQL");
        }
        return Response.ok("Player sent correctly").build();
    }

    /**
     * Called when receiving a POST request /backend/frontend/move/createGame. Send a game being created.
     * @param createGameBean createGameBean with the information about the player that joined
     * @return if it was successful, why it was not if not
     * @throws HttpRequestException special httpRequest which changes depending on the error that happened.
     */
    @POST
    @Path("/createGame")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendCreateGameEndpoint(CreateGameBean createGameBean) throws HttpRequestException {
        System.out.println("In create game");
        int proceed = sendCreateGame(createGameBean);
        if (proceed == 0) {
            System.out.println("Error 0");
            throw new HttpRequestException("Request didn't reach server", proceed);
        } else if (proceed!=1) {
            System.out.println("Error" + proceed);
            throw new HttpRequestException("Error happened: " + proceed, proceed);
        }
        if (!changeAmount(createGameBean.getUsername(), -createGameBean.getMaxBuyIn())) {
            throw new InternalServerErrorException("Communication with database not possible|SQL");
        }
        System.out.println("Went good");
        return Response.ok("Player sent correctly").build();
    }

    /**
     * Called when received a GET request to /backend/frontend/move. Gets the current money amount of the player.
     * @param username username of the get being checked
     * @return amount of the player, or error if happened.
     */
    @GET
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMoney(@PathParam("username") String username) {
        System.out.println(username + " username");
        Integer amount = getAmount(username);
        System.out.println(amount + " amount");
        boolean timer_was_running = timer_running;
        if (amount == null) {
            throw new InternalServerErrorException("Could not get connection with sql");
        }
        if ((amount < getResetAmount()) && !timer_running) {
            startTimer(username);
            timer_running = true;
        }
        if (!timer_was_running && timer_running) {
            timer_was_running = true;
        } else {
            timer_was_running = false;
        }
        MoneyBean moneyBean = new MoneyBean(amount, timer_was_running);
        System.out.println(moneyBean.getAmount());
        return Response.ok(moneyBean).build();
    }
}
