package revolute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Accounts() {
    }

    private static class Instance {

        private static final Accounts INSTANCE = new Accounts();
    }

    public static Accounts getInstance() {
        return Instance.INSTANCE;
    }

    @Override
    public void add(Account account) {
        accounts.put(account.getId(), account);
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
