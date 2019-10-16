package revolute;

/**
 * The interface to be implemented for funds transfer
 *
 * @author Festus Babatope
 */
public interface Processor {

    static final String SPACE = " ";

    /**
     * Processes fund transfer debiting source account and crediting target
     * account It also performs relevant check on the input data and accounts
     *
     * @param transfer Transfer object containing transfer parameters
     * @param repository In-Memory database
     * @return
     */
    Fault process(Transfer transfer, Repository repository);

    default boolean isNotaNumber(String number) {
        try {
            Long.parseLong(number);
            return false;
        } catch (NumberFormatException ex) {
            return true;
        }
    }
}
