import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionProcessor {

    private final ExecutorService executor;
    private final ConcurrentHashMap<String, Account> sharedAccounts;

    public TransactionProcessor() {
        this.executor = Executors.newFixedThreadPool(4);
        this.sharedAccounts = new ConcurrentHashMap<>();
    }

    public void submit(Runnable task) {
        if (task != null) {
            executor.execute(task);
        }
    }

    public void registerAccount(Account account) {
        if (account != null) {
            sharedAccounts.put(account.getAccountNumber(), account);
        }
    }

    public Account getSharedAccount(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        return sharedAccounts.get(accountNumber);
    }

    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
