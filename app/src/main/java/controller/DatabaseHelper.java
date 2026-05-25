package controller;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:database/students.db";

    static {
        try {
            File folder = new File("database");

            if (!folder.exists()) {
                folder.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS students ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "nis TEXT UNIQUE,"
                        + "nama TEXT UNIQUE,"
                        + "kelas TEXT,"
                        + "uts REAL,"
                        + "uas REAL,"
                        + "tugas TEXT,"
                        + "nilaiAkhir REAL)";

        try (
                Connection conn = connect();
                Statement stmt = conn.createStatement()
        ) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}