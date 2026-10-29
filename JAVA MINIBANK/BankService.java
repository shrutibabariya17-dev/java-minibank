import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BankService {

    private final Repository<Account> repository;
    private final Path logFile;

    public BankService(Repository<Account> repository, Path logFile) {
        this.repository = repository;
        this.logFile = logFile;
    }

    public Account openAccount(String type, String ownerName, long balance, long extra) throws BankException {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new InvalidAmountException("Owner name must not be empty.");
        }
        Account account;
        String t = type.trim().toUpperCase();
        if (t.equals("SAVINGS")) {
            account = new SavingsAccount(ownerName.trim(), balance, extra);
        } else if (t.equals("CURRENT")) {
            account = new CurrentAccount(ownerName.trim(), balance, extra);
        } else if (t.equals("FIXED")) {
            account = new FixedDepositAccount(ownerName.trim(), balance);
        } else {
            throw new InvalidAmountException("Unknown account type: " + type);
        }
        repository.save(account);
        return account;
    }

    public void deposit(String accountNumber, long amount) throws BankException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        account.deposit(amount);
        TransactionLog.append(logFile, accountNumber + ",DEPOSIT," + amount);
    }

    public void withdraw(String accountNumber, long amount) throws BankException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        account.withdraw(amount);
        TransactionLog.append(logFile, accountNumber + ",WITHDRAW," + amount);
    }

    public void transfer(String fromNumber, String toNumber, long amount) throws BankException {
        Account from = repository.findById(fromNumber);
        Account to = repository.findById(toNumber);
        if (from == null) {
            throw new AccountNotFoundException("Source account not found: " + fromNumber);
        }
        if (to == null) {
            throw new AccountNotFoundException("Destination account not found: " + toNumber);
        }
        from.safeTransfer(to, amount);
        TransactionLog.append(logFile, fromNumber + ",TRANSFER_OUT," + amount);
        TransactionLog.append(logFile, toNumber + ",TRANSFER_IN," + amount);
    }

    public String statement(String accountNumber) throws BankException {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        return StatementFormatter.buildStatement(account);
    }

    public ArrayList<Account> listAccounts() {
        return repository.findAll();
    }

    public ArrayList<Account> searchByName(String name) {
        ArrayList<Account> result = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            return result;
        }
        String lower = name.trim().toLowerCase();
        for (Account acc : repository.findAll()) {
            if (acc.getOwnerName().toLowerCase().contains(lower)) {
                result.add(acc);
            }
        }
        return result;
    }

    public void batchProcess(TransactionProcessor processor, String accountNumber, long depositAmount, long withdrawAmount) {
        Account account = repository.findById(accountNumber);
        if (account == null) {
            System.out.println("  [Batch] Account not found: " + accountNumber);
            return;
        }
        processor.registerAccount(account);
        processor.submit(() -> {
            try {
                account.deposit(depositAmount);
                TransactionLog.append(logFile, accountNumber + ",DEPOSIT," + depositAmount);
                System.out.println("  [Batch] Deposited Rs. " + depositAmount + " to " + accountNumber + " | Balance: Rs. " + account.getBalance());
            } catch (InvalidAmountException e) {
                System.out.println("  [Batch] Deposit failed: " + e.getMessage());
            }
        });
        processor.submit(() -> {
            try {
                account.withdraw(withdrawAmount);
                TransactionLog.append(logFile, accountNumber + ",WITHDRAW," + withdrawAmount);
                System.out.println("  [Batch] Withdrew Rs. " + withdrawAmount + " from " + accountNumber + " | Balance: Rs. " + account.getBalance());
            } catch (BankException e) {
                System.out.println("  [Batch] Withdrawal failed: " + e.getMessage());
            }
        });
        processor.stop();
    }
}
