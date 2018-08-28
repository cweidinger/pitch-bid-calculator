package pitch.bid;

import java.util.HashMap;
import java.util.Map;

import api_gateway.ApiGatewayRequest;
import api_gateway.ApiGatewayResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import pitch.simulation.CalcBid;
import pitch.simulation.CardsStack;

public class PitchBidCalculatorFuntion implements RequestHandler<ApiGatewayRequest, ApiGatewayResponse> {

    public ApiGatewayResponse handleRequest(final ApiGatewayRequest input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        try {
            String hand = input.getQueryStringParameters().get("hand");
            String callback = input.getQueryStringParameters().getOrDefault("callback", null);
            String json = handToJson(hand);
            if (callback == null) {
                return new ApiGatewayResponse(json, headers, 200);
            } else {
                headers.put("Content-Type", "text/javascript");
                headers.put("Encoding", "UTF-8");
                return new ApiGatewayResponse(String.format("%s(%s)", callback, json), headers, 200);
            }
        } catch (Exception e) {
            return new ApiGatewayResponse(String.format("{\"error\":\"%s\"}", e.toString()), headers, 400);
        }
    }

    private String handToJson(String handParam) {
        Integer dealer = 0;
        Integer bidder = 2;
        Map<String,Object> data = new HashMap<>(); // need to default so compiler will choose right data type
        CardsStack hand = new CardsStack(handParam);
        if (handParam.split(",").length == 9) {
            CalcBid calcBid = new CalcBid(CalcBid.MULTI_THREAD);
            data.put("advice", calcBid.forAllBidsAndSuits(dealer, hand, bidder));
            data.put("returnCode", "success");
        } else {
            data.put("advice", "default advice");
            data.put("returnCode", "There wasn't 9 cards in your hand.");
        }
        return new Gson().toJson(data);
    }

}
