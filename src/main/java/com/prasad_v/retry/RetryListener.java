package com.prasad_v.retry;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.logging.CustomLogger;

/**
 * RetryListener implements IAnnotationTransformer to automatically apply
 * the RetryAnalyzer to all test methods at runtime.
 */
public class RetryListener implements IAnnotationTransformer {

    private static final CustomLogger logger = new CustomLogger(RetryListener.class);

    /**
     * This method is called by TestNG to give the transformer an opportunity to modify
     * a TestNG annotation read from your test classes.
     *
     * @param annotation The annotation that will be used for the test
     * @param testClass The test class
     * @param testConstructor The test constructor
     * @param testMethod The test method
     */
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        boolean retryEnabled = Boolean.parseBoolean(firstNonBlank(
                ConfigurationManager.getInstance().getConfigProperty("request.retry.enabled", ""),
                ConfigurationManager.getInstance().getConfigProperty("retry.enabled", "true")
        ));

        if (retryEnabled) {
            String methodName = testMethod != null ? testMethod.getName() : "unknown";
            logger.info("Setting retry analyzer for: " + methodName);
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
