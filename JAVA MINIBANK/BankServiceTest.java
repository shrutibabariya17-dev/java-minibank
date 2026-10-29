import org.junit.Assert;
import org.junit.Test;
import java.nio.file.Paths;
import java.util.ArrayList;

public class BankServiceTest {

    private BankService makeService() {
        Repository<Account> repo = new Repository<>();
        return new BankService(repo, Paths.get("data", "logs", "test.log"));
    }

    @Test
    public void testDepositIncreasesBalance() throws BankException {
        BankService service = makeService();
        Account acc = service.openAccount("SAVINGS", "TestUser", 5000, 500);
        long before = acc.getBalance();
        service.deposit(acc.getAccountNumber(), 2000);
        Assert.assertEquals(before + 2000, acc.getBalance());
    }

    @Test
    public void testWithdrawBeyondLimitThrows() throws BankException {
        BankService service = makeService();
        Account acc = service.openAccount("SAVINGS", "TestUser2", 3000, 1000);
        try {
            service.withdraw(acc.getAccountNumber(), 5000);
            Assert.fail("Expected InsufficientFundsException");
        } catch (InsufficientFundsException e) {
            Assert.assertTrue(e.getShortfall() > 0);
        }
    }

    @Test
    public void testConcurrentDeposits() throws BankException, InterruptedException {
        Repository<Account> repo = new Repository<>();
        BankService service = new BankService(repo, Paths.get("data", "logs", "test-concurrent.log"));
        Account acc = service.openAccount("SAVINGS", "ConcurrentUser", 0, 0);
        String accNo = acc.getAccountNumber();

        int threads = 10;
        int depositsPerThread = 100;
        long amountEach = 10;
        long expected = threads * depositsPerThread * amountEach;

        Thread[] pool = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            pool[i] = new Thread(() -> {
                for (int j = 0; j < depositsPerThread; j++) {
                    try {
                        acc.deposit(amountEach);
                    } catch (InvalidAmountException e) {
                        Assert.fail("Unexpected exception: " + e.getMessage());
                    }
                }
            });
            pool[i].start();
        }
        for (Thread t : pool) {
            t.join();
        }
        Assert.assertEquals(expected, acc.getBalance());
    }

    @Test
    public void testPersistenceRoundTrip() throws BankException {
        Repository<Account> repo = new Repository<>();
        BankService service = new BankService(repo, Paths.get("data", "logs", "test-persist.log"));
        Account original = service.openAccount("CURRENT", "PersistUser", 9000, 2000);
        String accNo = original.getAccountNumber();
        String ownerName = original.getOwnerName();
        long balance = original.getBalance();

        java.nio.file.Path stateFile = Paths.get("data", "test-state.dat");
        ArrayList<Account> all = repo.findAll();
        StatePersister.save(all.toArray(new Account[0]), stateFile);

        Account[] loaded = StatePersister.load(stateFile);
        Assert.assertEquals(1, loaded.length);
        Assert.assertEquals(accNo, loaded[0].getAccountNumber());
        Assert.assertEquals(ownerName, loaded[0].getOwnerName());
        Assert.assertEquals(balance, loaded[0].getBalance());
    }
}
