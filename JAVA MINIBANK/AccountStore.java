import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public class AccountStore {

    private final HashMap<String, Account> store;
    private final ArrayList<String> history;

    public AccountStore() {
        this.store = new HashMap<>();
        this.history = new ArrayList<>();
    }

    public void add(Account account) {
        if (account == null) {
            System.out.println("  [AccountStore] Cannot add null account.");
            return;
        }
        String key = account.getAccountNumber();
        if (store.containsKey(key)) {
            System.out.println("  [AccountStore] Account " + key + " already exists. Skipping.");
            return;
        }
        store.put(key, account);
    }

    public Account get(String accountNumber) {
        if (accountNumber == null) {
            return null;
        }
        return store.get(accountNumber);
    }

    public Collection<Account> getAll() {
        return store.values();
    }

    public void recordTransaction(String entry) {
        history.add(entry);
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = new ArrayList<>(store.values());
        Comparator<Account> byBalance = new Comparator<Account>() {
            @Override
            public int compare(Account a, Account b) {
                int cmp = Long.compare(a.getBalance(), b.getBalance());
                if (cmp != 0) {
                    return cmp;
                }
                return a.getAccountNumber().compareTo(b.getAccountNumber());
            }
        };
        Collections.sort(list, byBalance);
        return list;
    }

    public ArrayList<Account> aboveBalance(long threshold) {
        ArrayList<Account> result = new ArrayList<>();
        for (Account acc : store.values()) {
            if (acc.getBalance() > threshold) {
                result.add(acc);
            }
        }
        return result;
    }

    public TreeMap<String, Account> toTreeMap() {
        return new TreeMap<>(store);
    }
}
