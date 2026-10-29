public class SavingsAccount extends Account implements InterestBearing {

    private static final long serialVersionUID = 1L;

    private long minBalance;

    public SavingsAccount(String ownername, long balance, long minBalance) {
        super(ownername, balance);
        this.minBalance = minBalance;
    }

    public SavingsAccount(String accountno, String ownername, long balance, long minBalance) {
        super(accountno, ownername, balance);
        this.minBalance = minBalance;
    }

    public long getMinBalance() {
        return minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return amount > 0 && (getBalance() - amount) >= minBalance;
    }

    @Override
    public long calculateShortfall(long amount) {
        long maxAllowed = getBalance() - minBalance;
        return amount > maxAllowed ? amount - maxAllowed : 0;
    }

    @Override
    public String toString() {
        return super.toString() + " - MinBalance: " + minBalance;
    }
}
