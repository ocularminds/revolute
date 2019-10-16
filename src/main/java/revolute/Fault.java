package revolute;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * The Fault class is processing error wrapper object for holding details result
 * after certain operations.
 *
 * The fault basically contains the error(error code) and the fault(error
 * description) with arbitrary object which is any additional data to be
 * returned along with the Fault.
 *
 * @author Jejelowo B. Festus
 * @author festus.jejelowo@ocularminds.com
 */
public class Fault implements Serializable {

    /**
     * The error code
     */
    private String error;

    /**
     * The error description
     */
    private String fault;

    /**
     * Optional data
     */
    private Object data;

    private static final Map<String, String> CODES = Fault.load();
    public static final String SUCCESS_APPROVAL = "00";
    public static final String INVALID_TRANSACTION = "12";
    public static final String INVALID_AMOUNT = "13";
    public static final String INVALID_ACCT_NO = "14";
    public static final String INSUFFICIENT_FUNDS = "51";
    public static final String NO_ACCOUNT_FOUND = "52";
    public static final String SYSTEM_MALFUNCTION = "96";

    public Fault() {
    }

    public Fault(String error, String fault) {
        this(error, fault, null);
    }

    /**
     * Primary constructor for the Fault class
     *
     * @param error Error code
     * @param fault Error details or description
     * @param data Optional data returned with the error if successful
     *
     */
    public Fault(String error, String fault, Object data) {
        this.error = error;
        this.fault = fault;
        this.data = data;
    }

    /**
     * @return Compares the error code to '00' to check is they are equal
     */
    public boolean isSuccess() {
        return this.error.equals("00") || this.error.equals("200") || this.error.equals("201");
    }

    /**
     * Checks is the error code meant failure. That is not equals 00
     *
     * @return boolean result of comparing error with 00
     */
    public boolean isFailed() {
        return !this.isSuccess();
    }

    /**
     *
     * @return String error code
     */
    public String getError() {
        return error;
    }

    /**
     * Assigns new error code
     *
     * @param error String error code
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     *
     * @return String the error description
     */
    public String getFault() {
        return fault;
    }

    /**
     * Assigns error details
     *
     * @param fault String error description
     */
    public void setFault(String fault) {
        this.fault = fault;
    }

    /**
     *
     * @return Optional data
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets data value
     *
     * @param o Optional data
     */
    public void setData(Object o) {
        data = o;
    }

    /**
     * Transaction error code mapper
     *
     * @param err String error code
     * @return Error description
     */
    public static String error(String err) {
        return CODES.get(err) != null ? CODES.get(err) : "Unknown error";
    }

    private static Map<String, String> load() {
        Map<String, String> responses = new HashMap<>();
        responses.put(Fault.SUCCESS_APPROVAL, "Successful approval");
        responses.put(Fault.INVALID_TRANSACTION, "Invalid transaction");
        responses.put(Fault.INVALID_AMOUNT, "Invalid amount ");
        responses.put(Fault.INVALID_ACCT_NO, "Invalid account number");
        responses.put(Fault.INSUFFICIENT_FUNDS, "Insufficient funds");
        responses.put(Fault.NO_ACCOUNT_FOUND, "No account found");
        responses.put(Fault.SYSTEM_MALFUNCTION, "System malfunction");
        return responses;

    }
}
