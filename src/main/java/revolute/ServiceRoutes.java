package revolute;

import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import spark.Spark;

/**
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
            return gson.toJson(save(gson.fromJson(req.body(), Account.class)));
        });

        Spark.get("/accounts", (req, res) -> {
            return gson.toJson(accounts.iterate());
        });

        Spark.get("/accounts/:id", (req, res) -> {
            return gson.toJson(accounts.get(req.params("id")));
        });

        Spark.get("/accounts/:id/transactions", (req, res) -> {
            return gson.toJson(accounts.transactions(req.params("id")));
        });

        Spark.post("/transfer", (req, res) -> {
            res.type(JSON);
            res.status(200);
            return gson.toJson(transfer(gson.fromJson(req.body(), Transfer.class)));
        });
    }

    public Fault save(Account account) {
        Fault fault = new Fault("00", "Completed successfully");
        try {
            if (account.getId() == null || account.getId().trim().isEmpty()) {
                accounts.add(account);
            } else {
                accounts.update(account);
            }
        } catch (Exception ex) {
            fault = new Fault("21", "error creating record");
            LOG.log(Level.SEVERE, "could not create record ", ex);
        }
        return fault;
    }

    public Fault transfer(Transfer transfer) {
        return new FundProcessor(accounts).process(transfer);
    }
}
