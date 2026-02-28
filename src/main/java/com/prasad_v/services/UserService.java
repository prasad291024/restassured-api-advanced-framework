package com.prasad_v.services;

import com.prasad_v.constants.APIConstants;
import com.prasad_v.enums.RequestType;
import io.restassured.response.Response;

public class UserService extends BaseApiService {

    public Response getUsers() {
        return execute(RequestType.GET, APIConstants.USERS_ENDPOINT, null, null);
    }

    public Response getUserById(int userId) {
        return execute(RequestType.GET, APIConstants.USERS_ENDPOINT + "/" + userId, null, null);
    }

    public Response createUser(Object payload) {
        return execute(RequestType.POST, APIConstants.USERS_ENDPOINT, null, payload);
    }

    public Response updateUser(int userId, Object payload) {
        return execute(RequestType.PUT, APIConstants.USERS_ENDPOINT + "/" + userId, null, payload);
    }

    public Response deleteUser(int userId) {
        return execute(RequestType.DELETE, APIConstants.USERS_ENDPOINT + "/" + userId, null, null);
    }
}
