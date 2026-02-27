package com.prasad_v.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestUtils {

    public static Response post(RequestSpecification spec, String body) {
        return RestAssured.given(spec).body(body).when().post();
    }

    public static Response get(RequestSpecification spec) {
        return RestAssured.given(spec).when().get();
    }

    public static Response put(RequestSpecification spec, String body, String token) {
        return RestAssured.given(spec).cookie("token", token).body(body).when().put();
    }

    public static Response delete(RequestSpecification spec, String token) {
        return RestAssured.given(spec).cookie("token", token).when().delete();
    }

    public static Response patch(RequestSpecification spec, String body, String token) {
        return RestAssured.given(spec).cookie("token", token).body(body).when().patch();
    }
}
