import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.TreeMap;

public class MiniBank {

    record bankinfo(String name, String branch) {
    }

    enum menuoption {
        OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
    }

    static class BankingSession implements AutoCloseable {
        private String sessionName;

        public BankingSession(String sessionName) {
            this.sessionName = sessionName;
            System.out.println("  [Session] Started: " + sessionName);
        }

        public void performAudit() {
            System.out.println("  [Session] Performing secure transaction audit...");
        }

        @Override
        public void close() {
            System.out.println("  [Session] Closed automatically: " + sessionName + " (Resources cleared)");
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("================================================================");
        System.out.println("        MINIBANK — OBJECT ORIENTED PROGRAMMING PRACTICALS       ");
        System.out.println("================================================================");

        System.out.println("\n>>> PRACTICAL 3: Validation, Command Parsing & Statement <<<");

        System.out.println("\n[1] Validator Tests:");
        System.out.println("  Mobile Valid (9876543210)  : " + Validator.isValidMobile("9876543210"));
        System.out.println("  Mobile Invalid (12345)     : " + Validator.isValidMobile("12345"));
        System.out.println("  Email Valid (shruti@aiml.com): " + Validator.isValidEmail("shruti@aiml.com"));
        System.out.println("  Email Invalid (shruti@)     : " + Validator.isValidEmail("shruti@"));
        System.out.println("  PAN Valid (ABCDE1234F)     : " + Validator.isValidPan("ABCDE1234F"));
        System.out.println("  PAN Invalid (12345XYZ)     : " + Validator.isValidPan("12345XYZ"));
        System.out.println("  IFSC Valid (SBIN0123456)   : " + Validator.isValidIfsc("SBIN0123456"));
        System.out.println("  IFSC Invalid (SBIN123)     : " + Validator.isValidIfsc("SBIN123"));
        System.out.println("  Amount Valid (500)         : " + Validator.isValidAmount("500"));
        System.out.println("  Amount Invalid (-50)       : " + Validator.isValidAmount("-50"));

        System.out.println("\n[2] Command Parsing Test:");
        String commandInput = "DEPOSIT AC0001 500";
        System.out.println("  Input Line: \"" + commandInput + "\"");
        Command parsedCmd = CommandParser.parse(commandInput);
        if (parsedCmd != null) {
            System.out.println("  Command Type   : " + parsedCmd.type());
            System.out.println("  Account Number : " + parsedCmd.accountNumber());
            System.out.println("  Amount         : " + parsedCmd.amount());
        }

        System.out.println("\n[3] Command Parsing Error Handling Tests:");
        System.out.print("  Wrong parts  : ");
        CommandParser.parse("DEPOSIT 500");
        System.out.print("  Invalid type : ");
        CommandParser.parse("BUY AC0001 500");
        System.out.print("  Invalid amt  : ");
        CommandParser.parse("WITHDRAW AC0001 -100");

        System.out.println("\n>>> PRACTICAL 4: Abstract Classes, Inheritance & Polymorphism <<<");

        Account[] accounts = new Account[3];
        accounts[0] = new SavingsAccount("shruti", 5000, 1000);
        accounts[1] = new CurrentAccount("Yashashvi", 10000, 5000);
        accounts[2] = new FixedDepositAccount("Bhavyata", 50000);

        System.out.println("\n[1] Polymorphic Account Array & Interest Calculation:");
        for (Account acc : accounts) {
            System.out.println("  " + acc);
            System.out.println("    Type             : " + acc.getClass().getSimpleName());
            System.out.println("    Interest Rate    : " + acc.interestRate() + "%");
            System.out.println("    Monthly Interest : Rs. " + String.format("%.2f", acc.monthlyInterest()));
        }

        System.out.println("\n[2] Pattern Matching with instanceof:");
        for (Account acc : accounts) {
            if (acc instanceof SavingsAccount sa) {
                System.out.println("  SavingsAccount detected for " + sa.getOwnerName()
                        + " | Min Balance: Rs. " + sa.getMinBalance());
            } else if (acc instanceof CurrentAccount ca) {
                System.out.println("  CurrentAccount detected for " + ca.getOwnerName()
                        + " | Overdraft Limit: Rs. " + ca.getOverdraftLimit());
            } else if (acc instanceof FixedDepositAccount fda) {
                System.out.println("  FixedDepositAccount detected for " + fda.getOwnerName()
                        + " | Locked Deposit");
            }
        }

        System.out.println("\n[3] StatementFormatter buildStatement():");
        System.out.println(StatementFormatter.buildStatement(accounts[0]));

        System.out.println("\n>>> PRACTICAL 5: Interfaces, Functional Interface & Marker Interface <<<");

        System.out.println("\n[1] InterestBearing Default Method (yearlyInterest):");
        for (Account acc : accounts) {
            if (acc instanceof InterestBearing ib) {
                System.out.println("  " + acc.getOwnerName() + " (" + acc.getClass().getSimpleName() + "): "
                        + "Yearly Interest = Rs. " + String.format("%.2f", ib.yearlyInterest()));
            }
        }

        System.out.println("\n[2] Functional Interface WithdrawRule (Anonymous Class vs Lambda):");
        WithdrawRule maxLimitRule = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return amount <= 5000;
            }
        };

        WithdrawRule activeAndPositiveRule = (account, amount) -> account.isActive() && amount > 0;

        System.out.println("  Anonymous Class (max limit <= 5000) for Rs. 4000: "
                + maxLimitRule.allow(accounts[0], 4000));
        System.out.println("  Anonymous Class (max limit <= 5000) for Rs. 8000: "
                + maxLimitRule.allow(accounts[0], 8000));
        System.out.println("  Lambda Expression (active & positive) for Rs. 1000: "
                + activeAndPositiveRule.allow(accounts[0], 1000));

        System.out.println("\n[3] Marker Interface Premium Check:");
        for (Account acc : accounts) {
            if (acc instanceof Premium) {
                System.out.println("  [PREMIUM ACCOUNT] " + acc.getAccountNumber()
                        + " (" + acc.getOwnerName() + " - " + acc.getClass().getSimpleName() + ")");
            } else {
                System.out.println("  [STANDARD ACCOUNT] " + acc.getAccountNumber()
                        + " (" + acc.getOwnerName() + " - " + acc.getClass().getSimpleName() + ")");
            }
        }

        System.out.println("\n>>> PRACTICAL 6: Custom Annotations & Reflection <<<");

        Account invalidAccount = new SavingsAccount("shrutiVeryLongOwnerName", -1500, 1000);
        System.out.println("\nValidating object with custom annotations via reflection:");
        System.out.println("Object: " + invalidAccount);

        String[] validationErrors = AnnotationValidator.validate(invalidAccount);
        if (validationErrors.length == 0) {
            System.out.println("  No validation errors found.");
        } else {
            System.out.println("  Validation Errors Found (" + validationErrors.length + "):");
            for (String err : validationErrors) {
                System.out.println("    - " + err);
            }
        }

        Account validAccount = new SavingsAccount("shruti", 5000, 1000);
        String[] validErrors = AnnotationValidator.validate(validAccount);
        System.out.println("Validating correctly configured account (" + validAccount.getOwnerName() + "):");
        System.out.println("  Errors count: " + validErrors.length + " (Passed all checks)");

        System.out.println("\n>>> PRACTICAL 7: Custom Exceptions & Fault Tolerance <<<");

        Account testSavings = new SavingsAccount("shruti", 5000, 1000);
        Account testCurrent = new CurrentAccount("Yashashvi", 2000, 3000);

        System.out.println("\n[1] Testing Successful Deposit:");
        try {
            testSavings.deposit(1500);
            System.out.println("  Deposit Rs. 1500 successful! New Balance: Rs. " + testSavings.getBalance());
        } catch (InvalidAmountException e) {
            System.out.println("  Deposit failed: " + e.getMessage());
        }

        System.out.println("\n[2] Testing Invalid Deposit Amount (<= 0):");
        try {
            testSavings.deposit(-200);
        } catch (InvalidAmountException e) {
            System.out.println("  Caught expected exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        }

        System.out.println("\n[3] Testing Successful Withdrawal:");
        try {
            testSavings.withdraw(2000);
            System.out.println("  Withdraw Rs. 2000 successful! New Balance: Rs. " + testSavings.getBalance());
        } catch (BankException e) {
            System.out.println("  Withdrawal failed: " + e.getMessage());
        }

        System.out.println("\n[4] Testing Insufficient Funds Withdrawal (Violating Min Balance):");
        try {
            testSavings.withdraw(4000);
        } catch (InsufficientFundsException e) {
            System.out.println("  Caught expected exception: " + e.getClass().getSimpleName());
            System.out.println("  Message   : " + e.getMessage());
            System.out.println("  Shortfall : Rs. " + e.getShortfall());
            System.out.println("  Balance preserved: Rs. " + testSavings.getBalance());
        } catch (InvalidAmountException e) {
            System.out.println("  Invalid amount: " + e.getMessage());
        }

        System.out.println("\n[5] Testing Invalid Withdrawal Amount (<= 0):");
        try {
            testSavings.withdraw(0);
        } catch (InvalidAmountException e) {
            System.out.println("  Caught expected exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println("  Insufficient funds: " + e.getMessage());
        }

        System.out.println("\n[6] Testing Successful Fund Transfer:");
        try {
            System.out.println("  Before Transfer:");
            System.out.println("    Source (" + testSavings.getOwnerName() + ") Balance: Rs. " + testSavings.getBalance());
            System.out.println("    Dest   (" + testCurrent.getOwnerName() + ") Balance: Rs. " + testCurrent.getBalance());

            testSavings.transfer(testCurrent, 1000);

            System.out.println("  Transfer Rs. 1000 Completed Successfully!");
            System.out.println("  After Transfer:");
            System.out.println("    Source (" + testSavings.getOwnerName() + ") Balance: Rs. " + testSavings.getBalance());
            System.out.println("    Dest   (" + testCurrent.getOwnerName() + ") Balance: Rs. " + testCurrent.getBalance());
        } catch (BankException e) {
            System.out.println("  Transfer error: " + e.getMessage());
        }

        System.out.println("\n[7] Testing Failed Transfer (Insufficient Funds):");
        try {
            testSavings.transfer(testCurrent, 5000);
        } catch (BankException e) {
            System.out.println("  Caught expected exception: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
            System.out.println("  Source balance safely intact: Rs. " + testSavings.getBalance());
            System.out.println("  Destination balance safely intact: Rs. " + testCurrent.getBalance());
        } finally {
            System.out.println("  [finally block] Transfer attempt transaction audit recorded.");
        }

        System.out.println("\n[8] Try-With-Resources Demonstration with AutoCloseable:");
        try (BankingSession session = new BankingSession("Audit-Session-2026")) {
            session.performAudit();
        } catch (Exception e) {
            System.out.println("  Session error: " + e.getMessage());
        }

        System.out.println("\n>>> PRACTICAL 8: Multithreading & Synchronization <<<");

        System.out.println("\n[1] Unsafe Deposit Demonstration (Demonstrating Race Condition):");
        Account raceAccount = new SavingsAccount("RaceDemo", 0, 0);
        int raceThreadsCount = 10;
        int raceDepositsPerThread = 50;
        long raceAmountPerDeposit = 10;
        long raceExpectedBalance = raceThreadsCount * raceDepositsPerThread * raceAmountPerDeposit;

        Thread[] raceThreads = new Thread[raceThreadsCount];
        for (int i = 0; i < raceThreadsCount; i++) {
            raceThreads[i] = new Thread(
                    new AccountWorker(raceAccount, raceDepositsPerThread, raceAmountPerDeposit, true),
                    "UnsafeWorker-" + (i + 1));
            raceThreads[i].start();
        }

        for (Thread t : raceThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }

        System.out.println("  Unsafe Deposit Test Results:");
        System.out.println("    Expected Balance : Rs. " + raceExpectedBalance);
        System.out.println("    Actual Balance   : Rs. " + raceAccount.getBalance());
        if (raceAccount.getBalance() != raceExpectedBalance) {
            System.out.println("    Observation      : RACE CONDITION OCCURRED! Lost updates due to unsynchronized access.");
        } else {
            System.out.println("    Observation      : Race condition did not manifest in this run (timing dependent).");
        }

        System.out.println("\n[2] Thread-Safe Synchronized Deposit Test (Verification):");
        Account syncAccount = new SavingsAccount("SyncDemo", 0, 0);
        int syncThreadsCount = 10;
        int syncDepositsPerThread = 1000;
        long syncAmountPerDeposit = 10;
        long syncExpectedBalance = (long) syncThreadsCount * syncDepositsPerThread * syncAmountPerDeposit;

        Thread[] syncThreads = new Thread[syncThreadsCount];
        for (int i = 0; i < syncThreadsCount; i++) {
            syncThreads[i] = new Thread(
                    new AccountWorker(syncAccount, syncDepositsPerThread, syncAmountPerDeposit, false),
                    "SyncWorker-" + (i + 1));
            syncThreads[i].start();
        }

        for (Thread t : syncThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            }
        }

        System.out.println("\n  Synchronized Test Results:");
        System.out.println("    Initial Balance  : Rs. 0");
        System.out.println("    Threads Count    : " + syncThreadsCount);
        System.out.println("    Deposits/Thread  : " + syncDepositsPerThread);
        System.out.println("    Deposit Amount   : Rs. " + syncAmountPerDeposit);
        System.out.println("    Expected Balance : Rs. " + syncExpectedBalance);
        System.out.println("    Actual Balance   : Rs. " + syncAccount.getBalance());
        System.out.println("    Test Status      : "
                + (syncAccount.getBalance() == syncExpectedBalance ? "PASSED (Thread-Safe)" : "FAILED"));

        System.out.println("\n>>> PRACTICAL 9: Thread Pools, Producer-Consumer & Deadlock <<<");

        System.out.println("\n[1] Thread Pool Execution with TransactionProcessor:");
        Account poolAccount = new SavingsAccount("PoolAcc", 5000, 500);
        TransactionProcessor processor = new TransactionProcessor();

        processor.submit(() -> {
            try {
                poolAccount.deposit(1000);
                System.out.println("  [PoolTask] Deposited Rs. 1000. Balance: Rs. " + poolAccount.getBalance());
            } catch (InvalidAmountException e) {
                System.out.println("  [PoolTask] Deposit failed: " + e.getMessage());
            }
        });

        processor.submit(() -> {
            try {
                poolAccount.withdraw(1500);
                System.out.println("  [PoolTask] Withdrawn Rs. 1500. Balance: Rs. " + poolAccount.getBalance());
            } catch (BankException e) {
                System.out.println("  [PoolTask] Withdrawal failed: " + e.getMessage());
            }
        });

        processor.submit(() -> {
            try {
                poolAccount.deposit(2500);
                System.out.println("  [PoolTask] Deposited Rs. 2500. Balance: Rs. " + poolAccount.getBalance());
            } catch (InvalidAmountException e) {
                System.out.println("  [PoolTask] Deposit failed: " + e.getMessage());
            }
        });

        processor.submit(() -> {
            try {
                poolAccount.withdraw(500);
                System.out.println("  [PoolTask] Withdrawn Rs. 500. Balance: Rs. " + poolAccount.getBalance());
            } catch (BankException e) {
                System.out.println("  [PoolTask] Withdrawal failed: " + e.getMessage());
            }
        });

        processor.stop();
        System.out.println("  ExecutorService shutdown cleanly. Final Pool Account Balance: Rs. " + poolAccount.getBalance());

        System.out.println("\n[2] Producer-Consumer Pattern using wait() and notify():");
        TransactionBuffer buffer = new TransactionBuffer(3);
        Account bufferAccount = new SavingsAccount("BufferAcc", 2000, 100);

        Thread producerThread = new Thread(() -> {
            Command[] transactions = {
                new Command(TransactionType.DEPOSIT, "AC0001", 500),
                new Command(TransactionType.DEPOSIT, "AC0001", 300),
                new Command(TransactionType.WITHDRAW, "AC0001", 400),
                new Command(TransactionType.DEPOSIT, "AC0001", 1000),
                new Command(TransactionType.WITHDRAW, "AC0001", 200)
            };

            for (Command cmd : transactions) {
                try {
                    buffer.produce(cmd);
                    System.out.println("  [Producer] Produced: " + cmd.type() + " Rs. " + cmd.amount());
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            buffer.finish();
            System.out.println("  [Producer] Finished producing all transactions.");
        }, "Producer-Thread");

        Thread consumerThread = new Thread(() -> {
            while (true) {
                try {
                    Command cmd = buffer.consume();
                    if (cmd == null) {
                        break;
                    }

                    if (cmd.type() == TransactionType.DEPOSIT) {
                        bufferAccount.deposit(cmd.amount());
                    } else if (cmd.type() == TransactionType.WITHDRAW) {
                        bufferAccount.withdraw(cmd.amount());
                    }

                    System.out.println("  [Consumer] Processed: " + cmd.type() + " Rs. " + cmd.amount()
                            + " | New Balance: Rs. " + bufferAccount.getBalance());
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (BankException e) {
                    System.out.println("  [Consumer] Transaction error: " + e.getMessage());
                }
            }
            System.out.println("  [Consumer] Processed all transactions and terminated.");
        }, "Consumer-Thread");

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());
        }

        System.out.println("  Final Buffer Account Balance: Rs. " + bufferAccount.getBalance());

        System.out.println("\n[3] Deadlock Demonstration & Safe Ordered-Lock Transfer:");
        Account deadA = new SavingsAccount("AC9001", "DemoA", 1000, 0);
        Account deadB = new SavingsAccount("AC9002", "DemoB", 1000, 0);

        System.out.println("  Demonstrating Potential Deadlock Scenario (Locking in Opposite Order):");
        Thread deadlockThread1 = new Thread(() -> {
            synchronized (deadA) {
                System.out.println("    [Deadlock-T1] Acquired lock on Account A (" + deadA.getAccountNumber() + "). Waiting for B...");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
                synchronized (deadB) {
                    System.out.println("    [Deadlock-T1] Acquired lock on Account B.");
                }
            }
        }, "DeadlockThread-1");

        Thread deadlockThread2 = new Thread(() -> {
            synchronized (deadB) {
                System.out.println("    [Deadlock-T2] Acquired lock on Account B (" + deadB.getAccountNumber() + "). Waiting for A...");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
                synchronized (deadA) {
                    System.out.println("    [Deadlock-T2] Acquired lock on Account A.");
                }
            }
        }, "DeadlockThread-2");

        deadlockThread1.setDaemon(true);
        deadlockThread2.setDaemon(true);
        deadlockThread1.start();
        deadlockThread2.start();

        try {
            deadlockThread1.join(300);
            deadlockThread2.join(300);
        } catch (InterruptedException ignored) {}

        if (deadlockThread1.isAlive() && deadlockThread2.isAlive()) {
            System.out.println("    -> DEADLOCK DETECTED! Thread-1 holds A waiting for B; Thread-2 holds B waiting for A.");
            System.out.println("    -> Circular wait condition confirmed (Threads are daemon and safely contained).");
        }

        System.out.println("\n  Applying Consistent Lock-Ordering Solution (Safe Transfer):");
        Account accountA = new SavingsAccount("AC0001", "Alice", 5000, 0);
        Account accountB = new SavingsAccount("AC0002", "Bob", 5000, 0);

        System.out.println("  Initial Balances: Alice (AC0001) = Rs. " + accountA.getBalance()
                + " | Bob (AC0002) = Rs. " + accountB.getBalance());
        System.out.println("  Total System Money: Rs. " + (accountA.getBalance() + accountB.getBalance()));

        Thread safeThread1 = new Thread(() -> {
            try {
                accountA.safeTransfer(accountB, 1000);
                System.out.println("    [SafeTransfer-1] Transferred Rs. 1000 from Alice to Bob.");
            } catch (BankException e) {
                System.out.println("    [SafeTransfer-1] Error: " + e.getMessage());
            }
        }, "SafeTransfer-1");

        Thread safeThread2 = new Thread(() -> {
            try {
                accountB.safeTransfer(accountA, 1000);
                System.out.println("    [SafeTransfer-2] Transferred Rs. 1000 from Bob to Alice.");
            } catch (BankException e) {
                System.out.println("    [SafeTransfer-2] Error: " + e.getMessage());
            }
        }, "SafeTransfer-2");

        safeThread1.start();
        safeThread2.start();

        try {
            safeThread1.join();
            safeThread2.join();
        } catch (InterruptedException e) {
            System.out.println("Safe transfer interrupted: " + e.getMessage());
        }

        System.out.println("  Final Balances: Alice = Rs. " + accountA.getBalance()
                + " | Bob = Rs. " + accountB.getBalance());
        System.out.println("  Total System Money Preserved: Rs. " + (accountA.getBalance() + accountB.getBalance())
                + " (No deadlock occurred, locks acquired in consistent order)");

        System.out.println("\n>>> PRACTICAL 11: Persistence and Reporting <<<");

        System.out.println("\n[1] Creating accounts for serialization:");
        Account p11acc0 = new SavingsAccount("Riya", 8000, 1000);
        Account p11acc1 = new CurrentAccount("Arjun", 15000, 5000);
        Account p11acc2 = new FixedDepositAccount("Meera", 30000);
        Account[] p11accounts = { p11acc0, p11acc1, p11acc2 };
        for (Account acc : p11accounts) {
            System.out.println("  " + acc);
        }

        Path dataDir = Paths.get("data");
        Path stateFile = dataDir.resolve("accounts.dat");

        System.out.println("\n[2] Saving accounts to: " + stateFile);
        StatePersister.save(p11accounts, stateFile);
        System.out.println("  Accounts saved successfully.");

        System.out.println("\n[3] Reloading accounts from: " + stateFile);
        Account[] reloaded = StatePersister.load(stateFile);
        System.out.println("  Reloaded " + reloaded.length + " account(s):");
        for (Account acc : reloaded) {
            System.out.println("  " + acc.getAccountNumber() + " | " + acc.getOwnerName() + " | Rs. " + acc.getBalance());
        }

        System.out.println("\n[4] Appending transactions to log:");
        Path logsDir = Paths.get("data", "logs");
        Path logFile = logsDir.resolve("transactions.log");
        String[] logEntries = {
            reloaded[0].getAccountNumber() + ",DEPOSIT,5000",
            reloaded[0].getAccountNumber() + ",WITHDRAW,2000",
            reloaded[1].getAccountNumber() + ",DEPOSIT,10000",
            reloaded[1].getAccountNumber() + ",WITHDRAW,3000",
            reloaded[2].getAccountNumber() + ",DEPOSIT,20000"
        };
        for (String entry : logEntries) {
            TransactionLog.append(logFile, entry);
            System.out.println("  Logged: " + entry);
        }
        System.out.println("  Appending again to verify non-overwrite:");
        TransactionLog.append(logFile, reloaded[0].getAccountNumber() + ",DEPOSIT,1000");
        System.out.println("  Appended extra entry: " + reloaded[0].getAccountNumber() + ",DEPOSIT,1000");

        System.out.println("\n[5] Generating end-of-day report:");
        Path reportFile = Paths.get("data", "report", "eod-report.txt");
        String reportContent = ReportGenerator.generate(logsDir, reportFile);
        System.out.println(reportContent);
        System.out.println("  Report written to: " + reportFile.toAbsolutePath());

        System.out.println("\n>>> PRACTICAL 12: Collections Framework <<<");

        System.out.println("\n[1] Adding accounts to AccountStore (HashMap):");
        AccountStore accountStore = new AccountStore();
        Account storeAcc1 = new SavingsAccount("Priya", 12000, 1000);
        Account storeAcc2 = new CurrentAccount("Rahul", 7500, 3000);
        Account storeAcc3 = new FixedDepositAccount("Sneha", 25000);
        Account storeAcc4 = new SavingsAccount("Karan", 7500, 500);
        accountStore.add(storeAcc1);
        accountStore.add(storeAcc2);
        accountStore.add(storeAcc3);
        accountStore.add(storeAcc4);
        System.out.println("  Added 4 accounts.");

        System.out.println("\n[2] Duplicate account number check:");
        accountStore.add(storeAcc1);

        System.out.println("\n[3] Retrieve account by account number:");
        String lookupNo = storeAcc2.getAccountNumber();
        Account found = accountStore.get(lookupNo);
        if (found != null) {
            System.out.println("  Found: " + found);
        } else {
            System.out.println("  Account " + lookupNo + " not found.");
        }
        System.out.println("  Lookup missing key: " + accountStore.get("AC9999"));

        System.out.println("\n[4] All stored accounts (HashMap, unordered):");
        for (Account acc : accountStore.getAll()) {
            System.out.println("  " + acc);
        }

        System.out.println("\n[5] Sorted by balance (ascending), tie-break by account number:");
        ArrayList<Account> byBalance = accountStore.sortedByBalance();
        for (Account acc : byBalance) {
            System.out.println("  Balance Rs. " + acc.getBalance() + " | " + acc.getAccountNumber() + " | " + acc.getOwnerName());
        }

        System.out.println("\n[6] Comparable<Account> — sorting by account number:");
        ArrayList<Account> byNumber = new ArrayList<>(accountStore.getAll());
        Collections.sort(byNumber);
        for (Account acc : byNumber) {
            System.out.println("  " + acc.getAccountNumber() + " | " + acc.getOwnerName());
        }

        System.out.println("\n[7] Transaction history (ArrayList<String>):");
        accountStore.recordTransaction(storeAcc1.getAccountNumber() + " DEPOSIT 5000");
        accountStore.recordTransaction(storeAcc1.getAccountNumber() + " WITHDRAW 1000");
        accountStore.recordTransaction(storeAcc2.getAccountNumber() + " DEPOSIT 2000");
        accountStore.recordTransaction(storeAcc3.getAccountNumber() + " DEPOSIT 3000");
        for (String entry : accountStore.getHistory()) {
            System.out.println("  " + entry);
        }

        System.out.println("\n[8] ConcurrentHashMap in TransactionProcessor — shared accounts:");
        TransactionProcessor sharedProcessor = new TransactionProcessor();
        sharedProcessor.registerAccount(storeAcc1);
        sharedProcessor.registerAccount(storeAcc2);
        System.out.println("  Registered: " + storeAcc1.getAccountNumber() + " and " + storeAcc2.getAccountNumber());
        Account retrieved = sharedProcessor.getSharedAccount(storeAcc1.getAccountNumber());
        System.out.println("  Retrieved from ConcurrentHashMap: " + (retrieved != null ? retrieved.toString() : "null"));
        sharedProcessor.stop();

        System.out.println("\n[9] Supplementary — accounts above threshold (Rs. 10000):");
        ArrayList<Account> richAccounts = accountStore.aboveBalance(10000);
        for (Account acc : richAccounts) {
            System.out.println("  " + acc.getAccountNumber() + " | " + acc.getOwnerName() + " | Rs. " + acc.getBalance());
        }

        System.out.println("\n[10] Supplementary — TreeMap (sorted by account number):");
        TreeMap<String, Account> treeMap = accountStore.toTreeMap();
        for (String key : treeMap.keySet()) {
            System.out.println("  " + key + " -> " + treeMap.get(key).getOwnerName() + " | Rs. " + treeMap.get(key).getBalance());
        }

        System.out.println("\n>>> PRACTICAL 13: Generic Repository Using Generics and Reflection <<<");

        System.out.println("\n[1] Repository<Account> — save() with reflection:");
        Repository<Account> accountRepo = new Repository<>();
        Account repoAcc1 = new SavingsAccount("Anika", 18000, 1000);
        Account repoAcc2 = new CurrentAccount("Dev", 9000, 3000);
        Account repoAcc3 = new FixedDepositAccount("Mihir", 40000);
        accountRepo.save(repoAcc1);
        accountRepo.save(repoAcc2);
        System.out.println("  Saved: " + repoAcc1.getAccountNumber() + " | " + repoAcc1.getOwnerName());
        System.out.println("  Saved: " + repoAcc2.getAccountNumber() + " | " + repoAcc2.getOwnerName());

        System.out.println("\n[2] Repository<Account> — saveAll():");
        accountRepo.saveAll(repoAcc3);
        System.out.println("  SaveAll saved: " + repoAcc3.getAccountNumber() + " | " + repoAcc3.getOwnerName());

        System.out.println("\n[3] Repository<Account> — findById():");
        String lookupId = repoAcc1.getAccountNumber();
        Account fetched = accountRepo.findById(lookupId);
        System.out.println("  findById(" + lookupId + "): " + (fetched != null ? fetched.toString() : "null"));
        System.out.println("  findById(AC9999): " + accountRepo.findById("AC9999"));

        System.out.println("\n[4] Repository<Account> — findAll():");
        ArrayList<Account> allAccounts = accountRepo.findAll();
        System.out.println("  Total accounts in repo: " + allAccounts.size());
        for (Account acc : allAccounts) {
            System.out.println("  " + acc.getAccountNumber() + " | " + acc.getOwnerName() + " | Rs. " + acc.getBalance());
        }

        System.out.println("\n[5] Repository<Account> — delete():");
        boolean deleted = accountRepo.delete(repoAcc2.getAccountNumber());
        System.out.println("  delete(" + repoAcc2.getAccountNumber() + ") returned: " + deleted);
        boolean deletedAgain = accountRepo.delete(repoAcc2.getAccountNumber());
        System.out.println("  delete(" + repoAcc2.getAccountNumber() + ") again returned: " + deletedAgain);
        System.out.println("  Remaining in repo: " + accountRepo.findAll().size());

        System.out.println("\n[6] Repository<Customer> — same Repository class, different type:");
        Repository<Customer> customerRepo = new Repository<>();
        Customer cust1 = new Customer("Nisha", "nisha@bank.com", "9876543210");
        Customer cust2 = new Customer("Raj", "raj@bank.com", "9123456780");
        customerRepo.save(cust1);
        customerRepo.save(cust2);
        System.out.println("  Saved: " + cust1.getCustomerId() + " | " + cust1.getName());
        System.out.println("  Saved: " + cust2.getCustomerId() + " | " + cust2.getName());
        Customer foundCust = customerRepo.findById(cust1.getCustomerId());
        System.out.println("  findById(" + cust1.getCustomerId() + "): " + (foundCust != null ? foundCust.toString() : "null"));
        System.out.println("  Total customers in repo: " + customerRepo.findAll().size());

        System.out.println("\n[7] Bounded generic average() <N extends Number>:");
        Long[] balances = { 18000L, 9000L, 40000L };
        double avg = Repository.average(balances);
        System.out.println("  Balances: 18000, 9000, 40000");
        System.out.printf("  Average : %.2f%n", avg);
        Integer[] scores = { 85, 90, 78, 92, 88 };
        System.out.println("  Scores : 85, 90, 78, 92, 88");
        System.out.printf("  Average : %.2f%n", Repository.average(scores));

        System.out.println("\n[8] Missing @Id field handled gracefully:");
        try {
            Repository<String> badRepo = new Repository<>();
            badRepo.save("no-id-field");
        } catch (IllegalArgumentException e) {
            System.out.println("  Caught expected error: " + e.getMessage());
        }

        System.out.println("\n>>> PRACTICAL 14: Final MiniBank Capstone <<<");

        Path p14StateFile = Paths.get("data", "accounts.dat");
        Path p14LogFile = Paths.get("data", "logs", "transactions.log");
        Path p14ReportFile = Paths.get("data", "report", "eod-report.txt");

        Repository<Account> capstoneRepo = new Repository<>();

        System.out.println("\n[Startup] Loading saved accounts from: " + p14StateFile);
        Account[] savedAccounts = StatePersister.load(p14StateFile);
        if (savedAccounts.length == 0) {
            System.out.println("  No saved accounts found. Starting fresh.");
        } else {
            for (Account a : savedAccounts) {
                capstoneRepo.save(a);
            }
            System.out.println("  Loaded " + savedAccounts.length + " account(s) from state file.");
        }

        BankService bankService = new BankService(capstoneRepo, p14LogFile);

        bankinfo bank = new bankinfo("MiniBank", "Sarthana Branch");
        boolean running = true;

        while (running) {
            System.out.println("\n================================================================");
            System.out.println("  " + bank.name() + " - " + bank.branch());
            System.out.println("================================================================");
            System.out.println("  1. Open Account");
            System.out.println("  2. Deposit");
            System.out.println("  3. Withdraw");
            System.out.println("  4. Transfer");
            System.out.println("  5. View Account Statement");
            System.out.println("  6. List All Accounts");
            System.out.println("  7. Generate End-of-Day Report");
            System.out.println("  8. Batch Process Transactions");
            System.out.println("  9. Save Accounts");
            System.out.println(" 10. Search by Owner Name");
            System.out.println("  0. Exit");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                sc.next();
                System.out.println("  Invalid input. Please enter a number.");
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.println("\n  Account Types: SAVINGS | CURRENT | FIXED");
                    System.out.print("  Type: ");
                    String type = sc.nextLine().trim();
                    System.out.print("  Owner Name: ");
                    String owner = sc.nextLine().trim();
                    System.out.print("  Opening Balance (Rs.): ");
                    long bal = 0;
                    if (sc.hasNextLong()) { bal = sc.nextLong(); sc.nextLine(); }
                    long extra = 0;
                    if (type.equalsIgnoreCase("SAVINGS")) {
                        System.out.print("  Minimum Balance (Rs.): ");
                        if (sc.hasNextLong()) { extra = sc.nextLong(); sc.nextLine(); }
                    } else if (type.equalsIgnoreCase("CURRENT")) {
                        System.out.print("  Overdraft Limit (Rs.): ");
                        if (sc.hasNextLong()) { extra = sc.nextLong(); sc.nextLine(); }
                    }
                    try {
                        Account newAcc = bankService.openAccount(type, owner, bal, extra);
                        System.out.println("  Account opened: " + newAcc.getAccountNumber() + " | " + newAcc.getOwnerName());
                    } catch (BankException e) {
                        System.out.println("  Error: " + e.getMessage());
                    }
                }
                case 2 -> {
                    System.out.print("  Account Number: ");
                    String accNo = sc.nextLine().trim();
                    System.out.print("  Amount (Rs.): ");
                    long amt = 0;
                    if (sc.hasNextLong()) { amt = sc.nextLong(); sc.nextLine(); }
                    try {
                        bankService.deposit(accNo, amt);
                        System.out.println("  Deposit successful.");
                    } catch (BankException e) {
                        System.out.println("  Error: " + e.getMessage());
                    }
                }
                case 3 -> {
                    System.out.print("  Account Number: ");
                    String accNo = sc.nextLine().trim();
                    System.out.print("  Amount (Rs.): ");
                    long amt = 0;
                    if (sc.hasNextLong()) { amt = sc.nextLong(); sc.nextLine(); }
                    try {
                        bankService.withdraw(accNo, amt);
                        System.out.println("  Withdrawal successful.");
                    } catch (BankException e) {
                        System.out.println("  Error: " + e.getMessage());
                    }
                }
                case 4 -> {
                    System.out.print("  From Account Number: ");
                    String from = sc.nextLine().trim();
                    System.out.print("  To Account Number: ");
                    String to = sc.nextLine().trim();
                    System.out.print("  Amount (Rs.): ");
                    long amt = 0;
                    if (sc.hasNextLong()) { amt = sc.nextLong(); sc.nextLine(); }
                    try {
                        bankService.transfer(from, to, amt);
                        System.out.println("  Transfer successful.");
                    } catch (BankException e) {
                        System.out.println("  Error: " + e.getMessage());
                    }
                }
                case 5 -> {
                    System.out.print("  Account Number: ");
                    String accNo = sc.nextLine().trim();
                    try {
                        System.out.println(bankService.statement(accNo));
                    } catch (BankException e) {
                        System.out.println("  Error: " + e.getMessage());
                    }
                }
                case 6 -> {
                    ArrayList<Account> all = bankService.listAccounts();
                    if (all.isEmpty()) {
                        System.out.println("  No accounts found.");
                    } else {
                        System.out.println("  All Accounts (" + all.size() + "):");
                        for (Account a : all) {
                            System.out.println("  " + a.getAccountNumber() + " | " + a.getOwnerName() + " | Rs. " + a.getBalance() + " | " + a.getClass().getSimpleName());
                        }
                    }
                }
                case 7 -> {
                    System.out.println("  Generating end-of-day report...");
                    String report = ReportGenerator.generate(Paths.get("data", "logs"), p14ReportFile);
                    System.out.println(report);
                    System.out.println("  Report saved to: " + p14ReportFile.toAbsolutePath());
                }
                case 8 -> {
                    ArrayList<Account> batchList = bankService.listAccounts();
                    if (batchList.isEmpty()) {
                        System.out.println("  No accounts available for batch processing.");
                    } else {
                        Account batchAcc = batchList.get(0);
                        System.out.println("  Running batch on: " + batchAcc.getAccountNumber() + " | " + batchAcc.getOwnerName());
                        System.out.println("  Current balance: Rs. " + batchAcc.getBalance());
                        TransactionProcessor batchProc = new TransactionProcessor();
                        bankService.batchProcess(batchProc, batchAcc.getAccountNumber(), 5000, 2000);
                        System.out.println("  Batch complete. Final balance: Rs. " + batchAcc.getBalance());
                    }
                }
                case 9 -> {
                    ArrayList<Account> toSave = bankService.listAccounts();
                    StatePersister.save(toSave.toArray(new Account[0]), p14StateFile);
                    System.out.println("  Saved " + toSave.size() + " account(s) to: " + p14StateFile.toAbsolutePath());
                }
                case 10 -> {
                    System.out.print("  Search owner name: ");
                    String name = sc.nextLine().trim();
                    ArrayList<Account> results = bankService.searchByName(name);
                    if (results.isEmpty()) {
                        System.out.println("  No accounts found for: " + name);
                    } else {
                        System.out.println("  Found " + results.size() + " account(s):");
                        for (Account a : results) {
                            System.out.println("  " + a.getAccountNumber() + " | " + a.getOwnerName() + " | Rs. " + a.getBalance());
                        }
                    }
                }
                case 0 -> {
                    ArrayList<Account> finalAccounts = bankService.listAccounts();
                    StatePersister.save(finalAccounts.toArray(new Account[0]), p14StateFile);
                    System.out.println("  Accounts saved. Goodbye!");
                    running = false;
                }
                default -> System.out.println("  Invalid choice. Please select 0-10.");
            }
        }

        System.out.println("\nThank you for using MiniBank!");
        sc.close();
    }
}
