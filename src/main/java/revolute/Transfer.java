package revolute;

import java.math.BigDecimal;

/**
 *
 * @author Babatope Festus
 */
public class Transfer {

    private String source;
    private String target;
    private BigDecimal amount;

    public Transfer() {
    }

    public Transfer(String source, String target, BigDecimal amount) {
        this.source = source;
        this.target = target;
        this.amount = amount;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
