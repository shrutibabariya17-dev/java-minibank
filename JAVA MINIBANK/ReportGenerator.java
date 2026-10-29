import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {

    public static String generate(Path logsDir, Path reportFile) {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

        report.append("================================================\n");
        report.append("          MINIBANK END-OF-DAY REPORT            \n");
        report.append("================================================\n");
        report.append("Generated : ").append(fmt.format(Instant.now())).append("\n");
        report.append("Logs Dir  : ").append(logsDir.toAbsolutePath()).append("\n");
        report.append("------------------------------------------------\n\n");

        if (!Files.isDirectory(logsDir)) {
            String msg = "Logs directory not found: " + logsDir;
            report.append(msg).append("\n");
            System.out.println(report);
            return report.toString();
        }

        long totalDeposits = 0;
        long totalWithdrawals = 0;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    continue;
                }
                String name = entry.getFileName().toString();
                if (!name.endsWith(".log")) {
                    continue;
                }

                BasicFileAttributes attrs;
                try {
                    attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                } catch (IOException e) {
                    report.append("  [SKIP] Cannot read attributes for ").append(name).append(": ").append(e.getMessage()).append("\n");
                    continue;
                }

                if (attrs.size() == 0) {
                    continue;
                }

                report.append("File      : ").append(name).append("\n");
                report.append("Size      : ").append(attrs.size()).append(" bytes\n");
                report.append("Modified  : ").append(fmt.format(attrs.lastModifiedTime().toInstant())).append("\n");

                long fileDeposits = 0;
                long fileWithdrawals = 0;

                try (BufferedReader reader = Files.newBufferedReader(entry, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) {
                            continue;
                        }
                        String[] parts = line.split(",");
                        if (parts.length < 3) {
                            continue;
                        }
                        String type = parts[1].trim();
                        String amountStr = parts[2].trim();
                        long amount;
                        try {
                            amount = Long.parseLong(amountStr);
                        } catch (NumberFormatException e) {
                            continue;
                        }
                        if (type.equalsIgnoreCase("DEPOSIT")) {
                            fileDeposits += amount;
                        } else if (type.equalsIgnoreCase("WITHDRAW")) {
                            fileWithdrawals += amount;
                        }
                    }
                } catch (IOException e) {
                    report.append("  [ERROR] Cannot read ").append(name).append(": ").append(e.getMessage()).append("\n");
                    continue;
                }

                report.append("Deposits  : Rs. ").append(fileDeposits).append("\n");
                report.append("Withdrawals: Rs. ").append(fileWithdrawals).append("\n");
                report.append("--------------------------------\n");

                totalDeposits += fileDeposits;
                totalWithdrawals += fileWithdrawals;
            }
        } catch (IOException e) {
            report.append("Error walking logs directory: ").append(e.getMessage()).append("\n");
        }

        report.append("\n================================================\n");
        report.append("TOTAL DEPOSITS    : Rs. ").append(totalDeposits).append("\n");
        report.append("TOTAL WITHDRAWALS : Rs. ").append(totalWithdrawals).append("\n");
        report.append("NET               : Rs. ").append(totalDeposits - totalWithdrawals).append("\n");
        report.append("================================================\n");

        String content = report.toString();

        try {
            if (reportFile.getParent() != null) {
                Files.createDirectories(reportFile.getParent());
            }
            Files.write(reportFile, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("ReportGenerator: could not write report file: " + e.getMessage());
        }

        return content;
    }
}
