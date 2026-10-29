public class CommandParser {

    public static Command parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            System.out.println("Error: Command line cannot be empty");
            return null;
        }

        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) {
            System.out.println("Error: Invalid command format. Expected: <TYPE> <ACCOUNT_NO> <AMOUNT>");
            return null;
        }

        TransactionType type;
        try {
            type = TransactionType.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid transaction type '" + parts[0] + "'. Allowed: DEPOSIT, WITHDRAW, TRANSFER");
            return null;
        }

        String accountNumber = parts[1];

        long amount;
        try {
            amount = Long.parseLong(parts[2]);
            if (amount <= 0) {
                System.out.println("Error: Amount must be a positive number: " + parts[2]);
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format for amount: " + parts[2]);
            return null;
        }

        return new Command(type, accountNumber, amount);
    }
}
