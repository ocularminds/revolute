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
 * @author Festus Babatope
 */
public interface Repository {

    /**
     * Creates next long id as transaction reference
     *
     * @return String Id
     */
    String createNextLongId();

    /**
     * Creates a new account or update the account record if ID already exists
     *
     * @param account
     * @return Fault containing error code, fault description and optional data for id
     */
    Fault save(Account account);


    /**
     * Creates a new account record
     *
     * @param account
     * @return String new account number
     */
    String add(Account account);    

    /**
     * Creates a new account or update the account record if ID already exists
     *
     * @param accountId
     * @param event
     */
    void trace(String accountId, String event);

    /**
     * Updates account record
     *
     * @param account Account to update
     */
    void update(Account account);

    

    /**
     * Query account by ID
     *
     * @param accountno
     * @return Account matching the account number
     */
    Account get(String accountno);

   

    /**
     * Queries list of accounts
     *
     * @return Collection of accounts
     */
    Collection<Account> iterate();

    /**
     *
     * @return
     */
    Collection<List<String>> transactions();    

    /**
     * Query transactions for certain account number
     *
     * @param id account Id
     * @return List of strings of account events
     */
    List<String> transactions(String id);
}
