public class InsufficientFundsException extends BankException {

    private long shortfall;

    public InsufficientFundsException(String message, long shortfall) {
        super(message);
        this.shortfall = shortfall;
    }

    public InsufficientFundsException(String message) {
        this(message, 0);
    }

    public long getShortfall() {
        return shortfall;
    }
}
