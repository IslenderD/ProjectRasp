package resources.Encoders;

import com.google.gson.Gson;
import jakarta.websocket.Encoder;
import beans.WinnerBean;

/**
 * Encodes a MoveBean to Json in order to be sent.
 */
public class WinnerEncoder implements Encoder.Text<WinnerBean> {

    private static Gson gson = new Gson();

    /**
     * When sending a winnerBean to frontEnd, make it a json.
     * @param winnerBean winnerBean with the information wanted
     * @return json with the information of the winnerBean.
     */
    @Override
    public String encode(WinnerBean winnerBean) {
        return gson.toJson(winnerBean);
    }

}
