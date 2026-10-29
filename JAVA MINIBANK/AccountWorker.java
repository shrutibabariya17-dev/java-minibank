public class AccountWorker implements Runnable {

    private Account account;
    private int times;
    private long amount;
    private boolean useUnsafe;

    public AccountWorker(Account account, int times, long amount) {
        this(account, times, amount, false);
    }

    public AccountWorker(Account account, int times, long amount, boolean useUnsafe) {
        this.account = account;
        this.times = times;
        this.amount = amount;
        this.useUnsafe = useUnsafe;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        System.out.println("  [" + threadName + "] started execution (" + times + " deposits of Rs. " + amount + ")");

        for (int i = 0; i < times; i++) {
            if (useUnsafe) {
                account.unsafeDeposit(amount);
            } else {
                try {
                    account.deposit(amount);
                } catch (InvalidAmountException e) {
                    System.out.println("  [" + threadName + "] error: " + e.getMessage());
                    break;
                }
            }
        }

        System.out.println("  [" + threadName + "] completed.");
    }
}
