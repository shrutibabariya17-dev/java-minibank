public class StatementFormatter {

    public static String buildStatement(Account account) {
        if (account == null) {
            return "No account provided.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------------\n");
        sb.append("       ACCOUNT STATEMENT        \n");
        sb.append("--------------------------------\n");
        sb.append("Account Number : ").append(account.getAccountNumber()).append("\n");
        sb.append("Owner Name     : ").append(account.getOwnerName()).append("\n");
        sb.append("Balance        : ").append(account.getBalance()).append("\n");
        sb.append("Active Status  : ").append(account.isActive() ? "Active" : "Inactive").append("\n");
        sb.append("--------------------------------");

        return sb.toString();
    }
}
