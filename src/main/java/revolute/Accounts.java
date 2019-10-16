package revolute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import static revolute.ServiceRoutes.LOG;

/**
 * Singleton class for In-Memory account database
 *
 * @author Babatope Festus
 */
public class Accounts implements Repository {

    // Just a singleton for simplicity
    private Map<String, Account> accounts = Collections.synchronizedMap(new HashMap<>());

    /**
     * Internal performance log account transfer information. This map can be
     * improved but for now provides the basis for future performance
     * optimizations. Due to the stateless deployment architecture we don't want
     * to write this to disk, but will pull it off using a REST request and
     * aggregate with other performance metrics {@link #ping()}
     */
    private Map<String, List<String>> transactions = Collections.synchronizedMap(new HashMap<>());
    private static AtomicLong sequence = new AtomicLong(1);

    private Accounts() {
    }

    private static class Instance {

        private static final Accounts INSTANCE = new Accounts();
    }

    public static Accounts getInstance() {
        return Instance.INSTANCE;
    }

    public String createNextId() {
        String threadId = String.format("%02d", Thread.currentThread().getId());
        String atomicId = String.format("%08d", sequence.getAndIncrement());
        return threadId + atomicId;
    }

    @Override
    public String createNextLongId() {
        String threadId = String.format("%02d", Thread.currentThread().getId());
        String atomicId = String.format("%14d", sequence.getAndIncrement());
        return threadId + atomicId;
    }

    @Override
    public Fault save(Account account) {
        Fault fault = new Fault("00", "Completed successfully");
        try {
            if (account.getId() == null || account.getId().trim().isEmpty()) {
               String id = add(account);
               fault.setData(id);
            } else {
                update(account);
            }
        } catch (Exception ex) {
            fault = new Fault("21", "error creating record");
            LOG.log(Level.SEVERE, "could not create record ", ex);
        }
        return fault;
    }

    @Override
    public String add(Account account) {
        String id = createNextId();
        account.setId(id);
        accounts.put(id, account);
        return id;
    }

    @Override
    public void update(Account account) {
        Account stored = accounts.get(account.getId());
        stored.setName(account.getName());
        stored.setBalance(account.getBalance());
        accounts.put(account.getId(), stored);
    }

    @Override
    public Account get(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public Collection<Account> iterate() {
        return Collections.unmodifiableCollection(accounts.values());
    }

    @Override
    public Collection<List<String>> transactions() {
        return Collections.unmodifiableCollection(transactions.values());
    }

    @Override
    public List<String> transactions(String id) {
        return transactions.get(id);
    }

    @Override
    public void trace(String accountId, String event) {
        List<String> trans = transactions.getOrDefault(accountId, new ArrayList<>());
        trans.add(event);
        transactions.put(accountId, trans);
    }

}
