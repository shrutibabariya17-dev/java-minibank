import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class StatePersister {

    public static void save(Account[] accounts, Path file) {
        try {
            if (file.getParent() != null) {
                Files.createDirectories(file.getParent());
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
                oos.writeObject(accounts);
            }
        } catch (IOException e) {
            System.err.println("StatePersister.save failed: " + e.getMessage());
        }
    }

    public static Account[] load(Path file) {
        if (!Files.exists(file)) {
            System.err.println("StatePersister.load: file not found: " + file);
            return new Account[0];
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            return (Account[]) ois.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("StatePersister.load: class not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("StatePersister.load: I/O error: " + e.getMessage());
        }
        return new Account[0];
    }
}
