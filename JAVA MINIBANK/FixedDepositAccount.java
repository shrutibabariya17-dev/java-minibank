public class FixedDepositAccount extends Account implements InterestBearing, Premium {

    private static final long serialVersionUID = 1L;

    public FixedDepositAccount(String ownername, long balance) {
        super(ownername, balance);
    }

    public FixedDepositAccount(String accountno, String ownername, long balance) {
        super(accountno, ownername, balance);
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return false;
    }

    @Override
    public long calculateShortfall(long amount) {
        return amount;
    }
}
