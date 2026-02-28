package com.prasad_v.services;

import com.prasad_v.enums.RequestType;
import com.prasad_v.exceptions.APIException;
import com.prasad_v.requestbuilder.RequestBuilder;
import io.restassured.response.Response;

import java.util.Map;

/**
 * Base service abstraction for domain-specific API services.
 */
public abstract class BaseApiService {

    protected Response execute(RequestType method, String path, Map<String, String> headers, Object body) {
        RequestBuilder builder = new RequestBuilder()
                .setRequestType(method)
                .setPath(path)
                .addCommonHeaders();

        if (headers != null && !headers.isEmpty()) {
            builder.addHeaders(headers);
        }
        if (body != null) {
            builder.setBody(body);
        }

        try {
            return builder.execute();
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw new APIException("Failed to execute request for path: " + path, e);
        }
    }
}
