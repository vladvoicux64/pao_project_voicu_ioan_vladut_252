// service/DatabaseCleanupService.java
package service;

import repository.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseCleanupService {
    private AuditService auditService = new AuditService();

    public void clearAllData() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Disable foreign key checks temporarily
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            // Clear all tables in reverse order of dependencies
            clearTable(stmt, "reactions");
            clearTable(stmt, "post_tags");
            clearTable(stmt, "tags");
            clearTable(stmt, "notifications");
            clearTable(stmt, "comments");
            clearTable(stmt, "posts");
            clearTable(stmt, "topics");
            clearTable(stmt, "categories");
            clearTable(stmt, "moderator_categories");
            clearTable(stmt, "users");

            // Re-enable foreign key checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            // Reset auto-increment counters
            resetAutoIncrement(stmt, "users");
            resetAutoIncrement(stmt, "categories");
            resetAutoIncrement(stmt, "topics");
            resetAutoIncrement(stmt, "posts");
            resetAutoIncrement(stmt, "comments");
            resetAutoIncrement(stmt, "notifications");
            resetAutoIncrement(stmt, "tags");
            resetAutoIncrement(stmt, "reactions");

            auditService.logSystemAction("DATABASE_CLEARED");
            System.out.println("✓ Database cleared successfully!");

        } catch (SQLException e) {
            auditService.log("DATABASE_CLEAR_FAILED");
            System.err.println("✗ Error clearing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearTable(Statement stmt, String tableName) throws SQLException {
        try {
            stmt.execute("DELETE FROM " + tableName);
            System.out.println("✓ Cleared table: " + tableName);
        } catch (SQLException e) {
            // Table might not exist, log but continue
            System.out.println("⚠ Could not clear table " + tableName + ": " + e.getMessage());
        }
    }

    private void resetAutoIncrement(Statement stmt, String tableName) throws SQLException {
        try {
            stmt.execute("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1");
            System.out.println("✓ Reset auto-increment for: " + tableName);
        } catch (SQLException e) {
            // Table might not exist or not have auto-increment, continue
            System.out.println("⚠ Could not reset auto-increment for " + tableName + ": " + e.getMessage());
        }
    }

    public void clearSpecificTable(String tableName) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            clearTable(stmt, tableName);
            resetAutoIncrement(stmt, tableName);

            auditService.logSystemAction("TABLE_CLEARED_" + tableName);
            System.out.println("✓ Table " + tableName + " cleared successfully!");

        } catch (SQLException e) {
            auditService.log("TABLE_CLEAR_FAILED_" + tableName);
            System.err.println("✗ Error clearing table " + tableName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clearAuditLog() {
        try {
            java.io.File auditFile = new java.io.File("audit.csv");
            if (auditFile.exists()) {
                auditFile.delete();
                System.out.println("✓ Audit log cleared!");
            }
        } catch (Exception e) {
            System.err.println("✗ Error clearing audit log: " + e.getMessage());
        }
    }
}
