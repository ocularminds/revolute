package revolute;

import java.util.Properties;
import spark.Spark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * App QBean
 * <p>
 * Bootstraps the embedded jetty server and runs to power the RESTful API and
 * the front end application
 *
 * @author Jejelowo B. Festus
 * @author Ocular-Minds Software
 */
public class Revolute {

    Properties properties;
    Repository repository;
    private static final Logger LOG = LoggerFactory.getLogger(Revolute.class);

    public Revolute() {
        LOG.info("Starting App revolute... ");
        repository = Accounts.getInstance();
    }

    public void runNow() {
        new ServiceRoutes(repository).define();
    }

    public static void main(String[] args) {
        Revolute revolute = new Revolute();
        revolute.runConfiguration();
        revolute.runNow();
    }

    private void runConfiguration() {
        try {
            Spark.threadPool(200, 10, 120);
            Spark.port(4132);
            Spark.exception(Exception.class, (ex, req, res) -> {
                LOG.error("request failed ", ex);
            });
        } catch (NumberFormatException ex) {
            LOG.info("cannot configure port 4321 using 4567 instead");
            LOG.error("error configuring Web Server ", ex);
        }
    }
}
