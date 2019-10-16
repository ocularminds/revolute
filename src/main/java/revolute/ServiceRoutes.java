package revolute;

import com.google.gson.Gson;
import java.util.logging.Logger;
import spark.Spark;

/**
 * Concrete class for endpoint paths
 *
 * @author Babatope Festus
 */
public class ServiceRoutes implements Routes {

    private final Gson gson;
    private Repository accounts;
    private static final String JSON = "application/json;charset=\"utf-8\"";
    static final Logger LOG = Logger.getLogger(ServiceRoutes.class.getName());

    public ServiceRoutes() {
        this(Accounts.getInstance());
    }

    public ServiceRoutes(Repository repository) {
        gson = new Gson();
        accounts = repository;
    }

    @Override
    public void define() {

        Spark.get("/", (req, res) -> "Ready. Healthy");

        Spark.get("/ping", (req, res) -> "Pong");

        Spark.post("/accounts", (req, res) -> {
            res.type(JSON);
            res.status(200);
            return gson.toJson(accounts.save(gson.fromJson(req.body(), Account.class)));
        });

        Spark.get("/accounts", (req, res) -> {
            return gson.toJson(accounts.iterate());
        });

        Spark.get("/accounts/:id", (req, res) -> {
            return gson.toJson(accounts.get(req.params("id")));
        });

        Spark.get("/accounts/:id/events", (req, res) -> {
            return gson.toJson(accounts.transactions(req.params("id")));
        });

        Spark.post("/transfer", (req, res) -> {
            res.type(JSON);
            res.status(200);
            return gson.toJson(transfer(req.body()));
        });
    }

    private Fault transfer(String json) {
        Transfer transfer = gson.fromJson(json, Transfer.class);
        return new FundProcessor().process(transfer, accounts);
    }
}
