package revolute;

import spark.servlet.SparkApplication;

/**
 * Wrapper server for unit testing endpoints
 *
 * @author Babatope Festus
 */
public class WebServer implements SparkApplication {

    @Override
    public void init() {
        new Revolute().runNow();
    }
}
