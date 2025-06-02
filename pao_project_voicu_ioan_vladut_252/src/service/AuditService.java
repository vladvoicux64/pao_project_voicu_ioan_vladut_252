package service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {
    private static final String FILE_PATH = "audit.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Initialize CSV file with headers if it doesn't exist
    static {
        try {
            java.io.File file = new java.io.File(FILE_PATH);
            if (!file.exists()) {
                try (FileWriter writer = new FileWriter(FILE_PATH)) {
                    writer.append("Action,Timestamp,Thread\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String actionName) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.append(actionName)
                    .append(",")
                    .append(LocalDateTime.now().format(formatter))
                    .append(",")
                    .append(Thread.currentThread().getName())
                    .append("\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logUserAction(String username, String action) {
        log("USER_" + action + "_" + username);
    }

    public void logSystemAction(String action) {
        log("SYSTEM_" + action);
    }
}
