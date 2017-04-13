package ru.mirea.ippo;

import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

abstract class PutConnect {
    void start() throws Exception {
        Class.forName("org.h2.Driver");
        try (Connection c = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")) {
            try {
                try (Statement stmt = c.createStatement()) {
                    stmt.execute("CREATE TABLE property(id BIGINT PRIMARY KEY AUTO_INCREMENT, value VARCHAR(100) NOT NULL)");
                } catch (SQLException ex) {
                    if (!ex.getSQLState().equals("42S01"))
                        throw ex;
                }
                afterConnected(c);
                try (Statement stmt = c.createStatement()) {
                    statement(stmt);
                }
            } finally {
                print(c);
            }
        }
    }

    protected void afterConnected(Connection c) throws Exception {

    }

    private int print(Statement stmt) throws SQLException {
        int count = 0;
        try (ResultSet rs = stmt.executeQuery("SELECT id, value FROM property")) {
            while (rs.next()) {
                ++count;
                System.out.println(rs.getInt(1) + " = " + rs.getString(2));
            }
        }
        return count;
    }

    private void print(Connection c) {
        try (Statement stmt = c.createStatement()) {
            int count = print(stmt);
            if (count == 0) {
                System.out.println("Ни одной записи не найдено");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void statement(Statement stmt) throws Exception {

    }

    final void putProperty(Statement stmt) throws Exception {
        stmt.executeUpdate(
                String.format("INSERT INTO property(value) VALUES('PROPERTY%d')", propertyCount.incrementAndGet()));
    }

    private final AtomicInteger propertyCount = new AtomicInteger(0);
}
