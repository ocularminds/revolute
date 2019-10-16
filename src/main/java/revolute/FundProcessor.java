package revolute;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 *
 * @author Babatope Festus
 */
public final class FundProcessor implements Processor {

    Repository accounts;
    static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yy hh:mm");
    String ref;
    String date;
    BigDecimal amount;

    public FundProcessor() {
        this(Accounts.getInstance());
    }

    public FundProcessor(Repository repository) {
        this.accounts = repository;
    }

    @Override
    public Fault process(final Transfer transfer) {
        String source = transfer.getSource();
        String target = transfer.getTarget();
        amount = transfer.getAmount();

        Fault fault = validate(source, target, amount);
        if (fault.isFailed()) {
            return fault;
        }

        ref = new StringBuilder(Long.toString(System.currentTimeMillis())).reverse().substring(0, 19);
        date = SDF.format(new java.util.Date());
        debit(source, amount);
        credit(target, amount);
        return new Fault(Fault.SUCCESS_APPROVAL, Fault.error(Fault.SUCCESS_APPROVAL));
    }

    private void debit(String accountId, BigDecimal amount) {
        Account account = accounts.get(accountId);
        BigDecimal balance = account.getBalance().subtract(amount);
        account.setBalance(balance);
        accounts.update(account);
        trace(accountId, "Fund transfer", balance);
    }

    private void credit(String accountId, BigDecimal amount) {
        Account account = accounts.get(accountId);
        BigDecimal balance = account.getBalance().add(amount);
        account.setBalance(balance);
        accounts.update(account);
        trace(accountId, "Fund received", balance);
    }

    private void trace(String account, String event, BigDecimal balance) {
        String pattern = "%s %s %6.2f %6.2f %s";
        String l = String.format(pattern, ref, event, amount, balance, date);
        accounts.trace(account, l);
    }

    private Fault validate(String source, String target, BigDecimal amount) {

        if (source == null || isNotaNumber(source)) {
            return new Fault(Fault.INVALID_ACCT_NO, Fault.error(Fault.INVALID_ACCT_NO));
        }

        if (target == null || isNotaNumber(target)) {
            return new Fault(Fault.INVALID_ACCT_NO, Fault.error(Fault.INVALID_ACCT_NO));
        }

        if (amount == null || amount.longValue() == 0) {
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
