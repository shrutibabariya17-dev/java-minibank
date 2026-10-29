public class CurrentAccount extends Account {

    private static final long serialVersionUID = 1L;

    private long overdraftLimit;

    public CurrentAccount(String ownername, long balance, long overdraftLimit) {
        super(ownername, balance);
        this.overdraftLimit = overdraftLimit;
    }

    public CurrentAccount(String accountno, String ownername, long balance, long overdraftLimit) {
        super(accountno, ownername, balance);
        this.overdraftLimit = overdraftLimit;
    }

    public long getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return amount > 0 && (getBalance() - amount) >= -overdraftLimit;
    }

    @Override
    public long calculateShortfall(long amount) {
        long maxAllowed = getBalance() + overdraftLimit;
        return amount > maxAllowed ? amount - maxAllowed : 0;
    }

    @Override
    public String toString() {
        return super.toString() + " - OverdraftLimit: " + overdraftLimit;
    }
}
