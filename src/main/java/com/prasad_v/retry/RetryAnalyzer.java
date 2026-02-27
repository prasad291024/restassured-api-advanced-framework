package APIAutomationFrameworkATB10x.src.main.java.com.prasad_v.retry;

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
        // Set RetryAnalyzer for all test methods
        if (annotation.getRetryAnalyzer() == null) {
            boolean retryEnabled = Boolean.parseBoolean(
                    ConfigurationManager.getInstance().getConfigProperty("retry.enabled", "true"));

            if (retryEnabled) {
                logger.info("Setting retry analyzer for: " + testMethod.getName());
                RetryAnalyzer analyzer = new RetryAnalyzer();

                // Get retry count from configuration or use default
                String retryCountStr = ConfigurationManager.getInstance().getConfigProperty("retry.count", "2");
                try {
                    int retryCount = Integer.parseInt(retryCountStr);
                    analyzer.setMaxRetryCount(retryCount);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid retry count in configuration. Using default value of 2");
                }

                annotation.setRetryAnalyzer(RetryAnalyzer.class);
            }
        }
    }
}