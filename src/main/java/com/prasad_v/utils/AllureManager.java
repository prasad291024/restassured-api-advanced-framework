package com.prasad_v.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;

public class AllureManager {

    public static void logStep(String message) {
        Allure.step(message);
    }

    public static void logStep(String message, Status status) {
        Allure.step(message, status);
    }

    public static void attachText(String name, String content) {
        Allure.addAttachment(name, "text/plain", content);
    }

    public static void attachJson(String name, String json) {
        Allure.addAttachment(name, "application/json", json);
    }

    public static void attachXml(String name, String xml) {
        Allure.addAttachment(name, "application/xml", xml);
    }

    public static void setTestDescription(String description) {
        Allure.description(description);
    }

    public static void addLabel(String name, String value) {
        Allure.label(name, value);
    }

    public static void addLink(String name, String url) {
        Allure.link(name, url);
    }
}
