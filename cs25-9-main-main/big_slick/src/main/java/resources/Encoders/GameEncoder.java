package resources.Encoders;

import com.google.gson.Gson;
import jakarta.websocket.Encoder;
import beans.GameBean;

/**
 * Encodes a gameBean into a json automatically when sending from GameSocket.
 */
public class GameEncoder implements Encoder.Text<GameBean> {
    private static Gson gson = new Gson();

    /**
     * When sending a gameBean to frontEnd, make it a json.
     * @param gameBean gameBean with the information wanted
     * @return json with the information of the gameBean.
     */
    @Override
    public String encode(GameBean gameBean) {
        return gson.toJson(gameBean);
    }
}
