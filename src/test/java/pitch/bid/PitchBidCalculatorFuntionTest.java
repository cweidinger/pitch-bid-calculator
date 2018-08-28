package pitch.bid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import api_gateway.ApiGatewayRequest;
import api_gateway.ApiGatewayResponse;
import org.junit.Test;

import java.util.Collections;

public class PitchBidCalculatorFuntionTest {

  @Test
  public void works() {
    PitchBidCalculatorFuntion pitchBidCalculatorFuntion = new PitchBidCalculatorFuntion();
    ApiGatewayRequest request = new ApiGatewayRequest();
    request.setQueryStringParameters(Collections.singletonMap("hand", "9h,ac,kc,qc,jc,tc,9c,8c,7c"));
    ApiGatewayResponse result = pitchBidCalculatorFuntion.handleRequest(request, null);
    assertEquals(200, result.getStatusCode());
    assertEquals("application/json", result.getHeaders().get("Content-Type"));
    assertNotNull(result.getBody());
  }
}
