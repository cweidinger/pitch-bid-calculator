package api_gateway;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ApiGatewayResponse {

    private final String body;
    private final Map<String, String> headers;
    private final int statusCode;

    public ApiGatewayResponse(final String body, final Map<String, String> headers, final int statusCode) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
