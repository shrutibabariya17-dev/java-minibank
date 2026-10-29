import java.io.Serializable;

public abstract class Account implements Transactable, Serializable, Comparable<Account> {

    private static final long serialVersionUID = 1L;

    @Id
    private final String accountno;

    @MaxLength(10)
    private String ownername;

    @Positive
    private long balance;

    private boolean active;

    private static long accountcnt = 0;

    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    public long calculateShortfall(long amount) {
        return amount > balance ? amount - balance : 0;
    }

    public double monthlyInterest() {
        return (balance * interestRate()) / (12.0 * 100.0);
    }

    @Override
    public String toString() {
        return accountno + " - " + ownername + " - Balance: " + balance + " - Active: " + active;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || !(obj instanceof Account)) {
            return false;
        }

        Account other = (Account) obj;

        if (this.accountno == null) {
            return other.accountno == null;
        }

        return this.accountno.equals(other.accountno);
    }

    @Override
    public int hashCode() {
        return accountno != null ? accountno.hashCode() : 0;
    }

    @Override
    public int compareTo(Account other) {
        if (other == null) {
            return 1;
        }
        if (this.accountno == null && other.accountno == null) {
            return 0;
        }
        if (this.accountno == null) {
            return -1;
        }
        if (other.accountno == null) {
            return 1;
        }
        return this.accountno.compareTo(other.accountno);
    }

    private static String generateAccountNumber() {
        accountcnt++;
        return String.format("AC%04d", accountcnt);
    }

    public Account(String ownername, long balance) {
        this.accountno = generateAccountNumber();
        this.ownername = ownername;
        this.balance = balance;
        this.active = true;
    }

    public Account(String ownername) {
        this(ownername, 0);
    }

    public Account(String accountno, String ownername, long balance) {
        this.accountno = accountno;
        this.ownername = ownername;
        this.balance = balance;
        this.active = true;
    }

    public void unsafeDeposit(long amount) {
        long current = this.balance;
        try {
            Thread.sleep(0, 50);
        } catch (InterruptedException ignored) {
        }
        this.balance = current + amount;
    }

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive: " + amount);
        }
        balance = balance + amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive: " + amount);
        }

        if (!canWithdraw(amount)) {
            long shortfall = calculateShortfall(amount);
            throw new InsufficientFundsException("Insufficient funds to withdraw Rs. " + amount, shortfall);
        }

        balance = balance - amount;
        return true;
    }

    public void transfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new AccountNotFoundException("Destination account not found (null).");
        }

        if (this.equals(to)) {
            throw new InvalidAmountException("Cannot transfer to the same account.");
        }

        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive: " + amount);
        }

        this.withdraw(amount);

        try {
            to.deposit(amount);
        } catch (InvalidAmountException e) {
            this.balance = this.balance + amount;
            throw new BankException("Transfer failed during deposit. Source balance restored.", e);
        } finally {
        }
    }

    public void safeTransfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new AccountNotFoundException("Destination account not found (null).");
        }

        if (this.equals(to)) {
            throw new InvalidAmountException("Cannot transfer to the same account.");
        }

        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be positive: " + amount);
        }

        Account firstLock = this.accountno.compareTo(to.accountno) < 0 ? this : to;
        Account secondLock = (firstLock == this) ? to : this;

        synchronized (firstLock) {
            synchronized (secondLock) {
                this.withdraw(amount);
                try {
                    to.deposit(amount);
                } catch (InvalidAmountException e) {
                    this.balance = this.balance + amount;
                    throw new BankException("Transfer failed during deposit. Source balance restored.", e);
                }
            }
        }
    }

    public String getAccountNumber() {
        return accountno;
    }

    public String getOwnerName() {
        return ownername;
    }

    public synchronized long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getStatement() {
        return "--------------------------------\n"
                + "       ACCOUNT STATEMENT        \n"
                + "--------------------------------\n"
                + "Account Number : " + accountno + "\n"
                + "Owner Name     : " + ownername + "\n"
                + "Balance        : " + balance + "\n"
                + "Active Status  : " + (active ? "Active" : "Inactive") + "\n"
                + "--------------------------------";
    }

    public String getAccountStatement() {
        return getStatement();
    }
}
