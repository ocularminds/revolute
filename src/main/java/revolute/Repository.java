/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package revolute;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author Dev.io
 */
public interface Repository {

    void add(Account account);

    void trace(String accountId, String event);

    void update(Account account);

    Account get(String accountno);

    Collection<Account> iterate();

    /**
     *
     * @return
     */
    Collection<List<String>> transactions();

    List<String> transactions(String id);
}
