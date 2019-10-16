package revolute;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 *
 * @author Babatope Festus
 */
public final class FundProcessor implements Processor {

    static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yy hh:mm");
    String ref;
    String date;
    BigDecimal amount;

    public FundProcessor() {
        date = SDF.format(new java.util.Date());
    }

    /**
     *
     * @param transfer
     * @param repository
     * @return
     */
    @Override
    public Fault process(Transfer transfer, Repository repository) {
        System.out.println("\nprocessing transfer");
        String source = transfer.getSource();
        String target = transfer.getTarget();
        ref = repository.createNextLongId();
        amount = transfer.getAmount();
        Fault fault = validate(repository, source, target, amount);
        if (fault.isFailed()) {
            return fault;
        }
        debit(repository, source, amount);
        credit(repository, target, amount);
        return new Fault(
                Fault.SUCCESS_APPROVAL,
                Fault.error(Fault.SUCCESS_APPROVAL),
                ref
        );
    }

    private void debit(Repository accounts, String accountId, BigDecimal amount) {
        System.out.println("debit " + accountId + " amount " + amount);
        Account account = accounts.get(accountId);
        BigDecimal balance = account.getBalance().subtract(amount);
        account.setBalance(balance);
        accounts.update(account);
        trace(accounts, accountId, "Fund transfer", balance);
    }

    private void credit(Repository accounts, String accountId, BigDecimal amount) {
        System.out.println("credit " + accountId + " amount " + amount);
        Account account = accounts.get(accountId);
        BigDecimal balance = account.getBalance().add(amount);
        account.setBalance(balance);
        accounts.update(account);
        trace(accounts, accountId, "Fund received", balance);
    }

    private void trace(Repository accounts, String account, String event, BigDecimal balance) {
        String pattern = "%s %s %6.2f %6.2f %s";
        String l = String.format(pattern, ref, event, amount, balance, date);
        System.out.println(l);
        accounts.trace(account, l);
    }

    private Fault validate(Repository accounts, String source, String target, BigDecimal amount) {

        if (source == null || isNotaNumber(source)) {
            return new Fault(Fault.INVALID_ACCT_NO, Fault.error(Fault.INVALID_ACCT_NO));
        }

        if (target == null || isNotaNumber(target)) {
            return new Fault(Fault.INVALID_ACCT_NO, Fault.error(Fault.INVALID_ACCT_NO));
        }

        if (amount == null || amount.longValue() == 0 || amount.compareTo(BigDecimal.ZERO) == -1) {
            return new Fault(Fault.INVALID_AMOUNT, Fault.error(Fault.INVALID_AMOUNT));
        }

        if (accounts.get(source) == null || accounts.get(target) == null) {
            return new Fault(Fault.NO_ACCOUNT_FOUND, Fault.error(Fault.NO_ACCOUNT_FOUND));
        }

        if (accounts.get(source).getBalance().compareTo(amount) == -1) {
            return new Fault(Fault.INSUFFICIENT_FUNDS, Fault.error(Fault.INSUFFICIENT_FUNDS));
        }
        return new Fault("00", "Success");
    }

}
